package com.parzivail.pswg.client.loader;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.client.render.p3d.P3DBakedBlockModel;
import com.parzivail.pswg.client.render.p3d.P3DUnbakedBlockModel;
import com.parzivail.pswg.client.render.pm3d.PM3DBakedBlockModel;
import com.parzivail.pswg.client.render.pm3d.PM3DFile;
import com.parzivail.pswg.client.render.pm3d.PM3DUnbakedBlockModel;
import com.parzivail.util.client.model.ClonableUnbakedModel;
import com.parzivail.util.client.model.DynamicBakedModel;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;

public class ModelLoader
{
	@Deprecated(since = "0.0.60-alpha", forRemoval = true)
	public static PM3DUnbakedBlockModel loadPM3D(Identifier modelFile, Identifier baseTexture, Identifier particleTexture)
	{
		return loadPM3D(DynamicBakedModel.CacheMethod.SINGLETON, modelFile, baseTexture, particleTexture);
	}

	@Deprecated(since = "0.0.60-alpha", forRemoval = true)
	public static PM3DUnbakedBlockModel loadPM3D(DynamicBakedModel.CacheMethod cacheMethod, Identifier modelFile, Identifier baseTexture, Identifier particleTexture)
	{
		return new PM3DUnbakedBlockModel(
				baseTexture,
				particleTexture,
				(m, spriteMap) -> PM3DBakedBlockModel.create(
						cacheMethod,
						PM3DFile.tryLoad(modelFile, true).getLevelOfDetail(0),
						baseTexture,
						particleTexture,
						spriteMap
				)
		);
	}

	public static P3DUnbakedBlockModel loadP3D(DynamicBakedModel.CacheMethod cacheMethod, Identifier modelFile, Identifier baseTexture, Identifier particleTexture)
	{
		if (particleTexture.equals(new Identifier("block/stone")))
			Galaxies.LOG.warn("Model[%s]+Texture[%s] does not have a particle texture", modelFile, baseTexture);

		return new P3DUnbakedBlockModel(
				baseTexture,
				particleTexture,

				(m, spriteMap) -> P3DBakedBlockModel.create(
						spriteMap,
						cacheMethod,
						baseTexture,
						particleTexture,
						((P3DUnbakedBlockModel)m).getAdditionalTextures(),
						modelFile
				)
		);
	}

	public static P3DUnbakedBlockModel withDyeColors(P3DUnbakedBlockModel model, BiFunction<P3DUnbakedBlockModel, DyeColor, P3DUnbakedBlockModel> dyer)
	{
		for (var color : DyeColor.values())
			model = dyer.apply(model, color);
		return model;
	}

	public static ClonableUnbakedModel loadPicklingP3D(Identifier baseTexture, Identifier particleTexture, Identifier... modelFiles)
	{
		return new P3DUnbakedBlockModel(
				baseTexture,
				particleTexture,
				(m, spriteMap) -> P3DBakedBlockModel.create(
						spriteMap,
						DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY,
						baseTexture,
						particleTexture,
						((P3DUnbakedBlockModel)m).getAdditionalTextures(),
						modelFiles
				)
		);
	}
}
