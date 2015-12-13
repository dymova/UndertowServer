package ru.nsu.ccfit.dymova.undertow_server;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import org.simpleframework.xml.core.Persister;
import ru.nsu.ccfit.dymova.undertow_server.handlers.FormHandler;
import ru.nsu.ccfit.dymova.undertow_server.handlers.IndexPageHandler;
import ru.nsu.ccfit.dymova.undertow_server.handlers.MessagesRequestHandler;
import ru.nsu.ccfit.dymova.undertow_server.handlers.UpdateHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Server implements Runnable{
    private static final String USAGE = "Usage: Server <port> <host>";
    private final int port;
    private final String host;
    private final Template indexTemplate;
    private final ArrayList<Message> messages;
    private static Persister serializer;
    private int nextMessageId = 0;

    public Server(int port, String host) throws IOException {
        this.port = port;
        this.host = host;
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassLoaderForTemplateLoading(Server.class.getClassLoader(),"templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

        indexTemplate = cfg.getTemplate("index.html");


        messages = new ArrayList<Message>();

        serializer = new Persister();
    }

    public static void main(final String[] args) throws IOException {
//        if(args.length != 2) {
//            throw new IllegalArgumentException(USAGE);
//        }
//        int port = Integer.parseInt(args[0]);
//        new Server(port, args[1]).run();
        new Server(8080, "localhost").run();
    }

    @Override
    public void run() {
        Undertow server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(Handlers.path()
//                        .addPrefixPath("/", new ResourceHandler(new ClassPathResourceManager(Server.class.getClassLoader(), "templates")))
                        .addPrefixPath("/", new IndexPageHandler(indexTemplate, this))
                        .addPrefixPath("/form", new EagerFormParsingHandler().setNext(new FormHandler(indexTemplate, this)))
                        .addPrefixPath("/api/message", new MessagesRequestHandler(serializer, this))
                        .addPrefixPath("/api/update", new UpdateHandler(serializer, this))
                ).build();

        server.start();
    }

    public void saveMessage(Message msg) {
        messages.add(msg);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public UpdateResponseMessage getNewMessage(final Integer lastId) {
        List<Message> newMessages =  messages.stream()
                .filter(message -> message.getId() > lastId)
                .collect(Collectors.toList());

        return new UpdateResponseMessage(newMessages);

    }

    public int getNextMessageId() {
        int oldMessageId = nextMessageId;
        nextMessageId++;
        return oldMessageId;
    }
}
