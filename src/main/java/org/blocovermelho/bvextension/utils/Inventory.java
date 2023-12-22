package org.blocovermelho.bvextension.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import static net.minecraft.block.Block.dropStack;
import static net.minecraft.block.Block.getDroppedStacks;

public class Inventory {
    public static void putItem(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, PlayerEntity entity, ItemStack stack) {

        if (world instanceof ServerWorld sworld) {
            getDroppedStacks(state, sworld, pos ,blockEntity, entity, stack).forEach( is -> {
                var item = is.getItem();
                var count = is.getCount();

                if (entity.getInventory().insertStack(is)) {
                    sworld.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.1f, (sworld.random.nextFloat() - sworld.random.nextFloat()) * 1);
                    entity.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), count);
                } else {
                    dropStack(sworld, pos, is);
                }
            });
        }
    }
}
