package me.kimovoid.modernbetacompanion.mixin.blockbreakspeed;

import com.google.common.collect.ObjectArrays;
import me.kimovoid.modernbetacompanion.ModernBetaCompanion;
import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ToolItem.class)
public class ToolItemMixin {

    @Shadow private Block[] effectiveBlocks;

    @Redirect(method = "getMiningSpeed", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ToolItem;effectiveBlocks:[Lnet/minecraft/block/Block;"))
    private Block[] setEffectiveness(ToolItem instance) {
        if (!ModernBetaCompanion.isPlayingModernBeta()) {
            return this.effectiveBlocks;
        }

        Block[] effective = this.effectiveBlocks;

        /* Append extra axe blocks */
        if (instance instanceof AxeItem) {
            return ObjectArrays.concat(effective, new Block[]{
                    Block.CRAFTING_TABLE,
                    Block.NOTEBLOCK,
                    Block.WOODEN_DOOR,
                    Block.WOODEN_PRESSURE_PLATE,
                    Block.STANDING_SIGN,
                    Block.WALL_SIGN,
                    Block.JUKEBOX,
                    Block.OAK_STAIRS,
                    Block.FENCE,
                    Block.PUMPKIN,
                    Block.LIT_PUMPKIN,
                    Block.TRAPDOOR
            }, Block.class);
        }

        /* Append extra pickaxe blocks */
        if (instance instanceof PickaxeItem) {
            return ObjectArrays.concat(effective, new Block[]{
                    Block.DISPENSER,
                    Block.RAIL,
                    Block.POWERED_RAIL,
                    Block.DETECTOR_RAIL,
                    Block.FURNACE,
                    Block.LIT_FURNACE,
                    Block.STONE_STAIRS,
                    Block.STONE_PRESSURE_PLATE,
                    Block.IRON_DOOR,
                    Block.REDSTONE_ORE,
                    Block.LIT_REDSTONE_ORE,
                    Block.STONE_BUTTON,
                    Block.BRICKS,
                    Block.MOB_SPAWNER
            }, Block.class);
        }

        /* Append extra shovel blocks */
        if (instance instanceof ShovelItem) {
            return ObjectArrays.concat(effective, new Block[]{Block.SOUL_SAND}, Block.class);
        }

        return effective;
    }
}