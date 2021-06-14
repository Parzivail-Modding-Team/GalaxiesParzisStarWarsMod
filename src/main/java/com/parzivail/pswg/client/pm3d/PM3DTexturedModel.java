package com.parzivail.pswg.client.pm3d;

import com.google.common.base.Suppliers;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.Supplier;

public class PM3DTexturedModel
{
	private final Supplier<PM3DFile> file;
	private final Identifier[] lodTextures;

	public PM3DTexturedModel(Supplier<PM3DFile> file, Identifier... lodTextures)
	{
		this.file = Suppliers.memoize(file::get);
		this.lodTextures = lodTextures;
	}

	public Identifier getTexture(int lod)
	{
		return lodTextures[MathHelper.clamp(lod, 0, lodTextures.length - 1)];
	}

	public PM3DLod getModel(int lod)
	{
		return file.get().getLevelOfDetail(lod);
	}
}
