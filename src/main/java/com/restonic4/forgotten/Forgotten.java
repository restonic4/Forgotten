package com.restonic4.forgotten;

import com.mojang.authlib.GameProfile;
import com.restonic4.forgotten.commdands.*;
import com.restonic4.forgotten.compatibility.vanish.ConfigOverride;
import com.restonic4.forgotten.compatibility.voicechat.Plugin;
import com.restonic4.forgotten.entity.common.ChainEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import com.restonic4.forgotten.item.PlayerSoul;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.registries.common.*;
import com.restonic4.forgotten.saving.PlayerData;
import com.restonic4.forgotten.saving.SaveManager;
import com.restonic4.forgotten.util.GriefingPrevention;
import com.restonic4.forgotten.util.RandomPlayerSpawnerManager;
import com.restonic4.forgotten.util.ServerCache;
import com.restonic4.forgotten.util.ServerShootingStarManager;
import com.restonic4.forgotten.util.helpers.RandomUtil;
import me.drex.vanish.api.VanishEvents;
import me.drex.vanish.config.ConfigManager;
import me.drex.vanish.util.VanishManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class Forgotten implements ModInitializer {
    public static final String MOD_ID = "forgotten";

    int ticksLeft = 0;
    int tickSaveCounter = 0;

    private static ServerPlayer playerBeingRevived = null;
    private static long reviveAtTime = 0;

    @Override
    public void onInitialize() {
        ForgottenSounds.register();
        registerEvents();
        ForgottenEntities.register();
        ForgottenParticleTypes.registerCommon();
        ForgottenItems.register();
        ForgottenBlocks.registerCommon();
        ForgottenCreativeTabs.register();
        PacketManager.registerClientToServer();
        GriefingPrevention.register();
        RandomPlayerSpawnerManager.register();
    }

    private void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register((minecraftServer) -> {
            SaveManager.getInstance(minecraftServer).loadFromFile();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register((minecraftServer) -> {
            SaveManager.getInstance(minecraftServer).saveToFile();
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TestDeath.register(dispatcher);
            TestBeam.register(dispatcher);
            TestMainRitual.register(dispatcher);
            SetUpForgotten.register(dispatcher);
            CleanupForgotten.register(dispatcher);
            KillOne.register(dispatcher);
            TestStarSpawn.register(dispatcher);
            TestShootStar.register(dispatcher);
            //Lodestone.register(dispatcher);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            SaveManager saveManager = SaveManager.getInstance(server);

            tickSaveCounter++;
            if (tickSaveCounter >= 3000) {
                saveManager.saveToFile();
                tickSaveCounter = 0;
            }

            if (!isSmallCoreLeft(server) && ServerCache.getMainCore() != null && !saveManager.get("MainCoreFallAnimation", Boolean.class)) {
                saveManager.save("MainCoreFallAnimation", true);

                for (ChainEntity entity : ServerCache.chains) {
                    entity.discard();
                }

                for (SmallCoreEntity entity : ServerCache.cores) {
                    entity.discard();
                }

                ServerCache.getMainCore().startFallAnimation();
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            SaveManager saveManager = SaveManager.getInstance(server);

            if (isVanishLoaded()) {
                ConfigManager.vanish().sendJoinDisconnectMessage = false;
                server.getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES).set(false, server);
            }

            if (saveManager.containsKey("center")) {
                BlockPos center = saveManager.get("center", BlockPos.class);

                ServerLevel serverLevel = server.overworld();
                serverLevel.setDefaultSpawnPos(center, 0);
            }
        });

        ServerLivingEntityEvents.ALLOW_DEATH.register((livingEntity, damageSource, damageAmount) -> {
            boolean isDamageByPlayer = damageSource.getEntity() != null && damageSource.getEntity() instanceof Player;

            if (livingEntity instanceof ServerPlayer player) {
                SaveManager saveManager = SaveManager.getInstance(player.getServer());
                Boolean isHardcore = saveManager.get("Hardcore", Boolean.class);

                if (!isDamageByPlayer && isHardcore != null && isHardcore) {
                    PlayerData playerData = saveManager.get(player.getGameProfile().getId().toString(), PlayerData.class);

                    if (playerData == null) {
                        playerData = new PlayerData();
                    }

                    boolean hasBeenSaved = playerData.hasBeenSavedFromLethalDamage();
                    int chance = RandomUtil.randomBetween(0, 100);

                    System.out.println("Death conditions: " + hasBeenSaved + ", " + chance);

                    if (!hasBeenSaved || chance <= 15) {
                        player.setHealth(1.0f);

                        playerData.setHasBeenSavedFromLethalDamage(true);

                        if (damageSource.getEntity() instanceof Projectile projectile) {
                            projectile.discard();
                        }

                        System.out.println(player.getDisplayName() + " saved by Forgotten");

                        saveManager.save(player.getGameProfile().getId().toString(), playerData);

                        return false;
                    }
                }
            }

            if (!livingEntity.level().isClientSide()) {
                SaveManager saveManager = SaveManager.getInstance(livingEntity.getServer());

                if (livingEntity instanceof ServerPlayer serverPlayer && isVanishLoaded() && !isPlayerGoingToUseTotem(serverPlayer)) {
                    Boolean isHardcore = saveManager.get("Hardcore", Boolean.class);

                    if (isHardcore != null && isHardcore) {
                        regeneratePlayer(serverPlayer);

                        if (!VanishManager.isVanished(serverPlayer)) {
                            //placePlayerHead(serverPlayer);
                            placePlayerSoul(serverPlayer);

                            serverPlayer.getInventory().dropAll();

                            if (serverPlayer.level() instanceof ServerLevel && !serverPlayer.wasExperienceConsumed()) {
                                ExperienceOrb.award((ServerLevel)serverPlayer.level(), serverPlayer.position(), serverPlayer.getExperienceReward());
                                serverPlayer.setExperienceLevels(0);
                                serverPlayer.setExperiencePoints(0);
                            }

                            if (isVoiceChatLoaded()) {
                                Plugin.leaveGroup(serverPlayer);
                            }

                            FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                            friendlyByteBuf.writeBoolean(true);
                            ServerPlayNetworking.send(serverPlayer, PacketManager.DEATH, friendlyByteBuf);

                            VanishManager.setVanished(serverPlayer.getGameProfile(), serverPlayer.server, true);
                        }

                        return false;
                    }
                }
            }

            return true;
        });

        ServerPlayConnectionEvents.JOIN.register((serverGamePacketListener, packetSender, minecraftServer) -> {
            SaveManager saveManager = SaveManager.getInstance(minecraftServer);

            ServerPlayer serverPlayer = serverGamePacketListener.getPlayer();

            if (isVanishLoaded() && VanishManager.isVanished(serverPlayer)) {
                FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                friendlyByteBuf.writeBoolean(true);
                ServerPlayNetworking.send(serverPlayer, PacketManager.DEATH, friendlyByteBuf);
            }

            ServerShootingStarManager.loadStarToClient(serverPlayer);

            boolean isHardcore = (saveManager.containsKey("Hardcore")) ? saveManager.get("Hardcore", Boolean.class) : false;

            FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
            friendlyByteBuf.writeBoolean(isHardcore);
            ServerPlayNetworking.send(serverPlayer, PacketManager.HARDCORE, friendlyByteBuf);
        });

        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            SaveManager saveManager = SaveManager.getInstance(server);

            if (!ServerCache.repulsionPoints.isEmpty()) {
                List<ServerPlayer> players = server.getPlayerList().getPlayers();
                for (int i = 0; i < players.size(); i++) {
                    ServerPlayer serverPlayer = players.get(i);
                    Vec3 playerPos = serverPlayer.position();

                    for (Vec3 vec3 : ServerCache.repulsionPoints) {
                        double distance = playerPos.distanceTo(vec3);

                        if (distance < 8) {
                            Vec3 direction = playerPos.subtract(vec3).normalize();

                            double forceMagnitude = Math.pow((8 - distance) / 8, 2);

                            double pushStrength = forceMagnitude * 10;

                            Vec3 pushForce = direction.multiply(new Vec3(pushStrength, pushStrength, pushStrength));

                            serverPlayer.hurtMarked = true;
                            serverPlayer.addDeltaMovement(pushForce);
                        }
                    }
                }
            }

            if (playerBeingRevived != null) {
                Vec3 center = saveManager.get("center", BlockPos.class).getCenter();
                playerBeingRevived.teleportTo(playerBeingRevived.server.overworld(), center.x, center.y + 25, center.z, 0, 0);

                if (System.currentTimeMillis() >= reviveAtTime) {
                    reviveTargetPlayer();
                }
            }

            if (isVanishLoaded()) {
                if (shouldPlayRareCreepySound()) {
                    List<ServerPlayer> players = server.getPlayerList().getPlayers();
                    for (int i = 0; i < players.size(); i++) {
                        ServerPlayer serverPlayer = players.get(i);

                        if (VanishManager.isVanished(serverPlayer)) {
                            int finalI = i;

                            new Thread(() -> {
                                try {
                                    Thread.sleep(new Random().nextInt(1000 * finalI));
                                    server.execute(() -> {
                                        serverPlayer.level().playSound(null, serverPlayer.blockPosition(), ForgottenSounds.getRandomWhisper(), SoundSource.PLAYERS, 0.25f, 1);
                                    });
                                } catch (InterruptedException ignored) {}
                            }).start();
                        }
                    }
                }

                if (ticksLeft > 0) {
                    ticksLeft--;
                    return;
                }

                ticksLeft = 10;

                if (saveManager.containsKey("center") && saveManager.get("Hardcore", Boolean.class)) {
                    applyVanishEffects(server);
                }
            }
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((livingEntity, damageSource, amount) -> {
            if (damageSource.getEntity() instanceof ServerPlayer attacker) {
                if (isVanishLoaded() && VanishManager.isVanished(attacker)) {
                    if (shouldPlayCreepySound()) {
                        livingEntity.level().playSound(null, attacker.blockPosition(), ForgottenSounds.getRandomWhisper(), SoundSource.PLAYERS, 0.25f, 1);
                    }

                    return false;
                }
            }

            return true;
        });

        UseBlockCallback.EVENT.register((player, level, interactionHand, blockHitResult) -> {
            if (!level.isClientSide() && isVanishLoaded() && VanishManager.isVanished(player)) {
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, level, interactionHand, entity, hitResult) -> {
            if (!level.isClientSide() && isVanishLoaded() && VanishManager.isVanished(player)) {
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        if (isVanishLoaded()) {
            VanishEvents.VANISH_EVENT.register((serverPlayer, isVanished) -> {
                if (!isVanished) {
                    FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                    friendlyByteBuf.writeBoolean(false);
                    ServerPlayNetworking.send(serverPlayer, PacketManager.DEATH, friendlyByteBuf);
                }
            });
        }
    }

    public static void resetCoreAnimation(MinecraftServer server) {
        SaveManager.getInstance(server).save("MainCoreFallAnimation", false);
    }

    public static boolean isSmallCoreLeft(MinecraftServer server) {
        SaveManager saveManager = SaveManager.getInstance(server);
        return !saveManager.containsKey("SmallCoresDefeated") || saveManager.get("SmallCoresDefeated", Integer.class) < 4;
    }

    private static boolean shouldPlayRareCreepySound() {
        Random random = new Random();
        int randomNumber = random.nextInt(3000);
        return randomNumber < 1;
    }

    private static boolean shouldPlayCreepySound() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000);
        return randomNumber < 8;
    }

    public static void startMainRitual(ServerLevel serverLevel) {
        SaveManager saveManager = SaveManager.getInstance(serverLevel.getServer());

        if (!saveManager.get("Hardcore", Boolean.class) && saveManager.containsKey("center")) {
            saveManager.save("Hardcore", true);

            ServerCache.addRepulsionPointIfPossible(saveManager.get("center", BlockPos.class).getCenter());

            for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
                if (!saveManager.containsKey("center")) {
                    throw new RuntimeException("Forgotten has not been initialized correctly");
                }

                FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                friendlyByteBuf.writeBlockPos(saveManager.get("center", BlockPos.class).offset(0, -8, 0));
                ServerPlayNetworking.send(serverPlayer, PacketManager.MAIN_RITUAL, friendlyByteBuf);
            }

            new Thread(() -> {
                try {
                    Thread.sleep(28000);
                } catch (Exception ignored) {}

                serverLevel.setBlock(saveManager.get("center", BlockPos.class), ForgottenBlocks.ALTAR.defaultBlockState(), 3);

                try {
                    Thread.sleep(2000);
                } catch (Exception ignored) {}

                ServerCache.removeRepulsionPointIfPossible(saveManager.get("center", BlockPos.class).getCenter());
            }).start();
        }
    }

    private static void placePlayerSoul(ServerPlayer victim) {
        Level level = victim.serverLevel();

        ItemStack playerSoulItem = new ItemStack(ForgottenItems.PLAYER_SOUL);
        CompoundTag tag = playerSoulItem.getOrCreateTag();
        tag.putString(PlayerSoul.MAIN_TAG, victim.getName().getString());

        GameProfile profile = victim.getGameProfile();
        tag.put(PlayerSoul.MAIN_TAG, NbtUtils.writeGameProfile(new CompoundTag(), profile));

        ItemEntity droppedItem = new ItemEntity(
                level,
                victim.getX(),
                victim.getY(),
                victim.getZ(),
                playerSoulItem
        );

        droppedItem.setDefaultPickUpDelay();

        level.addFreshEntity(droppedItem);
    }

    private static void placePlayerHead(ServerPlayer serverPlayer) {
        ServerLevel level = serverPlayer.serverLevel();
        BlockPos deathPos = serverPlayer.blockPosition();

        BlockPos headPos = findValidHeadPosition(level, deathPos);

        if (headPos != null) {
            level.setBlockAndUpdate(headPos, Blocks.PLAYER_HEAD.defaultBlockState());
            BlockEntity blockEntity = level.getBlockEntity(headPos);
            if (blockEntity instanceof SkullBlockEntity skullBlockEntity) {
                skullBlockEntity.setOwner(serverPlayer.getGameProfile());
            }
        } else {
            if (deathPos.getY() <= -64) {
                deathPos = new BlockPos(deathPos.getX(), -63, deathPos.getZ());
            }

            if (deathPos.getY() >= 319) {
                deathPos = new BlockPos(deathPos.getX(), 318, deathPos.getZ());
            }

            level.setBlockAndUpdate(deathPos, Blocks.PLAYER_HEAD.defaultBlockState());
            BlockEntity blockEntity = level.getBlockEntity(deathPos);
            if (blockEntity instanceof SkullBlockEntity skullBlockEntity) {
                skullBlockEntity.setOwner(serverPlayer.getGameProfile());
            }
        }
    }

    private static BlockPos findValidHeadPosition(ServerLevel level, BlockPos initialPos) {
        BlockPos.MutableBlockPos pos = initialPos.mutable();

        for (int i = 0; i < 256; i++) {
            pos.move(Direction.DOWN);
            if (canPlaceHead(level, pos)) {
                pos.move(Direction.UP);
                return pos.immutable();
            }
        }

        pos.set(initialPos);
        for (int i = 0; i < 256; i++) {
            pos.move(Direction.UP);
            if (canPlaceHead(level, pos)) {
                pos.move(Direction.UP);
                return pos.immutable();
            }
        }

        return null;
    }

    private static boolean canPlaceHead(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockState aboveState = level.getBlockState(pos.above());

        return state.isSolidRender(level, pos) && (aboveState.isAir() || !aboveState.isSolidRender(level, pos));
    }

    private boolean isPlayerGoingToUseTotem(ServerPlayer serverPlayer) {
        return serverPlayer.getOffhandItem().is(Items.TOTEM_OF_UNDYING) || serverPlayer.getMainHandItem().is(Items.TOTEM_OF_UNDYING);
    }

    private void applyVanishEffects(MinecraftServer server) {
        List<ServerPlayer> players = server.getPlayerList().getPlayers();

        for (int i = 0; i < players.size(); i++) {
            ServerPlayer serverPlayer = players.get(i);

            if (VanishManager.isVanished(serverPlayer)) {
                serverPlayer.setGameMode(GameType.ADVENTURE);
                regeneratePlayer(serverPlayer);
            } else {
                serverPlayer.setGameMode(GameType.SURVIVAL);
            }
        }
    }

    private void regeneratePlayer(ServerPlayer serverPlayer) {
        serverPlayer.setHealth(20.0F);
        serverPlayer.getFoodData().setFoodLevel(20);
    }

    public static boolean isVanishLoaded() {
        boolean result = FabricLoader.getInstance().isModLoaded("melius-vanish");

        if (result) {
            ConfigOverride.override();
        }

        return result;
    }

    public static boolean isVoiceChatLoaded() {
        return FabricLoader.getInstance().isModLoaded("voicechat");
    }

    public static boolean isVanishLoadedAndVanished(ServerPlayer serverPlayer) {
        if (!isVanishLoaded()) {
            return false;
        }

        ConfigOverride.override();

        return VanishManager.isVanished(serverPlayer);
    }

    public static boolean canExecuteRevivalRitual() {
        return playerBeingRevived == null;
    }

    public static void setRevivalRitualTarget(ServerPlayer serverPlayer) {
        playerBeingRevived = serverPlayer;
        reviveAtTime = System.currentTimeMillis() + 2000;
    }

    public static void reviveTargetPlayer() {
        if (isVanishLoaded()) {
            VanishManager.setVanished(playerBeingRevived.getGameProfile(), playerBeingRevived.server, false);

            MobEffectInstance slowFalling = new MobEffectInstance(
                    MobEffects.SLOW_FALLING,
                    20 * 20,
                    0,
                    false,
                    false
            );

            playerBeingRevived.addEffect(slowFalling);
        }

        playerBeingRevived = null;
    }
}
