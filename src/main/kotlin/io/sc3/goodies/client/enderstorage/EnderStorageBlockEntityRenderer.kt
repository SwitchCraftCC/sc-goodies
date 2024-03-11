package io.sc3.goodies.client.enderstorage

import io.sc3.goodies.Registration.ModBlocks
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.enderstorage.EnderStorageBlock
import io.sc3.goodies.enderstorage.EnderStorageBlockEntity
import io.sc3.goodies.enderstorage.Frequency
import net.minecraft.client.model.*
import net.minecraft.client.model.ModelPartBuilder.create
import net.minecraft.client.model.ModelTransform.pivot
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis
import kotlin.math.pow

class EnderStorageBlockEntityRenderer(
  ctx: BlockEntityRendererFactory.Context
) : BlockEntityRenderer<EnderStorageBlockEntity> {
  override fun render(entity: EnderStorageBlockEntity, tickDelta: Float, matrices: MatrixStack,
                      vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
    val world = entity.world
    val state = if (world != null) entity.cachedState else defaultState
    val facing = state.get(EnderStorageBlock.facing)

    val frequency = entity.frequency
    val computerChangesEnabled = entity.computerChangesEnabled
    val progress = easeOutCubic(entity.getAnimationProgress(tickDelta))

    renderChest(matrices, vertexConsumers, facing, progress, frequency, computerChangesEnabled, light, overlay)
  }

  companion object {
    private val texture = ModId("textures/entity/chest/ender_storage.png")
    private val layer = RenderLayer.getEntityCutout(texture)
    private val wool = DyeColor.values().associateWith {
      Identifier("textures/block/${it.getName()}_wool.png")
    }

    private val defaultState by lazy {
      ModBlocks.enderStorage.defaultState.with(EnderStorageBlock.facing, Direction.SOUTH)
    }

    private val modelData by lazy {
      val model = ModelData()
      val root = model.root

      root.addChild("bottom", create().uv(0, 19).cuboid(1.0f, 0.0f, 1.0f, 14.0f, 10.0f, 14.0f), ModelTransform.NONE)
      root.addChild("lid", create().uv(0, 0).cuboid(1.0f, 0.0f, 0.0f, 14.0f, 5.0f, 14.0f), pivot(0.0f, 9.0f, 1.0f))

      val latchPivot = pivot(0.0f, 8.0f, 0.0f)
      root.addChild("latch_normal", makeLatch(0, 0), latchPivot)
      root.addChild("latch_personal", makeLatch(6, 0), latchPivot)
      root.addChild("latch_automated", makeLatch(0, 5), latchPivot)

      val buttonPivot = pivot(0.0f, 9.0f, 1.0f)
      for (i in 0 until 3) {
        root.addChild("button_$i", makeButton(i), buttonPivot)
      }

      TexturedModelData.of(model, 64, 64)
    }

    private val part: ModelPart = modelData.createModel()
    private val base: ModelPart = part.getChild("bottom")
    private val lid: ModelPart = part.getChild("lid")
    private val latchNormal: ModelPart = part.getChild("latch_normal")
    private val latchPersonal: ModelPart = part.getChild("latch_personal")
    private val latchAutomated: ModelPart = part.getChild("latch_automated")
    private val buttons: List<ModelPart> = (0 until 3).map { part.getChild("button_$it") }.toList()

    private fun makeLatch(u: Int, v: Int) =
      create().uv(u, v).cuboid(7.0f, -1.0f, 15.0f, 2.0f, 4.0f, 1.0f)

    private fun makeButton(i: Int) =
      create().uv(1 + (i * 2), 1).cuboid(4.0f + (i * 3.0f), 5.0f, 5.0f, 2.0f, 1.0f, 4.0f, Dilation.NONE, 0.25f, 0.25f)

    private fun easeOutCubic(x: Float) = 1.0f - (1.0f - x).pow(3)

    fun renderChest(matrices: MatrixStack,
                    vertexConsumers: VertexConsumerProvider,
                    facing: Direction, progress: Float,
                    frequency: Frequency,
                    computerChangesEnabled: Boolean, light: Int, overlay: Int) {
      matrices.push()

      matrices.translate(0.5, 0.5, 0.5)
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()))
      matrices.translate(-0.5, -0.5, -0.5)

      // End portal effect
      if (progress >= 0.01f) {
        val portalConsumer = vertexConsumers.getBuffer(RenderLayer.getEndPortal())
        val matrix = matrices.peek().positionMatrix
        portalConsumer.vertex(matrix, 0.1875f, 0.626f, 0.8125f).next()
        portalConsumer.vertex(matrix, 0.8125f, 0.626f, 0.8125f).next()
        portalConsumer.vertex(matrix, 0.8125f, 0.626f, 0.1875f).next()
        portalConsumer.vertex(matrix, 0.1875f, 0.626f, 0.1875f).next()
      }

      // Base chest
      val latch = if (frequency.personal) {
        if (computerChangesEnabled) latchAutomated else latchPersonal
      } else {
        latchNormal
      }

      lid.pitch = -(progress * (Math.PI / 2).toFloat())
      latch.pitch = lid.pitch

      val consumer = vertexConsumers.getBuffer(layer)
      base.render(matrices, consumer, light, overlay)
      lid.render(matrices, consumer, light, overlay)
      latch.render(matrices, consumer, light, overlay)

      // Frequency buttons on top
      for (i in 0 until 3) {
        val button = buttons[i]
        val color = frequency.dyeColor(i)

        val buttonConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(wool[color]))
        button.pitch = lid.pitch
        button.render(matrices, buttonConsumer, light, overlay)
      }

      matrices.pop()
    }
  }
}
