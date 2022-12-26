package io.sc3.goodies.client.elytra

import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.ElytraEntityModel
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.entity.model.EntityModelLoader
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.render.item.ItemRenderer.getArmorGlintConsumer
import net.minecraft.client.texture.MissingSprite
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EquipmentSlot
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.elytra.BaseElytraItem
import io.sc3.goodies.elytra.DyedElytraItem
import io.sc3.goodies.elytra.SpecialElytraItem

class BaseElytraFeatureRenderer(
  ctx: FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>,
  loader: EntityModelLoader
) : FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(ctx) {
  private val dyedTextures = DyeColor.values().associateWith {
    ModId("textures/entity/elytra/elytra_${it.getName()}.png")
  }

  private val elytra = ElytraEntityModel<AbstractClientPlayerEntity>(loader.getModelPart(EntityModelLayers.ELYTRA))

  private fun elytraTexture(item: BaseElytraItem): Identifier = when (item) {
    is SpecialElytraItem -> item.type.modelTexture
    is DyedElytraItem -> dyedTextures[item.color]!!
    else -> MissingSprite.getMissingSpriteId()
  }

  override fun render(matrices: MatrixStack, consumers: VertexConsumerProvider, light: Int,
                      entity: AbstractClientPlayerEntity, limbAngle: Float, limbDistance: Float, tickDelta: Float,
                      animationProgress: Float, headYaw: Float, headPitch: Float) {
    val stack = entity.getEquippedStack(EquipmentSlot.CHEST)
    val item = stack.item
    if (item !is BaseElytraItem) return

    val tex = elytraTexture(item)

    matrices.push()
    matrices.translate(0.0, 0.0, 0.125)

    contextModel.copyStateTo(elytra)
    elytra.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch)

    val consumer = getArmorGlintConsumer(consumers, RenderLayer.getArmorCutoutNoCull(tex), false, stack.hasGlint())
    elytra.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f)

    matrices.pop()
  }
}
