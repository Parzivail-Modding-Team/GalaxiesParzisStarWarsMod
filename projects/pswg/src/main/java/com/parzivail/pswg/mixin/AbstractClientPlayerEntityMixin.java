package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.species.SwgSpeciesRenderer;
import com.parzivail.pswg.component.PlayerData;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin
{
	@Shadow
	protected abstract PlayerListEntry getPlayerListEntry();

	@Inject(method = "getSkinTextures()Lnet/minecraft/client/util/SkinTextures;", at = @At("HEAD"), cancellable = true)
	private void getSkinTextures(CallbackInfoReturnable<SkinTextures> cir)
	{
		var abstractClientPlayerEntity = (AbstractClientPlayerEntity)(Object)this;
		var pc = PlayerData.getPersistentPublic(abstractClientPlayerEntity);

		var species = pc.getCharacter();
		if (species == null)
			return;

		PlayerListEntry playerListEntry = getPlayerListEntry();
		var originalSkinTextures = playerListEntry == null ? DefaultSkinHelper.getSkinTextures(abstractClientPlayerEntity.getUuid()) : playerListEntry.getSkinTextures();

		var current = SwgSpeciesRenderer.getTexture(abstractClientPlayerEntity, species);

		cir.setReturnValue(new SkinTextures(
				current,
				originalSkinTextures.textureUrl(),
				originalSkinTextures.capeTexture(),
				originalSkinTextures.elytraTexture(),
				originalSkinTextures.model(),
				originalSkinTextures.secure()
		));
	}
}
