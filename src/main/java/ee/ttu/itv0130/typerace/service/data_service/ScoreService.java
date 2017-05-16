package ee.ttu.itv0130.typerace.service.data_service;

import java.util.Map;
import java.util.Optional;
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

	public synchronized PlayerScores getWithoutCreate(String sessionId, Optional<Integer> optAfterIndex) {
		if (scoreMap.containsKey(sessionId)) {
			return getWithCreate(sessionId, optAfterIndex);
		}
		
		return new PlayerScores();
	}

	public synchronized PlayerScores getWithCreate(String sessionId, Optional<Integer> optAfterIndex) {
		if (!scoreMap.containsKey(sessionId)) {
			PlayerScores playerScores = new PlayerScores();
			scoreMap.put(sessionId, playerScores);
		}
		
		PlayerScores scores = scoreMap.get(sessionId);
		if (optAfterIndex.isPresent()) {
			return scores.after(optAfterIndex.get());
		}
		return scores;
	}

	public synchronized void addRoundScore(String sessionId, RoundScore roundScore) {
		PlayerScores playerScores = getWithCreate(sessionId, Optional.ofNullable((Integer) null));
		playerScores.addRoundScore(roundScore);
	}
}
