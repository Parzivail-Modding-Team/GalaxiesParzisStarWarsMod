package com.parzivail.util.client.model;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public abstract class BaseUnbakedBlockModel<T extends AbstractModel> extends ClonableUnbakedModel
{
	@FunctionalInterface
	public interface BakerFunction<TM, T>
	{
		T bake(TM model, Function<SpriteIdentifier, Sprite> spriteBaker);
	}

	protected final Identifier baseTexture;
	protected final Identifier particleTexture;
	protected final BakerFunction<BaseUnbakedBlockModel<T>, T> baker;
	protected T cachedBakedModel = null;

	public BaseUnbakedBlockModel(Identifier baseTexture, Identifier particleTexture, BakerFunction<BaseUnbakedBlockModel<T>, T> baker)
	{
		this.baseTexture = baseTexture;
		this.particleTexture = particleTexture;
		this.baker = baker;
	}

	@Override
	public abstract ClonableUnbakedModel copy();

	@Override
	public Collection<Identifier> getModelDependencies()
	{
		return Collections.emptyList();
	}

	@Override
	public void setParents(Function<Identifier, UnbakedModel> modelLoader)
	{
		// TODO: parents?
	}

	// TODO: doesn't seem required anymore?
	//	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<Pair<String, String>> errors)
	{
		var ids = new ArrayList<SpriteIdentifier>();

		ids.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, baseTexture));
		if (!baseTexture.equals(particleTexture))
			ids.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, particleTexture));

		return ids;
	}

	@Nullable
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer)
	{
		if (cachedBakedModel != null)
			return cachedBakedModel;

		var result = this.baker.bake(this, textureGetter);
		cachedBakedModel = result;
		return result;
	}
}

