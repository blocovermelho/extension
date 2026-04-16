package org.blocovermelho.extension.mixin.carefulBreak;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.blocovermelho.extension.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static org.blocovermelho.extension.ext.Inventory.putItem;

@Mixin({PistonHeadBlock.class})
public class PistonHeadLikeBreak {
    @WrapOperation(method = "playerWillDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/DirectionalBlock;playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState bvext$carefulBreak_pistonlike(PistonHeadBlock instance, Level level, BlockPos blockPos, BlockState blockState, Player player, Operation<BlockState> original) {
        if (Settings.carefulBreak && player.isShiftKeyDown()) {
            var basePos = blockPos.relative(blockState.getValue(DirectionalBlock.FACING).getOpposite());
            var baseState = level.getBlockState(basePos);
            putItem(baseState, level, basePos, null, player, baseState.getBlock().asItem().getDefaultInstance());
            level.setBlock(basePos, Blocks.AIR.defaultBlockState(), 35);
            level.levelEvent(player, 2001, basePos, Block.getId(baseState));

            return baseState;
        } else {
            return original.call(instance, level, blockPos, blockState, player);
        }
    }
}
