package dev.pswg;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.texture.SpriteAtlasTexture;

import static net.minecraft.client.render.RenderPhase.*;

public class GalaxiesRenderLayers
{
	public static final RenderLayer GALAXIES_TRANSLUCENT = RenderLayer.of("pswg_translucent",
	                                                                      16384,
	                                                                      true,
	                                                                      true,
	                                                                      RenderPipelines.TRANSLUCENT_PARTICLE,
	                                                                      RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE, false))
	                                                                                             .lightmap(ENABLE_LIGHTMAP)
	                                                                                             .overlay(ENABLE_OVERLAY_COLOR)
	                                                                                                      .layering(VIEW_OFFSET_Z_LAYERING_FORWARD)
	                                                                                             .build(true));

	public static void init()
	{
	}
}
