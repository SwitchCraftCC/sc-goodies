package io.sc3.goodies.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.sc3.goodies.hoverboots.HoverBootsItem;

@SuppressWarnings("ConstantConditions")
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
  @Inject(method = "jump", at = @At("TAIL"))
  private void jump(CallbackInfo ci) {
    HoverBootsItem.onLivingJump((LivingEntity) (Object) this);
  }

  @ModifyVariable(method = "handleFallDamage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
  private float handleFallDamage(float fallDistance) {
    return HoverBootsItem.modifyFallDistance((LivingEntity) (Object) this, fallDistance);
  }
}
