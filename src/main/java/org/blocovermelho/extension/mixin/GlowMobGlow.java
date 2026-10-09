package org.blocovermelho.extension.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import org.blocovermelho.extension.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class GlowMobGlow extends Entity {
    @Shadow
    private boolean persistenceRequired;

    public GlowMobGlow(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    void bvext$persistentMobLoad(ValueInput input, CallbackInfo ci) {
        if (this.getType().getCategory() != MobCategory.MONSTER) {
            return;
        }

        if (Settings.glowPersistentMobs && this.persistenceRequired && !this.hasCustomName()) {
            this.setGlowingTag(true);
        } else {
            this.setGlowingTag(false);
        }
    }

    @Inject(method = "pickUpItem", at = @At("TAIL"))
    void bvext$persistentMobUpdate(CallbackInfo ci) {
        if (this.getType().getCategory() != MobCategory.MONSTER) {
            return;
        }

        if (Settings.glowPersistentMobs && this.persistenceRequired && !this.hasCustomName()) {
            this.setGlowingTag(true);
        } else {
            this.setGlowingTag(false);
        }
    }
}
