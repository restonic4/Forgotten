package com.restonic4.forgotten.entity.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SmallCoreEntity extends Animal {
    public final AnimationState idleAnimationState = new AnimationState();
    private long recoverTime, deSpawnTime;

    public SmallCoreEntity(EntityType<? extends Animal> entityType, Level level) {
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
            if (deSpawnTime != 0) {
                long currentTime = System.currentTimeMillis();

                if (currentTime >= deSpawnTime) {
                    this.discard();
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        long currentTime = System.currentTimeMillis();

        if (currentTime < recoverTime) {
            return false;
        }

        if (deSpawnTime == 0 && this.getHealth() - 1 <= 0) {
            deSpawnTime = currentTime + 2000;

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
}
