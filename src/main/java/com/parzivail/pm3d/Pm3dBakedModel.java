package com.parzivail.pm3d;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Pm3dBakedModel implements IBakedModel
{
	private static final ItemCameraTransforms TRANSFORMS = getAllTransforms();

	private final Pm3dModel pm3dModel;
	private final IModelState state;
	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;
	private HashMap<IBlockState, List<BakedQuad>> quadCache;

	public Pm3dBakedModel(Pm3dModel pm3dModel, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
	{
		this.pm3dModel = pm3dModel;
		this.state = state;
		this.format = format;
		this.bakedTextureGetter = bakedTextureGetter;

		quadCache = new HashMap<>();
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
	{
		if (quadCache.containsKey(state))
			return quadCache.get(state);

		List<BakedQuad> quads = new ArrayList<>();

		Matrix4f m = this.state.apply(Optional.empty()).orElse(TRSRTransformation.identity()).getMatrix();

		for (Map.Entry<Pm3dModelObjectInfo, ArrayList<Pm3dFace>> pair : pm3dModel.objects.entrySet())
		{
			TextureAtlasSprite sprite = bakedTextureGetter.apply(new ResourceLocation(pm3dModel.getTexture(pair.getKey().materialName)));

			for (Pm3dFace face : pair.getValue())
			{
				UnpackedBakedQuad.Builder b = new UnpackedBakedQuad.Builder(format);
				b.setTexture(sprite);

				switch (face.verts.size())
				{
					case 3:
						putTriangle(b, face, sprite, m);
						break;
					case 4:
						putQuad(b, face, sprite, m);
						break;
					default:
						CrashReport crashreport = CrashReport.makeCrashReport(new IOException(String.format("Pm3D face bakery expects triangles or quads, found n-gon (%s sides)", face.verts.size())), "Baking Pm3D structure");
						CrashReportCategory crashreportcategory = crashreport.makeCategory("Model being baked");
						crashreportcategory.addCrashSection("Filename", pm3dModel.filename);
						throw new ReportedException(crashreport);
				}

				quads.add(b.build());
			}
		}

		quadCache.put(state, quads);

		return quads;
	}

	private static ItemCameraTransforms getAllTransforms()
	{
		ItemTransformVec3f thirdperson_left = new ItemTransformVec3f(new Vector3f(75, 135, 0), new Vector3f(0, 0, 0), new Vector3f(0.375f, 0.375f, 0.375f)); //this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
		ItemTransformVec3f thirdperson_right = new ItemTransformVec3f(new Vector3f(75, 135, 0), new Vector3f(0, 0, 0), new Vector3f(0.375f, 0.375f, 0.375f)); //this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
		ItemTransformVec3f firstperson_left = new ItemTransformVec3f(new Vector3f(0, 135, 0), new Vector3f(0, 0, 0), new Vector3f(0.40f, 0.40f, 0.40f)); //this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
		ItemTransformVec3f firstperson_right = new ItemTransformVec3f(new Vector3f(0, 135, 0), new Vector3f(0, 0, 0), new Vector3f(0.40f, 0.40f, 0.40f)); //this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
		ItemTransformVec3f head = new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f()); //this.getTransform(ItemCameraTransforms.TransformType.HEAD);
		ItemTransformVec3f gui = new ItemTransformVec3f(new Vector3f(30, 225, 0), new Vector3f(0, 0, 0), new Vector3f(0.625f, 0.625f, 0.625f)); //this.getTransform(ItemCameraTransforms.TransformType.GUI);
		ItemTransformVec3f ground = new ItemTransformVec3f(new Vector3f(0, 0, 0), new Vector3f(0, 0.1f, 0), new Vector3f(0.25f, 0.25f, 0.25f)); //this.getTransform(ItemCameraTransforms.TransformType.GROUND);
		ItemTransformVec3f fixed = new ItemTransformVec3f(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.5f, 0.5f, 0.5f)); //this.getTransform(ItemCameraTransforms.TransformType.FIXED);
		return new ItemCameraTransforms(thirdperson_left, thirdperson_right, firstperson_left, firstperson_right, head, gui, ground, fixed);
	}

	private void putVertPointer(UnpackedBakedQuad.Builder b, Pm3dVertPointer pointer, TextureAtlasSprite sprite, Matrix4f transformation)
	{
		Pm3dVert vert = this.pm3dModel.verts.get(pointer.getVertex() - 1);
		Pm3dVert norm = this.pm3dModel.normals.get(pointer.getNormal() - 1);
		Pm3dUv uv = this.pm3dModel.uvs.get(pointer.getTexture() - 1);

		putVertexData(b, sprite, vert.scale(1 / 16f).apply(transformation).translate(0.5f, 0.5f, 0.5f), norm, uv);
	}

	private void putQuad(UnpackedBakedQuad.Builder b, Pm3dFace face, TextureAtlasSprite sprite, Matrix4f transformation)
	{
		putVertPointer(b, face.verts.get(0), sprite, transformation);
		putVertPointer(b, face.verts.get(1), sprite, transformation);
		putVertPointer(b, face.verts.get(2), sprite, transformation);
		putVertPointer(b, face.verts.get(3), sprite, transformation);
	}

	private void putTriangle(UnpackedBakedQuad.Builder b, Pm3dFace face, TextureAtlasSprite sprite, Matrix4f transformation)
	{
		putVertPointer(b, face.verts.get(0), sprite, transformation);
		putVertPointer(b, face.verts.get(1), sprite, transformation);
		putVertPointer(b, face.verts.get(2), sprite, transformation);
		putVertPointer(b, face.verts.get(2), sprite, transformation); // last vert twice
	}

	private final void putVertexData(UnpackedBakedQuad.Builder builder, TextureAtlasSprite sprite, Pm3dVert vert, Pm3dVert norm, Pm3dUv uv)
	{
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
					builder.put(e, sprite.getInterpolatedU(uv.u * 16), sprite.getInterpolatedV((1 - uv.v) * 16), 0, 1);
					break;
				case NORMAL:
					builder.put(e, norm.x, norm.y, norm.z, 0);
					break;
				default:
					builder.put(e);
			}
		}
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return pm3dModel.flags.contains(Pm3dFlags.AmbientOcclusion);
	}

	@Override
	public boolean isGui3d()
	{
		return true;
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		if (!pm3dModel.textures.containsKey("particle"))
			return ModelLoader.White.INSTANCE;
		return bakedTextureGetter.apply(new ResourceLocation(pm3dModel.textures.get("particle")));
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return ItemOverrideList.NONE;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms()
	{
		return TRANSFORMS;
	}
}
