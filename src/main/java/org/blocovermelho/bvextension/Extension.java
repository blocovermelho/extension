package org.blocovermelho.bvextension;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;


public class Extension implements CarpetExtension, ModInitializer {
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
