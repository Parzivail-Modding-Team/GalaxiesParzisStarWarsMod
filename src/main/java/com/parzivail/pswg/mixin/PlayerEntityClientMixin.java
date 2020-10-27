package com.parzivail.pswg.mixin;

import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.pswg.component.SwgPersistentComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
@Environment(EnvType.CLIENT)
public class PlayerEntityClientMixin
{
	@Inject(method = "isPartVisible", at = @At("HEAD"), cancellable = true)
	private <T extends Entity> void getRenderer(PlayerModelPart modelPart, CallbackInfoReturnable<Boolean> cir)
	{
		if (modelPart != PlayerModelPart.LEFT_PANTS_LEG && modelPart != PlayerModelPart.RIGHT_PANTS_LEG && modelPart != PlayerModelPart.LEFT_SLEEVE && modelPart != PlayerModelPart.RIGHT_SLEEVE && modelPart != PlayerModelPart.JACKET && modelPart != PlayerModelPart.HAT)
			return;

		SwgPersistentComponents pc = SwgEntityComponents.getPersistent((PlayerEntity)(Object)this);

		Identifier species = pc.getSpecies();
		if (species == null)
			return;

		cir.setReturnValue(false);
		cir.cancel();
	}
}
