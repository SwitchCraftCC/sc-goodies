package io.sc3.goodies.client.ironchest

import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelPartBuilder.create
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.ModelTransform.pivot
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.ironstorage.IronChestBlock
import io.sc3.goodies.ironstorage.IronChestBlockEntity
import io.sc3.goodies.ironstorage.IronStorageVariant
import kotlin.math.pow

class IronChestBlockEntityRenderer(private val block: IronChestBlock) : BlockEntityRenderer<IronChestBlockEntity> {
  private val variant by block::variant

  private val defaultState by lazy {
    block.defaultState.with(IronChestBlock.facing, Direction.SOUTH)
  }

  override fun render(entity: IronChestBlockEntity, tickDelta: Float, matrices: MatrixStack,
                      vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
    val world = entity.world
    val state = if (world != null) entity.cachedState else defaultState
    val facing = state.get(IronChestBlock.facing)

    val progress = entity.getAnimationProgress(tickDelta)

    renderChest(matrices, facing, progress, vertexConsumers, variant, light, overlay)
  }

  companion object {
    private fun easeOutCubic(x: Float) = 1.0f - (1.0f - x).pow(3)

    private val textures = IronStorageVariant.values()
      .associateWith { ModId("textures/entity/chest/${it.chestId}.png") }

    private val modelData by lazy {
      val model = ModelData()
      val root = model.root

      root.addChild("bottom", create().uv(0, 19).cuboid(1.0f, 0.0f, 1.0f, 14.0f, 10.0f, 14.0f), ModelTransform.NONE)
      root.addChild("lid", create().uv(0, 0).cuboid(1.0f, 0.0f, 0.0f, 14.0f, 5.0f, 14.0f), pivot(0.0f, 9.0f, 1.0f))
      root.addChild("lock", create().uv(0, 0).cuboid(7.0f, -2.0f, 14.0f, 2.0f, 4.0f, 1.0f), pivot(0.0f, 9.0f, 1.0f))

      TexturedModelData.of(model, 64, 64)
    }

    private val part: ModelPart = modelData.createModel()
    private val base: ModelPart = part.getChild("bottom")
    private val lid: ModelPart = part.getChild("lid")
    private val latch: ModelPart = part.getChild("lock")

    fun renderChest(matrices: MatrixStack, facing: Direction, animationProgress: Float,
                    vertexConsumers: VertexConsumerProvider, variant: IronStorageVariant,
                    light: Int, overlay: Int) {
      val texture = textures[variant] ?: return

      matrices.push()

      matrices.translate(0.5, 0.5, 0.5)
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()))
      matrices.translate(-0.5, -0.5, -0.5)

      val progress = easeOutCubic(animationProgress)
      lid.pitch = -(progress * (Math.PI / 2).toFloat())
      latch.pitch = lid.pitch

      val consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture))
      base.render(matrices, consumer, light, overlay)
      lid.render(matrices, consumer, light, overlay)
      latch.render(matrices, consumer, light, overlay)

      matrices.pop()
    }
  }
}
