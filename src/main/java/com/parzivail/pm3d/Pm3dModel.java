package com.parzivail.pm3d;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class Pm3dModel implements IModel
{
	public final ArrayList<Pm3dVert> verts;
	public final ArrayList<Pm3dNormal> normals;
	public final ArrayList<Pm3dUv> uvs;
	public final HashMap<Pm3dModelObjectInfo, ArrayList<Pm3dFace>> objects;

	public Pm3dModel(ArrayList<Pm3dVert> verts, ArrayList<Pm3dNormal> normals, ArrayList<Pm3dUv> uvs, HashMap<Pm3dModelObjectInfo, ArrayList<Pm3dFace>> objects)
	{
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
}
