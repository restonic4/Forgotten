package com.restonic4.forgotten.commdands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.restonic4.forgotten.registries.client.ForgottenLodestoneWorldParticleRenderType;
import com.restonic4.forgotten.registries.client.ForgottenRenderTypes;
import com.restonic4.forgotten.registries.client.ForgottenShaderHolders;
import com.restonic4.forgotten.util.trash.TestingVars;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Lodestone {
    private static final List<String> TYPES = Arrays.asList("render_type", "render_target", "far_plane", "time", "rotX", "rotY", "rotZ");

    private static final List<String> RENDER_TYPES = Arrays.asList(
            "ADDITIVE", "TRANSPARENT", "LUMITRANSPARENT",
            "TERRAIN_SHEET", "ADDITIVE_TERRAIN_SHEET",
            "VANILLA_TERRAIN_SHEET", "VANILLA_PARTICLE_SHEET_OPAQUE",
            "VANILLA_PARTICLE_SHEET_TRANSLUCENT",
            "VANILLA_PARTICLE_SHEET_LIT",
            "VANILLA_CUSTOM", "VANILLA_NO_RENDER", "CUSTOM"
    );
    private static final List<String> RENDER_TARGETS = Arrays.asList(
            "DELAYED_RENDER", "LATE_DELAYED_RENDER"
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("lodestone_utils")
                        .requires(source -> source.hasPermission(2))
                        .then(
                                Commands.argument("type", StringArgumentType.string())
                                        .suggests(Lodestone::suggestTypes)
                                        .then(
                                                Commands.argument("value", StringArgumentType.string())
                                                        .suggests(Lodestone::suggestValues)
                                                        .executes(Lodestone::execute)
                                        )
                        )
        );
    }

    private static CompletableFuture<Suggestions> suggestTypes(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        TYPES.forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestValues(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        String type = StringArgumentType.getString(context, "type");
        if (type.equals("render_type")) {
            RENDER_TYPES.forEach(builder::suggest);
        } else if (type.equals("render_target")) {
            RENDER_TARGETS.forEach(builder::suggest);
        }
        return builder.buildFuture();
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        String type = StringArgumentType.getString(context, "type");
        String value = StringArgumentType.getString(context, "value");

        switch (type) {
            case "render_type":
                executeRenderType(value);
                context.getSource().sendSuccess(() -> Component.literal("Setting render type to: " + value), false);
                break;
            case "render_target":
                executeRenderTarget(value);
                context.getSource().sendSuccess(() -> Component.literal("Setting render target to: " + value), false);
                break;
            case "far_plane":
                TestingVars.FAR_PLANE = Float.parseFloat(value);
                context.getSource().sendSuccess(() -> Component.literal("Setting far plane to: " + value), false);
                break;
            case "time":
                Minecraft.getInstance().execute(() -> {
                    ForgottenShaderHolders.SKY_WAVE.getInstance().get().safeGetUniform("Time").set(Integer.parseInt(value));
                });
                break;
            case "rotX":
                Minecraft.getInstance().execute(() -> {
                    TestingVars.ROTX = Float.parseFloat(value);
                });
                break;
            case "rotY":
                Minecraft.getInstance().execute(() -> {
                    TestingVars.ROTY = Float.parseFloat(value);
                });
                break;
            case "rotZ":
                Minecraft.getInstance().execute(() -> {
                    TestingVars.ROTZ = Float.parseFloat(value);
                });
                break;
            default:
                context.getSource().sendFailure(Component.literal("Unknown type: " + type));
        }

        return 1;
    }

    private static void executeRenderType(String value) {
        switch (value) {
            case "ADDITIVE":
                TestingVars.renderType = LodestoneWorldParticleRenderType.ADDITIVE;
                break;
            case "TRANSPARENT":
                TestingVars.renderType = LodestoneWorldParticleRenderType.TRANSPARENT;
                break;
            case "LUMITRANSPARENT":
                TestingVars.renderType = LodestoneWorldParticleRenderType.LUMITRANSPARENT;
                break;
            case "TERRAIN_SHEET":
                TestingVars.renderType = LodestoneWorldParticleRenderType.TERRAIN_SHEET;
                break;
            case "ADDITIVE_TERRAIN_SHEET":
                TestingVars.renderType = LodestoneWorldParticleRenderType.ADDITIVE_TERRAIN_SHEET;
                break;
            case "VANILLA_TERRAIN_SHEET":
                TestingVars.renderType = ParticleRenderType.TERRAIN_SHEET;
                break;
            case "VANILLA_PARTICLE_SHEET_OPAQUE":
                TestingVars.renderType = ParticleRenderType.PARTICLE_SHEET_OPAQUE;
                break;
            case "VANILLA_PARTICLE_SHEET_TRANSLUCENT":
                TestingVars.renderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
                break;
            case "VANILLA_PARTICLE_SHEET_LIT":
                TestingVars.renderType = ParticleRenderType.PARTICLE_SHEET_LIT;
                break;
            case "VANILLA_CUSTOM":
                TestingVars.renderType = ParticleRenderType.CUSTOM;
                break;
            case "VANILLA_NO_RENDER":
                TestingVars.renderType = ParticleRenderType.NO_RENDER;
                break;
            case "CUSTOM":
                TestingVars.renderType = ForgottenLodestoneWorldParticleRenderType.LUMITRANSPARENT_NO_FOG;
                break;
        }
    }

    private static void executeRenderTarget(String value) {
        switch (value) {
            case "DELAYED_RENDER":
                TestingVars.renderTarget = RenderHandler.DELAYED_RENDER;
                break;
            case "LATE_DELAYED_RENDER":
                TestingVars.renderTarget = RenderHandler.LATE_DELAYED_RENDER;
                break;
        }
    }

}
