package io.sc3.goodies.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.sc3.goodies.client.misc.ConcreteSpeedupHandler;

@SuppressWarnings("ConstantConditions")
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
  @Inject(
    method = "tick",
    at = @At(value="INVOKE", target="Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V")
  )
  private void tick(CallbackInfo ci) {
    ClientPlayerEntity cp = (ClientPlayerEntity) (Object) this;
    ConcreteSpeedupHandler.playerTick(cp);
  }
}
