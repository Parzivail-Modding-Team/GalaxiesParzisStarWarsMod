package com.parzivail.pswg.client.pm3d;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class PM3DUnbakedModel implements UnbakedModel
{
	protected PM3DContainer container;
	protected Mesh mesh;
	protected ModelTransformation transform;

	public PM3DUnbakedModel(PM3DContainer container, Mesh mesh, ModelTransformation transform)
	{
		if (transform == null)
			transform = ModelTransformation.NONE;

		this.container = container;
		this.mesh = mesh;
		this.transform = transform;
	}

	@Override
	public Collection<Identifier> getModelDependencies()
	{
		return Collections.emptySet();
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences)
	{
		//		List<Identifier> sprites = new ArrayList<>();
		//		builder.getMtlList().forEach(mtl -> sprites.add(new Identifier(mtl.getMapKd())));
		//
		//		return sprites;
		return Collections.emptySet();
	}

	@Override
	public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId)
	{
		return new PM3DBakedModel(mesh, transform);
	}
}
