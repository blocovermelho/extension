package org.blocovermelho.bvextension.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;


public class GamemodeSwitchCommand {

    public static HashMap<UUID, Pair<RegistryKey<World>, Pair<BlockPos, Pair<Float, Float>>>> oldPos = new HashMap<>();
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
                oldPos.put(player.getUuid(), new Pair<>(
                        player.getWorld().getRegistryKey(),
                        new Pair<>(player.getBlockPos(),
                                new Pair<>(player.getYaw(), player.getPitch())
                        )));
                player.changeGameMode(GameMode.SPECTATOR);
                player.sendMessage(Text.literal("[/s] SURVIVAL -> SPECTATOR"), true);
                player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 0.4f, 1);
            } else if (gamemode == GameMode.SPECTATOR) {
                var pos = oldPos.remove(player.getUuid());

                assert pos != null;

                var world = source.getServer().getWorld(pos.getA());

                player.teleport(world, pos.getB().getA().getX(), pos.getB().getA().getY(), pos.getB().getA().getZ(), pos.getB().getB().getA(), pos.getB().getB().getB());

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
