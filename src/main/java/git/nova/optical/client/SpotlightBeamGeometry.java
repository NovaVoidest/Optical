package git.nova.optical.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import git.nova.optical.common.block.SpotlightBlock;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Shared geometry for the spotlight model.
 * <p>
 * The projector model is authored pointing north ({@code -Z}) and sits in the lower part of the
 * block. The movable head pivots around its own centre, and the whole model is then rotated by
 * the same transform the blockstate uses for {@link SpotlightBlock#FACING}. Keeping both the
 * block entity renderer and the Veil light on this one helper makes the beam follow the head.
 */
public final class SpotlightBeamGeometry {

    /** Centre of the movable head, in block units (0..1). */
    public static final float PIVOT_X = 8.0F / 16.0F;
    public static final float PIVOT_Y = 9.0F / 16.0F;
    public static final float PIVOT_Z = 8.0F / 16.0F;

    /** Centre of the lens face, in block units (0..1). The lens faces north at zero tilt. */
    public static final float LENS_X = 8.0F / 16.0F;
    public static final float LENS_Y = 9.0F / 16.0F;
    public static final float LENS_Z = 2.0F / 16.0F;

    /** Block models are rotated around the centre of the block. */
    public static final float BLOCK_CENTER = 0.5F;

    /** Small push along the beam so the light is not buried inside the head geometry. */
    private static final float BEAM_ORIGIN_BIAS = 0.1F;

    private SpotlightBeamGeometry() {
    }

    /**
     * The rotation applied to the blockstate model, matching {@code BlockModelRotation}.
     */
    public static Quaternionf blockRotation(Direction facing) {
        return new Quaternionf().rotateYXZ(
                -SpotlightBlock.modelRotationY(facing) * Mth.DEG_TO_RAD,
                -SpotlightBlock.modelRotationX(facing) * Mth.DEG_TO_RAD,
                0.0F
        );
    }

    /**
     * Applies the blockstate rotation to a pose stack whose origin is the block corner.
     */
    public static void applyBlockRotation(PoseStack poseStack, Direction facing) {
        poseStack.translate(BLOCK_CENTER, BLOCK_CENTER, BLOCK_CENTER);
        poseStack.mulPose(blockRotation(facing));
        poseStack.translate(-BLOCK_CENTER, -BLOCK_CENTER, -BLOCK_CENTER);
    }

    /**
     * Tilts the head around its pivot. Positive values tilt the beam upward.
     */
    public static void applyHeadTilt(PoseStack poseStack, float tiltDegrees) {
        poseStack.translate(PIVOT_X, PIVOT_Y, PIVOT_Z);
        poseStack.mulPose(Axis.XP.rotationDegrees(tiltDegrees));
        poseStack.translate(-PIVOT_X, -PIVOT_Y, -PIVOT_Z);
    }

    /**
     * @return the direction the beam travels, in world space.
     */
    public static Vector3f beamDirection(Direction facing, float tiltDegrees) {
        Vector3f direction = new Vector3f(0.0F, 0.0F, -1.0F);
        direction.rotateX(tiltDegrees * Mth.DEG_TO_RAD);
        direction.rotate(blockRotation(facing));
        return direction.normalize();
    }

    /**
     * @return the origin of the beam, relative to the block corner.
     */
    public static Vector3f lensPosition(Direction facing, float tiltDegrees) {
        Vector3f lens = new Vector3f(
                LENS_X - PIVOT_X,
                LENS_Y - PIVOT_Y,
                LENS_Z - PIVOT_Z
        );
        lens.rotateX(tiltDegrees * Mth.DEG_TO_RAD);
        lens.add(PIVOT_X, PIVOT_Y, PIVOT_Z);

        // Model-space block centre, then the blockstate rotation about that centre.
        lens.sub(BLOCK_CENTER, BLOCK_CENTER, BLOCK_CENTER);
        lens.rotate(blockRotation(facing));
        lens.add(BLOCK_CENTER, BLOCK_CENTER, BLOCK_CENTER);

        Vector3f beam = beamDirection(facing, tiltDegrees);
        lens.add(beam.x * BEAM_ORIGIN_BIAS, beam.y * BEAM_ORIGIN_BIAS, beam.z * BEAM_ORIGIN_BIAS);
        return lens;
    }
}
