package pw.switchcraft.goodies

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object ScGoodies : ModInitializer {
  val log = LoggerFactory.getLogger("ScGoodies")!!

  val modId = "sc-goodies"
  fun ModId(value: String) = Identifier(modId, value)

  override fun onInitialize() {
    log.info("sc-goodies initializing")

    Registration.init()
  }
}
