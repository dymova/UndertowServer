package ru.nsu.ccfit.dymova.undertow_server.handlers;

import io.undertow.io.Receiver;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.simpleframework.xml.core.Persister;
import ru.nsu.ccfit.dymova.undertow_server.Server;
import ru.nsu.ccfit.dymova.undertow_server.UpdateResponseMessage;

import java.io.StringWriter;
import java.util.ArrayDeque;

public class UpdateHandler implements HttpHandler {
   private final Persister serializer;
   private final Server server;

    public UpdateHandler(Persister serializer, Server server) {
        this.serializer = serializer;
        this.server = server;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        Integer lastId = Integer.valueOf(exchange.getQueryParameters().getOrDefault("id", new ArrayDeque<String>()).poll());

        UpdateResponseMessage message = server.getNewMessage(lastId);

        StringWriter writer = new StringWriter();
        serializer.write(message, writer);
        exchange.getResponseSender().send(writer.toString());
    }
}
