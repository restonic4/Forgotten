package com.restonic4.forgotten.entity.common;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.commdands.SetUpForgotten;
import com.restonic4.forgotten.registries.common.ForgottenEntities;
import com.restonic4.forgotten.registries.common.ForgottenSounds;
import com.restonic4.forgotten.saving.Components;
import com.restonic4.forgotten.saving.JsonDataManager;
import com.restonic4.forgotten.util.ServerCache;
import com.restonic4.forgotten.util.helpers.RandomUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmallCoreEntity extends Animal {
    public final AnimationState idleAnimationState = new AnimationState();
    private long recoverTime, deSpawnTime;
    public boolean done = false;

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
            ServerCache.addCoreIfPossible(this);

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

        if (deSpawnTime == 0 && this.getHealth() - 1 <= 0) {
            deSpawnTime = currentTime + 2000;

            if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel && !done) {
                done = true;

                SetUpForgotten.killChainRow(serverLevel, this.getIndex());
            }

            return currentTime >= deSpawnTime;
        }

        if (deSpawnTime == 0 || currentTime >= deSpawnTime) {
            recoverTime = currentTime + 1500;

            this.playSound(ForgottenSounds.REJECT, 1, RandomUtil.randomBetween(0.75f, 1.25f));

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

    public int getIndex() {
        return Components.SMALL_CORE.get(this).getIndex();
    }

    public void setIndex(int index) {
        Components.SMALL_CORE.get(this).setIndex(index);
    }
}
