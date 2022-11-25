package pw.switchcraft.goodies.client.ironchest

import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.ironchest.IronChestBlock
import pw.switchcraft.goodies.ironchest.IronChestBlockEntity
import kotlin.math.pow

class IronChestBlockEntityRenderer(
  private val block: IronChestBlock,
  ctx: BlockEntityRendererFactory.Context
) : BlockEntityRenderer<IronChestBlockEntity> {
  private val variant by block::variant
  private val texture = ModId("textures/entity/chest/${variant.chestId}.png")

  private val defaultState by lazy {
    block.defaultState.with(IronChestBlock.facing, Direction.SOUTH)
  }

  private val part: ModelPart = ctx.getLayerModelPart(EntityModelLayers.CHEST)
  private val base: ModelPart = part.getChild("bottom")
  private val lid: ModelPart = part.getChild("lid")
  private val latch: ModelPart = part.getChild("lock")

  override fun render(entity: IronChestBlockEntity, tickDelta: Float, matrices: MatrixStack,
                      vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
    val world = entity.world
    val state = if (world != null) entity.cachedState else defaultState
    val facing = state.get(IronChestBlock.facing)

    matrices.push()

    matrices.translate(0.5, 0.5, 0.5)
    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()))
    matrices.translate(-0.5, -0.5, -0.5)

    val progress = easeOutCubic(entity.getAnimationProgress(tickDelta))
    lid.pitch = -(progress * (Math.PI / 2).toFloat())
    latch.pitch = lid.pitch

    val consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture))
    base.render(matrices, consumer, light, overlay)
    lid.render(matrices, consumer, light, overlay)
    latch.render(matrices, consumer, light, overlay)

    matrices.pop()
  }

  companion object {
    private fun easeOutCubic(x: Float) = 1.0f - (1.0f - x).pow(3)
  }
}
