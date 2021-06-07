package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DispenserBlock.class)
public interface DispenserBlockAccessor {

    @Accessor("BEHAVIORS")
    @NotNull
    static Map<Item, DispenserBehavior> getBehaviors() {
        throw new AssertionError();
    }
}
