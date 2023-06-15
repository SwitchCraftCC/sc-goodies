package io.sc3.goodies.client.ironchest

import io.sc3.goodies.client.ironchest.IronChestBlockEntityRenderer.Companion.renderChest
import io.sc3.goodies.ironstorage.IronStorageVariant
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction

class IronChestItemRenderer(private val variant: IronStorageVariant) : DynamicItemRenderer {
  override fun render(stack: ItemStack, mode: ModelTransformationMode, matrices: MatrixStack,
                      vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
    renderChest(matrices, Direction.NORTH, 0.0f, vertexConsumers, variant, light, overlay)
  }
}
