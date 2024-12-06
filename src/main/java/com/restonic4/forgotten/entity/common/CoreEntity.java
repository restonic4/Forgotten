package com.restonic4.forgotten.entity.common;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.commdands.SetUpForgotten;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.saving.Components;
import com.restonic4.forgotten.saving.JsonDataManager;
import com.restonic4.forgotten.util.ServerCache;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoreEntity extends Animal {
    public final AnimationState idleAnimationState = new AnimationState();
    private long recoverTime, deSpawnTime;

    public CoreEntity(EntityType<? extends Animal> entityType, Level level) {
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
            if (deSpawnTime != 0) {
                setupAnimationStates();
            }
        } else {
            ServerCache.addMainCoreIfPossible(this);

            if (deSpawnTime != 0) {
                long currentTime = System.currentTimeMillis();

                if (currentTime >= deSpawnTime) {
                    this.discard();
                }
            }
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float f) {
        long currentTime = System.currentTimeMillis();

        if (currentTime < recoverTime) {
            return false;
        }

        if (this.level() instanceof ServerLevel && Forgotten.isSmallCoreLeft()) {
            return false;
        }

        if (deSpawnTime == 0 && this.getHealth() - 1 <= 0) {
            deSpawnTime = currentTime + 2000;

            if (!this.level().isClientSide()) {
                Forgotten.startMainRitual((ServerLevel) this.level());
            }

            return currentTime >= deSpawnTime;
        }

        if (deSpawnTime == 0 || currentTime >= deSpawnTime) {
            recoverTime = currentTime + 1500;
            return super.hurt(damageSource, 1);
        }

        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3)
                .add(Attributes.MOVEMENT_SPEED, 0f);
    }

    public void startFallAnimation() {
        this.setPos(new Vec3(this.position().x, this.position().y - 21.5f, this.position().z));

        for (ServerPlayer serverPlayer : this.level().getServer().getPlayerList().getPlayers()) {
            FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
            ServerPlayNetworking.send(serverPlayer, PacketManager.CORE_FALL, friendlyByteBuf);
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    public long getDeSpawnTime() {
        return this.deSpawnTime;
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

    public int getIndex() {
        return Components.SMALL_CORE.get(this).getIndex();
    }

    public void setIndex(int index) {
        Components.SMALL_CORE.get(this).setIndex(index);
    }
}
