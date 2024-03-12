package io.sc3.goodies.enderstorage

import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.context.CommandContext
import io.sc3.text.hover
import io.sc3.text.of
import io.sc3.text.pagination.Pagination
import io.sc3.text.plus
import io.sc3.text.runCommand
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Formatting.GREEN
import net.minecraft.util.Formatting.YELLOW

class EnderStorageLocateCommand(target: EnderStorageTargetType) : EnderStorageBaseCommand(target) {
  override fun run(ctx: CommandContext<ServerCommandSource>): Int {
    val (inv, frequency) = getInventory(ctx)

    val locations = inv.snapshotBlockEntities().map {
      val world = it.world?.registryKey?.value.toString()
      val p = it.pos
      val text = of("${p.x}, ${p.y}, ${p.z}", YELLOW) + of(" in ", GREEN) +
        of(world.replace("minecraft:", ""), YELLOW)

      text
        .hover(of("Click to teleport to this location", GREEN))
        .runCommand("/tppos ${p.x} ${p.y} ${p.z} $world")
    }

    val title = of("Locations of $frequency", GREEN)

    Pagination(locations, title, padding = padding).sendTo(ctx.source)
    return SINGLE_SUCCESS
  }

  companion object {
    private val padding = of("-", GREEN)
  }
}
