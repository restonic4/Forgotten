package com.restonic4.forgotten;

import com.mojang.authlib.GameProfile;
import com.restonic4.forgotten.commdands.*;
import com.restonic4.forgotten.compatibility.voicechat.Plugin;
import com.restonic4.forgotten.entity.common.ChainEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import com.restonic4.forgotten.item.PlayerSoul;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.registries.common.*;
import com.restonic4.forgotten.saving.JsonDataManager;
import com.restonic4.forgotten.util.GriefingPrevention;
import com.restonic4.forgotten.util.ServerCache;
import io.github.fabricators_of_create.porting_lib.event.client.InteractEvents;
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
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class Forgotten implements ModInitializer {
    public static final String MOD_ID = "forgotten";

    private static final JsonDataManager dataManager = new JsonDataManager();
    int ticksLeft = 0;
    int tickSaveCounter = 0;

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
    }

    private void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(dataManager::loadFromDisk);

        ServerLifecycleEvents.SERVER_STOPPING.register(dataManager::saveToDisk);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TestDeath.register(dispatcher);
            TestBeam.register(dispatcher);
            TestMainRitual.register(dispatcher);
            SetUpForgotten.register(dispatcher);
            CleanupForgotten.register(dispatcher);
            KillOne.register(dispatcher);
            //Lodestone.register(dispatcher);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickSaveCounter++;
            if (tickSaveCounter >= 3000) {
                dataManager.saveToDisk(server);
                tickSaveCounter = 0;
            }

            if (!isSmallCoreLeft() && ServerCache.getMainCore() != null && !dataManager.getBoolean("MainCoreFallAnimation")) {
                dataManager.save("MainCoreFallAnimation", true);

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
            if (isVanishLoaded()) {
                ConfigManager.vanish().sendJoinDisconnectMessage = false;
                server.getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES).set(false, server);
            }
        });

        ServerLivingEntityEvents.ALLOW_DEATH.register((livingEntity, damageSource, damageAmount) -> {
            if (livingEntity instanceof ServerPlayer serverPlayer && isVanishLoaded() && !isPlayerGoingToUseTotem(serverPlayer) && dataManager.getBoolean("Hardcore")) {
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

            return true;
        });

        ServerPlayConnectionEvents.JOIN.register((serverGamePacketListener, packetSender, minecraftServer) -> {
            ServerPlayer serverPlayer = serverGamePacketListener.getPlayer();

            if (isVanishLoaded() && VanishManager.isVanished(serverPlayer)) {
                FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                friendlyByteBuf.writeBoolean(true);
                ServerPlayNetworking.send(serverPlayer, PacketManager.DEATH, friendlyByteBuf);
            }

            FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
            friendlyByteBuf.writeBoolean(dataManager.getBoolean("Hardcore"));
            ServerPlayNetworking.send(serverPlayer, PacketManager.HARDCORE, friendlyByteBuf);
        });

        ServerTickEvents.START_SERVER_TICK.register((server) -> {
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

                applyVanishEffects(server);
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

    public static void resetCoreAnimation() {
        dataManager.save("MainCoreFallAnimation", false);
    }

    public static boolean isSmallCoreLeft() {
        JsonDataManager dataManager = Forgotten.getDataManager();

        return dataManager.getInt("SmallCoresDefeated") < 4;
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
        if (!dataManager.getBoolean("Hardcore")) {
            dataManager.save("Hardcore", true);

            ServerCache.addRepulsionPointIfPossible(dataManager.getBlockPos("center").getCenter());

            for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
                JsonDataManager dataManager = Forgotten.getDataManager();

                if (!dataManager.contains("center")) {
                    throw new RuntimeException("Forgotten has not been initialized correctly");
                }

                FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                friendlyByteBuf.writeBlockPos(dataManager.getBlockPos("center").offset(0, -8, 0));
                ServerPlayNetworking.send(serverPlayer, PacketManager.MAIN_RITUAL, friendlyByteBuf);
            }

            new Thread(() -> {
                try {
                    Thread.sleep(28000);
                } catch (Exception ignored) {}

                serverLevel.setBlock(dataManager.getBlockPos("center"), ForgottenBlocks.ALTAR.defaultBlockState(), 3);

                try {
                    Thread.sleep(2000);
                } catch (Exception ignored) {}

                ServerCache.removeRepulsionPointIfPossible(dataManager.getBlockPos("center").getCenter());
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
        return FabricLoader.getInstance().isModLoaded("melius-vanish");
    }

    public static boolean isVoiceChatLoaded() {
        return FabricLoader.getInstance().isModLoaded("voicechat");
    }

    public static JsonDataManager getDataManager() {
        return dataManager;
    }

    public static boolean isVanishLoadedAndVanished(ServerPlayer serverPlayer) {
        if (!isVanishLoaded()) {
            return false;
        }

        return VanishManager.isVanished(serverPlayer);
    }
}
