package org.blocovermelho.bvextension;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.commons.io.IOUtils;
import org.blocovermelho.bvextension.commands.GamemodeSwitchCommand;
import org.blocovermelho.bvextension.events.RestorePositionLogoff;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;


public class Extension implements CarpetExtension, ModInitializer {
    public static MiniMessage MM = MiniMessage.miniMessage();
    public static FabricServerAudiences audiences;

    @Override
    public String version() {
        return "bv-extension";
    }

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(Settings.class);
    }

    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(new Extension());

        ServerLifecycleEvents.SERVER_STARTED.register(x -> {
            audiences = FabricServerAudiences.of(x);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(x -> { audiences = null; });

        ServerPlayConnectionEvents.DISCONNECT.register(new RestorePositionLogoff());
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        GamemodeSwitchCommand.register(dispatcher);
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        InputStream langFile = Extension.class.getClassLoader().getResourceAsStream("assets/bv-extension/lang/%s.json".formatted(lang));
        if (langFile == null) {
            // we don't have that language
            return Collections.emptyMap();
        }
        String jsonData;
        try {
            jsonData = IOUtils.toString(langFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
        Gson gson = new GsonBuilder().setLenient().create(); // lenient allows for comments
        return gson.fromJson(jsonData, new TypeToken<Map<String, String>>() {}.getType());
    }
}
