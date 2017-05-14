package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.player;

import org.json.JSONObject;

public class PlayerMessage {
	private JSONObject sourceMessage;

	protected JSONObject getSourceMessage() {
		return sourceMessage;
	}

	public PlayerMessage(JSONObject sourceMessage) {
		this.sourceMessage = sourceMessage;
	}
}
