package org.blocovermelho.extension.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.blocovermelho.extension.Settings;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public abstract class LargaDissoEnderman extends Entity {
    public LargaDissoEnderman(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract @Nullable BlockState getCarriedBlock();

    @Shadow
    public abstract void setCarriedBlock(@Nullable BlockState blockState);

    @Inject(method = "hurtServer", at = @At("HEAD"))
    void bvext$largaDissoEnderman (ServerLevel serverLevel, DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        if (!Settings.largaDissoEnderman) {
            return;
        }

        BlockState block = this.getCarriedBlock();
        if (damageSource.getEntity() instanceof Player && block != null) {
            Item item = block.getBlock().asItem();
            serverLevel.addFreshEntity(new ItemEntity(serverLevel,position().x, position().y, position().z, new ItemStack(item)));
            setCarriedBlock(null);
        }
    }
}
