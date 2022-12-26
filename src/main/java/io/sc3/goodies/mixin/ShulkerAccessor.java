package pw.switchcraft.goodies.mixin;

import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ShulkerEntity.class)
public interface ShulkerAccessor {
  @Invoker
  void invokeSetAttachedFace(Direction direction);
}
