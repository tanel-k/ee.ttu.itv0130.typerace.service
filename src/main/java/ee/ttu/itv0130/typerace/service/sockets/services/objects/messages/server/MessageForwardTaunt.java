package ee.ttu.itv0130.typerace.service.sockets.services.objects.messages.server;

import ee.ttu.itv0130.typerace.service.sockets.services.objects.ServerMessageType;

public class MessageForwardTaunt extends ServerMessage {
	public MessageForwardTaunt() {
		super(ServerMessageType.FORWARD_TAUNT);
	}
}
