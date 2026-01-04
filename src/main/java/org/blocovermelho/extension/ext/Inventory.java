package org.blocovermelho.extension.ext;

import carpet.CarpetSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;


import static net.minecraft.world.level.block.Block.dropResources;
import static net.minecraft.world.level.block.Block.getDrops;

public class Inventory {
    public static void putItem(BlockState state, Level world, BlockPos pos, BlockEntity blockEntity, Player entity, ItemStack stack) {
        if (world instanceof ServerLevel sworld) {
            getDrops(state, sworld, pos ,blockEntity, entity, stack).forEach( is -> {
                var item = is.getItem();
                var count = is.getCount();

                if (blockEntity instanceof ShulkerBoxBlockEntity sbbe) {
                    if (sbbe.isEmpty()) { is.remove(DataComponents.BLOCK_ENTITY_DATA); }
                    int candidate = SBox.getCandidate(sbbe, entity.getInventory());
                    if (candidate != -1) {
                        ItemStack slot = entity.getInventory().getItem(candidate);
                        slot.setCount(slot.getCount() + 1);
                        entity.getInventory().setItem(candidate,slot);
                        entity.awardStat(Stats.ITEM_PICKED_UP.get(item), count);
                        return;
                    }
                }

                if (entity.getInventory().add(is)) {
                    sworld.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.1f, (sworld.random.nextFloat() - sworld.random.nextFloat()) * 1);
                    entity.awardStat(Stats.ITEM_PICKED_UP.get(item), count);
                } else {
                    dropResources(state, sworld, pos, blockEntity, entity, stack);
                }
            });
        }

    }

    public class SBox {
        public static boolean HasItem(ItemStack sboxItem) {
            DataComponentMap components = sboxItem.getComponents();
            ItemContainerContents items = components.get(DataComponents.CONTAINER);
            if (items != null) {
                return items.nonEmptyStream().findAny().isPresent();
            }

            return false;
        }

        public static int getCandidate(ShulkerBoxBlockEntity box, net.minecraft.world.entity.player.Inventory inventory) {
            if (CarpetSettings.stackableShulkerBoxes.equals("false")
                    || CarpetSettings.shulkerBoxStackSize == 1
                    || !box.isEmpty()
            ) {
                return -1;
            }

            Item boxKind = ShulkerBoxBlock.getBlockByColor(box.getColor()).asItem();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.isEmpty()) continue;
                if (!stack.is(boxKind)) continue;
                if (SBox.HasItem(stack)) continue;

                Component stackName = stack.getHoverName();
                Component boxName = box.getName();

                if (!box.hasCustomName() && stackName.getContents() instanceof PlainTextContents) continue;
                if (box.hasCustomName() && !stackName.equals(boxName)) continue;
                if (stack.getCount() + 1 > CarpetSettings.shulkerBoxStackSize) continue;

                return i;
            }
            return -1;
        }
    }
}
