// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package com.restonic4.forgotten.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.restonic4.forgotten.entity.animations.BlockAnim;
import com.restonic4.forgotten.entity.common.BlockGeoEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.model.geom.PartPose;
import org.jetbrains.annotations.NotNull;

public class BlockGeo<T extends BlockGeoEntity> extends HierarchicalModel<T> {
	private final ModelPart bone;
	private final ModelPart bone2;
	public BlockGeo(ModelPart root) {
		this.bone = root.getChild("bone");
		this.bone2 = root.getChild("bone2");
	}
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition bone = modelPartData.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -1.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, -7.0F));

		PartDefinition bone2 = modelPartData.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -27.0F, -3.0F, 6.0F, 38.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		return LayerDefinition.create(modelData, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.idleAnimationState, BlockAnim.ANIMATIONYES, ageInTicks);
		/*if (entity.idleAnimationState.isStarted()) {
			//this.animate(entity.idleAnimationState, BlockAnim.ANIMATIONYES, ageInTicks, 1f);
			this.applyStatic(BlockAnim.ANIMATIONYES);
		}*/
	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		bone2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public @NotNull ModelPart root() {
		return bone;
	}
}