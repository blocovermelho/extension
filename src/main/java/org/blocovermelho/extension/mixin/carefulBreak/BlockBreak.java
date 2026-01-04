package org.blocovermelho.extension.mixin.carefulBreak;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.blocovermelho.extension.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static org.blocovermelho.extension.ext.Inventory.putItem;

@Mixin(Block.class)
public class BlockBreak {
    @WrapOperation(
            method = "playerDestroy",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V")

    )
    void bv$carefulBreak_general (BlockState blockState, Level level, BlockPos blockPos, BlockEntity blockEntity, Entity entity, ItemStack itemStack, Operation<Void> original) {
        if (Settings.carefulBreak && entity.isShiftKeyDown()) {
            putItem(blockState, level, blockPos, blockEntity, (Player) entity, itemStack);
        } else {
            original.call(blockState, level, blockPos, blockEntity, entity, itemStack);
        }

    }
}
