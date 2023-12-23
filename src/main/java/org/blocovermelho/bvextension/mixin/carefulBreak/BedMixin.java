package org.blocovermelho.bvextension.mixin.carefulBreak;

import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.blocovermelho.bvextension.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static org.blocovermelho.bvextension.utils.Inventory.putItem;

@Mixin(value = BedBlock.class, priority = 999)
public abstract class BedMixin {
    @Shadow
    private static Direction getDirectionTowardsOtherPart(BedPart part, Direction direction) {
        return null;
    }

    @Redirect(method = "onBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/HorizontalFacingBlock;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    public void onBreak(HorizontalFacingBlock instance, World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        if (Settings.carefulBreak && playerEntity.isInSneakingPose()) {
            var bedPart = blockState.get(BedBlock.PART);
            var otherPartDirection = getDirectionTowardsOtherPart(bedPart, blockState.get(HorizontalFacingBlock.FACING));
            var otherPart = blockPos.offset(otherPartDirection);
            var headPart = bedPart == BedPart.FOOT ? world.getBlockState(otherPart) : blockState;
            var stack = headPart.getBlock().asItem().getDefaultStack();

            putItem(headPart, world, (bedPart == BedPart.FOOT ? otherPart : blockPos), null, playerEntity, stack);
            world.setBlockState(bedPart == BedPart.FOOT ? otherPart : blockPos,  Blocks.AIR.getDefaultState(), 35);
            world.syncWorldEvent(playerEntity, 2001, bedPart == BedPart.FOOT ? otherPart : blockPos, Block.getRawIdFromState(blockState));
        } else {
            Block.dropStacks(blockState, world, blockPos);
        }
    }
}
