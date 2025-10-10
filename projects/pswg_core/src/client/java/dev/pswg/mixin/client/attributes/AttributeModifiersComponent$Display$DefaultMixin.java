package dev.pswg.mixin.client.attributes;

import dev.pswg.attributes.GalaxiesEntityAttributes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(AttributeModifiersComponent.Display.Default.class)
public abstract class AttributeModifiersComponent$Display$DefaultMixin
{
	/**
	 * Replaces the default attribute text for some custom
	 * attributes with custom formatting
	 */
	@Inject(method = "Lnet/minecraft/component/type/AttributeModifiersComponent$Display$Default;addTooltip(Ljava/util/function/Consumer;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/entity/attribute/EntityAttributeModifier;)V", at = @At("HEAD"), cancellable = true)
	public void addTooltip(Consumer<Text> textConsumer, PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, CallbackInfo ci)
	{
		if (attribute.matchesId(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM_ID))
		{
			var d = modifier.value();

			textConsumer.accept(Text.translatable(GalaxiesEntityAttributes.I18N_ATTR_MULTIPLIER, AttributeModifiersComponent.DECIMAL_FORMAT.format(d), Text.translatable(attribute.value().getTranslationKey())).formatted(attribute.value().getFormatting(true)));

			ci.cancel();
		}
	}
}
