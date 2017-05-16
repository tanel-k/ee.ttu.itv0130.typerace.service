package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import ee.ttu.itv0130.typerace.service.sockets.services.objects.ServerMessageType;

public class MessageJoinGameResponse extends ServerMessage {
	private List<String> errors = new LinkedList<>();

	public void addError(String error) {
		this.errors.add(error);
	}

	public List<String> getErrors() {
		return new LinkedList<String>(errors);
	}

	@Override
	protected void extendJSON(JSONObject json) {
		json.put("errors", errors);
	}

	public MessageJoinGameResponse() {
		super(ServerMessageType.JOIN_GAME_RESPONSE);
	}
}
