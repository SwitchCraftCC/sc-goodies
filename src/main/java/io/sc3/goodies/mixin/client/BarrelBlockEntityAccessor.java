package io.sc3.goodies.mixin.client;

import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BarrelBlockEntity.class)
public interface BarrelBlockEntityAccessor {
  @Accessor
  ViewerCountManager getStateManager();
}
