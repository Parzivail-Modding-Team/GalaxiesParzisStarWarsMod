package dev.pswg.particles;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.texture.SpriteAtlasTexture;

import static net.minecraft.client.render.RenderPhase.*;

public class GadgetsRenderLayers
{
	public static final RenderLayer PSWG_CUSTOM = RenderLayer.of("pswg_custom",
	                                                             4096,
	                                                             true,
	                                                             true,
	                                                             RenderPipelines.TRANSLUCENT_PARTICLE,
	                                                             RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE, false))
	                                                                                             .lightmap(ENABLE_LIGHTMAP)
	                                                                                             .overlay(ENABLE_OVERLAY_COLOR)
	                                                                                             .layering(VIEW_OFFSET_Z_LAYERING)
	                                                                                             .build(true));

	public static void init()
	{
	}
}
