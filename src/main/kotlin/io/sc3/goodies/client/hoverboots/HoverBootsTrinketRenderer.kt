package pw.switchcraft.goodies.client.hoverboots

import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.client.TrinketRenderer
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.DyeColor
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.hoverboots.HoverBootsItem

object HoverBootsTrinketRenderer : TrinketRenderer {
  private val textures = DyeColor.values().associateWith {
    ModId("textures/entity/hover_boots/hover_boots_${it.getName()}.png")
  }

  private val model by lazy { HoverBootsModel() }

  override fun render(stack: ItemStack, slotReference: SlotReference, contextModel: EntityModel<out LivingEntity>,
                      matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, entity: LivingEntity,
                      limbAngle: Float, limbDistance: Float, tickDelta: Float, animationProgress: Float, headYaw: Float,
                      headPitch: Float) {
    val item = stack.item as? HoverBootsItem ?: return
    val color = item.color

    model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch)
    model.animateModel(entity, limbAngle, limbDistance, tickDelta)
    TrinketRenderer.followBodyRotations(entity, model)

    val consumer = vertexConsumers.getBuffer(model.getLayer(textures[color]))
    model.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f)
  }
}
