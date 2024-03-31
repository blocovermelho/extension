package org.blocovermelho.bvextension.utils;

import carpet.CarpetSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import java.util.ArrayList;
import java.util.List;

import static carpet.helpers.InventoryHelper.TAG_COMPOUND;
import static carpet.helpers.InventoryHelper.TAG_LIST;
import static net.minecraft.block.Block.dropStack;
import static net.minecraft.block.Block.getDroppedStacks;

public class Inventory {
    public static void putItem(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, PlayerEntity entity, ItemStack stack) {

        if (world instanceof ServerWorld sworld) {
            getDroppedStacks(state, sworld, pos ,blockEntity, entity, stack).forEach( is -> {
                var item = is.getItem();
                var count = is.getCount();

                if (blockEntity instanceof ShulkerBoxBlockEntity sbbe) {
                    if (sbbe.isEmpty()) { is.removeSubNbt("BlockEntityTag"); }
                    int candidate = SBox.getCandidate(sbbe, entity.getInventory());
                    if (candidate != -1) {
                        ItemStack slot = entity.getInventory().getStack(candidate);
                        slot.increment(1);
                        entity.getInventory().setStack(candidate,slot);
                        entity.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), count);
                        return;
                    }
                }

                if (entity.getInventory().insertStack(is)) {
                    sworld.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.1f, (sworld.random.nextFloat() - sworld.random.nextFloat()) * 1);
                    entity.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), count);
                } else {
                    dropStack(sworld, pos, is);
                }
            });
        }
    }

    public class SBox {
        public static boolean HasItem(ItemStack sboxItem) {
            NbtCompound tag = sboxItem.getNbt();

            if (tag == null || !tag.contains("BlockEntityTag", TAG_COMPOUND))
                return false;

            NbtCompound bet = tag.getCompound("BlockEntityTag");
            return bet.contains("Items", TAG_LIST) && !bet.getList("Items", TAG_COMPOUND).isEmpty();
        }
        public static int getCandidate(ShulkerBoxBlockEntity box, net.minecraft.inventory.Inventory inventory) {
            if (CarpetSettings.stackableShulkerBoxes.equals("false")
                    || CarpetSettings.shulkerBoxStackSize == 1
                    || !box.isEmpty()
            ) {
                return -1;
            }

            Item boxKind = ShulkerBoxBlock.getItemStack(box.getColor()).getItem();
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (stack.isEmpty()) continue;
                if (!stack.isOf(boxKind)) continue;
                if (SBox.HasItem(stack)) continue;

                Text stackName = stack.getName();
                Text boxName = box.getName();

                if (!box.hasCustomName() && stackName.getContent() instanceof LiteralTextContent) continue;
                if (box.hasCustomName() && !stackName.equals(boxName)) continue;
                if (stack.getCount() + 1 > CarpetSettings.shulkerBoxStackSize) continue;

                return i;
            }
            return -1;
        }
    }

}
