package com.parzivail.pswg.client.render.p3d;

import com.mojang.datafixers.util.Pair;
import com.parzivail.util.client.model.BaseUnbakedBlockModel;
import com.parzivail.util.client.model.ClonableUnbakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

public class P3DUnbakedBlockModel extends BaseUnbakedBlockModel<P3DBakedBlockModel>
{
	private final HashMap<String, Identifier> additionalTextures = new HashMap<>();

	public P3DUnbakedBlockModel(Identifier baseTexture, Identifier particleTexture, BakerFunction<BaseUnbakedBlockModel<P3DBakedBlockModel>, P3DBakedBlockModel> baker)
	{
		super(baseTexture, particleTexture, baker);
	}

	public P3DUnbakedBlockModel withTexture(String id, Identifier texture)
	{
		this.additionalTextures.put(id, texture);
		return this;
	}

	public P3DUnbakedBlockModel withTextures(HashMap<String, Identifier> textures)
	{
		this.additionalTextures.putAll(textures);
		return this;
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<Pair<String, String>> errors)
	{
		var ids = super.getTextureDependencies(function, errors);

		for (var texture : additionalTextures.values())
			ids.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, texture));

		return ids;
	}

	@Override
	public ClonableUnbakedModel copy()
	{
		return new P3DUnbakedBlockModel(baseTexture, particleTexture, baker)
				.withTextures(additionalTextures);
	}

	public HashMap<String, Identifier> getAdditionalTextures()
	{
		return additionalTextures;
	}
}
