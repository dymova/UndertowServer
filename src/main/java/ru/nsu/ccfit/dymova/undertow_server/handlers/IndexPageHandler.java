package ru.nsu.ccfit.dymova.undertow_server.handlers;

import freemarker.template.Template;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import ru.nsu.ccfit.dymova.undertow_server.Message;
import ru.nsu.ccfit.dymova.undertow_server.Server;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexPageHandler implements HttpHandler {
    private final Template indexTemplate;
    private final Server server;

    public IndexPageHandler(Template indexTemplate, Server server) {

        this.indexTemplate = indexTemplate;
        this.server = server;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Map<String, Object> model = new HashMap<>();
        List<Message> messages = server.getMessages();
        model.put("messages", messages);
        model.put("emptyFieldExist", false);

        StringWriter writer = new StringWriter();
        indexTemplate.process(model, writer);
        exchange.getResponseSender().send(writer.toString());
    }
}
