package com.parzivail.pswg.client.loader;

import com.parzivail.pswg.client.render.pm3d.PM3DBakedBlockModel;
import com.parzivail.pswg.client.render.pm3d.PM3DFile;
import com.parzivail.pswg.client.render.pm3d.PM3DUnbakedBlockModel;
import com.parzivail.util.client.model.DynamicBakedModel;
import net.minecraft.util.Identifier;

public class ModelLoader
{
	public static PM3DUnbakedBlockModel loadPM3D(Identifier modelFile, Identifier baseTexture, Identifier particleTexture)
	{
		return loadPM3D(DynamicBakedModel.Discriminator.GLOBAL, modelFile, baseTexture, particleTexture);
	}

	public static PM3DUnbakedBlockModel loadPM3D(DynamicBakedModel.Discriminator discriminator, Identifier modelFile, Identifier baseTexture, Identifier particleTexture)
	{
		return new PM3DUnbakedBlockModel(
				baseTexture,
				particleTexture,
				spriteMap -> PM3DBakedBlockModel.create(
						discriminator,
						PM3DFile.tryLoad(modelFile, true).getLevelOfDetail(0),
						baseTexture,
						particleTexture,
						spriteMap
				)
		);
	}
}
