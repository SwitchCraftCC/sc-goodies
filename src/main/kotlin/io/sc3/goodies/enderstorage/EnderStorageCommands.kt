package io.sc3.goodies.enderstorage

import com.mojang.brigadier.arguments.StringArgumentType.greedyString
import io.sc3.goodies.enderstorage.EnderStorageTargetType.*
import io.sc3.goodies.util.dyeArg
import io.sc3.goodies.util.userArg
import me.lucko.fabric.api.permissions.v0.Permissions.require
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal

object EnderStorageCommands {
  private const val P = "sc-goodies.enderstorage"

  private const val P_OWN_BASE        = "$P.own.base"
  private const val P_OWN_NAME        = "$P.own.name"
  private const val P_OWN_DESCRIPTION = "$P.own.description"

  private const val P_PUBLIC_BASE        = "$P.public.base"
  private const val P_PUBLIC_VIEW        = "$P.public.view"
  private const val P_PUBLIC_LOCATE      = "$P.public.locate"
  private const val P_PUBLIC_NAME        = "$P.public.name"
  private const val P_PUBLIC_DESCRIPTION = "$P.public.description"

  private const val P_PRIVATE_BASE               = "$P.private.base"
  private const val P_PRIVATE_OTHERS_BASE        = "$P.private.others.base"
  private const val P_PRIVATE_OTHERS_VIEW        = "$P.private.others.view"
  private const val P_PRIVATE_OTHERS_LOCATE      = "$P.private.others.locate"
  private const val P_PRIVATE_OTHERS_NAME        = "$P.private.others.name"
  private const val P_PRIVATE_OTHERS_DESCRIPTION = "$P.private.others.description"

  internal fun register() {
    CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
      dispatcher.register(literal("enderstorage")
        // (Player commands)
        // /enderstorage own ...
        .then(literal("own")
          .requires(require(P_OWN_BASE, 0))
          .then(dyeArg("left").then(dyeArg("middle").then(dyeArg("right")
            // /enderstorage own <left> <middle> <right> name [name]
            .then(literal("name")
              .requires(require(P_OWN_NAME, 0))
              .then(argument("name", greedyString())
                .executes(EnderStorageNameCommand(OWN, clear = false)))
              .executes(EnderStorageNameCommand(OWN, clear = true)))
            // /enderstorage own <left> <middle> <right> description [description]
            .then(literal("description")
              .requires(require(P_OWN_DESCRIPTION, 0))
              .then(argument("description", greedyString())
                .executes(EnderStorageDescriptionCommand(OWN, clear = false)))
              .executes(EnderStorageDescriptionCommand(OWN, clear = true)))))))
        // (Staff commands)
        // /enderstorage public ...
        .then(literal("public")
          .requires(require(P_PUBLIC_BASE, 3))
          .then(dyeArg("left").then(dyeArg("middle").then(dyeArg("right")
            // /enderstorage public <left> <middle> <right> view
            .then(literal("view")
              .requires(require(P_PUBLIC_VIEW, 3))
              .executes(EnderStorageViewCommand(PUBLIC)))
            // /enderstorage public <left> <middle> <right> locate
            .then(literal("locate")
              .requires(require(P_PUBLIC_LOCATE, 3))
              .executes(EnderStorageLocateCommand(PUBLIC)))
            // /enderstorage public <left> <middle> <right> name [name]
            .then(literal("name")
              .requires(require(P_PUBLIC_NAME, 3))
              .then(argument("name", greedyString())
                .executes(EnderStorageNameCommand(PUBLIC, clear = false)))
              .executes(EnderStorageNameCommand(PUBLIC, clear = true)))
            // /enderstorage public <left> <middle> <right> description [description]
            .then(literal("description")
              .requires(require(P_PUBLIC_DESCRIPTION, 3))
              .then(argument("description", greedyString())
                .executes(EnderStorageDescriptionCommand(PUBLIC, clear = false)))
              .executes(EnderStorageDescriptionCommand(PUBLIC, clear = true)))))))
        // /enderstorage private ...
        .then(literal("private")
          .requires(require(P_PRIVATE_BASE, 3))
          .then(userArg("user")
            .requires(require(P_PRIVATE_OTHERS_BASE, 3))
            .then(dyeArg("left").then(dyeArg("middle").then(dyeArg("right")
              // /enderstorage private <left> <middle> <right> view
              .then(literal("view")
                .requires(require(P_PRIVATE_OTHERS_VIEW, 3))
                .executes(EnderStorageViewCommand(PRIVATE)))
              // /enderstorage private <left> <middle> <right> locate
              .then(literal("locate")
                .requires(require(P_PRIVATE_OTHERS_LOCATE, 3))
                .executes(EnderStorageLocateCommand(PRIVATE)))
              // /enderstorage private <left> <middle> <right> name [name]
              .then(literal("name")
                .requires(require(P_PRIVATE_OTHERS_NAME, 3))
                .then(argument("name", greedyString())
                  .executes(EnderStorageNameCommand(PRIVATE, clear = false)))
                .executes(EnderStorageNameCommand(PRIVATE, clear = true)))
              // /enderstorage private <left> <middle> <right> description [description]
              .then(literal("description")
                .requires(require(P_PRIVATE_OTHERS_DESCRIPTION, 3))
                .then(argument("description", greedyString())
                  .executes(EnderStorageDescriptionCommand(PRIVATE, clear = false)))
                .executes(EnderStorageDescriptionCommand(PRIVATE, clear = true)))))))))
    }
  }
}
