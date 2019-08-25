package com.parzivail.pm3d;

import com.parzivail.util.jsonpipeline.Quad;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Pm3dBakedModel implements IBakedModel
{
	private final Pm3dModel pm3dModel;
	private final IModelState state;
	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

	public Pm3dBakedModel(Pm3dModel pm3dModel, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
	{
		this.pm3dModel = pm3dModel;
		this.state = state;
		this.format = format;
		this.bakedTextureGetter = bakedTextureGetter;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
	{
		List<BakedQuad> quads = new ArrayList<>();

		for (Map.Entry<Pm3dModelObjectInfo, ArrayList<Pm3dFace>> pair : pm3dModel.objects.entrySet())
		{
			TextureAtlasSprite sprite = bakedTextureGetter.apply(new ResourceLocation(pair.getKey().materialName));

			for (Pm3dFace face : pair.getValue())
			{
				Pm3dVertPointer ptr0 = face.verts.get(0);
				Pm3dVert vert0 = this.pm3dModel.verts.get(ptr0.getVertex() - 1);
				Pm3dNormal norm0 = this.pm3dModel.normals.get(ptr0.getNormal() - 1);
				Pm3dUv uv0 = this.pm3dModel.uvs.get(ptr0.getTexture() - 1);

				Pm3dVertPointer ptr1 = face.verts.get(1);
				Pm3dVert vert1 = this.pm3dModel.verts.get(ptr1.getVertex() - 1);
				Pm3dNormal norm1 = this.pm3dModel.normals.get(ptr1.getNormal() - 1);
				Pm3dUv uv1 = this.pm3dModel.uvs.get(ptr1.getTexture() - 1);

				Pm3dVertPointer ptr2 = face.verts.get(2);
				Pm3dVert vert2 = this.pm3dModel.verts.get(ptr2.getVertex() - 1);
				Pm3dNormal norm2 = this.pm3dModel.normals.get(ptr2.getNormal() - 1);
				Pm3dUv uv2 = this.pm3dModel.uvs.get(ptr2.getTexture() - 1);

				UnpackedBakedQuad.Builder b = new UnpackedBakedQuad.Builder(format);
				b.setTexture(sprite);

				putVertexData(b, sprite, vert0, norm0, uv0);
				putVertexData(b, sprite, vert1, norm1, uv1);
				putVertexData(b, sprite, vert2, norm2, uv2);
				putVertexData(b, sprite, vert2, norm2, uv2);

				quads.add(b.build());
			}
		}

		return quads;
	}

	private final void putVertexData(UnpackedBakedQuad.Builder builder, TextureAtlasSprite sprite, Pm3dVert vert, Pm3dNormal norm, Pm3dUv uv)
	{
		// TODO handle everything not handled (texture transformations, bones, transformations, normals, e.t.c)
		for (int e = 0; e < format.getElementCount(); e++)
		{
			switch (format.getElement(e).getUsage())
			{
				case POSITION:
					builder.put(e, vert.x, vert.y, vert.z, 1);
					break;
				case COLOR:
					builder.put(e, 1, 1, 1, 1);
					break;
				case UV:
					// TODO handle more brushes
					builder.put(e, sprite.getInterpolatedU(uv.u * 16), sprite.getInterpolatedV(uv.v * 16), 0, 1);
					break;
				case NORMAL:
					builder.put(e, norm.x, norm.y, norm.z, 0);
					break;
				default:
					builder.put(e);
			}
		}
	}

	private void putVert(Pm3dVert vert0, Pm3dNormal norm0, Pm3dUv uv0, Quad.Builder b)
	{
		b.put(0, vert0.x, vert0.y, vert0.z);
		b.put(1, uv0.u, uv0.v);
		b.put(2, norm0.x, norm0.y, norm0.z);
		b.put(3, 0);
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return false;
	}

	@Override
	public boolean isGui3d()
	{
		return false;
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return ModelLoader.White.INSTANCE;
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return ItemOverrideList.NONE;
	}
}
