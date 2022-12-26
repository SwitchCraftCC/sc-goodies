package io.sc3.goodies.client.hoverboots

import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelPartBuilder.create
import net.minecraft.client.model.ModelTransform.pivot
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.client.render.entity.model.EntityModelPartNames.LEFT_LEG
import net.minecraft.client.render.entity.model.EntityModelPartNames.RIGHT_LEG
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Identifier

class HoverBootsModel(
  renderLayerFactory: (Identifier) -> RenderLayer = RenderLayer::getEntityCutout,
) : BipedEntityModel<LivingEntity>(createModelData().createModel(), renderLayerFactory) {
  init {
    setVisible(false)
    leftLeg.visible = true
    rightLeg.visible = true
  }

  companion object {
    private fun createModelData(): TexturedModelData {
      val modelData = getModelData(Dilation.NONE, 0.0f)
      val root = modelData.root

      root.addChild(
        RIGHT_LEG,
        create()
          .uv(0, 7).cuboid(-3.0f, 11.5f, -3.0f, 6.0f, 1.0f, 6.0f, Dilation(0.0f))
          .uv(0, 0).cuboid(-6.0f, 11.5f, 2.0f, 6.0f, 1.0f, 6.0f, Dilation(0.0f))
          .uv(0, 0).cuboid(-6.0f, 11.5f, -8.0f, 6.0f, 1.0f, 6.0f, Dilation(0.0f)),
        pivot(-2.0f, 24.0f, 0.0f)
      )

      root.addChild(
        LEFT_LEG,
        create()
          .uv(0, 7).cuboid(-3.0f, 11.5f, -3.0f, 6.0f, 1.0f, 6.0f, Dilation(0.0f))
          .uv(0, 0).cuboid(0.0f, 11.5f, 2.0f, 6.0f, 1.0f, 6.0f, Dilation(0.0f))
          .uv(0, 0).cuboid(0.0f, 11.5f, -8.0f, 6.0f, 1.0f, 6.0f, Dilation(0.0f)),
        pivot(2.0f, 24.0f, 0.0f)
      )

      return TexturedModelData.of(modelData, 32, 16)
    }
  }
}
