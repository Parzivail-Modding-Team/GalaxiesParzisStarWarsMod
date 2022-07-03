package com.parzivail.pswg.client.render.pm3d;

import com.parzivail.util.client.model.BaseUnbakedBlockModel;
import com.parzivail.util.client.model.ClonableUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class PM3DUnbakedBlockModel extends BaseUnbakedBlockModel<PM3DBakedBlockModel>
{
	public PM3DUnbakedBlockModel(Identifier baseTexture, Identifier particleTexture, Function<Function<SpriteIdentifier, Sprite>, PM3DBakedBlockModel> baker)
	{
		super(baseTexture, particleTexture, baker);
	}

	@Override
	public ClonableUnbakedModel copy()
	{
		return new PM3DUnbakedBlockModel(baseTexture, particleTexture, baker);
	}
}
