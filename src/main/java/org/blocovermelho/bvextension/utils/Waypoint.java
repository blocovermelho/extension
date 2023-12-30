package org.blocovermelho.bvextension.utils;

import net.kyori.adventure.text.Component;
import net.minecraft.server.network.ServerPlayerEntity;
import org.blocovermelho.bvextension.Extension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Waypoint {

    public static Component intoChatMessage(@NotNull ServerPlayerEntity player, @Nullable String name) {

        var pos = player.getPos();
        var dim = player.getWorld().getRegistryKey().getValue();

        var playerName = player.getGameProfile().getName();

        var x = (int) pos.getX();
        var y = (int) pos.getY();
        var z = (int) pos.getZ();

        var lineOne = "<blue>" + playerName + "</blue>"
                + "<gray> compartilhou </gray>"
                + "<gold>" + (name == null || name.isBlank() ? "sua posição" : name)  + "</gold>";

        var lineTwo = "Coordenadas (<red>X<green>Y<blue>Z<white>): "
                + "<red>" + x + " <green>" + y + " <blue>" + z + "<white> @ <gray>" + dim.getPath().toUpperCase();

        var lineThree = "Waypoints : <#6435F2>"
                + "<click:run_command:'" + intoJourneyCommand(player, name) + "'>[JourneyMap]</click:run_command>"
                + "<hover:show_text:'Preste atenção na <bold>dimenção</bold>. Esse mod de minimapa não permite o server compartilhar a dimensão do waypoint.'>"
                + "<click:run_command:'" + intoXaerosCommand(player, name) + "'>[Xaero's]</click:run_command></hover:show_text>";

        return Extension.MM.deserialize(String.join("\n", List.of(lineOne, lineTwo, lineThree)));
    }

    public static String intoJourneyCommand(@NotNull ServerPlayerEntity player, @Nullable String name) {
        // Journey has support for the "dim:" key
        var dimensionKey = player.getWorld().getRegistryKey().getValue();
        var pos = player.getPos();

        if (name == null || name.isBlank()) {
            name = "Posição de " + player.getGameProfile().getName();
        }

        return "/jm wpedit " + "x:" +
                (int) pos.getX() +
                ", y:" +
                (int) pos.getY() +
                ", z:" +
                (int) pos.getZ() +
                ", dim:" +
                dimensionKey.toString() +
                ", name:\"" +
                name +
                '\"';
    }


    public static String intoXaerosCommand(@NotNull ServerPlayerEntity player, @Nullable String name) {
        // Xaero's handling of dimensions is wierd and is proprietary
        // (since the mod predates the concept of Identifiers. Yes, 'tis an olde modification.)

        // There's also a distinction between "Internal" and "Global" waypoints
        // which seems to be cross-world, and I don't want to add this complexity to it.

        // If in the future it allows me to just add a dimension identifier like jm
        // support for filling the dimension could be easily be added.

        // var dimensionKey = player.getWorld().getRegistryKey().getValue();

        var pos = player.getPos();
        var playerName = player.getGameProfile().getName();

        if (name == null || name.isBlank()) {

            name = "Posição de " + playerName;
        }

        // Xaeros require some 1-2 digit code for each waypoint.
        // We can generate some based on the first letters of the player's name

        var code = playerName.substring(0, 2);

        // Let's give it a random color just because why not.
        var color = player.getWorld().getRandom().nextBetween(0, 15);

        return "/xaero_waypoint_add:" +
                name +
                ":" +
                code +
                ":" +
                (int) pos.getX() +
                ":" +
                (int) pos.getY() +
                ":" +
                (int) pos.getZ() +
                ":" +
                color +
                ":false:0";
    }

}
