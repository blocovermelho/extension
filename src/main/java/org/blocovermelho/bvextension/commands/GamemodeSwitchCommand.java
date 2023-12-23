package org.blocovermelho.bvextension.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import static net.minecraft.server.command.CommandManager.literal;


public class GamemodeSwitchCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("s").executes(c -> {
            var source = (ServerCommandSource) c.getSource();
            if (!source.isExecutedByPlayer()) {
                return 1;
            }
            var player = source.getPlayer();
            assert player != null;

            var gamemode = player.interactionManager.getGameMode();
            if (gamemode == GameMode.SURVIVAL) {
                player.changeGameMode(GameMode.SPECTATOR);
                player.sendMessage(Text.literal("[/s] SURVIVAL -> SPECTATOR"), true);
                player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 0.4f, 1);
            } else if (gamemode == GameMode.SPECTATOR) {
                player.changeGameMode(GameMode.SURVIVAL);
                player.sendMessage(Text.literal("[/s] SPECTATOR -> SURVIVAL"), true);
                player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 0.4f, 1);
            } else {
                player.sendMessage(Text.literal("[/s] Current gamemode not supported by gamemode [/s]witcher."));
            }

            return 1;
        }));
    }
}
