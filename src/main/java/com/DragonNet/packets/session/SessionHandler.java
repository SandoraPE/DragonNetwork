package com.DragonNet.packets.session;

import com.DragonNet.packets.Packet;
import com.DragonNet.packets.impl.ConsoleCommandPacket;
import com.DragonNet.packets.impl.ConsoleRedirectPacket;
import com.DragonNet.packets.impl.LoginPacket;

/**
 * A pocketmine based session handler
 */
public interface SessionHandler {

    /**
     * Handle login packet for current session.
     */
    void handleLogin(LoginPacket packet);

    /**
     * Redirect console packet to another connected client.
     */
    void handleClientToServer(ConsoleRedirectPacket packet);

    /**
     * Handle server's command to the prior server.
     */
    void handleServerCommand(ConsoleCommandPacket packet);

    /**
     * Handles raw packet
     */
    void handleRawPacket(Packet packet);
}
