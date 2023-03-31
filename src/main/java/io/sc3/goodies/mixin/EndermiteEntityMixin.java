package io.sc3.goodies.mixin;

import io.sc3.goodies.ScGoodies;
import io.sc3.goodies.glue.EndermiteEntityOwner;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.jetbrains.annotations.Nullable;
import java.util.UUID;

import static io.sc3.goodies.ScGoodies.modId;

@Mixin(EndermiteEntity.class)
public class EndermiteEntityMixin implements EndermiteEntityOwner {
  private static final String OWNER_KEY = new Identifier(modId, "endermite_owner").toString();

  @Unique
  @Nullable
  public UUID owner;

  @Override
  @Nullable
  public UUID getOwner() {
    return owner;
  }

  @Override
  public void setOwner(@Nullable UUID owner) {
    this.owner = owner;
  }

  @Inject(
    method = "writeCustomDataToNbt",
    at = @At("TAIL")
  )
  public void saveEndermiteOwner(NbtCompound nbt, CallbackInfo ci) {
    try {
      if (owner != null) {
        nbt.putUuid(OWNER_KEY, owner);
      } else if (nbt.contains(OWNER_KEY)) {
        nbt.remove(OWNER_KEY);
      }
    } catch (Exception e) {
      ScGoodies.log.error("Could not save endermite owner", e);
    }
  }

  @Inject(
    method = "readCustomDataFromNbt",
    at = @At("TAIL")
  )
  public void readEndermiteOwner(NbtCompound nbt, CallbackInfo ci) {
    try {
      owner = nbt.containsUuid(OWNER_KEY) ? nbt.getUuid(OWNER_KEY) : null;
    } catch (Exception e) {
      ScGoodies.log.error("Could not read endermite owner", e);
    }
  }
}
