package io.sc3.goodies.tomes

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import net.minecraft.item.ItemStack
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.function.ConditionalLootFunction
import net.minecraft.loot.function.LootFunctionType

class TomeLootFunction(conditions: Array<out LootCondition>) : ConditionalLootFunction(conditions) {
  override fun process(stack: ItemStack, context: LootContext): ItemStack {
    TomeEnchantments.applyRandomEnchantment(stack, context.random)
    return stack
  }

  override fun getType() = TomeLootFunction.type

  companion object {
    val type = LootFunctionType(Serializer())
  }

  class Serializer : ConditionalLootFunction.Serializer<TomeLootFunction>() {
    override fun fromJson(json: JsonObject, context: JsonDeserializationContext,
                          conditions: Array<out LootCondition>): TomeLootFunction {
      return TomeLootFunction(conditions)
    }
  }
}
