package git.nova.optical.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import git.nova.optical.OpticalMod;
import git.nova.optical.common.block.SpotlightBlock;
import git.nova.optical.common.blockentity.SpotlightBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SpotlightBlockEntityRenderer implements BlockEntityRenderer<SpotlightBlockEntity> {

    public static final ModelResourceLocation HEAD_OFF = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(OpticalMod.MODID, "block/spotlight_head"));
    public static final ModelResourceLocation HEAD_ON = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(OpticalMod.MODID, "block/spotlight_head_on"));

    public SpotlightBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SpotlightBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof SpotlightBlock)) {
            return;
        }

        BakedModel model = Minecraft.getInstance().getModelManager()
                .getModel(state.getValue(SpotlightBlock.LIT) ? HEAD_ON : HEAD_OFF);

        poseStack.pushPose();
        SpotlightBeamGeometry.applyBlockRotation(poseStack, state.getValue(SpotlightBlock.FACING),
                state.getValue(SpotlightBlock.ROTATION));
        SpotlightBeamGeometry.applyHeadTilt(poseStack, blockEntity.getRotationDegrees());

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutout());
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                consumer,
                state,
                model,
                1.0F,
                1.0F,
                1.0F,
                packedLight,
                packedOverlay
        );
        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(SpotlightBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(0.5D);
    }
}
