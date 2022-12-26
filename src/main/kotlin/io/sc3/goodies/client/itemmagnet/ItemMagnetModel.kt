package io.sc3.goodies.client.itemmagnet

import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelPartBuilder.create
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.ModelTransform.pivot
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.client.render.entity.model.EntityModelPartNames.RIGHT_ARM
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Identifier


class ItemMagnetModel(
  renderLayerFactory: (Identifier) -> RenderLayer = RenderLayer::getEntityCutout,
) : BipedEntityModel<LivingEntity>(createModelData().createModel(), renderLayerFactory) {
  init {
    setVisible(false)
    rightArm.visible = true
  }

  companion object {
    private fun createModelData(): TexturedModelData {
      val modelData = getModelData(Dilation.NONE, 0.0f)
      val root = modelData.root

      val arm = root.addChild(RIGHT_ARM, create(), pivot(0.0f, 24.0f, 0.0f))

      arm.addChild(
        "magnet",
        create()
          .uv(0, 6).cuboid(2.0f, 7.0f, -2.0f, 1.0f, 2.0f, 6.0f, Dilation(0.0f))
          .uv(0, 3).cuboid(-3.0f, 7.0f, 3.0f, 5.0f, 2.0f, 1.0f, Dilation(0.0f))
          .uv(0, 0).cuboid(-3.0f, 7.0f, -2.0f, 5.0f, 2.0f, 1.0f, Dilation(0.0f)),
        ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, Math.toRadians(270.0).toFloat(), 0.0f)
      )

      return TexturedModelData.of(modelData, 16, 16)
    }
  }
}
