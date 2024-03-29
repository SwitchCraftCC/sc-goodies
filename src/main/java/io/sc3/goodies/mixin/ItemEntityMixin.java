package io.sc3.goodies.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import io.sc3.goodies.itemmagnet.ItemMagnetState;

@Mixin(ItemEntity.class)
@SuppressWarnings("ConstantConditions")
public abstract class ItemEntityMixin {
  @Inject(
    method = "onPlayerCollision",
    at = @At(value="INVOKE", target="Lnet/minecraft/entity/player/PlayerEntity;sendPickup(Lnet/minecraft/entity/Entity;I)V"),
    locals = LocalCapture.CAPTURE_FAILEXCEPTION
  )
  public void onPlayerCollision(PlayerEntity player, CallbackInfo ci, ItemStack itemStack, Item item, int i) {
    ItemMagnetState.pickupItem((ItemEntity) (Object) this, player, i);
  }
}
