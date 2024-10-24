package dev.pswg.mixin.client;

import dev.pswg.attributes.GalaxiesEntityAttributes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class Mixin$ItemStack$GalaxiesAttributes
{
	@Inject(method = "appendAttributeModifierTooltip(Ljava/util/function/Consumer;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/entity/attribute/EntityAttributeModifier;)V", at = @At("HEAD"), cancellable = true)
	public void appendAttributeModifierTooltip(Consumer<Text> textConsumer, PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, CallbackInfo ci)
	{
		if (attribute.matchesId(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM_ID))
		{
			var d = modifier.value();

			textConsumer.accept(Text.translatable(GalaxiesEntityAttributes.I18N_ATTR_MULTIPLIER, AttributeModifiersComponent.DECIMAL_FORMAT.format(d), Text.translatable(attribute.value().getTranslationKey())).formatted(attribute.value().getFormatting(true)));

			ci.cancel();
		}
	}
}
