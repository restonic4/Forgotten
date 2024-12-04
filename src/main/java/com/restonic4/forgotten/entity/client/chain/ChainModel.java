package com.restonic4.forgotten.entity.client.chain;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.entity.animations.ChainAnim;
import com.restonic4.forgotten.entity.animations.SmallCoreAnim;
import com.restonic4.forgotten.entity.common.ChainEntity;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class ChainModel <T extends ChainEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Forgotten.MOD_ID, "chain"), "main");
    private final ModelPart root;
    private final ModelPart rotationManager;
    private final ModelPart part1;
    private final ModelPart part2;
    private final ModelPart part3;
    private final ModelPart part4;

    public ChainModel(ModelPart root) {
        this.root = root.getChild("root");
        this.rotationManager = this.root.getChild("rotationManager");
        this.part1 = this.rotationManager.getChild("part1");
        this.part2 = this.rotationManager.getChild("part2");
        this.part3 = this.rotationManager.getChild("part3");
        this.part4 = this.rotationManager.getChild("part4");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition rotationManager = root.addOrReplaceChild("rotationManager", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

        PartDefinition part1 = rotationManager.addOrReplaceChild("part1", CubeListBuilder.create().texOffs(0, 38).addBox(-1.0F, -2.0F, -1.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -7.0F));

        PartDefinition part2 = rotationManager.addOrReplaceChild("part2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -8.0F, 3.0F, 3.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition part3 = rotationManager.addOrReplaceChild("part3", CubeListBuilder.create().texOffs(38, 0).addBox(-1.0F, -3.0F, -1.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 6.0F));

        PartDefinition part4 = rotationManager.addOrReplaceChild("part4", CubeListBuilder.create().texOffs(0, 19).addBox(-1.0F, -2.0F, -8.0F, 3.0F, 3.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        root().getAllParts().forEach(ModelPart::resetPose);

        AnimationDefinition animationDefinition;

        if (entity.isVertical()) {
            if (entity.isAlt()) {
                animationDefinition = ChainAnim.VERTICAL_ALT;
            } else {
                animationDefinition = ChainAnim.VERTICAL;
            }
        } else {
            if (entity.isAlt()) {
                animationDefinition = ChainAnim.HORIZONTAL_ALT;
            } else {
                animationDefinition = ChainAnim.HORIZONTAL;
            }
        }

        this.animate(entity.idleAnimationState, animationDefinition, ageInTicks);

        if (entity.isD) {

        }
    }
}
