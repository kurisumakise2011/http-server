package study.net;

import java.net.Socket;

public interface HttpServerHandler {

    void handle(ServerThreadDelegator thread, Socket socket);

}
