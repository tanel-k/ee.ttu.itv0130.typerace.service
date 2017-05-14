package ee.ttu.itv0130.typerace.service.sockets.services.objects;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;


public class PlayerSocketSession {
	private String nickname;
	private WebSocketSession session;

	public String getId() {
		return session.getId();
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return nickname;
	}

	public void send(JSONObject json) throws IOException {
		session.sendMessage(new TextMessage(json.toString()));
	}

	public WebSocketSession getSession() {
		return session;
	}

	public PlayerSocketSession(WebSocketSession session) {
		this.session = session;
	}
}
