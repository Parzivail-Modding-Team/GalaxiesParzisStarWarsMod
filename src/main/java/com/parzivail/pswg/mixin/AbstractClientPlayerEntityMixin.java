package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.species.SwgSpeciesModels;
import com.parzivail.pswg.component.SwgEntityComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
@Environment(EnvType.CLIENT)
public class AbstractClientPlayerEntityMixin
{
	@Inject(method = "getSkinTexture", at = @At(value = "HEAD"), cancellable = true)
	private void getSkinTexture(CallbackInfoReturnable<Identifier> cir)
	{
		var pc = SwgEntityComponents.getPersistent((PlayerEntity)(Object)this);

		var species = pc.getSpecies();
		if (species == null)
			return;

		cir.setReturnValue(SwgSpeciesModels.getTexture((PlayerEntity)(Object)this, species));
		cir.cancel();
	}
}
