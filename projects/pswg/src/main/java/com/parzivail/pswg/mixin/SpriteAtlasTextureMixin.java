package com.parzivail.pswg.mixin;

import com.parzivail.util.client.sprite.LayeredSpriteBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.IOException;

@Mixin(SpriteLoader.class)
@Environment(EnvType.CLIENT)
public abstract class SpriteAtlasTextureMixin
{
	@Shadow
	protected abstract Identifier getTexturePath(Identifier identifier);

	@ModifyVariable(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;getWidth()I", shift = At.Shift.BEFORE, ordinal = 0), ordinal = 0)
	public NativeImage spriteAddBaseLayer(NativeImage value, Identifier id, Resource resource) throws IOException
	{
		return LayeredSpriteBuilder.build(value, getTexturePath(id), container, this::getTexturePath);
	}
}
