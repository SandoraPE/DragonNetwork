package DragonNetwork;

import DragonNetwork.session.ClientSession;
import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class DragonNetClient {

    private final ClientSession server;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public static void main(String[] args) throws Exception {
        new DragonNetClient(8080);
    }

    public DragonNetClient(int port) throws InterruptedException {
        server = new ClientSession(new InetSocketAddress("127.0.0.1", port));

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
