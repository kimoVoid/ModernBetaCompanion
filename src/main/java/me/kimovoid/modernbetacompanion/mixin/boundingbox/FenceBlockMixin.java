package me.kimovoid.modernbetacompanion.mixin.boundingbox;

import me.kimovoid.modernbetacompanion.ModernBetaCompanion;
import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public abstract class FenceBlockMixin extends Block {

    protected FenceBlockMixin(int id, Material material) {
        super(id, material);
    }

    @Unique
    private Box modernFenceBox(World world, int x, int y, int z) {
        int fenceId = Block.FENCE.id;
        boolean posX = world.getBlock(x + 1, y, z) == fenceId;
        boolean negX = world.getBlock(x - 1, y, z) == fenceId;
        boolean posZ = world.getBlock(x, y, z + 1) == fenceId;
        boolean negZ = world.getBlock(x, y, z - 1) == fenceId;

        return Box.of(negX ? 0 : 0.375F, 0.F, negZ ? 0.F : 0.375F, posX ? 1.F : 0.625F, 1.F, posZ ? 1.F : 0.625F);
    }

    @Unique
    private Box modernFenceBox(World world, int x, int y, int z, boolean collider) {
        Box box = modernFenceBox(world, x, y, z);

        box.minX += x;
        box.minY += y;
        box.minZ += z;
        box.maxX += x;
        box.maxY += y;
        box.maxZ += z;

        if (collider) {
            box.maxY += 0.5F;
        }

        return box;
    }

    @Inject(method = "getCollisionShape", at = @At("RETURN"), cancellable = true)
    public void modifyCollisionShape(World level, int x, int y, int z, CallbackInfoReturnable<Box> cir) {
        if (ModernBetaCompanion.isPlayingModernBeta()) {
            cir.setReturnValue(this.modernFenceBox(level, x, y, z, true));
        }
    }

    @Unique private World world;

    @Override
    public Box getCollisionShape(World world, int x, int y, int z) {
        if (ModernBetaCompanion.isPlayingModernBeta()) {
            this.world = world;
            return this.modernFenceBox(world, x, y, z, false);
        }
        else {
            return super.getCollisionShape(world, x, y, z);
        }
    }

    @Override
    public void updateShape(WorldView blockView, int x, int y, int z) {
        if (ModernBetaCompanion.isPlayingModernBeta()) {
            if (this.world == null)
                return;
            Box box = this.modernFenceBox(this.world, x, y, z);
            setShape((float) box.minX, (float) box.minY, (float) box.minZ, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
        }
        else {
            super.updateShape(blockView, x, y, z);
        }
    }
}