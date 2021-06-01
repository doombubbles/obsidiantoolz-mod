package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow
    @Final
    private List<DefaultedList<ItemStack>> combinedInventory;
    @Shadow
    @Final
    public DefaultedList<ItemStack> main;
    @Shadow
    @Final
    public DefaultedList<ItemStack> armor;
    @Shadow
    @Final
    public DefaultedList<ItemStack> offHand;

    @Shadow
    @Final
    public PlayerEntity player;

    public List<ItemStack> mainBackup;
    public List<ItemStack> armorBackup;
    public List<ItemStack> offHandBackup;

    //@SuppressWarnings({ "unchecked", "rawtypes" })
    @Inject(at = @At("HEAD"), method = "dropAll")
    public void backupSoulBoundItems(CallbackInfo ci) {
        mainBackup = new ArrayList<>(main);
        armorBackup = new ArrayList<>(armor);
        offHandBackup = new ArrayList<>(offHand);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;"), method = "dropAll")
    public ItemEntity maybeDropItem(PlayerEntity pe, ItemStack itemStack_1, boolean boolean_1, boolean boolean_2) {
        if (EnchantmentHelper.getLevel(ObsidianToolzMod.SOULBOUND, itemStack_1) > 0) {
            return null;
        } else {
            return pe.dropItem(itemStack_1, boolean_1, boolean_2);
        }
    }

    private void downgradeSoulBound(ItemStack iS)
    {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(iS);
        int soulboundLvl = EnchantmentHelper.getLevel(ObsidianToolzMod.SOULBOUND, iS);
        iS.removeSubTag("Enchantments");
        enchantments.remove(ObsidianToolzMod.SOULBOUND);
        for(Enchantment ench : enchantments.keySet())
        {
            iS.addEnchantment(ench, enchantments.get(ench));
        }
        if(soulboundLvl > 1)
        {
            iS.addEnchantment(ObsidianToolzMod.SOULBOUND, soulboundLvl-1);
        }
    }

    @Inject(at = @At("RETURN"), method = "dropAll")
    public void restoreSoulBoundItems(CallbackInfo ci) {
        restore(mainBackup, main);
        restore(armorBackup, armor);
        restore(offHandBackup, offHand);
        mainBackup = null;
        armorBackup = null;
        offHandBackup = null;
    }

    private void restore(List<ItemStack> mainBackup, DefaultedList<ItemStack> main) {
        ItemStack current;
        for (int i = 0; i < mainBackup.size(); i++) {
            if (EnchantmentHelper.getLevel(ObsidianToolzMod.SOULBOUND, mainBackup.get(i)) > 0) {
                //System.out.println("found soulbound in main");
                current = mainBackup.get(i).copy();
                main.set(i, current);
                downgradeSoulBound(current);

            }
        }
    }

}
