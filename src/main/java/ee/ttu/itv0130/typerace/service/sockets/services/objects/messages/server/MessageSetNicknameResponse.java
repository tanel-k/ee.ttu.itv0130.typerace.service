package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server;

import org.json.JSONObject;

import ee.ttu.itv0130.typerace.service.sockets.services.objects.ServerMessageType;

public class MessageSetNicknameResponse extends ServerMessage {
	private boolean isAccepted;

	public void setIsAccepted(boolean isAccepted) {
		this.isAccepted = isAccepted;
	}

	public boolean isAccepted() {
		return isAccepted;
	}

	@Override
	protected void extendJSON(JSONObject json) {
		json.put("isAccepted", isAccepted);
	}

	public MessageSetNicknameResponse() {
		super(ServerMessageType.SET_NICKNAME_RESPONSE);
	}
}
