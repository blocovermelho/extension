package org.blocovermelho.bvextension.events;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.world.GameMode;
import org.blocovermelho.bvextension.commands.GamemodeSwitchCommand;

public class RestorePositionLogoff  implements ServerPlayConnectionEvents.Disconnect {
    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        var player = handler.player;
        if (GamemodeSwitchCommand.oldPos.containsKey(player.getUuid())) {
            var playerLastPos = GamemodeSwitchCommand.oldPos.remove(player.getUuid());
            var world = server.getWorld(playerLastPos.world());

            player.teleport(world, playerLastPos.pos().getX(), playerLastPos.pos().getY(), playerLastPos.pos().getZ(), playerLastPos.yaw(), playerLastPos.pitch());
            player.changeGameMode(GameMode.SURVIVAL);
        }
    }
}
