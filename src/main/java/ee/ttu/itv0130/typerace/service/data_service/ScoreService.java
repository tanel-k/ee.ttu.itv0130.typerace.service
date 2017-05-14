package ee.ttu.itv0130.typerace.service.data_service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import ee.ttu.itv0130.typerace.service.data_service.objects.PlayerScores;
import ee.ttu.itv0130.typerace.service.data_service.objects.RoundScore;

@Service
public class ScoreService {
	private Map<String, PlayerScores> scoreMap = new ConcurrentHashMap<>();

	public synchronized void remove(String sessionId) {
		scoreMap.remove(sessionId);
	}

	public synchronized PlayerScores get(String sessionId) {
		if (!scoreMap.containsKey(sessionId)) {
			PlayerScores playerScores = new PlayerScores();
			scoreMap.put(sessionId, playerScores);
			return playerScores;
		}
		
		return scoreMap.get(sessionId);
	}

	public synchronized void addRoundScore(String sessionId, RoundScore roundScore) {
		PlayerScores playerScores = get(sessionId);
		playerScores.addRoundScore(roundScore);
	}
}
