package io.sc3.goodies.mixin;

import io.sc3.goodies.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
  @Unique
  private boolean isBarrelHammering = false;

  @Inject(
    method = "attack",
    at = @At("HEAD")
  )
  private void onAttack(Entity target, CallbackInfo ci) {
    isBarrelHammering = false;
  }

  @Inject(
    method = "attack",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"
    )
  )
  private void onGetStackInHand(Entity target, CallbackInfo ci) {
    PlayerEntity player = (PlayerEntity) (Object) this;
    isBarrelHammering = player.getStackInHand(Hand.MAIN_HAND).isOf(Registration.ModItems.INSTANCE.getBarrelHammer());
  }

  @Redirect(
    method = "attack",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/damage/DamageSources;playerAttack(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/entity/damage/DamageSource;"
    )
  )
  private net.minecraft.entity.damage.DamageSource redirectPlayerAttack(DamageSources instance, PlayerEntity attacker) {
    if (isBarrelHammering) {
      PlayerEntity player = (PlayerEntity) (Object) this;
      var damageType = player.world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE)
        .entryOf(Registration.ModDamageSources.INSTANCE.getBarrelHammer());
      isBarrelHammering = false;
      return new DamageSource(damageType, player);
    } else {
      return instance.playerAttack(attacker);
    }
  }
}
