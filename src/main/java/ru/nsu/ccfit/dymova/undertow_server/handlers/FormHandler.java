package ru.nsu.ccfit.dymova.undertow_server.handlers;

import freemarker.template.Template;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.util.Headers;
import ru.nsu.ccfit.dymova.undertow_server.Message;
import ru.nsu.ccfit.dymova.undertow_server.Server;

import java.io.StringWriter;
import java.util.HashMap;

public class FormHandler implements HttpHandler {
    private final Template indexTemplate;
    private final Server server;

    public FormHandler(Template indexTemplate, Server server) {
        this.indexTemplate = indexTemplate;

        this.server = server;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        FormData form = exchange.getAttachment(FormDataParser.FORM_DATA);

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
        HashMap<String, Object> templateData = new HashMap<String, Object>();

        FormData.FormValue nameFv = form.getFirst("name");
        FormData.FormValue messageFv = form.getFirst("message");
        String name = nameFv.getValue();
        String message = messageFv.getValue();
        if (name.isEmpty() || message.isEmpty()) {
            templateData.put("emptyFieldExist", true);
        } else {
            server.saveMessage(new Message(name, message));
            templateData.put("emptyFieldExist", false);
        }

        templateData.put("messages", server.getMessages());


        StringWriter stringWriter = new StringWriter();
        indexTemplate.process(templateData, stringWriter);

        exchange.getResponseSender().send(stringWriter.toString());
    }
}
