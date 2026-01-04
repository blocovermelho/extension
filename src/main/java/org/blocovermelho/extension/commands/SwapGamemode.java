package org.blocovermelho.extension.commands;

import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.blocovermelho.extension.types.LastRecordedPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static 	net.minecraft.commands.Commands.*;

public class SwapGamemode {
    public static HashMap<UUID, LastRecordedPos> oldPos = new HashMap<>();
    static HashMap<GameType, GameType> mapping = new HashMap<>();

    static  {
        mapping.put(GameType.SURVIVAL, GameType.SPECTATOR);
        mapping.put(GameType.SPECTATOR, GameType.SURVIVAL);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("s").requires(CommandSourceStack::isPlayer).executes(c -> {
            ServerPlayer player = c.getSource().getPlayer();
            assert player != null;
            GameType from = player.gameMode();

            if (!mapping.containsKey(from)) {
                player.sendSystemMessage(Component.literal("[/s] Current gamemode not supported by gamemode [/s]witcher."), true);
                return 1;
            }

            if (from == GameType.SURVIVAL) {
                oldPos.put(player.getUUID(), new LastRecordedPos(player.position(), player.level().dimension(), player.getYRot(), player.getXRot()));
            } else {
                LastRecordedPos oldPos = SwapGamemode.oldPos.remove(player.getUUID());
                if (oldPos != null) {
                    ServerLevel level = c.getSource().getServer().getLevel(oldPos.level());
                    assert level != null;
                    player.teleport(new TeleportTransition(level, oldPos.pos(), Vec3.ZERO, oldPos.yaw(), oldPos.pitch(), TeleportTransition.DO_NOTHING));
                }
            }

            GameType into = mapping.get(from);

            player.setGameMode(into);
            Component message = TagParser.QUICK_TEXT.parseNode(String.format("<yellow>[/s] <red>%s <white>-> <green>%s", from, into)).toText();

            player.sendSystemMessage(message, true);

            return 1;
        }));
    }
}
