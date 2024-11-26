package com.restonic4.forgotten.registries;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.restonic4.forgotten.registries.client.CustomRenderTypes;
import net.minecraft.client.renderer.RenderType;
import team.lodestar.lodestone.registry.client.LodestoneShaderRegistry;

public class BeamRenderType extends RenderType {
    public BeamRenderType(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
    }

    public static RenderType MY_RENDERTYPE = create("my_rendertype", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 2097152, true, true,
            RenderType.CompositeState.builder()
                    .setShaderState(LodestoneShaderRegistry.LODESTONE_TEXT.getShard())
                    .setCullState(NO_CULL)
                    .createCompositeState(true)
    );
}
