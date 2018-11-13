package com.parzivail.swg.render.sbrh;

import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.render.pipeline.*;
import com.parzivail.util.block.PBlockContainer;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import javax.annotation.Nullable;

public class SimpleBlockRenderHandlerTest implements ISimpleBlockRenderingHandler
{
	private static final float SCALE_ROTATION_22_5 = 1.0F / (float)Math.cos(0.39269909262657166D) - 1.0F;
	private static final float SCALE_ROTATION_GENERAL = 1.0F / (float)Math.cos((Math.PI / 4D)) - 1.0F;

	private final int id;
	private final ModelBlock model;

	public SimpleBlockRenderHandlerTest(PBlockContainer block, ModelBlock model)
	{
		id = block.name.hashCode();
		this.model = model;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		RenderBlocks.getInstance().setRenderBoundsFromBlock(block);
		Tessellator tessellator = Tessellator.instance;
		tessellator.addTranslation(x, y, z);

		SimpleBakedModel.Builder simplebakedmodel$builder = (new SimpleBakedModel.Builder(model));
		for (BlockPart blockpart : model.getElements())
		{
			for (EnumFacing enumfacing : blockpart.mapFaces.keySet())
			{
				BlockPartFace blockpartface = blockpart.mapFaces.get(enumfacing);
				TextureAtlasSprite textureatlassprite1 = Client.mc.getTextureMapBlocks().getAtlasSprite(translateTextureName(model.resolveTextureName(blockpartface.texture)));
				simplebakedmodel$builder.addGeneralQuad(makeBakedQuad(blockpart, blockpartface, textureatlassprite1, enumfacing));
			}
		}

		tessellator.addTranslation(-x, -y, -z);
		return true;
	}

	private String translateTextureName(String name)
	{
		return name.replace("blocks/", "");
	}

	private BakedQuad makeBakedQuad(BlockPart blockpart, BlockPartFace blockpartface, TextureAtlasSprite textureatlassprite1, EnumFacing enumfacing)
	{
		return makeBakedQuad(blockpart.positionFrom, blockpart.positionTo, blockpartface, textureatlassprite1, enumfacing, blockpart.partRotation, true);
	}

	public BakedQuad makeBakedQuad(Vector3f posFrom, Vector3f posTo, BlockPartFace face, TextureAtlasSprite sprite, EnumFacing facing, BlockPartRotation partRotation, boolean shade)
	{
		BlockFaceUV blockfaceuv = face.blockFaceUV;

		int[] aint = makeQuadVertexData(blockfaceuv, sprite, facing, getPositionsDiv16(posFrom, posTo), partRotation, false);
		EnumFacing enumfacing = getFacingFromVertexData(aint);

		if (partRotation == null)
		{
			applyFacing(aint, enumfacing);
		}

		fillNormal(aint, enumfacing);
		return new BakedQuad(aint, face.tintIndex, enumfacing, sprite, shade);
	}

	public static void fillNormal(int[] faceData, EnumFacing facing)
	{
		javax.vecmath.Vector3f v1 = getVertexPos(faceData, 3);
		javax.vecmath.Vector3f t = getVertexPos(faceData, 1);
		javax.vecmath.Vector3f v2 = getVertexPos(faceData, 2);
		v1.sub(t);
		t.set(getVertexPos(faceData, 0));
		v2.sub(t);
		v1.cross(v2, v1);
		v1.normalize();

		int x = ((byte)Math.round(v1.x * 127)) & 0xFF;
		int y = ((byte)Math.round(v1.y * 127)) & 0xFF;
		int z = ((byte)Math.round(v1.z * 127)) & 0xFF;

		int normal = x | (y << 0x08) | (z << 0x10);

		for (int i = 0; i < 4; i++)
		{
			faceData[i * 7 + 6] = normal;
		}
	}

	private static javax.vecmath.Vector3f getVertexPos(int[] data, int vertex)
	{
		int idx = vertex * 7;

		float x = Float.intBitsToFloat(data[idx]);
		float y = Float.intBitsToFloat(data[idx + 1]);
		float z = Float.intBitsToFloat(data[idx + 2]);

		return new javax.vecmath.Vector3f(x, y, z);
	}

	private void applyFacing(int[] p_178408_1_, EnumFacing p_178408_2_)
	{
		int[] aint = new int[p_178408_1_.length];
		System.arraycopy(p_178408_1_, 0, aint, 0, p_178408_1_.length);
		float[] afloat = new float[EnumFacing.values().length];
		afloat[EnumFaceDirection.Constants.WEST_INDEX] = 999.0F;
		afloat[EnumFaceDirection.Constants.DOWN_INDEX] = 999.0F;
		afloat[EnumFaceDirection.Constants.NORTH_INDEX] = 999.0F;
		afloat[EnumFaceDirection.Constants.EAST_INDEX] = -999.0F;
		afloat[EnumFaceDirection.Constants.UP_INDEX] = -999.0F;
		afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = -999.0F;

		for (int i = 0; i < 4; ++i)
		{
			int j = 7 * i;
			float f = Float.intBitsToFloat(aint[j]);
			float f1 = Float.intBitsToFloat(aint[j + 1]);
			float f2 = Float.intBitsToFloat(aint[j + 2]);

			if (f < afloat[EnumFaceDirection.Constants.WEST_INDEX])
			{
				afloat[EnumFaceDirection.Constants.WEST_INDEX] = f;
			}

			if (f1 < afloat[EnumFaceDirection.Constants.DOWN_INDEX])
			{
				afloat[EnumFaceDirection.Constants.DOWN_INDEX] = f1;
			}

			if (f2 < afloat[EnumFaceDirection.Constants.NORTH_INDEX])
			{
				afloat[EnumFaceDirection.Constants.NORTH_INDEX] = f2;
			}

			if (f > afloat[EnumFaceDirection.Constants.EAST_INDEX])
			{
				afloat[EnumFaceDirection.Constants.EAST_INDEX] = f;
			}

			if (f1 > afloat[EnumFaceDirection.Constants.UP_INDEX])
			{
				afloat[EnumFaceDirection.Constants.UP_INDEX] = f1;
			}

			if (f2 > afloat[EnumFaceDirection.Constants.SOUTH_INDEX])
			{
				afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = f2;
			}
		}

		EnumFaceDirection enumfacedirection = EnumFaceDirection.getFacing(p_178408_2_);

		for (int i1 = 0; i1 < 4; ++i1)
		{
			int j1 = 7 * i1;
			EnumFaceDirection.VertexInformation enumfacedirection$vertexinformation = enumfacedirection.getVertexInformation(i1);
			float f8 = afloat[enumfacedirection$vertexinformation.xIndex];
			float f3 = afloat[enumfacedirection$vertexinformation.yIndex];
			float f4 = afloat[enumfacedirection$vertexinformation.zIndex];
			p_178408_1_[j1] = Float.floatToRawIntBits(f8);
			p_178408_1_[j1 + 1] = Float.floatToRawIntBits(f3);
			p_178408_1_[j1 + 2] = Float.floatToRawIntBits(f4);

			for (int k = 0; k < 4; ++k)
			{
				int l = 7 * k;
				float f5 = Float.intBitsToFloat(aint[l]);
				float f6 = Float.intBitsToFloat(aint[l + 1]);
				float f7 = Float.intBitsToFloat(aint[l + 2]);

				if (epsilonEquals(f8, f5) && epsilonEquals(f3, f6) && epsilonEquals(f4, f7))
				{
					p_178408_1_[j1 + 4] = aint[l + 4];
					p_178408_1_[j1 + 4 + 1] = aint[l + 4 + 1];
				}
			}
		}
	}

	public static boolean epsilonEquals(float p_180185_0_, float p_180185_1_)
	{
		return Math.abs(p_180185_1_ - p_180185_0_) < 1.0E-5F;
	}

	public static EnumFacing getFacingFromVertexData(int[] faceData)
	{
		Vector3f vector3f = new Vector3f(Float.intBitsToFloat(faceData[0]), Float.intBitsToFloat(faceData[1]), Float.intBitsToFloat(faceData[2]));
		Vector3f vector3f1 = new Vector3f(Float.intBitsToFloat(faceData[7]), Float.intBitsToFloat(faceData[8]), Float.intBitsToFloat(faceData[9]));
		Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(faceData[14]), Float.intBitsToFloat(faceData[15]), Float.intBitsToFloat(faceData[16]));
		Vector3f vector3f3 = new Vector3f();
		Vector3f vector3f4 = new Vector3f();
		Vector3f vector3f5 = new Vector3f();
		Vector3f.sub(vector3f, vector3f1, vector3f3);
		Vector3f.sub(vector3f2, vector3f1, vector3f4);
		Vector3f.cross(vector3f4, vector3f3, vector3f5);
		float f = (float)Math.sqrt((double)(vector3f5.x * vector3f5.x + vector3f5.y * vector3f5.y + vector3f5.z * vector3f5.z));
		vector3f5.x /= f;
		vector3f5.y /= f;
		vector3f5.z /= f;
		EnumFacing enumfacing = null;
		float f1 = 0.0F;

		for (EnumFacing enumfacing1 : EnumFacing.values())
		{
			Vec3 vec3i = getDirectionVec(enumfacing1);
			Vector3f vector3f6 = new Vector3f((float)vec3i.xCoord, (float)vec3i.yCoord, (float)vec3i.zCoord);
			float f2 = Vector3f.dot(vector3f5, vector3f6);

			if (f2 >= 0.0F && f2 > f1)
			{
				f1 = f2;
				enumfacing = enumfacing1;
			}
		}

		if (enumfacing == null)
		{
			return EnumFacing.UP;
		}
		else
		{
			return enumfacing;
		}
	}

	private static Vec3 getDirectionVec(EnumFacing enumfacing1)
	{
		switch (enumfacing1)
		{
			case DOWN:
				return Vec3.createVectorHelper(0, -1, 0);
			case UP:
				return Vec3.createVectorHelper(0, 1, 0);
			case NORTH:
				return Vec3.createVectorHelper(0, 0, -1);
			case SOUTH:
				return Vec3.createVectorHelper(0, 0, 1);
			case WEST:
				return Vec3.createVectorHelper(-1, 0, 0);
			case EAST:
				return Vec3.createVectorHelper(1, 0, 0);
		}
		return null;
	}

	private float[] getPositionsDiv16(Vector3f pos1, Vector3f pos2)
	{
		float[] afloat = new float[EnumFacing.values().length];
		afloat[EnumFaceDirection.Constants.WEST_INDEX] = pos1.x / 16.0F;
		afloat[EnumFaceDirection.Constants.DOWN_INDEX] = pos1.y / 16.0F;
		afloat[EnumFaceDirection.Constants.NORTH_INDEX] = pos1.z / 16.0F;
		afloat[EnumFaceDirection.Constants.EAST_INDEX] = pos2.x / 16.0F;
		afloat[EnumFaceDirection.Constants.UP_INDEX] = pos2.y / 16.0F;
		afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = pos2.z / 16.0F;
		return afloat;
	}

	private int[] makeQuadVertexData(BlockFaceUV uvs, TextureAtlasSprite sprite, EnumFacing orientation, float[] p_188012_4_, BlockPartRotation partRotation, boolean shade)
	{
		int[] aint = new int[28];

		for (int i = 0; i < 4; ++i)
		{
			fillVertexData(aint, i, orientation, uvs, p_188012_4_, sprite, partRotation, shade);
		}

		return aint;
	}

	private void fillVertexData(int[] p_188015_1_, int p_188015_2_, EnumFacing p_188015_3_, BlockFaceUV p_188015_4_, float[] p_188015_5_, TextureAtlasSprite p_188015_6_, BlockPartRotation p_188015_8_, boolean p_188015_9_)
	{
		EnumFacing enumfacing = p_188015_3_;
		int i = p_188015_9_ ? getFaceShadeColor(enumfacing) : -1;
		EnumFaceDirection.VertexInformation enumfacedirection$vertexinformation = EnumFaceDirection.getFacing(p_188015_3_).getVertexInformation(p_188015_2_);
		Vector3f vector3f = new Vector3f(p_188015_5_[enumfacedirection$vertexinformation.xIndex], p_188015_5_[enumfacedirection$vertexinformation.yIndex], p_188015_5_[enumfacedirection$vertexinformation.zIndex]);
		rotatePart(vector3f, p_188015_8_);
		storeVertexData(p_188015_1_, p_188015_2_, p_188015_2_, vector3f, i, p_188015_6_, p_188015_4_);
	}

	private void storeVertexData(int[] faceData, int storeIndex, int vertexIndex, Vector3f position, int shadeColor, TextureAtlasSprite sprite, BlockFaceUV faceUV)
	{
		int i = storeIndex * 7;
		faceData[i] = Float.floatToRawIntBits(position.x);
		faceData[i + 1] = Float.floatToRawIntBits(position.y);
		faceData[i + 2] = Float.floatToRawIntBits(position.z);
		faceData[i + 3] = shadeColor;
		faceData[i + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU((double)faceUV.getVertexU(vertexIndex) * .999 + faceUV.getVertexU((vertexIndex + 2) % 4) * .001));
		faceData[i + 4 + 1] = Float.floatToRawIntBits(sprite.getInterpolatedV((double)faceUV.getVertexV(vertexIndex) * .999 + faceUV.getVertexV((vertexIndex + 2) % 4) * .001));
		Tessellator.instance.setColorOpaque_I(shadeColor);
		Tessellator.instance.addVertexWithUV(position.x, position.y, position.z, sprite.getInterpolatedU((double)faceUV.getVertexU(vertexIndex) * .999 + faceUV.getVertexU((vertexIndex + 2) % 4) * .001), sprite.getInterpolatedV((double)faceUV.getVertexV(vertexIndex) * .999 + faceUV.getVertexV((vertexIndex + 2) % 4) * .001));
	}

	private void rotatePart(Vector3f p_178407_1_, @Nullable BlockPartRotation partRotation)
	{
		if (partRotation != null)
		{
			Matrix4f matrix4f = getMatrixIdentity();
			Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);

			switch (partRotation.axis)
			{
				case X:
					Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(1.0F, 0.0F, 0.0F), matrix4f, matrix4f);
					vector3f.set(0.0F, 1.0F, 1.0F);
					break;
				case Y:
					Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(0.0F, 1.0F, 0.0F), matrix4f, matrix4f);
					vector3f.set(1.0F, 0.0F, 1.0F);
					break;
				case Z:
					Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(0.0F, 0.0F, 1.0F), matrix4f, matrix4f);
					vector3f.set(1.0F, 1.0F, 0.0F);
			}

			if (partRotation.rescale)
			{
				if (Math.abs(partRotation.angle) == 22.5F)
				{
					vector3f.scale(SCALE_ROTATION_22_5);
				}
				else
				{
					vector3f.scale(SCALE_ROTATION_GENERAL);
				}

				Vector3f.add(vector3f, new Vector3f(1.0F, 1.0F, 1.0F), vector3f);
			}
			else
			{
				vector3f.set(1.0F, 1.0F, 1.0F);
			}

			rotateScale(p_178407_1_, new Vector3f(partRotation.origin), matrix4f, vector3f);
		}
	}

	private void rotateScale(Vector3f position, Vector3f rotationOrigin, Matrix4f rotationMatrix, Vector3f scale)
	{
		Vector4f vector4f = new Vector4f(position.x - rotationOrigin.x, position.y - rotationOrigin.y, position.z - rotationOrigin.z, 1.0F);
		Matrix4f.transform(rotationMatrix, vector4f, vector4f);
		vector4f.x *= scale.x;
		vector4f.y *= scale.y;
		vector4f.z *= scale.z;
		position.set(vector4f.x + rotationOrigin.x, vector4f.y + rotationOrigin.y, vector4f.z + rotationOrigin.z);
	}

	private Matrix4f getMatrixIdentity()
	{
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.setIdentity();
		return matrix4f;
	}

	private int getFaceShadeColor(EnumFacing facing)
	{
		float f = getFaceBrightness(facing);
		int i = MathHelper.clamp_int((int)(f * 255.0F), 0, 255);
		return -16777216 | i << 16 | i << 8 | i;
	}

	private float getFaceBrightness(EnumFacing facing)
	{
		switch (facing)
		{
			case DOWN:
				return 0.5F;
			case UP:
				return 1.0F;
			case NORTH:
			case SOUTH:
				return 0.8F;
			case WEST:
			case EAST:
				return 0.6F;
			default:
				return 1.0F;
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return id;
	}
}
