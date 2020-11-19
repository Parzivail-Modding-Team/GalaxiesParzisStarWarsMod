package com.parzivail.pswg.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ResourceTexture.class)
@Environment(EnvType.CLIENT)
public interface ResourceTextureMixin
{
	@Invoker
	ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager);
}
