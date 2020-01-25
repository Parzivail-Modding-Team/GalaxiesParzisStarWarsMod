package com.parzivail.pswg.client;

import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.client.pm3d.PM3DModel;
import com.parzivail.pswg.client.pm3d.PM3DUnbakedModel;
import net.minecraft.util.Identifier;

public class ModelLoader
{
	public static PM3DUnbakedModel loadPM3D(Identifier modelFile, Identifier baseTexture, Identifier particleTexture)
	{
		return new PM3DUnbakedModel(baseTexture, particleTexture, spriteMap -> PM3DModel.create(PM3DFile.tryLoad(modelFile), baseTexture, particleTexture, spriteMap));
	}
}
