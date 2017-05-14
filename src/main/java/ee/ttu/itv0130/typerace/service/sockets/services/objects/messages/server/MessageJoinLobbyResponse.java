package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server;

import org.json.JSONObject;

import ee.ttu.itv0130.typerace.service.sockets.services.objects.ServerMessageType;

public class MessageJoinLobbyResponse extends ServerMessage {
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

	public MessageJoinLobbyResponse() {
		super(ServerMessageType.JOIN_LOBBY_RESPONSE);
	}
}
