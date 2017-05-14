package ee.ttu.itv0130.typerace.service.sockets.handlers.objects;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


public class JSONWebSocketHandler extends TextWebSocketHandler {
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		handleJSON(session, new JSONObject(message.getPayload()));
	}

	protected void handleJSON(WebSocketSession session, JSONObject json) throws Exception {
		// do nothing by default
	}

	protected void sendJSON(WebSocketSession session, JSONObject json) throws Exception {
		session.sendMessage(new TextMessage(json.toString()));
	}
}
