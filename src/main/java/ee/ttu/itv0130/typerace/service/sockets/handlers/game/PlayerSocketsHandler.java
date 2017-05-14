package ee.ttu.itv0130.typerace.service.sockets.handlers.game;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import ee.ttu.itv0130.typerace.service.sockets.handlers.objects.JSONWebSocketHandler;
import ee.ttu.itv0130.typerace.service.sockets.services.PlayerSocketService;

@Component
public class PlayerSocketsHandler extends JSONWebSocketHandler {
	@Autowired
	private PlayerSocketService playerService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		playerService.register(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		playerService.drop(session);
	}

	@Override
	protected void handleJSON(WebSocketSession session, JSONObject json) throws Exception {
		playerService.handleMessage(session, json);
	}
}
