package dev.pswg.mixin.client.attributes;

import dev.pswg.attributes.GalaxiesEntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemAttributeModifiers;

@Mixin(ItemAttributeModifiers.Display.Default.class)
public abstract class AttributeModifiersComponent$Display$DefaultMixin
{
	/**
	 * Replaces the default attribute text for some custom
	 * attributes with custom formatting
	 */
	@Inject(method = "apply(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V", at = @At("HEAD"), cancellable = true)
	public void addTooltip(Consumer<Component> textConsumer, Player player, Holder<Attribute> attribute, AttributeModifier modifier, CallbackInfo ci)
	{
		if (attribute.is(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM_ID))
		{
			var d = modifier.amount();

			textConsumer.accept(Component.translatable(GalaxiesEntityAttributes.I18N_ATTR_MULTIPLIER, ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(d), Component.translatable(attribute.value().getDescriptionId())).withStyle(attribute.value().getStyle(true)));

			ci.cancel();
		}
	}
}
