package com.restonic4.forgotten.mixin.client;

import com.restonic4.forgotten.client.ForgottenClient;
import com.restonic4.forgotten.item.PlayerSoul;
import com.restonic4.forgotten.util.OptimizedSine;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(method = "getPackedLightCoords", at = @At("HEAD"), cancellable = true)
    protected <T extends Entity> void getPackedLightCoords(T entity, float f, CallbackInfoReturnable<Integer> cir) {
        if (isSoul(entity)) {
            cir.setReturnValue(LightTexture.pack(15, 15));
            cir.cancel();
        }
    }

    @Unique
    public boolean isSoul(Entity entity) {
        return entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof PlayerSoul playerSoul;
    }
}
