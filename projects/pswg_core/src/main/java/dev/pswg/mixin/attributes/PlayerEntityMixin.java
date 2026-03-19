package dev.pswg.mixin.attributes;

import dev.pswg.attributes.GalaxiesEntityAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Appends our custom attributes to the player's attribute builder
 */
@Mixin(Player.class)
public class PlayerEntityMixin
{
	@Inject(method = "createAttributes", at = @At("RETURN"))
	private static void modifyPlayerAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir)
	{
		AttributeSupplier.Builder builder = cir.getReturnValue();
		builder.add(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM);
	}
}
