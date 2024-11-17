package com.restonic4.forgotten;

import com.restonic4.forgotten.compatibility.voicechat.Plugin;
import com.restonic4.forgotten.networking.PacketManager;
import me.drex.vanish.api.VanishEvents;
import me.drex.vanish.config.ConfigManager;
import me.drex.vanish.util.VanishManager;
import net.fabricmc.api.ModInitializer;
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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Forgotten implements ModInitializer {
    public static final String MOD_ID = "forgotten";
    public static SoundEvent DEATH_SOUND;
    public static SoundEvent WHISPER1;
    public static SoundEvent WHISPER2;
    public static SoundEvent WHISPER3;
    public static SoundEvent WHISPER4;
    public static SoundEvent WHISPER5;
    public static SoundEvent WHISPER6;
    public static SoundEvent WHISPER7;
    public static SoundEvent WHISPER8;

    int ticksLeft = 0;

    @Override
    public void onInitialize() {
        registerSounds();
        registerEvents();
    }

    private void registerSounds() {
        ResourceLocation deathLocation = new ResourceLocation(MOD_ID, "death");
        DEATH_SOUND = Registry.register(BuiltInRegistries.SOUND_EVENT, deathLocation, SoundEvent.createVariableRangeEvent(deathLocation));

        WHISPER1 = registerWhisper(1);
        WHISPER2 = registerWhisper(2);
        WHISPER3 = registerWhisper(3);
        WHISPER4 = registerWhisper(4);
        WHISPER5 = registerWhisper(5);
        WHISPER6 = registerWhisper(6);
        WHISPER7 = registerWhisper(7);
        WHISPER8 = registerWhisper(8);
    }

    private SoundEvent registerWhisper(int number) {
        ResourceLocation whisperLocation = new ResourceLocation(MOD_ID, "whisper" + number);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, whisperLocation, SoundEvent.createVariableRangeEvent(whisperLocation));
    }

    private void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            if (isVanishLoaded()) {
                ConfigManager.vanish().sendJoinDisconnectMessage = false;
                server.getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES).set(false, server);
            }
        });

        ServerLivingEntityEvents.ALLOW_DEATH.register((livingEntity, damageSource, damageAmount) -> {
            if (livingEntity instanceof ServerPlayer serverPlayer && isVanishLoaded() && !isPlayerGoingToUseTotem(serverPlayer)) {
                regeneratePlayer(serverPlayer);

                if (!VanishManager.isVanished(serverPlayer)) {
                    placePlayerHead(serverPlayer);
                    serverPlayer.getInventory().dropAll();

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
        });

        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            if (!isVanishLoaded()) {
                return;
            }

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
                                    serverPlayer.level().playLocalSound(serverPlayer.blockPosition(), getRandomWhisper(), SoundSource.PLAYERS, 1, 1, false);
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
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((livingEntity, damageSource, amount) -> {
            if (damageSource.getEntity() instanceof ServerPlayer attacker) {
                if (isVanishLoaded() && VanishManager.isVanished(attacker)) {
                    if (shouldPlayCreepySound()) {
                        livingEntity.level().playSound(null, attacker.blockPosition(), getRandomWhisper(), SoundSource.PLAYERS);
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

    public static SoundEvent getRandomWhisper() {
        List<SoundEvent> whispers = Arrays.asList(
                WHISPER1,
                WHISPER2,
                WHISPER3,
                WHISPER4,
                WHISPER5,
                WHISPER6,
                WHISPER7,
                WHISPER8
        );

        int randomIndex = new Random().nextInt(whispers.size());
        return whispers.get(randomIndex);
    }

    private static boolean shouldPlayRareCreepySound() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000);
        return randomNumber < 5;
    }

    private static boolean shouldPlayCreepySound() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000);
        return randomNumber < 5;
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
}
