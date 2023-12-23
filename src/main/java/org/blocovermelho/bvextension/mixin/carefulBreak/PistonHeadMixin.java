package org.blocovermelho.bvextension.mixin.carefulBreak;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.blocovermelho.bvextension.Settings;
import org.blocovermelho.bvextension.utils.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static org.blocovermelho.bvextension.utils.Inventory.putItem;

@Mixin(value = PistonHeadBlock.class, priority = 999)
public class PistonHeadMixin {
    @Redirect(method = "onBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FacingBlock;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void onBreak(FacingBlock instance, World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        if (Settings.carefulBreak && playerEntity.isInSneakingPose()) {
            var pistonBasePos = blockPos.offset(blockState.get(FacingBlock.FACING).getOpposite());
            var pistonBlock = world.getBlockState(pistonBasePos);

            var stack = pistonBlock.getBlock().asItem().getDefaultStack();
            putItem(pistonBlock, world, pistonBasePos, null, playerEntity, stack);

            world.setBlockState(pistonBasePos,  Blocks.AIR.getDefaultState(), 35);
            world.syncWorldEvent(playerEntity, 2001, pistonBasePos, Block.getRawIdFromState(blockState));
        } else {
            Block.dropStacks(blockState, world, blockPos);
        }
    }
}
