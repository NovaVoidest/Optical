package git.nova.optical.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import git.nova.optical.common.block.SpotlightBlock;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class SpotlightBeamGeometry {

    public static final float PIVOT_X = 8.0F / 16.0F;
    public static final float PIVOT_Y = 9.0F / 16.0F;
    public static final float PIVOT_Z = 8.0F / 16.0F;

    public static final float LENS_X = 8.0F / 16.0F;
    public static final float LENS_Y = 9.0F / 16.0F;
    public static final float LENS_Z = 2.0F / 16.0F;

    public static final float BLOCK_CENTER = 0.5F;

    private static final float BEAM_ORIGIN_BIAS = 0.1F;

    private SpotlightBeamGeometry() {
    }

    public static Quaternionf blockRotation(Direction facing, int rotation) {
        return new Quaternionf().rotateYXZ(
                -SpotlightBlock.modelRotationY(facing, rotation) * Mth.DEG_TO_RAD,
                -SpotlightBlock.modelRotationX(facing, rotation) * Mth.DEG_TO_RAD,
                0.0F
        );
    }

    public static void applyBlockRotation(PoseStack poseStack, Direction facing, int rotation) {
        poseStack.translate(BLOCK_CENTER, BLOCK_CENTER, BLOCK_CENTER);
        poseStack.mulPose(blockRotation(facing, rotation));
        poseStack.translate(-BLOCK_CENTER, -BLOCK_CENTER, -BLOCK_CENTER);
    }

    public static void applyHeadTilt(PoseStack poseStack, float tiltDegrees) {
        poseStack.translate(PIVOT_X, PIVOT_Y, PIVOT_Z);
        poseStack.mulPose(Axis.XP.rotationDegrees(tiltDegrees));
        poseStack.translate(-PIVOT_X, -PIVOT_Y, -PIVOT_Z);
    }

    public static Vector3f beamDirection(Direction facing, int rotation, float tiltDegrees) {
        Vector3f direction = new Vector3f(0.0F, 0.0F, -1.0F);
        direction.rotateX(tiltDegrees * Mth.DEG_TO_RAD);
        direction.rotate(blockRotation(facing, rotation));
        return direction.normalize();
    }

    public static Vector3f lensPosition(Direction facing, int rotation, float tiltDegrees) {
        Vector3f lens = new Vector3f(
                LENS_X - PIVOT_X,
                LENS_Y - PIVOT_Y,
                LENS_Z - PIVOT_Z
        );
        lens.rotateX(tiltDegrees * Mth.DEG_TO_RAD);
        lens.add(PIVOT_X, PIVOT_Y, PIVOT_Z);

        lens.sub(BLOCK_CENTER, BLOCK_CENTER, BLOCK_CENTER);
        lens.rotate(blockRotation(facing, rotation));
        lens.add(BLOCK_CENTER, BLOCK_CENTER, BLOCK_CENTER);

        Vector3f beam = beamDirection(facing, rotation, tiltDegrees);
        lens.add(beam.x * BEAM_ORIGIN_BIAS, beam.y * BEAM_ORIGIN_BIAS, beam.z * BEAM_ORIGIN_BIAS);
        return lens;
    }
}
