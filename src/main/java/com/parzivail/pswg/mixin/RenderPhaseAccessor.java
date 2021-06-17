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
	@Accessor("NO_LAYERING")
	static RenderPhase.Layering get_NO_LAYERING()
	{
		throw new AssertionError();
	}

	@Accessor("VIEW_OFFSET_Z_LAYERING")
	static RenderPhase.Layering get_VIEW_OFFSET_Z_LAYERING()
	{
		throw new AssertionError();
	}

	@Accessor("TRANSLUCENT_TRANSPARENCY")
	static RenderPhase.Transparency get_TRANSLUCENT_TRANSPARENCY()
	{
		throw new AssertionError();
	}

	@Accessor("LIGHTNING_SHADER")
	static RenderPhase.Shader get_LIGHTNING_SHADER()
	{
		throw new AssertionError();
	}
}
