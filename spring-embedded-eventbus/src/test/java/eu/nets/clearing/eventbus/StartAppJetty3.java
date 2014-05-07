package eu.nets.clearing.eventbus;


import eu.nets.utils.jetty.embedded.*;
import org.eclipse.jetty.util.component.LifeCycle;
import org.springframework.web.context.WebApplicationContext;

import java.util.EventListener;

import static eu.nets.utils.jetty.embedded.EmbeddedSpringBuilder.createSpringContextLoader;

public class StartAppJetty3 {
    private LifeCycle.Listener jettyLifeCycleListener;

    public static void main(String... args) {
        StartAppJetty3 startApp = new StartAppJetty3();
        startApp.startJetty();

    }

    public EmbeddedJettyBuilder startJetty() {
        configureHazelcastAndVertx();

        ContextPathConfig webAppSource = new PropertiesFileConfig();
        final EmbeddedJettyBuilder builder = new EmbeddedJettyBuilder(webAppSource, false);

        StdoutRedirect.tieSystemOutAndErrToLog();
        builder.addHttpAccessLogAtRoot();

        WebApplicationContext ctx = EmbeddedSpringBuilder.createApplicationContext("App3 Context",
                SpringConfigApp2.class);

        EventListener springContextLoader = createSpringContextLoader(ctx);
        builder.addKeystore(10000);

        EmbeddedJettyBuilder.ServletContextHandlerBuilder servletContextHandlerBuilder = builder.createRootServletContextHandler("")
                .addEventListener(springContextLoader);
        builder.createNetsStandardClasspathResourceHandler();

        if (jettyLifeCycleListener != null) {
            builder.addLifecycleListener(jettyLifeCycleListener);
        }
        try {
            builder.startJetty();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return builder;
    }


    private static void configureHazelcastAndVertx() {
        System.setProperty("vertx.clusterManagerFactory", "org.vertx.java.spi.cluster.impl.hazelcast.HazelcastClusterManagerFactory");
        System.setProperty("hazelcast.logging.type", "slf4j");
        System.setProperty("org.vertx.logger-delegate-factory-class-name", "org.vertx.java.core.logging.impl.SLF4JLogDelegateFactory");
        System.setProperty("port", "9070");
    }

    public void setJettyLifeCycleListener(LifeCycle.Listener jettyLifeCycleListener) {
        this.jettyLifeCycleListener = jettyLifeCycleListener;
    }
}
