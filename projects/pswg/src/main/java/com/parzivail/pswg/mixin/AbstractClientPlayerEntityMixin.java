package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.mixinaccessor.AbstractClientPlayerEntityAccessor;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin implements AbstractClientPlayerEntityAccessor
{
	@Unique
	private Identifier lastKnownSpeciesTexture;

	@Override
	public Identifier pswg$getSpeciesTexture(Identifier current)
	{
		if (current.equals(Client.TEX_TRANSPARENT))
		{
			// Current species texture isn't ready, use the last one if it isn't null
			if (lastKnownSpeciesTexture != null)
			{
				return lastKnownSpeciesTexture;
			}

			// If the last texture is null, allow the current one to use the fallback
		}

		lastKnownSpeciesTexture = current;
		return current;
	}
}
