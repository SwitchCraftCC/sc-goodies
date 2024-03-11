package io.sc3.goodies.mixin.client;

import io.sc3.goodies.shark.BaseSharkItem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin {
  @Shadow
  public @Final ModelPart leftArm;
  @Shadow
  public @Final ModelPart rightArm;
  
  @Inject(
    method = { "positionRightArm", "positionLeftArm" },
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/render/entity/model/CrossbowPosing;hold(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Z)V",
      shift = At.Shift.AFTER
    ),
    cancellable = true
  )
  public void positionArmsForShark(LivingEntity entity, CallbackInfo ci) {
    // Adjust the player's arms when holding a Shark. Based on the Blåhaj Mod by hibi, licensed under The Unlicense.
    if (entity.getMainHandStack().getItem() instanceof BaseSharkItem
      || entity.getOffHandStack().getItem() instanceof BaseSharkItem) {
      this.leftArm.pitch  = -0.90f;
      this.leftArm.yaw    = (float) (Math.PI / 8f);
      this.rightArm.pitch = -0.95f;
      this.rightArm.yaw   = (float) (-Math.PI / 8f);
      ci.cancel();
    }
  }
}
