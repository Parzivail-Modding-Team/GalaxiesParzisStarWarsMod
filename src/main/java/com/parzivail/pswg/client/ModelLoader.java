package com.parzivail.pswg.client;

import com.parzivail.pswg.client.pm3d.PM3DBakedBlockModel;
import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.client.pm3d.PM3DUnbakedBlockModel;
import net.minecraft.util.Identifier;

public class ModelLoader
{
	public static PM3DUnbakedBlockModel loadPM3D(Identifier modelFile, Identifier baseTexture, Identifier particleTexture)
	{
		return new PM3DUnbakedBlockModel(baseTexture, particleTexture, spriteMap -> PM3DBakedBlockModel.create(PM3DFile.tryLoad(modelFile), baseTexture, particleTexture, spriteMap));
	}
}
