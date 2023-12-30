package org.blocovermelho.bvextension.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.blocovermelho.bvextension.Extension;
import org.blocovermelho.bvextension.utils.Waypoint;

import java.util.Objects;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SharePosCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("pos")
                .requires(ServerCommandSource::isExecutedByPlayer)
                .then(argument("nome", string())
                        .executes(s -> {
                            var player = Objects.requireNonNull(s.getSource().getPlayer());
                            var name = getString(s, "nome");

                            var text = Waypoint.intoChatMessage(player, name);
                            Extension.audiences.players().sendMessage(text);
                            return 1;
                        })

                ).executes(s -> {
                    var player = Objects.requireNonNull(s.getSource().getPlayer());
                    var text = Waypoint.intoChatMessage(player, null);
                    Extension.audiences.players().sendMessage(text);
                    return 1;
                }));
    }
}
