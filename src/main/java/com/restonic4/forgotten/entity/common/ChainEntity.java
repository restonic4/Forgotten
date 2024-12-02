package com.restonic4.forgotten.entity.common;

import com.restonic4.forgotten.networking.PacketManager;
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
    private boolean isVertical;
    private boolean isAlt;

    private static final TrackedData<Boolean> IS_VERTICAL = TrackedDataHandlerRegistry.BOOLEAN.create();

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
        return isVertical;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;

        if (!clientSide) {
            updateClients();
        }
    }

    public boolean isAlt() {
        return isAlt;
    }

    public void setAlt(boolean alt) {
        isAlt = alt;

        if (!clientSide) {
            updateClients();
        }
    }

    private void updateClients() {
        if (this.getServer() == null) {
            return;
        }

        for (ServerPlayer serverPlayer : this.getServer().getPlayerList().getPlayers()) {
            FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
            friendlyByteBuf.writeUUID(this.getUUID());
            friendlyByteBuf.writeBoolean(isAlt);
            friendlyByteBuf.writeBoolean(isVertical);
            ServerPlayNetworking.send(serverPlayer, PacketManager.CHAIN_STATE, friendlyByteBuf);
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsVertical", this.isVertical);
        compound.putBoolean("IsAlt", this.isAlt);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("IsVertical")) {
            setVertical(compound.getBoolean("IsVertical"));
        }
        if (compound.contains("IsAlt")) {
            setAlt(compound.getBoolean("IsAlt"));
        }
    }
}
