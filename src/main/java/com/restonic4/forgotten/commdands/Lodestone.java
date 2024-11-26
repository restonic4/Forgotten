package com.restonic4.forgotten.commdands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.restonic4.forgotten.registries.client.CustomRenderTypes;
import com.restonic4.forgotten.util.LodestoneVars;
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
                LodestoneVars.FAR_PLANE = Float.parseFloat(value);
                context.getSource().sendSuccess(() -> Component.literal("Setting far plane to: " + value), false);
                break;
            case "time":
                Minecraft.getInstance().execute(() -> {
                    CustomRenderTypes.WAVE_SHADER.getInstance().get().safeGetUniform("Time").set(Integer.parseInt(value));
                });
                break;
            case "rotX":
                Minecraft.getInstance().execute(() -> {
                    LodestoneVars.ROTX = Float.parseFloat(value);
                });
                break;
            case "rotY":
                Minecraft.getInstance().execute(() -> {
                    LodestoneVars.ROTY = Float.parseFloat(value);
                });
                break;
            case "rotZ":
                Minecraft.getInstance().execute(() -> {
                    LodestoneVars.ROTZ = Float.parseFloat(value);
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
                LodestoneVars.renderType = LodestoneWorldParticleRenderType.ADDITIVE;
                break;
            case "TRANSPARENT":
                LodestoneVars.renderType = LodestoneWorldParticleRenderType.TRANSPARENT;
                break;
            case "LUMITRANSPARENT":
                LodestoneVars.renderType = LodestoneWorldParticleRenderType.LUMITRANSPARENT;
                break;
            case "TERRAIN_SHEET":
                LodestoneVars.renderType = LodestoneWorldParticleRenderType.TERRAIN_SHEET;
                break;
            case "ADDITIVE_TERRAIN_SHEET":
                LodestoneVars.renderType = LodestoneWorldParticleRenderType.ADDITIVE_TERRAIN_SHEET;
                break;
            case "VANILLA_TERRAIN_SHEET":
                LodestoneVars.renderType = ParticleRenderType.TERRAIN_SHEET;
                break;
            case "VANILLA_PARTICLE_SHEET_OPAQUE":
                LodestoneVars.renderType = ParticleRenderType.PARTICLE_SHEET_OPAQUE;
                break;
            case "VANILLA_PARTICLE_SHEET_TRANSLUCENT":
                LodestoneVars.renderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
                break;
            case "VANILLA_PARTICLE_SHEET_LIT":
                LodestoneVars.renderType = ParticleRenderType.PARTICLE_SHEET_LIT;
                break;
            case "VANILLA_CUSTOM":
                LodestoneVars.renderType = ParticleRenderType.CUSTOM;
                break;
            case "VANILLA_NO_RENDER":
                LodestoneVars.renderType = ParticleRenderType.NO_RENDER;
                break;
            case "CUSTOM":
                LodestoneVars.renderType = CustomRenderTypes.particleType;
                break;
        }
    }

    private static void executeRenderTarget(String value) {
        switch (value) {
            case "DELAYED_RENDER":
                LodestoneVars.renderTarget = RenderHandler.DELAYED_RENDER;
                break;
            case "LATE_DELAYED_RENDER":
                LodestoneVars.renderTarget = RenderHandler.LATE_DELAYED_RENDER;
                break;
        }
    }

}
