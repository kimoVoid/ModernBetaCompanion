package me.kimovoid.modernbetacompanion.mixin.blockplacement;

import me.kimovoid.modernbetacompanion.ModernBetaCompanion;
import net.minecraft.block.FenceBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public class FenceBlockMixin {

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void canBePlaced(CallbackInfoReturnable<Boolean> cir) {
        if (ModernBetaCompanion.isPlayingModernBeta()) {
            cir.setReturnValue(true);
        }
    }
}