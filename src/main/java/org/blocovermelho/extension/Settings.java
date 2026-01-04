package org.blocovermelho.extension;

import carpet.api.settings.Rule;

import static carpet.api.settings.RuleCategory.SURVIVAL;

public class Settings {
    @Rule(
            categories = {SURVIVAL, "bv-extension"}
    )
    public static boolean carefulBreak = false;

    @Rule(
            categories = {SURVIVAL, "bv-extension"}
    )
    public static boolean botsDontSleep = false;

    @Rule(
            categories = {SURVIVAL, "PROTOCOL", "bv-extension" }
    )
    public static boolean botPlayerListPrefix = false;

    @Rule(
            categories = {SURVIVAL, "bv-extension" }
    )
    public static boolean disableEndermanGriefing = false;

    @Rule(
            categories = {SURVIVAL, "bv-extension" }
    )
    public static boolean largaDissoEnderman = false;

}
