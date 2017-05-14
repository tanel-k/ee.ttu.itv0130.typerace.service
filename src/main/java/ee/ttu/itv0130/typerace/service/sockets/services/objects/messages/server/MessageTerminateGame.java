package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server;

import org.json.JSONObject;

import ee.ttu.itv0130.typerace.service.sockets.services.objects.ServerMessageType;

public class MessageTerminateGame extends ServerMessage {
	private String reason;

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	@Override
	protected void extendJSON(JSONObject json) {
		json.put("reason", reason);
	}

	public MessageTerminateGame() {
		super(ServerMessageType.TERMINATE_GAME);
	}
}
