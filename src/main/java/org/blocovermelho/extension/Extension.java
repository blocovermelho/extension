package org.blocovermelho.extension;


import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;
import org.apache.commons.io.IOUtils;
import org.blocovermelho.extension.commands.SwapGamemode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class Extension implements CarpetExtension, ModInitializer {

    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(new Extension());
    }

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(Settings.class);
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        ServerScoreboard scoreboard = server.getScoreboard();
        PlayerTeam carpetBotTeam = scoreboard.addPlayerTeam("bv_dyn_bot");
        MutableComponent name = Component.literal("[BOT]").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        carpetBotTeam.setDisplayName(name);
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext commandBuildContext) {
        SwapGamemode.register(dispatcher);
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
        Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).create(); // lenient allows for comments
        return gson.fromJson(jsonData, new TypeToken<Map<String, String>>() {}.getType());
    }
}
