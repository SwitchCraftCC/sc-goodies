package io.sc3.goodies.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import io.sc3.goodies.util.AnvilEvents;

@SuppressWarnings("ConstantConditions")
@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
  @Shadow private String newItemName;

  @Shadow @Final private Property levelCost;

  @Inject(
    method="updateResult",
    at=@At(value="INVOKE", target="Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal=2),
    cancellable = true,
    locals = LocalCapture.CAPTURE_FAILHARD
  )
  private void updateResult(CallbackInfo ci, ItemStack itemStack, int i, int baseCost, int k, ItemStack itemStack2,
                            ItemStack itemStack3) {
    AnvilScreenHandler handler = (AnvilScreenHandler) (Object) this;
    ForgingScreenHandlerAccessor forging = (ForgingScreenHandlerAccessor) this;

    if (!AnvilEvents.CHANGE.invoker().invoke(
      handler, itemStack, itemStack3, forging.getOutput(), newItemName, baseCost, forging.getPlayer(), levelCost)
    ) {
      ci.cancel();
    }
  }
}
