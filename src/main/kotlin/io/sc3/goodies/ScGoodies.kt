package pw.switchcraft.goodies

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object ScGoodies : ModInitializer {
  internal val log = LoggerFactory.getLogger("ScGoodies")!!

  internal const val modId = "sc-goodies"
  internal fun ModId(value: String) = Identifier(modId, value)

  override fun onInitialize() {
    log.info("sc-goodies initializing")

    Registration.init()
  }
}
