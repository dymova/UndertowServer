package ru.nsu.ccfit.dymova.undertow_server.handlers;


import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.simpleframework.xml.core.Persister;
import ru.nsu.ccfit.dymova.undertow_server.Message;
import ru.nsu.ccfit.dymova.undertow_server.Server;

public class MessagesRequestHandler implements HttpHandler {
    private final Persister serializer;
    private final Server server;


    public MessagesRequestHandler(Persister serializer, Server server) {

        this.serializer = serializer;
        this.server = server;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getRequestReceiver().receiveFullString((serverExchange, data) -> {
            try {
                if(!serializer.validate(Message.class, data)) {
                    exchange.getResponseSender().send("not validate message");
                    return;
                }
                Message newMessage = serializer.read(Message.class, data);
                System.out.printf("new Message:" + newMessage.getId() + newMessage.getText());

                newMessage.setId(server.getNextMessageId());
                server.saveMessage(newMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }
}
