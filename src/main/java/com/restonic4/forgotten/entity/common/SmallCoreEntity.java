package com.restonic4.forgotten.entity.common;

import com.restonic4.forgotten.commdands.SetUpForgotten;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.registries.common.ForgottenSounds;
import com.restonic4.forgotten.saving.Components;
import com.restonic4.forgotten.saving.SaveManager;
import com.restonic4.forgotten.util.ServerCache;
import com.restonic4.forgotten.util.helpers.RandomUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmallCoreEntity extends GlowSquid {
    public final AnimationState idleAnimationState = new AnimationState();
    private long recoverTime, deSpawnTime;
    public boolean done = false;

    public SmallCoreEntity(EntityType<? extends GlowSquid> entityType, Level level) {
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

            if (this.level().isClientSide() && !done) {
                done = true;
            }

            if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel && !done) {
                done = true;

                serverLevel.getServer().execute(() -> {
                    Vec3 particlePos = new Vec3(this.getX() + 0.5, this.getY() + 0.5, this.getZ() + 0.5);

                    serverLevel.sendParticles(
                            ParticleTypes.EXPLOSION, // Tipo de partícula
                            particlePos.x,           // Coordenada X
                            particlePos.y,           // Coordenada Y
                            particlePos.z,           // Coordenada Z
                            50,                      // Cantidad de partículas
                            0.5,                     // Desviación en X
                            0.5,                     // Desviación en Y
                            0.5,                     // Desviación en Z
                            0.1                      // Velocidad de las partículas
                    );

                    serverLevel.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE);

                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                        } catch (Exception ignored) {}

                        BlockPos center = SaveManager.getInstance(serverLevel.getServer()).get("center", BlockPos.class);
                        int defeated = SaveManager.getInstance(serverLevel.getServer()).get("SmallCoresDefeated", Integer.class);
                        for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
                            System.out.println("Checking distance: " + serverPlayer.getDisplayName() + " " + serverPlayer.blockPosition().distSqr(center));
                            if (serverPlayer.blockPosition().distSqr(center) <= 4500) {
                                FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                                if (defeated >= 3) {
                                    friendlyByteBuf.writeBoolean(true);
                                } else {
                                    friendlyByteBuf.writeBoolean(false);
                                }
                                ServerPlayNetworking.send(serverPlayer, PacketManager.CUTSCENE, friendlyByteBuf);
                                System.out.println("Jaja camara: " + serverPlayer.getDisplayName());

                                /*if (defeated < 3) {
                                    FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                                    friendlyByteBuf.writeBoolean(false);
                                    ServerPlayNetworking.send(serverPlayer, PacketManager.CUTSCENE, friendlyByteBuf);
                                }
                                System.out.println("Jaja camara: " + serverPlayer.getDisplayName());*/
                            }
                        }
                    }).start();
                });

                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (Exception ignored) {}
                    SetUpForgotten.killChainRow(serverLevel, this.getIndex());
                }).start();
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

    /*@Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }*/

    public long getDeSpawnTime() {
        return this.deSpawnTime;
    }

    @Override
    protected void playHurtSound(DamageSource damageSource) {

    }

    @Override
    public void playAmbientSound() {

    }

    @Override
    protected void playSwimSound(float f) {

    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {

    }

    @Override
    protected void playMuffledStepSound(BlockState blockState) {

    }

    @Override
    public void aiStep() {

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
