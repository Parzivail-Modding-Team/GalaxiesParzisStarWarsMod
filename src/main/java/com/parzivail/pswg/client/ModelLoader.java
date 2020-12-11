package com.parzivail.pswg.client;

import com.parzivail.pswg.client.model.SimpleModel;
import com.parzivail.pswg.client.pm3d.PM3DBakedBlockModel;
import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.client.pm3d.PM3DUnbakedBlockModel;
import net.minecraft.util.Identifier;

public class ModelLoader
{
	public static PM3DUnbakedBlockModel loadPM3D(Identifier modelFile, Identifier baseTexture, Identifier particleTexture)
	{
		return new PM3DUnbakedBlockModel(baseTexture, particleTexture, spriteMap -> PM3DBakedBlockModel.create(SimpleModel.Discriminator.GLOBAL, PM3DFile.tryLoad(modelFile).getLevelOfDetail(0), baseTexture, particleTexture, spriteMap));
	}

	public static PM3DUnbakedBlockModel loadPM3D(SimpleModel.Discriminator discriminator, Identifier modelFile, Identifier baseTexture, Identifier particleTexture)
	{
		return new PM3DUnbakedBlockModel(baseTexture, particleTexture, spriteMap -> PM3DBakedBlockModel.create(discriminator, PM3DFile.tryLoad(modelFile).getLevelOfDetail(0), baseTexture, particleTexture, spriteMap));
	}
}
