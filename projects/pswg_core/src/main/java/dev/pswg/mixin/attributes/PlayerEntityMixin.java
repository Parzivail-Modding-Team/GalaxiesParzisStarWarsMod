package dev.pswg.mixin.attributes;

import dev.pswg.attributes.GalaxiesEntityAttributes;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Appends our custom attributes to the player's attribute builder
 */
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin
{
	@Inject(method = "createPlayerAttributes", at = @At("RETURN"))
	private static void modifyPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir)
	{
		DefaultAttributeContainer.Builder builder = cir.getReturnValue();
		builder.add(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM);
	}
}
