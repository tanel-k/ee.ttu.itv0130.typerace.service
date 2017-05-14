package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.player;

import org.json.JSONObject;

public class MessageJoinLobby extends PlayerMessage {
	public MessageJoinLobby(JSONObject sourceMessage) {
		super(sourceMessage);
	}
}
