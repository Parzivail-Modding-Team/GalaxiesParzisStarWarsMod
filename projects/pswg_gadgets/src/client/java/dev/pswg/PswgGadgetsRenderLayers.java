package dev.pswg;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;

import java.util.function.BiFunction;

import static net.minecraft.client.render.RenderPhase.*;

public class PswgGadgetsRenderLayers
{
	private static final BiFunction<Identifier, Boolean, RenderLayer> PSWG_PARTICLE = Util
			.memoize((texture, affectsOutline) -> {
				RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				                                                                                        .texture(new RenderPhase.Texture(texture, false))
				                                                                                        //.transparency(RenderLayer.TRANSLUCENT_TRANSPARENCY).cull(DISABLE_CULLING)
				                                                                                        .lightmap(RenderLayer.ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).layering(VIEW_OFFSET_Z_LAYERING)
				                                                                                        //.writeMaskState(RenderLayer.ALL_MASK)
				                                                                                        .build(true);
				return RenderLayer.of("pswg_particle", 1, true, true,
				                      RenderPipelines.TRANSLUCENT_PARTICLE, multiPhaseParameters);
			});

	public static RenderLayer pswgParticle(Identifier texture, boolean affectsOutline)
	{
		return PSWG_PARTICLE.apply(texture, affectsOutline);
	}
}
