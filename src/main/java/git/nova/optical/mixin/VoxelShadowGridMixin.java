package git.nova.optical.mixin;

import foundry.veil.impl.client.render.light.VoxelShadowGrid;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL42C;
import org.lwjgl.opengl.GL44C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.ByteBuffer;

@Mixin(VoxelShadowGrid.class)
public class VoxelShadowGridMixin {

    @Redirect(method = "ensureTexture", at = @At(value = "INVOKE",
            target = "Lorg/lwjgl/opengl/GL15C;glTexImage3D(IIIIIIIIILjava/nio/ByteBuffer;)V"))
    private static void optical$allocateShadowGridTexture(int target, int level, int internalFormat,
                                                         int width, int height, int depth, int border,
                                                         int format, int type, ByteBuffer pixels) {
        GL42C.glTexStorage3D(target, 1, internalFormat, width, height, depth);
        int texture = GL11C.glGetInteger(GL15C.GL_TEXTURE_BINDING_3D);
        GL44C.glClearTexImage(texture, 0, format, type, (ByteBuffer) null);
    }
}
