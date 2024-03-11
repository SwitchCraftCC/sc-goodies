package io.sc3.goodies.client.itemmagnet

import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.client.TrinketRenderer
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.itemmagnet.ItemMagnetItem
import io.sc3.goodies.mixin.client.PlayerEntityModelAccessor
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexConsumers
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

object ItemMagnetTrinketRenderer : TrinketRenderer {
  private val texture = ModId("textures/entity/item_magnet.png")

  private val model by lazy { ItemMagnetModel() }

  override fun render(stack: ItemStack, slotReference: SlotReference, contextModel: EntityModel<out LivingEntity>,
                      matrices: MatrixStack, provider: VertexConsumerProvider, light: Int, entity: LivingEntity,
                      limbAngle: Float, limbDistance: Float, tickDelta: Float, animationProgress: Float, headYaw: Float,
                      headPitch: Float) {
    model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch)
    model.animateModel(entity, limbAngle, limbDistance, tickDelta)
    TrinketRenderer.followBodyRotations(entity, model)

    matrices.push()

    if (contextModel.isThinArms) {
      matrices.scale(3.0f / 4.0f, 1.0f, 1.0f)
      matrices.translate(-1.5 / 16.0, 0.0, 0.0)
    }

    val enabled = ItemMagnetItem.stackEnabled(stack)

    val consumer = if (enabled) {
      VertexConsumers.union(
        provider.getBuffer(RenderLayer.getEntityGlint()),
        provider.getBuffer(model.getLayer(texture))
      )
    } else {
      provider.getBuffer(model.getLayer(texture))
    }

    model.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f)

    matrices.pop()
  }

  private val EntityModel<out LivingEntity>.isThinArms: Boolean
    get() = this is PlayerEntityModel<*> && (this as PlayerEntityModelAccessor).isThinArms
}
