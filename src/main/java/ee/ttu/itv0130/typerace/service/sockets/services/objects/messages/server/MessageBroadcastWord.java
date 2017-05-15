package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server;

import org.json.JSONObject;

import ee.ttu.itv0130.typerace.service.sockets.services.objects.ServerMessageType;

public class MessageBroadcastWord extends ServerMessage {
	private String word;
	private String opponentNickname;

	public void setWord(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	public void setOpponentNickname(String opponentNickname) {
		this.opponentNickname = opponentNickname;
	}

	public String getOpponentNickname() {
		return opponentNickname;
	}

	@Override
	protected void extendJSON(JSONObject json) {
		json.put("word", word);
		json.put("opponentNickname", opponentNickname);
	}

	public MessageBroadcastWord() {
		super(ServerMessageType.BROADCAST_WORD);
	}
}
