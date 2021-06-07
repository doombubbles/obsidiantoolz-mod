package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.Enchantments.*;
import net.doombubbles.obsidiantoolz.Items.BugNet;
import net.doombubbles.obsidiantoolz.Items.ObsidianStuff;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow
    protected boolean canTakeOutput(PlayerEntity player, boolean present) {
        return (player.getAbilities().creativeMode || player.experienceLevel >= this.levelCost.get()) && this.levelCost.get() > 0;
    }

    @Override
    public void onTakeOutput(PlayerEntity player, ItemStack stack) {
        if (!player.getAbilities().creativeMode) {
            player.addExperienceLevels(-this.levelCost.get());
        }

        this.input.setStack(0, ItemStack.EMPTY);
        if (this.repairItemUsage > 0) {
            ItemStack itemStack = this.input.getStack(1);
            if (!itemStack.isEmpty() && itemStack.getCount() > this.repairItemUsage) {
                itemStack.decrement(this.repairItemUsage);
                this.input.setStack(1, itemStack);
            } else {
                this.input.setStack(1, ItemStack.EMPTY);
            }
        } else {
            this.input.setStack(1, ItemStack.EMPTY);
        }

        this.levelCost.set(0);
        this.context.run((world, blockPos) -> {
            BlockState blockState = world.getBlockState(blockPos);
            if (!player.getAbilities().creativeMode && blockState.isIn(BlockTags.ANVIL)
                    && ((player.getRandom().nextFloat() < 0.12F && !isNetherite()) || (player.getRandom().nextFloat() < 0.01477104874444F && isNetherite()))) {
                BlockState blockState2 = AnvilBlock.getLandingState(blockState);
                if (blockState2 == null) {
                    world.removeBlock(blockPos, false);
                    world.syncWorldEvent(1029, blockPos, 0);
                } else {
                    world.setBlockState(blockPos, blockState2, 2);
                    world.syncWorldEvent(1030, blockPos, 0);
                }
            } else {
                world.syncWorldEvent(1030, blockPos, 0);
            }

        });
    }

    @Shadow
    @Override
    protected boolean canUse(BlockState state) {
        return state.isIn(BlockTags.ANVIL);
    }

    public AnvilScreenHandlerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    /*
    @Inject(method = "updateResult", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", ordinal = 1))
    public void changebl4(CallbackInfo ci, ItemStack itemStack, int i, int j, int k, ItemStack itemStack2, ItemStack itemStack3, Map map, boolean bl, Map map2, boolean bl2, boolean bl3, Iterator var12, Enchantment enchantment, int u, boolean bl4) {
        bl4 = true;
        System.out.println("doin it");
    }
    @Inject(method = "updateResult", locals = LocalCapture.PRINT, at = @At(value = "FIELD", target = "Lnet/minecraft/screen/AnvilScreenHandler;repairItemUsage:Ljava/lang/Integer;", ordinal = 0))
    public void ignoreRepairCost(CallbackInfo ci) {
        System.out.println("hi");
    }
     */


    @Shadow
    private int repairItemUsage;
    @Shadow
    private String newItemName;
    @Shadow
    private Property levelCost;

    @Override
    public void updateResult() {
        ItemStack itemStack = this.input.getStack(0);
        this.levelCost.set(1);
        int i = 0;
        int j = 0;
        int k = 0;
        if (itemStack.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
        } else {
            ItemStack itemStack2 = itemStack.copy();
            ItemStack itemStack3 = this.input.getStack(1);
            Map<Enchantment, Integer> map = EnchantmentHelper.get(itemStack2);
            j = itemStack.getRepairCost() + (itemStack3.isEmpty() ? 0 : itemStack3.getRepairCost());
            this.repairItemUsage = 0;
            if (!itemStack3.isEmpty()) {
                boolean bl = itemStack3.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantmentNbt(itemStack3).isEmpty();
                int o;
                int p;
                int q;
                if (itemStack2.isDamageable() && itemStack2.getItem().canRepair(itemStack, itemStack3)) {
                    o = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
                    if (o <= 0) {
                        this.output.setStack(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }

                    for(p = 0; o > 0 && p < itemStack3.getCount(); ++p) {
                        q = itemStack2.getDamage() - o;
                        itemStack2.setDamage(q);
                        ++i;
                        o = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
                    }

                    this.repairItemUsage = p;
                } else {
                    if (!bl && (itemStack2.getItem() != itemStack3.getItem() || !itemStack2.isDamageable())) {
                        this.output.setStack(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }

                    if (ObsidianStuff.is(itemStack2) != ObsidianStuff.is(itemStack3) && itemStack3.getItem() != Items.ENCHANTED_BOOK) {
                        return;
                    }


                    if (itemStack2.isDamageable() && !bl) {
                        o = itemStack.getMaxDamage() - itemStack.getDamage();
                        p = itemStack3.getMaxDamage() - itemStack3.getDamage();
                        q = p + itemStack2.getMaxDamage() * 12 / 100;
                        int r = o + q;
                        int s = itemStack2.getMaxDamage() - r;
                        if (s < 0) {
                            s = 0;
                        }

                        if (s < itemStack2.getDamage()) {
                            itemStack2.setDamage(s);
                            i += 2;
                        }
                    }

                    Map<Enchantment, Integer> map2 = EnchantmentHelper.get(itemStack3);
                    boolean bl2 = false;
                    boolean bl3 = false;
                    Iterator var24 = map2.keySet().iterator();

                    label160:
                    while(true) {
                        Enchantment enchantment;
                        do {
                            if (!var24.hasNext()) {
                                if (bl3 && !bl2) {
                                    this.output.setStack(0, ItemStack.EMPTY);
                                    this.levelCost.set(0);
                                    return;
                                }
                                break label160;
                            }

                            enchantment = (Enchantment)var24.next();
                        } while(enchantment == null);

                        int t = map.containsKey(enchantment) ? (Integer)map.get(enchantment) : 0;
                        int u = (Integer)map2.get(enchantment);
                        u = t == u ? u + 1 : Math.max(u, t);
                        boolean bl4 = enchantment.isAcceptableItem(itemStack);
                        if (enchantment instanceof InfinityEnchantment && itemStack.getItem() instanceof ElytraItem && isNetherite()) bl4 = true;
                        if (enchantment instanceof InfinityEnchantment && itemStack.getItem() instanceof CrossbowItem) bl4 = true;
                        if (enchantment instanceof PowerEnchantment && itemStack.getItem() instanceof CrossbowItem) bl4 = true;
                        if (enchantment instanceof SpaceEnchantment && itemStack.getItem() instanceof CrossbowItem) bl4 = true;
                        if (enchantment instanceof PiercingEnchantment && itemStack.getItem() instanceof BowItem) bl4 = true;
                        // if (enchantment instanceof ProtectionEnchantment && itemStack.getItem() instanceof ElytraItem && isNetherite()) bl4 = true;
                        if (enchantment instanceof MuggingEnchantment && itemStack.getItem() instanceof ShearsItem) bl4 = true;
                        if (enchantment instanceof MuggingEnchantment && BugNet.is(itemStack)) bl4 = true;
                        if (enchantment instanceof InfinityEnchantment && (itemStack.getItem() instanceof BucketItem || itemStack.getItem() instanceof MilkBucketItem) && isNetherite()) {
                            bl4 = true;
                            i = 30;
                        }
                        if (this.player.getAbilities().creativeMode || itemStack.getItem() == Items.ENCHANTED_BOOK) {
                            bl4 = true;
                        }

                        for (Enchantment enchantment2 : map.keySet()) {
                            if (enchantment2 != enchantment && (!enchantment.canCombine(enchantment2) || !enchantment2.canCombine(enchantment))) {
                                ++i;
                                if (isNetherite()) {
                                    if (enchantment instanceof SmeltTouchEnchantment && enchantment2 instanceof SilkTouchEnchantment
                                    || enchantment2 instanceof SmeltTouchEnchantment && enchantment instanceof SilkTouchEnchantment) {
                                        continue;
                                    }

                                    if (enchantment instanceof ExpertiseEnchantment && enchantment2 instanceof LuckEnchantment
                                    || enchantment2 instanceof ExpertiseEnchantment && enchantment instanceof LuckEnchantment) {
                                        continue;
                                    }

                                    if (enchantment instanceof GrindingEnchantment && enchantment2 instanceof LuckEnchantment
                                    || enchantment2 instanceof GrindingEnchantment && enchantment instanceof LuckEnchantment) {
                                        continue;
                                    }

                                    /*
                                    if (enchantment instanceof PiercingEnchantment && enchantment2 instanceof PowerEnchantment
                                    || enchantment2 instanceof PiercingEnchantment && enchantment instanceof PowerEnchantment) {
                                        continue;
                                    }
                                    */

                                    if (enchantment instanceof MendingEnchantment && enchantment2 instanceof InfinityEnchantment
                                    || enchantment2 instanceof MendingEnchantment && enchantment instanceof InfinityEnchantment) {
                                        if (itemStack2.getItem() != Items.ELYTRA) {
                                            continue;
                                        }
                                    }


                                }
                                bl4 = false;
                            }
                        }

                        if (!bl4) {
                            bl3 = true;
                        } else {
                            bl2 = true;
                            int v = 0;
                            switch(enchantment.getRarity()) {
                                case COMMON:
                                    v = 1;
                                    break;
                                case UNCOMMON:
                                    v = 2;
                                    break;
                                case RARE:
                                    v = 4;
                                    break;
                                case VERY_RARE:
                                    v = 8;
                            }
                            if (isNetherite()) {
                                if (u > enchantment.getMaxLevel() + 1) {
                                    u = enchantment.getMaxLevel() + 1;
                                }
                                v *= 2;
                            } else {
                                if (u > enchantment.getMaxLevel()) {
                                    u = enchantment.getMaxLevel();
                                }
                            }

                            map.put(enchantment, u);

                            if (bl) {
                                v = Math.max(1, v / 2);
                            }

                            i += v * u;
                            if (itemStack.getCount() > 1) {
                                i = 40;
                            }
                        }
                    }
                }
            }

            if (StringUtils.isBlank(this.newItemName)) {
                if (itemStack.hasCustomName()) {
                    k = 1;
                    i += k;
                    itemStack2.removeCustomName();
                }
            } else if (!this.newItemName.equals(itemStack.getName().getString())) {
                k = 1;
                i += k;
                itemStack2.setCustomName(new LiteralText(this.newItemName));
            }

            this.levelCost.set(j + i);

            if (i <= 0) {
                itemStack2 = ItemStack.EMPTY;
            }

            if (isNetherite() && this.levelCost.get() > 30) {
                this.levelCost.set(30);
            }

            if (k == i && k > 0 && this.levelCost.get() >= 40) {
                this.levelCost.set(39);
            }

            if (this.levelCost.get() >= 40 && !this.player.getAbilities().creativeMode) {
                itemStack2 = ItemStack.EMPTY;
            }

            if (!itemStack2.isEmpty()) {
                int w = itemStack2.getRepairCost();
                if (!itemStack3.isEmpty() && w < itemStack3.getRepairCost()) {
                    w = itemStack3.getRepairCost();
                }

                if (k != i || k == 0) {
                    w = AnvilScreenHandler.getNextCost(w);
                }

                itemStack2.setRepairCost(w);
                EnchantmentHelper.set(map, itemStack2);
            }

            this.output.setStack(0, itemStack2);
            this.sendContentUpdates();
        }
    }


    private boolean isNetherite() {
        AtomicBoolean ret = new AtomicBoolean(false);
        this.context.run((world, blockPos) -> {
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.get(AnvilBlock.FACING) == Direction.SOUTH || blockState.get(AnvilBlock.FACING) == Direction.EAST) {
                ret.set(true);
            }
        });
        return ret.get();
    }
}
