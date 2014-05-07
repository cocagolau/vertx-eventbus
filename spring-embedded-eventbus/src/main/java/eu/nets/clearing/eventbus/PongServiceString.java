package eu.nets.clearing.eventbus;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;

import javax.annotation.PostConstruct;

@Component
public class PongServiceString implements Handler<Message<String>> {

    private static Logger log = LoggerFactory.getLogger(PongServiceString.class);

    @Autowired
    EventBus eventBus;

    @PostConstruct
    public void registerVertxHandler() {
        log.info("Registering handler on ping");
        eventBus.registerHandler("vertx.ping", this);
    }

    @Override
    public void handle(Message<String> message) {
        JsonPing ping = new Gson().fromJson(message.body(), JsonPing.class);
        JsonPing pong = new JsonPing("pong");
        message.reply(new Gson().toJson(pong));
    }
}
