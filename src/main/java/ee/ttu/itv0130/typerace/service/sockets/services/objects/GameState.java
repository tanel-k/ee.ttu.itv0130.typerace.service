package ee.ttu.itv0130.typerace.service.sockets.services.objects;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameState {
	private Map<String, PlayerSocketSession> playerMap = new ConcurrentHashMap<>();
	private Map<String, Long> playerTimeMap = new ConcurrentHashMap<>();

	private PlayerSocketSession winnerSession;
	private Long roundStartedMillis;
	private String currentWord;
	private boolean hasWinner = false;
	private boolean isSuspended = false;

	public void setSuspended(boolean isSuspended) {
		this.isSuspended = isSuspended;
	}

	public boolean isSuspended() {
		return isSuspended;
	}

	public void setRoundStartedMillis(Long roundStartedMillis) {
		this.roundStartedMillis = roundStartedMillis;
	}

	public void setPlayerTime(String sessionId, Long playerTimeMillis) {
		playerTimeMap.put(sessionId, playerTimeMillis);
	}

	public Long getPlayerTime(String sessionId) {
		return playerTimeMap.get(sessionId);
	}

	public Long getRoundStartedMillis() {
		return roundStartedMillis;
	}

	public String addPlayer(PlayerSocketSession playerSession) {
		playerMap.put(playerSession.getId(), playerSession);
		return playerSession.getId();
	}

	public PlayerSocketSession getPlayer(String sessionId) {
		return playerMap.get(sessionId);
	}

	public void setWinner(PlayerSocketSession winnerSession) {
		this.winnerSession = winnerSession;
	}

	public PlayerSocketSession getWinner() {
		return winnerSession;
	}

	public String getOtherPlayerSessionId(String sessionId) {
		for (String key : playerMap.keySet()) {
			if (!key.equals(sessionId)) {
				return key;
			}
		}
	
		return null;
	}

	public PlayerSocketSession getOtherPlayer(String sessionId) {
		for (String key : playerMap.keySet()) {
			if (!key.equals(sessionId)) {
				return playerMap.get(key);
			}
		}
	
		return null;
	}

	public PlayerSocketSession getOtherPlayer(PlayerSocketSession otherPlayerSession) {
		return getOtherPlayer(otherPlayerSession.getId());
	}

	public Collection<PlayerSocketSession> getPlayers() {
		return playerMap.values();
	}

	public String getCurrentWord() {
		return currentWord;
	}

	public void setCurrentWord(String currentWord) {
		this.currentWord = currentWord;
	}

	public boolean hasWinner() {
		return hasWinner;
	}

	public void setHasWinner(boolean hasWinner) {
		this.hasWinner = hasWinner;
	}
}
