package ee.ttu.itv0130.typerace.service.data_service.objects;

import java.util.LinkedList;
import java.util.List;

public class PlayerScores {
	private List<RoundScore> roundScores = new LinkedList<>();

	public List<RoundScore> getRoundScores() {
		return roundScores;
	}

	public void addRoundScore(RoundScore roundScore) {
		roundScores.add(roundScore);
	}
}
