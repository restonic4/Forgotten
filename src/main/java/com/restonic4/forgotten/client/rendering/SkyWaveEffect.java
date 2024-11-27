package com.restonic4.forgotten.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.restonic4.forgotten.registries.client.ForgottenShaderHolders;
import com.restonic4.forgotten.util.helpers.MathHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

public class SkyWaveEffect {
    private static final float MAX_BEAM_SOUND_DISTANCE = 1000;

    private long startTime, endTime, actionTime;
    private Vector3f position;
    private float height;
    private Color color;
    private Runnable actionExecutedAbovePlayerHead;
    private Runnable actionExecutedBeforeAbovePlayerHead;
    private boolean actionBeforeExecuted = false;
    private boolean actionExecuted = false;

    private VertexBuffer waveBuffer;

    private float[] playerUniformPosCache = new float[3];
    private float[] beamUniformPosCache = new float[3];
    private float[] beamUniformColorCache = new float[4];

    public SkyWaveEffect() {
        this.startTime = System.currentTimeMillis();
        this.position = new Vector3f();
    }

    public SkyWaveEffect lifetime(float lifetimeInSecs) {
        this.endTime = this.startTime + (long) (lifetimeInSecs * 1000);
        return this;
    }

    public SkyWaveEffect setPosition(Vector3f position) {
        this.position = position;
        return this;
    }

    public SkyWaveEffect height(float height) {
        this.height = height;
        return this;
    }

    public SkyWaveEffect actionExecutedAbovePlayerHead(Runnable actionExecutedAbovePlayerHead) {
        this.actionExecutedAbovePlayerHead = actionExecutedAbovePlayerHead;
        return this;
    }

    public SkyWaveEffect actionExecutedBeforeAbovePlayerHead(Runnable actionExecutedAbovePlayerHead) {
        this.actionExecutedBeforeAbovePlayerHead = actionExecutedAbovePlayerHead;
        return this;
    }

    public SkyWaveEffect color(Color color) {
        this.color = color;
        return this;
    }

    public void render(PoseStack poseStack, Matrix4f matrix4f) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }

        executeAction();

        Vec3 vec3 = Minecraft.getInstance().level.getSkyColor(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), 18000);

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor((float)vec3.x, (float)vec3.y, (float)vec3.z, 1.0F);

        ShaderInstance shaderInstance = ForgottenShaderHolders.SKY_WAVE.getInstance().get();
        updateSkyShaderData(shaderInstance);

        RenderSystem.enableBlend();

        if (this.waveBuffer == null) {
            this.waveBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            BufferBuilder.RenderedBuffer renderedBuffer = LevelRenderer.buildSkyDisc(bufferBuilder, 16.0F);
            this.waveBuffer.bind();
            this.waveBuffer.upload(renderedBuffer);
            VertexBuffer.unbind();
        }

        this.waveBuffer.bind();
        this.waveBuffer.drawWithShader(poseStack.last().pose(), matrix4f, shaderInstance);

        VertexBuffer.unbind();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
    }

    private void executeAction() {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        Vector3f distance = position.sub(Minecraft.getInstance().player.position().toVector3f());

        if (actionTime == 0) {
            actionTime = (long) (Math.max(4, MathHelper.calculateScale(distance, MAX_BEAM_SOUND_DISTANCE, 9)) * 1000);
        }

        if (System.currentTimeMillis() >= actionTime - 1.5f && !actionBeforeExecuted) {
            actionBeforeExecuted = true;
            this.actionExecutedBeforeAbovePlayerHead.run();
        }

        if (System.currentTimeMillis() >= actionTime && !actionExecuted) {
            actionExecuted = true;
            this.actionExecutedAbovePlayerHead.run();
        }
    }

    private void updateSkyShaderData(ShaderInstance shader) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }

        Vec3 playerPos = Minecraft.getInstance().player.position();

        playerUniformPosCache[0] = (float) playerPos.x;
        playerUniformPosCache[1] = (float) playerPos.y;
        playerUniformPosCache[2] = (float) playerPos.z;

        beamUniformPosCache[0] = position.x;
        beamUniformPosCache[1] = position.x;
        beamUniformPosCache[2] = position.x;

        beamUniformColorCache[0] = MathHelper.getNormalizedColorR(color);
        beamUniformColorCache[1] = MathHelper.getNormalizedColorG(color);
        beamUniformColorCache[2] = MathHelper.getNormalizedColorB(color);
        beamUniformColorCache[3] = MathHelper.getNormalizedColorA(color);

        shader.safeGetUniform("PlayerPos").set(playerUniformPosCache);
        shader.safeGetUniform("Center").set(beamUniformPosCache);

        shader.safeGetUniform("WaveColor").set(beamUniformColorCache);
        shader.safeGetUniform("Height").set(this.height);
        shader.safeGetUniform("Progress").set(getProgress());
        shader.safeGetUniform("Alpha").set(1f);
    }

    public float getProgress() {
        long currentTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        if (duration <= 0) return 0.0f;

        long elapsedTime = currentTime - startTime;
        float progress = (float) elapsedTime / (float) duration;

        return Math.min(Math.max(progress, 0.0f), 1.0f);
    }


    public boolean isFinished() {
        return System.currentTimeMillis() >= this.endTime;
    }
}
