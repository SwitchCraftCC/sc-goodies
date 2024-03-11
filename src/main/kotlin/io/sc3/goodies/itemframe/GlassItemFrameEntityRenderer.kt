package io.sc3.goodies.itemframe

import dan200.computercraft.client.ClientHooks
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.library.ext.ItemFrameEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BannerBlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.model.ModelLoader
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.BannerItem
import net.minecraft.item.FilledMapItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ShieldItem
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d

private const val ITEM_RENDER_SCALE = 1.5f

class GlassItemFrameEntityRenderer(
  ctx: EntityRendererFactory.Context
) : EntityRenderer<GlassItemFrameEntity>(ctx) {
  private val mc = MinecraftClient.getInstance()
  private val blockRenderManager = ctx.blockRenderManager
  private val itemRenderer = ctx.itemRenderer
  private val mapRenderer = mc.gameRenderer.mapRenderer

  // Dummy banner entity for rendering banner patterns in item frames
  private val bannerEntity = BannerBlockEntity(BlockPos.ORIGIN, Blocks.WHITE_BANNER.defaultState)
  private val bannerModel = ctx.getPart(EntityModelLayers.BANNER).getChild("flag")

  override fun getTexture(entity: GlassItemFrameEntity): Identifier =
    SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE

  override fun render(entity: GlassItemFrameEntity, yaw: Float, tickDelta: Float, matrices: MatrixStack,
                      consumers: VertexConsumerProvider, originalLight: Int) {
    super.render(entity, yaw, tickDelta, matrices, consumers, originalLight)

    matrices.push()

    val facing = entity.horizontalFacing
    val offset = getPositionOffset(entity, tickDelta)

    matrices.translate(-offset.x, -offset.y, -offset.z)
    matrices.translate(facing.offsetX * 0.46875, facing.offsetY * 0.46875, facing.offsetZ * 0.46875)
    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.pitch))
    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - entity.yaw))

    val stack = entity.heldItemStack
    val light = if (entity.dataTracker.get(GlassItemFrameEntity.isGlowingFrame)) 0xF000F0 else originalLight

    // Glass Item Frame background model
    if (stack.isEmpty) {
      matrices.push()
      matrices.translate(-0.5, -0.5, -0.5)

      val model = blockRenderManager.models.modelManager.getModel(modelId)
      val buffer = consumers.getBuffer(TexturedRenderLayers.getEntityCutout())
      blockRenderManager.modelRenderer.render(matrices.peek(), buffer, null, model,
        1.0f, 1.0f, 1.0f, light, OverlayTexture.DEFAULT_UV)

      matrices.pop()
    } else {
      // Item in Item Frame
      renderItemStack(entity, matrices, consumers, light, stack)
    }

    matrices.pop()
  }

  private fun renderItemStack(entity: GlassItemFrameEntity, matrices: MatrixStack, consumers: VertexConsumerProvider,
                              light: Int, stack: ItemStack) {
    matrices.push()

    val mapId = entity.mapId
    val mapState = if (mapId.isPresent) FilledMapItem.getMapState(mapId.asInt, entity.world) else null
    val rotation = if (mapState != null) entity.rotation % 4 * 2 else entity.rotation
    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation * 360.0f / 8.0f))

    matrices.translate(0.0f, 0.0f, 0.5f)

    // Allow ComputerCraft to render printed pages
    if (ccLoaded && ClientHooks.onRenderItemFrame(matrices, consumers, entity, stack, light)) {
      // TODO: Apply correct translation for printouts here
      matrices.pop() // CC pops here in its ItemFrameRendererMixin
      return
    }

    // Allow sc-library mods to render custom items
    if (ItemFrameEvents.ITEM_RENDER.invoker().invoke(entity, stack, matrices, consumers, light)) {
      matrices.pop()
      return
    }

    if (mapId.isPresent) {
      matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f))
      matrices.scale(0.0078125f, 0.0078125f, 0.0078125f)
      matrices.translate(-64.0, -64.0, 0.0)
      matrices.translate(0.0, 0.0, -1.0)

      if (mapState != null) {
        mapRenderer.draw(matrices, consumers, mapId.asInt, mapState, true, light)
      }
    } else {
      var scale = ITEM_RENDER_SCALE
      if (stack.item is BannerItem) {
        bannerEntity.readFrom(stack)
        val patterns = bannerEntity.patterns

        matrices.push()
        matrices.translate(0.0001f, -0.5001f, 0.05f)
        matrices.scale(0.799999f, 0.399999f, 0.5f)
        BannerBlockEntityRenderer.renderCanvas(matrices, consumers, light, OverlayTexture.DEFAULT_UV, bannerModel,
          ModelLoader.BANNER_BASE, true, patterns)
        matrices.pop()
      } else {
        if (stack.item is ShieldItem) {
          scale *= 2.6666667f
          matrices.translate(-0.25f, 0.0f, 0.0f)
          matrices.scale(scale, scale, scale)
        } else {
          matrices.translate(0.0f, 0.0f, -0.025f)
          matrices.scale(scale, scale, scale)
        }

        matrices.scale(0.5f, 0.5f, 0.5f)
        itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices,
          consumers, entity.world, entity.id)
      }
    }

    matrices.pop()
  }

  override fun getPositionOffset(entity: GlassItemFrameEntity, tickDelta: Float): Vec3d = Vec3d(
    (entity.horizontalFacing.offsetX.toFloat() * 0.3f).toDouble(),
    -0.25,
    (entity.horizontalFacing.offsetZ.toFloat() * 0.3f).toDouble()
  )

  override fun hasLabel(entity: GlassItemFrameEntity) =
    if (MinecraftClient.isHudEnabled()
      && !entity.heldItemStack.isEmpty
      && entity.heldItemStack.hasCustomName()
      && dispatcher.targetedEntity === entity
    ) {
      val d = dispatcher.getSquaredDistanceToCamera(entity)
      val f = if (entity.isSneaky) 32.0f else 64.0f
      d < (f * f).toDouble()
    } else {
      false
    }

  override fun renderLabelIfPresent(entity: GlassItemFrameEntity, text: Text, matrices: MatrixStack,
                                    consumers: VertexConsumerProvider, light: Int) {
    super.renderLabelIfPresent(entity, entity.heldItemStack.name, matrices, consumers, light)
  }

  companion object {
    val modelId = ModelIdentifier(ModId("glass_item_frame_back"), "inventory")
    val ccLoaded by lazy { FabricLoader.getInstance().isModLoaded("computercraft") }
  }
}
