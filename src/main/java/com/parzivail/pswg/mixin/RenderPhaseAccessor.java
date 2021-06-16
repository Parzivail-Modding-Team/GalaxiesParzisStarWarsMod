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

	@Accessor("POLYGON_OFFSET_LAYERING")
	static RenderPhase.Layering get_POLYGON_OFFSET_LAYERING()
	{
		throw new AssertionError();
	}

	@Accessor("VIEW_OFFSET_Z_LAYERING")
	static RenderPhase.Layering get_VIEW_OFFSET_Z_LAYERING()
	{
		throw new AssertionError();
	}

	@Accessor("ADDITIVE_TRANSPARENCY")
	static RenderPhase.Transparency get_ADDITIVE_TRANSPARENCY()
	{
		throw new AssertionError();
	}

	@Accessor("LIGHTNING_TRANSPARENCY")
	static RenderPhase.Transparency get_LIGHTNING_TRANSPARENCY()
	{
		throw new AssertionError();
	}

	@Accessor("DISABLE_CULLING")
	static RenderPhase.Cull get_DISABLE_CULLING()
	{
		throw new AssertionError();
	}

	@Accessor("POSITION_COLOR_TEXTURE_SHADER")
	static RenderPhase.Shader get_POSITION_COLOR_TEXTURE_SHADER()
	{
		throw new AssertionError();
	}
}
