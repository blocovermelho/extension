package org.blocovermelho.bvextension.mixin.carefulBreak;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
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

@Mixin(TallPlantBlock.class)
public class TallBlockMixin {
    @Redirect(method = "onBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/TallPlantBlock;dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V"))
    private void onDropStacks(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool) {
        var player = (PlayerEntity) entity;

        if (Settings.carefulBreak && player.isInSneakingPose()) {
            var doubleHalf = state.get(TallPlantBlock.HALF);

            if (doubleHalf == DoubleBlockHalf.UPPER) {
                BlockPos downPos = pos.down();
                var plant = world.getBlockState(downPos);
                if (plant.getBlock() == state.getBlock() && plant.get(TallPlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                    putItem(plant, world, downPos, blockEntity, player, tool);
                    world.setBlockState(downPos, Blocks.AIR.getDefaultState(), 35);
                    world.syncWorldEvent(player, 2001, downPos, Block.getRawIdFromState(state));
                }
            } else {
                putItem(state, world , pos, blockEntity, player, tool);
            }

        } else {
            Block.dropStacks(state, world , pos, blockEntity, entity, tool);
        }
    }
}
