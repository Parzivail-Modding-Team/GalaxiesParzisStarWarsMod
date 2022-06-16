package com.parzivail.pswg.client.render.p3d;

import com.parzivail.util.client.model.BaseUnbakedBlockModel;
import com.parzivail.util.client.model.ClonableUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class P3DUnbakedBlockModel extends BaseUnbakedBlockModel<P3DBakedBlockModel>
{
	public P3DUnbakedBlockModel(Identifier baseTexture, Identifier particleTexture, Function<Function<SpriteIdentifier, Sprite>, P3DBakedBlockModel> baker)
	{
		super(baseTexture, particleTexture, baker);
	}

	@Override
	public ClonableUnbakedModel copy()
	{
		return new P3DUnbakedBlockModel(baseTexture, particleTexture, baker);
	}
}
