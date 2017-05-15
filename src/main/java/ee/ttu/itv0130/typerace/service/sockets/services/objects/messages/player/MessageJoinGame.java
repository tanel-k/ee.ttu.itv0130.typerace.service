package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.player;

import org.json.JSONObject;

public class MessageJoinGame extends PlayerMessage {
	public MessageJoinGame(JSONObject sourceMessage) {
		super(sourceMessage);
	}
}
