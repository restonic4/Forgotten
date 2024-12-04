package com.restonic4.forgotten.entity.common;

import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.saving.Components;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChainEntity extends Animal {
    public final AnimationState idleAnimationState = new AnimationState();
    private boolean clientSide = false;

    public ChainEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    private void setupAnimationStates() {
        this.idleAnimationState.animateWhen(true, this.tickCount);
    }

    @Override
    public void tick() {
        super.tick();

        setNoAi(true);

        if (this.level().isClientSide()) {
            clientSide = true;
            setupAnimationStates();
        }
    }

    public void destroy() {
        this.discard();
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3)
                .add(Attributes.MOVEMENT_SPEED, 0f);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    protected void playHurtSound(DamageSource damageSource) {

    }

    @Override
    public void knockback(double d, double e, double f) {

    }

    @Override
    public void setNoAi(boolean bl) {
        super.setNoAi(bl);
    }


    @Override
    public boolean isPushable() {
        return false;
    }

    public boolean isVertical() {
        return Components.CHAIN_STATE.get(this).isVertical();
    }

    public void setVertical(boolean vertical) {
        Components.CHAIN_STATE.get(this).setVertical(vertical);
    }

    public boolean isAlt() {
        return Components.CHAIN_STATE.get(this).isAlt();
    }

    public void setAlt(boolean alt) {
        Components.CHAIN_STATE.get(this).setAlt(alt);
    }

    public boolean isRotated() {
        return Components.CHAIN_STATE.get(this).isRotated();
    }

    public void setRotated(boolean rotated) {
        Components.CHAIN_STATE.get(this).setRotated(rotated);
        this.setYRot(rotated ? 90 : 0);
    }

    public int getIndex() {
        return Components.CHAIN_STATE.get(this).getIndex();
    }

    public void setIndex(int index) {
        Components.CHAIN_STATE.get(this).setIndex(index);
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return isRotated() ? 90 : 0;
    }

    @Override
    public float getYHeadRot() {
        return isRotated() ? 90 : 0;
    }

    @Override
    public @NotNull String toString() {
        return "ChainEntity{index=" + getIndex() + "}";
    }
}
