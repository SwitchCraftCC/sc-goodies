package pw.switchcraft.goodies.client.ironchest

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.DyeColor
import net.minecraft.util.math.Direction
import pw.switchcraft.goodies.client.ironchest.IronShulkerBlockEntityRenderer.Companion.renderShulker
import pw.switchcraft.goodies.ironchest.IronChestVariant

class IronShulkerItemRenderer(
  private val variant: IronChestVariant,
  private val dyeColor: DyeColor?
) : DynamicItemRenderer {
  override fun render(stack: ItemStack, mode: ModelTransformation.Mode, matrices: MatrixStack,
                      vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
    renderShulker(variant, dyeColor, matrices, Direction.UP, 0.0f, vertexConsumers, light, overlay)
  }
}
