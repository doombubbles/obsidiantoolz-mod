package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.Enchantments.BoostedEnchantment;
import net.doombubbles.obsidiantoolz.Enchantments.PhotosynthesisEnchantment;
import net.doombubbles.obsidiantoolz.Enchantments.SoulboundEnchantment;
import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin {

    @Shadow
    private ScreenHandlerContext context;
    @Shadow
    private int[] enchantmentId;

    @Shadow
    private int[] enchantmentLevel;

    @Inject(at = @At("TAIL"), method = "onContentChanged")
    private void onContentChanged(Inventory inventory, CallbackInfo callbackInfo) {
        this.context.run((world, blockPos) -> {
            List<Enchantment> enchantmentList = new ArrayList<>();
            for (int i : enchantmentId) {
                enchantmentList.add(Registry.ENCHANTMENT.get(i));
            }
            if (enchantmentList.stream().anyMatch(enchantment -> enchantment instanceof ObsidianToolzEnchantment)) {
                PlayerEntity closestPlayer = world.getClosestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 200, entity -> {
                    if (entity instanceof ServerPlayerEntity) {
                        PlayerEntity playerEntity = (PlayerEntity) entity;
                        return playerEntity.currentScreenHandler == (Object) this;
                    }
                    return false;
                });
                if (closestPlayer instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) closestPlayer;


                    serverPlayerEntity.networkHandler.sendPacket(new OverlayMessageS2CPacket(new LiteralText(getFromEnchantments(enchantmentList))));
                }
            }
        });
    }


    private String getFromEnchantments(List<Enchantment> enchantmentList) {
        String value = "Hidden Hovers:";
        for (int i = 0; i < enchantmentList.size(); i++) {
            Enchantment enchantment = enchantmentList.get(i);
            if (enchantment instanceof ObsidianToolzEnchantment) {
                int level = enchantmentLevel[i];
                String name = ((ObsidianToolzEnchantment) enchantment).getRealName(level);
                if (enchantment instanceof SoulboundEnchantment || enchantment instanceof PhotosynthesisEnchantment || enchantment instanceof BoostedEnchantment) {
                    name = name.split(" ")[0] + " ?";
                }
                value +=  " " + (i+1) + ". " + name;
            }
        }
        return value;
    }
}
