package org.blocovermelho.extension.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.blocovermelho.extension.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Enderman.class)
public abstract class GlowEndermanGlow extends Entity {
    public GlowEndermanGlow(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "setCarriedBlock", at = @At("HEAD"))
    private void bvext$glowIfHoldingBlocks(BlockState carryingBlock, CallbackInfo ci) {
        if (Settings.glowPersistentMobs) {
            this.setGlowingTag(carryingBlock != null);
        } else {
            this.setGlowingTag(false);
        }
    }
}
