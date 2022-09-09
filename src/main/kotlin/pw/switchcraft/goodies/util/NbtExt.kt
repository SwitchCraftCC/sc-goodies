package pw.switchcraft.goodies.util

import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.nbt.NbtCompound

fun NbtCompound.optCompound(key: String): NbtCompound? =
  if (contains(key, NbtType.COMPOUND)) getCompound(key) else null
