package io.sc3.goodies.client.ironchest

import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelPartBuilder.create
import net.minecraft.client.model.ModelTransform.pivot
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.entity.model.EntityModelPartNames.HEAD
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.DyeColor
import net.minecraft.util.math.Direction
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.ironchest.IronChestVariant
import io.sc3.goodies.ironshulker.IronShulkerBlock.Companion.facing
import io.sc3.goodies.ironshulker.IronShulkerBlockEntity

class IronShulkerBlockEntityRenderer(
  private val variant: IronChestVariant
) : BlockEntityRenderer<IronShulkerBlockEntity> {
  override fun render(entity: IronShulkerBlockEntity, tickDelta: Float, matrices: MatrixStack,
                      vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
    val world = entity.world
    val facing = if (world != null) {
      entity.cachedState.get(facing)
    } else {
      Direction.UP
    }

    val color = entity.cachedColor
    val progress = entity.getAnimationProgress(tickDelta)
    
    renderShulker(variant, color, matrices, facing, progress, vertexConsumers, light, overlay)
  }

  companion object {
    private val undyedTextures = IronChestVariant.values()
      .associateWith { ModId("textures/entity/shulker/${it.shulkerId}.png") }
    
    private val dyedTextures = IronChestVariant.values().associateWith { variant ->
      DyeColor.values().associateWith { ModId("textures/entity/shulker/${variant.shulkerId}_${it.getName()}.png") }
    }
    
    private val modelData by lazy {
      val model = ModelData()
      val root = model.root

      root.addChild("lid", create().uv(0, 0).cuboid(-8.0f, -16.0f, -8.0f, 16.0f, 12.0f, 16.0f), pivot(0.0f, 24.0f, 0.0f))
      root.addChild("base", create().uv(0, 28).cuboid(-8.0f, -8.0f, -8.0f, 16.0f, 8.0f, 16.0f), pivot(0.0f, 24.0f, 0.0f))
      root.addChild(HEAD, create().uv(0, 52).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 6.0f, 6.0f), pivot(0.0f, 12.0f, 0.0f))

      TexturedModelData.of(model, 64, 64)
    }

    private val part: ModelPart = modelData.createModel()
    private val lid: ModelPart = part.getChild("lid")
    
    fun renderShulker(variant: IronChestVariant, color: DyeColor?, matrices: MatrixStack,
                              facing: Direction, animationProgress: Float, vertexConsumers: VertexConsumerProvider,
                              light: Int, overlay: Int) {
      val texture = (if (color != null) dyedTextures[variant]!![color] else null) ?: undyedTextures[variant]!!

      matrices.push()

      matrices.translate(0.5, 0.5, 0.5)
      matrices.scale(0.9995f, 0.9995f, 0.9995f)
      matrices.multiply(facing.rotationQuaternion)
      matrices.scale(1.0f, -1.0f, -1.0f)
      matrices.translate(0.0, -1.0, 0.0)

      lid.setPivot(0.0f, 24.0f - animationProgress * 0.5f * 16.0f, 0.0f)
      lid.yaw = 270.0f * animationProgress * (Math.PI / 180.0f).toFloat()

      val consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture))
      part.render(matrices, consumer, light, overlay)

      matrices.pop()
    }
  }
}
