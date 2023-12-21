package org.blocovermelho.bvextension.mixin.carefulBreak;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.blocovermelho.bvextension.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static org.blocovermelho.bvextension.utils.Inventory.putItem;

@Mixin(value = Block.class, priority = 999)
public class BlockMixin {
    @Redirect(method = "afterBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V"))
    private void onDropStacks(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool) {
        var player = (PlayerEntity) entity;

        if (Settings.carefulBreak && player.isInSneakingPose()) {
            putItem(state, world , pos, blockEntity, player, tool);
        } else {
            Block.dropStacks(state, world , pos, blockEntity, entity, tool);
        }
    }
}
