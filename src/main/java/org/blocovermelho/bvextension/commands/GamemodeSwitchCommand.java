package org.blocovermelho.bvextension.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameMode;
import org.blocovermelho.bvextension.Extension;
import org.blocovermelho.bvextension.types.LastRecordedPosition;

import java.util.HashMap;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;


public class GamemodeSwitchCommand {

    public static HashMap<UUID, LastRecordedPosition> oldPos = new HashMap<>();
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("s").executes(c -> {
            var source = (ServerCommandSource) c.getSource();
            var audience = Extension.audiences.audience(source);
            Sound sound = Sound.sound(Key.key("block.amethyst_block.chime"), Sound.Source.PLAYER, 0.4f, 1);

            if (!source.isExecutedByPlayer()) {
                return 1;
            }
            var player = source.getPlayer();
            assert player != null;

            var gamemode = player.interactionManager.getGameMode();
            if (gamemode == GameMode.SURVIVAL) {
                oldPos.put(player.getUuid(), new LastRecordedPosition(player.getBlockPos(),
                        player.getWorld().getRegistryKey(),
                        player.getYaw(), player.getPitch()));
                player.changeGameMode(GameMode.SPECTATOR);

                audience.sendActionBar(Extension.MM.deserialize("<yellow>[/s] <red>SURVIVAL <white>-> <green>SPECTATOR"));
                audience.playSound(sound);
            } else if (gamemode == GameMode.SPECTATOR) {
                var pos = oldPos.remove(player.getUuid());

                assert pos != null;

                var world = source.getServer().getWorld(pos.world());

                player.teleport(world, pos.pos().getX(), pos.pos().getY(), pos.pos().getZ(), pos.yaw(), pos.pitch());

                player.changeGameMode(GameMode.SURVIVAL);
                audience.sendActionBar(Extension.MM.deserialize("<yellow>[/s] <red>SPECTATOR <white>-> <green>SURVIVAL"));
                audience.playSound(sound);

            } else {
                audience.sendMessage(Component.text("[/s] Current gamemode not supported by gamemode [/s]witcher."));
            }

            return 1;
        }));
    }
}
