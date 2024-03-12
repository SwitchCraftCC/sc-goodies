package io.sc3.goodies.glue;

import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public interface EndermiteEntityOwner {
  @Nullable
  default UUID sc_goodies$getOwner() {
    return null;
  }

  default void sc_goodies$setOwner(@Nullable UUID owner) {}
}
