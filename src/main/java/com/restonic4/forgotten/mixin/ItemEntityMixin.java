package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.item.PlayerSoul;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Final @Shadow private static EntityDataAccessor<ItemStack> DATA_ITEM;
    @Shadow private int health;
    @Shadow private int age;
    @Shadow private int pickupDelay;
    @Shadow private UUID thrower;
    @Shadow private UUID target;

    @Shadow public ItemStack getItem() { return null; };
    @Shadow public void setItem(ItemStack itemStack) { };


    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    public void playerTouch(Player player, CallbackInfo ci) {
        ItemEntity current = (ItemEntity) (Object) this;

        if (isSoul(current)) {
            ci.cancel();
        }
    }

    @Inject(method = "getSpin", at = @At("HEAD"), cancellable = true)
    public void getSpin(float f, CallbackInfoReturnable<Float> cir) {
        ItemEntity current = (ItemEntity) (Object) this;

        if (isSoul(current)) {
            cir.setReturnValue(0f);
            cir.cancel();
        }
    }

    @Inject(method = "getVisualRotationYInDegrees", at = @At("HEAD"), cancellable = true)
    public void getVisualRotationYInDegrees(CallbackInfoReturnable<Float> cir) {
        ItemEntity current = (ItemEntity) (Object) this;

        if (isSoul(current)) {
            cir.setReturnValue(0f);
            cir.cancel();
        }
    }

    @Unique
    public boolean isSoul(Entity entity) {
        return entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof PlayerSoul playerSoul;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
}
