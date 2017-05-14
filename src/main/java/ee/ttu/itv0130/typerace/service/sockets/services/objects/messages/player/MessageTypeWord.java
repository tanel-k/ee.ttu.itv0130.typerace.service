package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.player;

import org.json.JSONObject;

public class MessageTypeWord extends PlayerMessage {
	public String getWord() {
		return getSourceMessage().getString("word");
	}

	public MessageTypeWord(JSONObject sourceMessage) {
		super(sourceMessage);
	}
}
