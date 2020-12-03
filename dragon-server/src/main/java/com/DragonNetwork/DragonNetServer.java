package com.DragonNetwork;

import com.DragonNetwork.session.ServerSession;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class DragonNetServer {

    private final ServerSession server;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public static void main(String[] args) throws Exception {
        new DragonNetServer(8080);
    }

    public DragonNetServer(int port) throws InterruptedException {
        this.server = new ServerSession(port);

        start();
    }

    @SuppressWarnings("BusyWait")
    private void start() throws InterruptedException {
        // 50 ticks per second unit.
        while (isRunning.get()) {
            server.tickSession();

            Thread.sleep(20);
        }

        server.shutdown();
    }
}
