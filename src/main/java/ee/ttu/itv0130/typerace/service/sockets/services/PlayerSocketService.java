package ee.ttu.itv0130.typerace.service.sockets.services;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import ee.ttu.itv0130.typerace.service.data_service.ScoreService;
import ee.ttu.itv0130.typerace.service.data_service.WordService;
import ee.ttu.itv0130.typerace.service.data_service.objects.RoundScore;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.GameMessageType;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.GameState;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.PlayerMessageType;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.PlayerSocketSession;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.player.MessageJoinGame;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.player.MessageSetNickname;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.player.MessageTypeWord;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server.MessageBroadcastWord;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server.MessageConnectResponse;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server.MessageJoinGameResponse;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server.MessageSetNicknameResponse;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server.MessageTerminateGame;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server.MessageTypeWordResponse;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server.ServerMessage;
import ee.ttu.itv0130.typerace.service.utils.StringUtils;

@Service
public class PlayerSocketService {
	private static final int NEW_ROUND_THROTTLE_MILLIS = 2000;
	private static class LobbyItem {
		public PlayerSocketSession playerSession;
		public Date insertionDate;
	
		public LobbyItem(PlayerSocketSession playerSession, Date insertionDate) {
			this.playerSession = playerSession;
			this.insertionDate = insertionDate;
		}
	}

	@Autowired
	private ScoreService scoreService;
	@Autowired
	private WordService wordService;

	private Map<String, PlayerSocketSession> socketMap = new ConcurrentHashMap<>();
	private Map<String, LobbyItem> lobbyMap = new ConcurrentHashMap<>();
	private Map<String, GameState> gameStateMap = new ConcurrentHashMap<>();

	public synchronized void handleMessage(WebSocketSession session, JSONObject jsonMessage) {
		String sessionId = session.getId();
		String type = jsonMessage.getString("type");
		PlayerMessageType messageType = PlayerMessageType.valueOf(type);
		PlayerSocketSession playerSession = socketMap.get(sessionId);
		
		switch (messageType) {
			case JOIN_GAME:
				handleJoinGameMessage(playerSession, new MessageJoinGame(jsonMessage));
				break;
			case TYPE_WORD:
				handleTypeWordMessage(playerSession, new MessageTypeWord(jsonMessage));
				break;
			case SET_NICKNAME:
				handleSetNicknameMessage(playerSession, new MessageSetNickname(jsonMessage));
				break;
			case PING:
				// some servers don't like to keep inactive conns open
				break;
			default:
				break;
		}
	}

	public synchronized void register(WebSocketSession session) {
		String sessionId = session.getId();
		MessageConnectResponse message = new MessageConnectResponse();
		message.setSessionId(sessionId);
		
		PlayerSocketSession gameSession = new PlayerSocketSession(session);
		socketMap.put(sessionId, gameSession);
		
		sendMessage(gameSession, message);
	}

	public synchronized void drop(WebSocketSession session) {
		String sessionId = session.getId();
		socketMap.remove(sessionId);
		scoreService.remove(sessionId);
		
		if (lobbyMap.containsKey(sessionId)) {
			lobbyMap.remove(sessionId);
		} else if (gameStateMap.containsKey(sessionId)) {
			GameState gameState = gameStateMap.get(sessionId);
			String otherSessionId = gameState.getOtherPlayerSessionId(sessionId);
			gameStateMap.remove(sessionId);
			gameStateMap.remove(otherSessionId);
			
			PlayerSocketSession otherPlayerSession = gameState.getPlayer(otherSessionId);
			MessageTerminateGame terminationMessage = new MessageTerminateGame();
			terminationMessage.setReason("Opponent left");
			// opponent should manually re-join the game
			sendMessage(otherPlayerSession, terminationMessage);
		}
	}

	private void handleJoinGameMessage(PlayerSocketSession playerSession, MessageJoinGame message) {
		if (gameStateMap.containsKey(playerSession.getId())) {
			// already in-game
			return;
		}
		
		findGameForPlayer(playerSession);
	}

	private void handleSetNicknameMessage(PlayerSocketSession playerSession, MessageSetNickname message) {
		// nickname is broadcast together with current word
		String nickname = message.getNickname();
		
		MessageSetNicknameResponse response = new MessageSetNicknameResponse();
		if (isNicknameValid(nickname)) {
			response.setIsAccepted(true);
			playerSession.setNickname(nickname);
		} else {
			response.setIsAccepted(false);
		}
		
		sendMessage(playerSession, response);
	}

	private void handleTypeWordMessage(PlayerSocketSession playerSession, MessageTypeWord message) {
		GameState gameState = gameStateMap.get(playerSession.getId());
		MessageTypeWordResponse responseMessage = new MessageTypeWordResponse();
		GameMessageType gameMessageType;
		
		boolean startNewRound = false;
		if (gameState != null) {
			// game exists
			if (gameState.hasWinner() && gameState.getWinner().getId().equals(playerSession.getId())) {
				// fixes double submission
				gameMessageType = GameMessageType.NOT_ALLOWED;
			} else if (gameState.isSuspended()) {
				// fixes loser double submission
				gameMessageType = GameMessageType.NOT_ALLOWED;
			} else {
			
				Long currentTimeMillis = new Date().getTime();
				if (gameState.getCurrentWord().equals(message.getWord())) {
					// player sent word correctly
					Long playerTimeMillis = currentTimeMillis - gameState.getRoundStartedMillis();
					gameState.setPlayerTime(playerSession.getId(), playerTimeMillis);
					responseMessage.setPlayerTimeMillis(playerTimeMillis);
					// 1 point per character for the loser
					int loserScore = gameState.getCurrentWord().length();
					// 10 points per character for the winner
					int winnerScore = loserScore * 10;
					
					if (gameState.hasWinner()) {
						// round already has a winner - player lost
						gameMessageType = GameMessageType.ROUND_LOST;
						responseMessage.setPlayerScore(loserScore);
						
						// store scores
						String otherPlayerSessionId = gameState.getOtherPlayerSessionId(playerSession.getId());
						RoundScore roundScore = new RoundScore();
						roundScore.setWord(gameState.getCurrentWord());
						roundScore.setDidWin(false);
						roundScore.setPlayerTimeMillis(playerTimeMillis);
						roundScore.setPlayerScore(loserScore);
						roundScore.setOpponentScore(winnerScore);
						roundScore.setOpponentTimeMillis(gameState.getPlayerTime(otherPlayerSessionId));
						scoreService.addRoundScore(playerSession.getId(), roundScore);
						scoreService.addRoundScore(otherPlayerSessionId, roundScore.forOpponent());
						
						// start the next round
						startNewRound = true;
					} else {
						// round had no previous winner - player won
						gameMessageType = GameMessageType.ROUND_WON;
						responseMessage.setPlayerScore(winnerScore);
						gameState.setHasWinner(true);
						gameState.setWinner(playerSession);
						// wait for other player to complete
					}
				} else {
					// player mistyped word
					gameMessageType = GameMessageType.WORD_MISMATCH;
				}
			}
			
		} else {
			// no game found
			gameMessageType = GameMessageType.NO_GAME_FOUND;
		}
		
		responseMessage.setGameMessageType(gameMessageType);
		sendMessage(playerSession, responseMessage);
		
		if (startNewRound) {
			gameState.setSuspended(true);
			// delay the next round
			new Timer().schedule( 
				new TimerTask() {
					@Override
					public void run() {
						startNewRound(gameState);
					}
				}, NEW_ROUND_THROTTLE_MILLIS);
		}
	}

	private void findGameForPlayer(PlayerSocketSession playerSession) {
		MessageJoinGameResponse message = new MessageJoinGameResponse();
		boolean doErrorsExist = false;
		if (!isNicknameValid(playerSession.getNickname())) {
			message.addError("Invalid nickname");
			doErrorsExist = true;
		}
		
		sendMessage(playerSession, message);
		
		if (!doErrorsExist) {
			if (lobbyMap.isEmpty()) {
				// nobody else is waiting to join a game
				lobbyMap.put(playerSession.getId(), new LobbyItem(playerSession, new Date()));
			} else {
				// find the player who has been in the lobby the longest
				String lobbyKey = lobbyMap.entrySet()
					.stream()
					.reduce(null, (accEntry, currEntry) -> {
						if (accEntry == null) {
							return currEntry;
						}
						
						LobbyItem acc = accEntry.getValue();
						LobbyItem curr = currEntry.getValue();
						if (curr.insertionDate.before(acc.insertionDate)) {
							return currEntry;
						}
						
						return accEntry;
					})
					.getKey();
				
				PlayerSocketSession otherPlayerSession = lobbyMap.remove(lobbyKey).playerSession;
				startNewGame(playerSession, otherPlayerSession);
			}
		}
	}

	private void startNewGame(PlayerSocketSession firstPlayer, PlayerSocketSession secondPlayer) {
		GameState gameState = new GameState();
		gameState.addPlayer(firstPlayer);
		gameState.addPlayer(secondPlayer);
		
		gameStateMap.put(firstPlayer.getId(), gameState);
		gameStateMap.put(secondPlayer.getId(), gameState);
		
		startNewRound(gameState);
	}

	private void startNewRound(GameState gameState) {
		String nextWord = wordService.getRandomWord();
		gameState.setSuspended(false);
		gameState.setCurrentWord(nextWord);
		gameState.setRoundStartedMillis(new Date().getTime());
		gameState.setHasWinner(false);
		gameState.setWinner(null);
		broadcastWord(gameState);
	}

	private void broadcastWord(GameState gameState) {
		MessageBroadcastWord message = new MessageBroadcastWord();
		message.setWord(gameState.getCurrentWord());
		
		for (PlayerSocketSession playerSession : gameState.getPlayers()) {
			message.setOpponentNickname(gameState.getOtherPlayer(playerSession).getNickname());
			sendMessage(playerSession, message);
		}
	}

	private void sendMessage(PlayerSocketSession playerSession, ServerMessage message) {
		sendMessage(playerSession, message.toJSON());
	}

	private void sendMessage(PlayerSocketSession playerSession, JSONObject responseJson) {
		try {
			playerSession.send(responseJson);
		} catch (IOException e) {
			// ignore for now
		}
	}

	private boolean isNicknameValid(String nickname) {
		return !StringUtils.isEmpty(nickname);
	}
}
