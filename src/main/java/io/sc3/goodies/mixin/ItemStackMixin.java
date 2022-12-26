package io.sc3.goodies.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import io.sc3.goodies.Registration.ModItems;
import io.sc3.goodies.ScGoodies;

import java.util.List;

import static net.minecraft.text.Text.translatable;

@SuppressWarnings("ConstantConditions")
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
  @Redirect(
    method = "getTooltip",
    at = @At(value="INVOKE", target="Lnet/minecraft/item/ItemStack;isDamaged()Z")
  )
  private boolean isDamaged(ItemStack stack) {
    return !stack.isOf(ModItems.INSTANCE.getItemMagnet()) && stack.isDamaged();
  }

  @Inject(
    method = "getTooltip",
    at = @At(value="INVOKE", target="Lnet/minecraft/item/ItemStack;isDamaged()Z"),
    locals = LocalCapture.CAPTURE_FAILHARD
  )
  private void addMagnetChargeTooltip(@Nullable PlayerEntity player, TooltipContext context,
                                      CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
    ItemStack stack = (ItemStack) (Object) this;
    if (stack.isOf(ModItems.INSTANCE.getItemMagnet())) {
      int charge = stack.getMaxDamage() - stack.getDamage();
      int max = stack.getMaxDamage();
      list.add(translatable("item." + ScGoodies.modId + ".item_magnet.charge", charge, max));
    }
  }
}
