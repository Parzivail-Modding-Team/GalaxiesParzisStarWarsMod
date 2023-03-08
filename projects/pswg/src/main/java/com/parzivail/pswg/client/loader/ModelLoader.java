package com.parzivail.pswg.client.loader;

import com.parzivail.p3d.P3dBakedBlockModel;
import com.parzivail.p3d.P3dUnbakedBlockModel;
import com.parzivail.pswg.Galaxies;
import com.parzivail.util.client.model.ClonableUnbakedModel;
import com.parzivail.util.client.model.DynamicBakedModel;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;

public class ModelLoader
{
	public static P3dUnbakedBlockModel loadP3D(DynamicBakedModel.CacheMethod cacheMethod, Identifier modelFile, Identifier baseTexture, Identifier particleTexture)
	{
		if (particleTexture.equals(new Identifier("block/stone")))
			Galaxies.LOG.warn("Model[%s]+Texture[%s] does not have a particle texture", modelFile, baseTexture);

		return new P3dUnbakedBlockModel(
				baseTexture,
				particleTexture,

				(m, spriteMap) -> P3dBakedBlockModel.create(
						spriteMap,
						cacheMethod,
						baseTexture,
						particleTexture,
						((P3dUnbakedBlockModel)m).getAdditionalTextures(),
						modelFile
				)
		);
	}

	public static P3dUnbakedBlockModel withDyeColors(P3dUnbakedBlockModel model, BiFunction<P3dUnbakedBlockModel, DyeColor, P3dUnbakedBlockModel> dyer)
	{
		for (var color : DyeColor.values())
			model = dyer.apply(model, color);
		return model;
	}

	public static ClonableUnbakedModel loadPicklingP3D(Identifier baseTexture, Identifier particleTexture, Identifier... modelFiles)
	{
		return new P3dUnbakedBlockModel(
				baseTexture,
				particleTexture,
				(m, spriteMap) -> P3dBakedBlockModel.create(
						spriteMap,
						DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY,
						baseTexture,
						particleTexture,
						((P3dUnbakedBlockModel)m).getAdditionalTextures(),
						modelFiles
				)
		);
	}
}
