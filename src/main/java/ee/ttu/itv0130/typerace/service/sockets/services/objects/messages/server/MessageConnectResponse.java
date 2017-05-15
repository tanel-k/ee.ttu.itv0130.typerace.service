package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server;

import org.json.JSONObject;

import ee.ttu.itv0130.typerace.service.sockets.services.objects.ServerMessageType;

public class MessageConnectResponse extends ServerMessage {
	private String sessionId;

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	@Override
	protected void extendJSON(JSONObject json) {
		json.put("sessionId", sessionId);
	}

	public MessageConnectResponse() {
		super(ServerMessageType.CONNECT_RESPONSE);
	}
}
