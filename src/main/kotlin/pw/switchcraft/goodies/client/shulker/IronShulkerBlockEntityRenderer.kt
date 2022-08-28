package pw.switchcraft.goodies.client.shulker

import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.DyeColor
import net.minecraft.util.math.Direction
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.chest.IronChestVariant
import pw.switchcraft.goodies.shulker.IronShulkerBlock.Companion.facing
import pw.switchcraft.goodies.shulker.IronShulkerBlockEntity

class IronShulkerBlockEntityRenderer(
  private val variant: IronChestVariant,
  ctx: BlockEntityRendererFactory.Context
) : BlockEntityRenderer<IronShulkerBlockEntity> {
  private val undyedTexture = ModId("textures/entity/shulker/${variant.shulkerId}.png")
  private val dyedTextures = DyeColor.values()
    .associateWith { ModId("textures/entity/shulker/${variant.shulkerId}_${it.getName()}.png") }

  private val model: ModelPart = ctx.getLayerModelPart(EntityModelLayers.SHULKER)
  private val lid: ModelPart = model.getChild("lid")

  override fun render(entity: IronShulkerBlockEntity, tickDelta: Float, matrices: MatrixStack,
                      vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
    val world = entity.world
    val facing = if (world != null) {
      entity.cachedState.get(facing)
    } else {
      Direction.UP
    }

    val color = entity.cachedColor
    val texture = (if (color != null) dyedTextures[color] else undyedTexture) ?: undyedTexture

    matrices.push()

    matrices.translate(0.5, 0.5, 0.5)
    matrices.scale(0.9995f, 0.9995f, 0.9995f)
    matrices.multiply(facing.rotationQuaternion)
    matrices.scale(1.0f, -1.0f, -1.0f)
    matrices.translate(0.0, -1.0, 0.0)

    lid.setPivot(0.0f, 24.0f - entity.getAnimationProgress(tickDelta) * 0.5f * 16.0f, 0.0f)
    lid.yaw = 270.0f * entity.getAnimationProgress(tickDelta) * (Math.PI / 180.0f).toFloat()

    val consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture))
    model.render(matrices, consumer, light, overlay)

    matrices.pop()
  }
}
