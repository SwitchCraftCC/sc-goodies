package io.sc3.goodies.mixin;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.sc3.goodies.dragonscale.DragonScale;

@SuppressWarnings("ConstantConditions")
@Mixin(EnderDragonEntity.class)
public class EnderDragonEntityMixin {
  @Inject(method="updatePostDeath", at=@At("HEAD"))
  private void updatePostDeath(CallbackInfo ci) {
    DragonScale.dragonUpdatePostDeath((EnderDragonEntity) (Object) this);
  }
}
