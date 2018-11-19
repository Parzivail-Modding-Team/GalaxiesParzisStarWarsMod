package com.parzivail.swg.render.sbrh;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.render.pipeline.*;
import com.parzivail.util.binary.PIO;
import com.parzivail.util.block.PBlockContainer;
import com.parzivail.util.block.TileRotatable;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import jdk.internal.util.xml.impl.ReaderUTF8;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import javax.annotation.Nullable;

public class JsonBlockRenderer implements ISimpleBlockRenderingHandler
{
	private static final float SCALE_ROTATION_22_5 = 1.0F / (float)Math.cos(0.39269909262657166D) - 1.0F;
	private static final float SCALE_ROTATION_GENERAL = 1.0F / (float)Math.cos((Math.PI / 4D)) - 1.0F;

	private final int id;
	private final ModelBlock model;

	public JsonBlockRenderer(PBlockContainer block, ResourceLocation modelLocation)
	{
		id = block.name.hashCode();
		model = ModelBlock.deserialize(new ReaderUTF8(PIO.getResource(StarWarsGalaxy.class, modelLocation)));
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		RenderHelper.disableStandardItemLighting();
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		drawBlock(block, ModelRotation.X0_Y0, 0x1fffff);
		tessellator.draw();
		RenderHelper.enableStandardItemLighting();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		Tessellator tessellator = Tessellator.instance;
		tessellator.addTranslation(x, y, z);
		int brightness = block.getMixedBrightnessForBlock(world, x, y, z);

		ModelRotation rotation = ModelRotation.X0_Y0;

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileRotatable)
		{
			TileRotatable tile = (TileRotatable)te;
			float angle = 90 * tile.getFacing() + 180;

			rotation = new ModelRotation(0, (int)angle);
		}

		drawBlock(block, rotation, brightness);

		tessellator.addTranslation(-x, -y, -z);
		return true;
	}

	private void drawBlock(Block block, ITransformation modelRotationIn, int brightness)
	{
		RenderBlocks.getInstance().setRenderBoundsFromBlock(block);
		for (BlockPart blockpart : model.getElements())
		{
			for (EnumFacing enumfacing : blockpart.mapFaces.keySet())
			{
				BlockPartFace blockpartface = blockpart.mapFaces.get(enumfacing);
				String texName = translateTextureName(model.resolveTextureName(blockpartface.texture));
				TextureAtlasSprite textureatlassprite1 = Client.mc.getTextureMapBlocks().getAtlasSprite(texName);
				PartType type = PartType.Textured;

				if (texName.equals("pswg:model/special_lit"))
					type = PartType.Lit;

				drawQuad(blockpartface.blockFaceUV, textureatlassprite1, enumfacing, getPositionsDiv16(blockpart.positionFrom, blockpart.positionTo), blockpart.partRotation, modelRotationIn, brightness, type);
			}
		}
	}

	private String translateTextureName(String name)
	{
		return name.replace("blocks/", "");
	}

	private static javax.vecmath.Vector3f getVertexPos(int[] data, int vertex)
	{
		int idx = vertex * 7;

		float x = Float.intBitsToFloat(data[idx]);
		float y = Float.intBitsToFloat(data[idx + 1]);
		float z = Float.intBitsToFloat(data[idx + 2]);

		return new javax.vecmath.Vector3f(x, y, z);
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

	private void drawQuad(BlockFaceUV uvs, TextureAtlasSprite sprite, EnumFacing orientation, float[] p_188012_4_, BlockPartRotation partRotation, ITransformation transformation, int brightness, PartType type)
	{
		for (int i = 0; i < 4; ++i)
			drawVertex(i, orientation, uvs, p_188012_4_, sprite, partRotation, transformation, brightness, type);
	}

	private void drawVertex(int storeIndex, EnumFacing facing, BlockFaceUV faceUV, float[] p_188015_5_, TextureAtlasSprite sprite, BlockPartRotation rotation, ITransformation transformation, int brightness, PartType type)
	{
		EnumFacing enumfacing = transformation.rotate(facing);
		int shadeColor = type == PartType.Lit ? 0xFFFFFF : getFaceShadeColor(enumfacing);
		EnumFaceDirection.VertexInformation vertexInformation = EnumFaceDirection.getFacing(facing).getVertexInformation(storeIndex);
		Vector3f position = new Vector3f(p_188015_5_[vertexInformation.xIndex], p_188015_5_[vertexInformation.yIndex], p_188015_5_[vertexInformation.zIndex]);
		rotatePart(position, rotation);
		rotateVertex(position, facing, storeIndex, transformation);
		int[] faceData = new int[28];
		storeVertexData(faceData, storeIndex, storeIndex, position, shadeColor, sprite, faceUV);

		javax.vecmath.Vector3f v1 = getVertexPos(faceData, 3);
		javax.vecmath.Vector3f t = getVertexPos(faceData, 1);
		javax.vecmath.Vector3f v2 = getVertexPos(faceData, 2);
		v1.sub(t);
		t.set(getVertexPos(faceData, 0));
		v2.sub(t);
		v1.cross(v2, v1);
		v1.normalize();

		Tessellator.instance.setNormal(v1.x, v1.y, v1.z);
		Tessellator.instance.setColorOpaque_I(shadeColor);
		if (type == PartType.Lit)
			Tessellator.instance.setBrightness(0xdfffff);
		else
			Tessellator.instance.setBrightness(brightness);
		Tessellator.instance.addVertexWithUV(position.x, position.y, position.z, sprite.getInterpolatedU((double)faceUV.getVertexU(storeIndex) * .999 + faceUV.getVertexU((storeIndex + 2) % 4) * .001), sprite.getInterpolatedV((double)faceUV.getVertexV(storeIndex) * .999 + faceUV.getVertexV((storeIndex + 2) % 4) * .001));
	}

	public int rotateVertex(Vector3f p_188011_1_, EnumFacing p_188011_2_, int p_188011_3_, ITransformation p_188011_4_)
	{
		if (p_188011_4_ == ModelRotation.X0_Y0)
		{
			return p_188011_3_;
		}
		else
		{
			transform(p_188011_1_, p_188011_4_.getMatrix());
			return p_188011_4_.rotate(p_188011_2_, p_188011_3_);
		}
	}

	public static void transform(org.lwjgl.util.vector.Vector3f vec, javax.vecmath.Matrix4f m)
	{
		javax.vecmath.Vector4f tmp = new javax.vecmath.Vector4f(vec.x, vec.y, vec.z, 1f);
		m.transform(tmp);
		if (Math.abs(tmp.w - 1f) > 1e-5)
			tmp.scale(1f / tmp.w);
		vec.set(tmp.x, tmp.y, tmp.z);
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
