package org.blocovermelho.bvextension.mixin;

import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.world.SleepManager;
import org.blocovermelho.bvextension.Settings;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SleepManager.class)
public class BotsDontSleep {
    @ModifyExpressionValue(
            method = "getNightSkippingRequirement",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/server/world/SleepManager;total:I"
            ))
    public int bvext$botsDontSleep(int original) {
        if (!Settings.botsDontSleep) {
            return original;
        }

        var players = CarpetServer.minecraft_server.getPlayerManager().getPlayerList();

        var bots = (int) players.stream().filter(p -> {
            return p instanceof EntityPlayerMPFake;
        }).count();

        return original - bots;
    }
}
