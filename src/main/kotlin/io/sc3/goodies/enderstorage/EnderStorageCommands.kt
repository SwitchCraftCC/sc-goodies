package io.sc3.goodies.enderstorage

import io.sc3.goodies.util.dyeArg
import io.sc3.goodies.util.userArg
import me.lucko.fabric.api.permissions.v0.Permissions.require
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager.literal

object EnderStorageCommands {
  internal fun register() {
    CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
      dispatcher.register(literal("enderstorage")
        .then(literal("public")
          .requires(require("sc-goodies.enderstorage.view.public", 3))
          .then(dyeArg("left").then(dyeArg("middle").then(dyeArg("right")
            .then(literal("view")
              .executes(EnderStorageViewCommand(false)))
            .then(literal("locate")
              .executes(EnderStorageLocateCommand(false)))))))
        .then(literal("private")
          .requires(require("sc-goodies.enderstorage.view.private", 3))
          .then(userArg("user")
            .then(dyeArg("left").then(dyeArg("middle").then(dyeArg("right")
              .then(literal("view")
                .executes(EnderStorageViewCommand(true)))
              .then(literal("locate")
                .executes(EnderStorageLocateCommand(true))))))))
      )
    }
  }
}
