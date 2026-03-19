package dev.pswg;

import static net.minecraft.client.renderer.RenderStateShard.*;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;

public class GalaxiesRenderLayers
{
	public static final RenderType GALAXIES_TRANSLUCENT = RenderType.create("pswg_translucent",
	                                                                      16384,
	                                                                      true,
	                                                                      true,
	                                                                      RenderPipelines.TRANSLUCENT_PARTICLE,
	                                                                      RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_PARTICLES, false))
	                                                                                             .setLightmapState(LIGHTMAP)
	                                                                                             .setOverlayState(OVERLAY)
	                                                                                                      .setLayeringState(VIEW_OFFSET_Z_LAYERING_FORWARD)
	                                                                                             .createCompositeState(true));

	public static void init()
	{
	}
}
