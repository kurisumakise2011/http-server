package study.net;

import java.util.function.Consumer;

public interface ExceptionalHandler {

    void handle(Runnable task, Consumer<HttpBody> recover);

}
