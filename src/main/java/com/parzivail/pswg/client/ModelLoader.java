package com.parzivail.pswg.client;

import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.client.pm3d.PM3DModel;
import com.parzivail.pswg.client.pm3d.PM3DUnbakedModel;
import net.minecraft.util.Identifier;

public class ModelLoader
{
	public static PM3DUnbakedModel loadPM3D(Identifier identifier)
	{
		return new PM3DUnbakedModel(identifier, spriteMap -> PM3DModel.create(spriteMap, PM3DFile.tryLoad(identifier)));
	}
}
