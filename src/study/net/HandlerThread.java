package study.net;

import java.net.Socket;

public class HandlerThread extends Thread {
    private Socket socket;
    private boolean running;

    public HandlerThread(Runnable target, Socket socket) {
        super(target);
        this.socket = socket;
    }

    public void finish() {
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
