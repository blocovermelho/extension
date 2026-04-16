package org.blocovermelho.extension.mixin.carefulBreak;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.blocovermelho.extension.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static org.blocovermelho.extension.ext.Inventory.putItem;

@Mixin({BedBlock.class})
public abstract class BedLikeBreak {
    @Shadow
    private static Direction getNeighbourDirection(BedPart part, Direction facing) {
        return null;
    }

    @WrapOperation(method = "playerWillDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/HorizontalDirectionalBlock;playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState bvext$carefulBreak_bedlike(BedBlock instance, Level level, BlockPos blockPos, BlockState blockState, Player player, Operation<BlockState> original) {
        if (Settings.carefulBreak && player.isShiftKeyDown()) {
            BedPart part = blockState.getValue(BedBlock.PART);
            var headPos = part == BedPart.HEAD ? blockPos : bvext$getOtherPos(blockPos, blockState, part);
            var headState = part == BedPart.HEAD ? blockState : level.getBlockState(headPos);
            putItem(headState, level, headPos, null, player, headState.getBlock().asItem().getDefaultInstance());
            level.setBlock(headPos, Blocks.AIR.defaultBlockState(), 35);
            level.levelEvent(player, 2001, headPos, Block.getId(headState));

            return headState;
        } else {
           return original.call(instance, level, blockPos, blockState, player);
        }
    }

    @Unique
    private BlockPos bvext$getOtherPos(BlockPos pos, BlockState blockState, BedPart part) {
        Direction direction = getNeighbourDirection(part, blockState.getValue(BedBlock.FACING));
        return pos.relative(direction);
    }
}
