package com.parzivail.pswg.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderPhase.class)
@Environment(EnvType.CLIENT)
public interface RenderPhaseAccessor
{
	@Accessor("POSITION_COLOR_TEXTURE_SHADER")
	static RenderPhase.Shader get_POSITION_COLOR_TEXTURE_SHADER()
	{
		throw new AssertionError();
	}
}
