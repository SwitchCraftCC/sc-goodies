package io.sc3.goodies.mixin.client;

import io.sc3.goodies.client.elytra.BaseElytraFeatureRenderer;
import io.sc3.goodies.shark.BaseSharkItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantConditions")
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<
  AbstractClientPlayerEntity,
  PlayerEntityModel<AbstractClientPlayerEntity>
> {
  @SuppressWarnings("ConstantConditions")
  public PlayerEntityRendererMixin() { super(null, null, 0);}

  @Inject(method = "<init>", at = @At("TAIL"))
  public void init(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
    // Add the Elytra feature renderer to the player renderer
    addFeature(new BaseElytraFeatureRenderer((PlayerEntityRenderer) (Object) this, ctx.getModelLoader()));
  }
  
  @Inject(
    method = "getArmPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;",
    at = @At("TAIL"),
    cancellable = true
  )
  private static void posePlayerForShark(
    AbstractClientPlayerEntity player,
    Hand hand,
    CallbackInfoReturnable<ArmPose> ci
  ) {
    // If the player is holding a shark, switch them to the crossbow holding pose
    var handItem = player.getStackInHand(hand);
    if (handItem.getItem() instanceof BaseSharkItem) {
      ci.setReturnValue(ArmPose.CROSSBOW_HOLD);
      ci.cancel();
    }
  }
}
