package com.parzivail.pm3d;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.function.Function;

public class Pm3dModel implements IModel
{
	public final ResourceLocation filename;
	public final String credit;
	public final EnumSet<Pm3dFlags> flags;
	public final HashMap<String, String> textures;
	public final ArrayList<Pm3dVert> verts;
	public final ArrayList<Pm3dVert> normals;
	public final ArrayList<Pm3dUv> uvs;
	public final HashMap<Pm3dModelObjectInfo, ArrayList<Pm3dFace>> objects;

	public Pm3dModel(ResourceLocation filename, String credit, EnumSet<Pm3dFlags> flags, HashMap<String, String> textures, ArrayList<Pm3dVert> verts, ArrayList<Pm3dVert> normals, ArrayList<Pm3dUv> uvs, HashMap<Pm3dModelObjectInfo, ArrayList<Pm3dFace>> objects)
	{
		this.filename = filename;
		this.credit = credit;
		this.flags = flags;
		this.textures = textures;
		this.verts = verts;
		this.normals = normals;
		this.uvs = uvs;
		this.objects = objects;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
	{
		return new Pm3dBakedModel(this, state, format, bakedTextureGetter);
	}

	@Override
	public Collection<ResourceLocation> getTextures()
	{
		ArrayList<ResourceLocation> textures = new ArrayList<>();
		for (String texture : this.textures.values())
			textures.add(new ResourceLocation(texture));
		return textures;
	}

	public String getTexture(String textureKey)
	{
		if (!textures.containsKey(textureKey))
			return "";

		return textures.get(textureKey.toLowerCase());
	}
}
