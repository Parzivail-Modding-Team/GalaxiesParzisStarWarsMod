package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.client.species.SwgSpeciesRenderer;
import com.parzivail.pswg.component.PlayerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
@Environment(EnvType.CLIENT)
public class AbstractClientPlayerEntityMixin
{
	@Unique
	private Identifier lastKnownSpeciesTexture;

	@Inject(method = "getSkinTexture", at = @At(value = "HEAD"), cancellable = true)
	private void getSkinTexture(CallbackInfoReturnable<Identifier> cir)
	{
		var pc = PlayerData.getPersistentPublic((PlayerEntity)(Object)this);

		var species = pc.getCharacter();
		if (species == null)
			return;

		var speciesTexture = SwgSpeciesRenderer.getTexture((PlayerEntity)(Object)this, species);

		if (speciesTexture.equals(Client.TEX_TRANSPARENT))
		{
			// Current species texture isn't ready, use the last one if it isn't null
			if (lastKnownSpeciesTexture != null)
			{
				cir.setReturnValue(lastKnownSpeciesTexture);
				return;
			}

			// If the last texture is null, allow the current one to use the fallback
		}

		lastKnownSpeciesTexture = speciesTexture;
		cir.setReturnValue(speciesTexture);
		cir.cancel();
	}
}
