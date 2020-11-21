package com.DragonNet;

import com.DragonNet.handler.ServerInstance;
import com.DragonNet.packets.session.ServerSession;
import lombok.extern.log4j.Log4j2;

/**
 * Server-side netty systems.
 */
@Log4j2
public class DragonNetServer {

    private final ServerInstance server;

    public static void main(String[] args) throws Exception {
        new DragonNetServer(8080);
    }

    public DragonNetServer(int port) throws InterruptedException {
        this.server = new ServerInstance(new ServerSession(port), null);

        start();
    }

    private void start() throws InterruptedException {
        while (true) {
            Thread.sleep(50);
        }
    }
}
