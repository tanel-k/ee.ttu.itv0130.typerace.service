package ee.ttu.itv0130.typerace.service.sockets.services;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
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
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.player.MessageJoinLobby;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.player.MessageSetNickname;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.player.MessageTypeWord;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server.MessageBroadcastWord;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server.MessageJoinLobbyResponse;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server.MessageTerminateGame;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server.MessageTypeWordResponse;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server.ServerMessage;

@Service
public class PlayerSocketService {
	@Autowired
	private ScoreService scoreService;
	@Autowired
	private WordService wordService;

	private Map<String, PlayerSocketSession> socketMap = new ConcurrentHashMap<>();
	private Map<String, PlayerSocketSession> lobbyMap = new ConcurrentHashMap<>();
	private Map<String, GameState> gameStateMap = new ConcurrentHashMap<>();

	public synchronized void handleMessage(WebSocketSession session, JSONObject jsonMessage) {
		String sessionId = session.getId();
		String type = jsonMessage.getString("type");
		PlayerMessageType messageType = PlayerMessageType.valueOf(type);
		PlayerSocketSession playerSession = socketMap.get(sessionId);
		
		switch (messageType) {
			case JOIN_LOBBY:
				handleJoinLobbyMessage(playerSession, new MessageJoinLobby(jsonMessage));
				break;
			case TYPE_WORD:
				handleTypeWordMessage(playerSession, new MessageTypeWord(jsonMessage));
				break;
			case SET_NICKNAME:
				handleSetNicknameMessage(playerSession, new MessageSetNickname(jsonMessage));
			default:
				break;
		}
	}

	public synchronized void register(WebSocketSession session) {
		String sessionId = session.getId();
		MessageJoinLobbyResponse message = new MessageJoinLobbyResponse();
		message.setSessionId(sessionId);
		
		PlayerSocketSession gameSession = new PlayerSocketSession(session);
		socketMap.put(sessionId, gameSession);
		
		sendResponse(gameSession, message);
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
			// opponent should manually re-join the lobby
			sendResponse(otherPlayerSession, terminationMessage);
		}
	}

	private void handleJoinLobbyMessage(PlayerSocketSession playerSession, MessageJoinLobby message) {
		addToLobby(playerSession);
	}

	private void addToLobby(PlayerSocketSession playerSession) {
		if (lobbyMap.isEmpty()) {
			lobbyMap.put(playerSession.getId(), playerSession);
		} else {
			String lobbyKey = lobbyMap.entrySet().iterator().next().getKey();
			PlayerSocketSession otherPlayerSession = lobbyMap.remove(lobbyKey);
			startGame(playerSession, otherPlayerSession);
		}
	}

	private void startGame(PlayerSocketSession firstPlayer, PlayerSocketSession secondPlayer) {
		GameState gameState = new GameState();
		gameState.addPlayer(firstPlayer);
		gameState.addPlayer(secondPlayer);
		
		gameStateMap.put(firstPlayer.getId(), gameState);
		gameStateMap.put(secondPlayer.getId(), gameState);
		
		newRound(gameState);
	}

	private void newRound(GameState gameState) {
		String nextWord = null;
		
		do {
			nextWord = wordService.getRandomWord();
		} while (gameState.getPreviousWords().contains(nextWord));
		
		gameState.setCurrentWord(nextWord);
		gameState.setRoundStartedMillis(new Date().getTime());
		gameState.setHasWinner(false);
		broadcastWord(gameState);
	}

	private void broadcastWord(GameState gameState) {
		MessageBroadcastWord message = new MessageBroadcastWord();
		message.setWord(gameState.getCurrentWord());
		
		for (PlayerSocketSession playerSession : gameState.getPlayers()) {
			sendResponse(playerSession, message);
		}
	}

	private void handleSetNicknameMessage(PlayerSocketSession playerSession, MessageSetNickname message) {
		String nickname = message.getNickname();
		playerSession.setNickname(nickname);
	}

	private void handleTypeWordMessage(PlayerSocketSession playerSession, MessageTypeWord message) {
		Long currentTimeMillis = new Date().getTime();
		MessageTypeWordResponse responseMessage = new MessageTypeWordResponse();
		
		GameState gameState = gameStateMap.get(playerSession.getId());
		GameMessageType gameMessageType;
		if (gameState.getCurrentWord().equals(message.getWord())) {
			Long playerTimeMillis = currentTimeMillis - gameState.getRoundStartedMillis();
			gameState.setPlayerTime(playerSession.getId(), playerTimeMillis);
			responseMessage.setPlayerTimeMillis(playerTimeMillis);
			
			if (gameState.hasWinner()) {
				gameMessageType = GameMessageType.ROUND_LOST;
				
				// store scores
				String otherPlayerSessionId = gameState.getOtherPlayerSessionId(playerSession.getId());
				int loserScore = gameState.getCurrentWord().length();
				int winnerScore = loserScore * 10;
				RoundScore roundScore = new RoundScore();
				roundScore.setDidWin(false);
				roundScore.setPlayerTimeMillis(playerTimeMillis);
				roundScore.setPlayerScore(loserScore);
				roundScore.setPlayerScore(winnerScore);
				roundScore.setOpponentTimeMillis(gameState.getPlayerTime(otherPlayerSessionId));
				scoreService.addRoundScore(playerSession.getId(), roundScore);
				
				// start the next round
				newRound(gameState);
			} else {
				gameMessageType = GameMessageType.ROUND_WON;
				gameState.setHasWinner(true);
			}
		} else {
			gameMessageType = GameMessageType.WORD_MISMATCH;
		}
		
		responseMessage.setGameMessageType(gameMessageType);
		sendResponse(playerSession, responseMessage);
	}

	private void sendResponse(PlayerSocketSession playerSession, ServerMessage message) {
		sendResponse(playerSession, message.toJSON());
	}

	private void sendResponse(PlayerSocketSession playerSession, JSONObject responseJson) {
		try {
			playerSession.send(responseJson);
		} catch (IOException e) {
			// ignore for now
		}
	}
}
