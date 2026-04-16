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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;


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
                    sworld.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.1f, (sworld.getRandom().nextFloat() - sworld.getRandom().nextFloat()) * 1);
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
                return items.nonEmptyItemCopyStream().findAny().isPresent();
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

            Item boxKind = SBox.getBlockByColor(box.getColor()).asItem();
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

        public static Block getBlockByColor(final @Nullable DyeColor color) {
            if (color == null) {
                return Blocks.SHULKER_BOX;
            } else {
                return switch (color) {
                    case WHITE -> Blocks.WHITE_SHULKER_BOX;
                    case ORANGE -> Blocks.ORANGE_SHULKER_BOX;
                    case MAGENTA -> Blocks.MAGENTA_SHULKER_BOX;
                    case LIGHT_BLUE -> Blocks.LIGHT_BLUE_SHULKER_BOX;
                    case YELLOW -> Blocks.YELLOW_SHULKER_BOX;
                    case LIME -> Blocks.LIME_SHULKER_BOX;
                    case PINK -> Blocks.PINK_SHULKER_BOX;
                    case GRAY -> Blocks.GRAY_SHULKER_BOX;
                    case LIGHT_GRAY -> Blocks.LIGHT_GRAY_SHULKER_BOX;
                    case CYAN -> Blocks.CYAN_SHULKER_BOX;
                    case BLUE -> Blocks.BLUE_SHULKER_BOX;
                    case BROWN -> Blocks.BROWN_SHULKER_BOX;
                    case GREEN -> Blocks.GREEN_SHULKER_BOX;
                    case RED -> Blocks.RED_SHULKER_BOX;
                    case BLACK -> Blocks.BLACK_SHULKER_BOX;
                    case PURPLE -> Blocks.PURPLE_SHULKER_BOX;
                };
            }
        }
    }
}
