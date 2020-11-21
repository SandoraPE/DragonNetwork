package com.DragonNet.packets.session;

import com.DragonNet.packets.Packet;

/**
 * A pocketmine based session handler
 */
public interface SessionHandler {

    /**
     * Handles raw packet
     */
    void queueRawPacket(Packet packet);
}
