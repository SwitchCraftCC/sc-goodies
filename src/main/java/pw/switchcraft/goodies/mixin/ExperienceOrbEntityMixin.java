package pw.switchcraft.goodies.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.switchcraft.goodies.itemmagnet.ItemMagnetState;

@SuppressWarnings("ConstantConditions")
@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin {
  @Shadow
  protected abstract int repairPlayerGears(PlayerEntity player, int amount);

  @Inject(
    method = "repairPlayerGears",
    at = @At(value = "RETURN", ordinal = 2),
    cancellable = true
  )
  private void repairPlayerGears(PlayerEntity player, int amount, CallbackInfoReturnable<Integer> cir) {
    int retAmount = ItemMagnetState.consumeXpOrb(((ExperienceOrbEntity) (Object) this), player, amount);
    // cir.setReturnValue(retAmount > 0 ? repairPlayerGears(player, retAmount) : 0);
    cir.setReturnValue(retAmount);
  }
}
