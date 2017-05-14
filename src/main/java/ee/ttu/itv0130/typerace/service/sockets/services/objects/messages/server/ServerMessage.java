package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server;

import org.json.JSONObject;

import ee.ttu.itv0130.typerace.service.sockets.services.objects.ServerMessageType;

public class ServerMessage {
	private ServerMessageType messageType;

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("type", messageType);
		extendJSON(json);
		return json;
	}

	protected void extendJSON(JSONObject json) {
		// do nothing by default
	}

	public ServerMessageType getMessageType() {
		return messageType;
	}

	public ServerMessage(ServerMessageType messageType) {
		this.messageType = messageType;
	}
}
