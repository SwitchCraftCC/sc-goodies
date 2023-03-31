package io.sc3.goodies.glue;

import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public interface EndermiteEntityOwner {
  @Nullable
  default UUID getOwner() {
    return null;
  }

  default void setOwner(@Nullable UUID owner) {}
}
