package net.doombubbles.obsidiantoolz.Enchantments;

import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

public class MagneticEnchantment extends ObsidianToolzEnchantment {

    public MagneticEnchantment(Rarity rarity, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(rarity, type, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public String getRealName(int level) {
        return "Magnetic" + (level > 1 ? " " + toRoman(level) : "");
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack) && (stack.getItem() instanceof MiningToolItem || stack.getItem() instanceof ShearsItem);
    }

    public static void putInDefaultedList(DefaultedList<ItemStack> inventory, ItemStack droppedStack) {
        if (droppedStack.getItem() instanceof BlockItem && ((BlockItem) droppedStack.getItem()).getBlock() instanceof ShulkerBoxBlock) {
            return;
        }
        if (!isFull(inventory)) {
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack iStack = inventory.get(i);
                if (iStack.isEmpty()) {
                    inventory.set(i, droppedStack.copy());
                    droppedStack.setCount(0);
                    break;
                } else if (canStackAddMore(iStack, droppedStack)) {
                    int amount = Math.min(droppedStack.getCount(), iStack.getMaxCount() - iStack.getCount());
                    if (amount > 0) {
                        droppedStack.decrement(amount);
                        iStack.increment(amount);
                        break;
                    }
                }
            }
        }
    }

    public static void magnetStack(PlayerEntity playerEntity, int magnet, ItemStack droppedStack, World world, BlockPos pos) {
        ItemStack off = playerEntity.getOffHandStack();
        if (magnet > 1 && off.getItem() instanceof BlockItem && ((BlockItem) off.getItem()).getBlock() instanceof ShulkerBoxBlock) {
            DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
            NbtCompound tag = off.getOrCreateTag().getCompound("BlockEntityTag");
            Inventories.readNbt(tag, inventory);

            putInDefaultedList(inventory, droppedStack);

            NbtCompound newTag = new NbtCompound();
            Inventories.writeNbt(newTag, inventory);
            off.putSubTag("BlockEntityTag", newTag);

            playerEntity.playerScreenHandler.sendContentUpdates();
        }

        if (droppedStack.getCount() > 0) {
            if (playerEntity.giveItemStack(droppedStack)) {
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (world.random.nextFloat() - world.random.nextFloat()) * 1.4F + 2.0F);
            } else {
                Block.dropStack(world, pos, droppedStack);
            }
        }
    }

    public static void doMagnet(BlockState state, ServerWorld world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfo callbackInfo, int magnet) {
        ServerPlayerEntity playerEntity = (ServerPlayerEntity) entity;
        for (ItemStack droppedStack : Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, entity, tool)) {
            magnetStack(playerEntity, magnet, droppedStack, world, pos);
        }
        state.onStacksDropped(world, new BlockPos(playerEntity.getPos()), tool);
        callbackInfo.cancel();
    }

    private static boolean isFull(DefaultedList<ItemStack> inventory) {
        Iterator var1 = inventory.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = (ItemStack)var1.next();
        } while(!itemStack.isEmpty() && itemStack.getCount() == itemStack.getMaxCount());

        return false;
    }

    private static boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areTagsEqual(stack1, stack2);
    }

    private static boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() && areItemsEqual(existingStack, stack) && existingStack.isStackable() && existingStack.getCount() < existingStack.getMaxCount() && existingStack.getCount() < 64;
    }
}
