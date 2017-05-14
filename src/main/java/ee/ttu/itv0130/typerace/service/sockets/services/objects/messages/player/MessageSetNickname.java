package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.player;

import org.json.JSONObject;

public class MessageSetNickname extends PlayerMessage {
	public String getNickname() {
		return getSourceMessage().getString("nickname");
	}

	public MessageSetNickname(JSONObject sourceMessage) {
		super(sourceMessage);
	}
}
