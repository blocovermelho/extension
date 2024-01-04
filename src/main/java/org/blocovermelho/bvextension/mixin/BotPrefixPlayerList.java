package org.blocovermelho.bvextension.mixin;

import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import com.mojang.authlib.GameProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.blocovermelho.bvextension.Extension;
import org.blocovermelho.bvextension.Settings;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerListS2CPacket.Entry.class)
public abstract class BotPrefixPlayerList {
    @Shadow @Final private GameProfile profile;

    @Mutable
    @Shadow
    @Final
    @Nullable
    private Text displayName;

    @Inject(
            method = "<init>(Ljava/util/UUID;Lcom/mojang/authlib/GameProfile;ZILnet/minecraft/world/GameMode;Lnet/minecraft/text/Text;Lnet/minecraft/network/encryption/PublicPlayerSession$Serialized;)V",
            at = @At(value = "RETURN")
    )
    public void bvext$botPrefix(UUID uuid, GameProfile profile, boolean listed, int ping, GameMode gameMode, Text text, PublicPlayerSession.Serialized chatSession, CallbackInfo ci) {
        if (!Settings.botPlayerListPrefix) { return; }
        var player = CarpetServer.minecraft_server.getPlayerManager().getPlayer(this.profile.getId());
        if (player instanceof EntityPlayerMPFake) {
            var botName = Component.text("[ʙᴏᴛ]").color(TextColor.color(0x9a26ff))
                    .appendSpace()
                    .append(
                            Component.text(player.getDisplayName().getString())
                                    .color(TextColor.color(0xf0d3e0))
                    );

            this.displayName = Extension.audiences.toNative(botName);
        }
    }

}
