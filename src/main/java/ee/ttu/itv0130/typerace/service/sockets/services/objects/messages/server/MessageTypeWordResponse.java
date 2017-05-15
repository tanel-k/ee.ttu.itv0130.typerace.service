package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server;

import org.json.JSONObject;

import ee.ttu.itv0130.typerace.service.sockets.services.objects.GameMessageType;
import ee.ttu.itv0130.typerace.service.sockets.services.objects.ServerMessageType;

public class MessageTypeWordResponse extends ServerMessage {
	private GameMessageType gameMessageType;
	private Long playerTimeMillis;
	private Integer playerScore;

	public GameMessageType getGameMessageType() {
		return gameMessageType;
	}

	public void setGameMessageType(GameMessageType gameMessageType) {
		this.gameMessageType = gameMessageType;
	}

	public Long getPlayerTimeMillis() {
		return playerTimeMillis;
	}

	public void setPlayerTimeMillis(Long playerTimeMillis) {
		this.playerTimeMillis = playerTimeMillis;
	}

	public void setPlayerScore(int playerScore) {
		this.playerScore = playerScore;
	}

	public Integer getPlayerScore() {
		return playerScore;
	}

	@Override
	protected void extendJSON(JSONObject json) {
		json.put("gameMessageType", gameMessageType);
		
		if (playerTimeMillis != null) {
			json.put("playerTimeMillis", playerTimeMillis);
		}
		
		if (playerScore != null) {
			json.put("playerScore", playerScore);
		}
	}

	public MessageTypeWordResponse() {
		super(ServerMessageType.TYPE_WORD_RESPONSE);
	}
}
