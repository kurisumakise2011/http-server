package study.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;

public class Bootstrap {
    private static final LogManager log = LogManager.getLogManager();

    static {
        try (InputStream is = Bootstrap.class.getResourceAsStream("logger.properties")) {
            log.readConfiguration(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestController controller = new TestController();

        Server server = Server.builder()
                .configuration(ServerConfiguration.builder()
                        .port(8080)
                        .requestTimeout(120_000)
                        .requestSizeBytes(100_000)
                        .urlMaxSize(50)
                        .protocol("HTTP/1.1")
                        .routes()
                            .add("/", controller::returnIndexPage)
                            .add("/files", controller::getFile)
                            .add("/location", controller::doRedirect)
                            .add("/meta", controller::statistic)
                            .build()
                        .build())
                .executorService(Executors.newFixedThreadPool(50))
                .exceptionalHandler(new DefaultExceptionalHandler())
                .build();
        try {
            server.run();
            server.join();
        } finally {
            server.stop(() -> {});
        }


    }
}
