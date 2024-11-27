package com.restonic4.forgotten.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.restonic4.forgotten.registries.client.ForgottenShaderHolders;
import com.restonic4.forgotten.util.helpers.MathHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

public class SkyWaveEffect {
    private long startTime, endTime;
    private float actionExecutionProgress;
    private Vector3f position;
    private float height;
    private Color color;
    private Runnable actionExecutedAbovePlayerHead;
    private Runnable actionExecutedBeforeAbovePlayerHead;
    private boolean actionBeforeExecuted = false;
    private boolean actionExecuted = false;
    private float actionBeforeHeadOffset;

    private VertexBuffer waveBuffer;

    private float[] playerUniformPosCache = new float[3];
    private float[] beamUniformPosCache = new float[3];
    private float[] beamUniformColorCache = new float[4];

    public SkyWaveEffect() {
        this.startTime = System.currentTimeMillis();
        this.position = new Vector3f();
        actionBeforeHeadOffset = 0.2f;
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

    public SkyWaveEffect offsetActionBeforeHead(float progressAmount) {
        this.actionBeforeHeadOffset = progressAmount;
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
            BufferBuilder.RenderedBuffer renderedBuffer = buildSkyDisc(bufferBuilder, 16.0F);
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

    public static BufferBuilder.RenderedBuffer buildSkyDisc(BufferBuilder bufferBuilder, float f) {
        float g = Math.signum(f) * 512.0F;
        float h = 512.0F;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        bufferBuilder.vertex(0.0, (double)f, 0.0).endVertex();

        for(int i = -180; i <= 180; i += 45) {
            bufferBuilder.vertex((double)(g * Mth.cos((float)i * 0.017453292F)), (double)f, (double)(512.0F * Mth.sin((float)i * 0.017453292F))).endVertex();
        }

        return bufferBuilder.end();
    }

    private void executeAction() {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        if (actionExecutionProgress == 0) {
            Vector3f playerPos = Minecraft.getInstance().player.position().toVector3f();
            Vector3f distance = new Vector3f(position.x - playerPos.x, position.y - playerPos.y,position.z - playerPos.z);

            actionExecutionProgress = calculateActionProgressFactor(distance.length());
        }

        if (getProgress() >= actionExecutionProgress - actionBeforeHeadOffset && !actionBeforeExecuted) {
            actionBeforeExecuted = true;
            this.actionExecutedBeforeAbovePlayerHead.run();
        }

        if (getProgress() >= actionExecutionProgress && !actionExecuted) {
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

    public static float calculateActionProgressFactor(float input) {
        float maxInput = 1000.0f;
        float minOutput = 0.2f;
        float maxOutput = 0.6f;

        if (input >= maxInput) {
            return maxOutput;
        }

        return minOutput + (maxOutput - minOutput) * (input / maxInput);
    }

    public boolean isFinished() {
        return System.currentTimeMillis() >= this.endTime;
    }

    public void cleanup() {
        if (this.waveBuffer != null) {
            this.waveBuffer.close();
            this.waveBuffer = null;
        }

        this.actionExecutedBeforeAbovePlayerHead = null;
        this.actionExecutedAbovePlayerHead = null;
    }
}
