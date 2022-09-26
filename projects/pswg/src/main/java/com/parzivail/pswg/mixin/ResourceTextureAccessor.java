package com.parzivail.pswg.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ResourceTexture.class)
@Environment(EnvType.CLIENT)
public interface ResourceTextureAccessor
{
	@Accessor("location")
	Identifier getLocation();
}
