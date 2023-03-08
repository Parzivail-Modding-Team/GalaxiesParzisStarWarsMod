package com.parzivail.p3d;

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

public class P3dUnbakedBlockModel extends BaseUnbakedBlockModel<P3dBakedBlockModel>
{
	private final HashMap<String, Identifier> additionalTextures = new HashMap<>();

	public P3dUnbakedBlockModel(Identifier baseTexture, Identifier particleTexture, BakerFunction<BaseUnbakedBlockModel<P3dBakedBlockModel>, P3dBakedBlockModel> baker)
	{
		super(baseTexture, particleTexture, baker);
	}

	public P3dUnbakedBlockModel withTexture(String id, Identifier texture)
	{
		this.additionalTextures.put(id, texture);
		return this;
	}

	public P3dUnbakedBlockModel withTextures(HashMap<String, Identifier> textures)
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
		return new P3dUnbakedBlockModel(baseTexture, particleTexture, baker)
				.withTextures(additionalTextures);
	}

	public HashMap<String, Identifier> getAdditionalTextures()
	{
		return additionalTextures;
	}
}
