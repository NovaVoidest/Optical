package git.nova.optical.mixin;

import git.nova.optical.client.OpticalKeyController;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Shadow
    private double accumulatedDX;

    @Shadow
    private double accumulatedDY;

    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void optical$lockCameraWhileAdjusting(double movementTime, CallbackInfo ci) {
        if (OpticalKeyController.handleMouseDelta(this.accumulatedDX, this.accumulatedDY)) {
            ci.cancel();
        }
    }
}
