package ee.ttu.itv0130.typerace.service.data_service.objects;

import java.util.LinkedList;
import java.util.List;

public class PlayerScores {
	private List<RoundScore> roundScores = new LinkedList<>();

	public List<RoundScore> getRoundScores() {
		return roundScores;
	}

	public void addRoundScore(RoundScore roundScore) {
		roundScore.setIndex(roundScores.size() + 1);
		roundScores.add(roundScore);
	}

	protected void addRoundScoreWithoutIndex(RoundScore roundScore) {
		roundScores.add(roundScore);
	}

	public PlayerScores after(Integer roundIndex) {
		PlayerScores copyScores = new PlayerScores();
		
		for (RoundScore roundScore : roundScores) {
			if (roundScore.getIndex() > roundIndex) {
				copyScores.addRoundScoreWithoutIndex(roundScore);
			}
		}
		
		return copyScores;
	}
}
