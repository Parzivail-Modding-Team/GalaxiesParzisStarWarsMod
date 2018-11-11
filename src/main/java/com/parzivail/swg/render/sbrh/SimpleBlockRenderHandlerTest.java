package com.parzivail.swg.render.sbrh;

import com.parzivail.swg.render.machine.ModelMV;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;

public class SimpleBlockRenderHandlerTest implements ISimpleBlockRenderingHandler
{
	private final int id;

	public SimpleBlockRenderHandlerTest(int id)
	{
		this.id = id;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		Tessellator tessellator = Tessellator.instance;

		ModelBase m = new ModelMV();
		IIcon icon = block.getIcon(0, 0);
		float minu = icon.getMinU();
		float maxu = icon.getMaxU();
		float minv = icon.getMinV();
		float maxv = icon.getMaxV();

		tessellator.addTranslation(x + 0.5f, y + 1.5f, z + 0.5f);
		for (Object o : m.boxList)
		{
			ModelRenderer modelRenderer = (ModelRenderer)o;
			int s = ReflectionHelper.getPrivateValue(ModelRenderer.class, modelRenderer, "textureOffsetX");
			int t = ReflectionHelper.getPrivateValue(ModelRenderer.class, modelRenderer, "textureOffsetY");

			for (int i = 0; i < modelRenderer.cubeList.size(); ++i)
			{
				ModelBox box = ((ModelBox)modelRenderer.cubeList.get(i));

				// x' = -x
				// y' = -y

				float posX1 = -(box.posX1 + modelRenderer.rotationPointX);
				float posY1 = -(box.posY1 + modelRenderer.rotationPointY);
				float posZ1 = box.posZ1 + modelRenderer.rotationPointZ;
				float posX2 = -(box.posX2 + modelRenderer.rotationPointX);
				float posY2 = -(box.posY2 + modelRenderer.rotationPointY);
				float posZ2 = box.posZ2 + modelRenderer.rotationPointZ;
				int width = -(int)(posX2 - posX1);
				int height = -(int)(posY2 - posY1);
				int depth = (int)(posZ2 - posZ1);

				if (modelRenderer.mirror)
				{
					float f7 = posX2;
					posX2 = posX1;
					posX1 = f7;
				}

				TexturedQuad[] quadList = new TexturedQuad[6];

				PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(posX1, posY1, posZ1, 0.0F, 0.0F);
				PositionTextureVertex positiontexturevertex0 = new PositionTextureVertex(posX2, posY1, posZ1, 0.0F, 8.0F);
				PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(posX2, posY2, posZ1, 8.0F, 8.0F);
				PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(posX1, posY2, posZ1, 8.0F, 0.0F);
				PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(posX1, posY1, posZ2, 0.0F, 0.0F);
				PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(posX2, posY1, posZ2, 0.0F, 8.0F);
				PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(posX2, posY2, posZ2, 8.0F, 8.0F);
				PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(posX1, posY2, posZ2, 8.0F, 0.0F);
				quadList[0] = new TexturedQuad(new PositionTextureVertex[] {
						positiontexturevertex4, positiontexturevertex0, positiontexturevertex1, positiontexturevertex5
				}, s + depth + width, t + depth, s + depth + width + depth, t + depth + height, modelRenderer.textureWidth, modelRenderer.textureHeight);
				quadList[1] = new TexturedQuad(new PositionTextureVertex[] {
						positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2
				}, s, t + depth, s + depth, t + depth + height, modelRenderer.textureWidth, modelRenderer.textureHeight);
				quadList[2] = new TexturedQuad(new PositionTextureVertex[] {
						positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex0
				}, s + depth, t, s + depth + width, t + depth, modelRenderer.textureWidth, modelRenderer.textureHeight);
				quadList[3] = new TexturedQuad(new PositionTextureVertex[] {
						positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5
				}, s + depth + width, t + depth, s + depth + width + width, t, modelRenderer.textureWidth, modelRenderer.textureHeight);
				quadList[4] = new TexturedQuad(new PositionTextureVertex[] {
						positiontexturevertex0, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1
				}, s + depth, t + depth, s + depth + width, t + depth + height, modelRenderer.textureWidth, modelRenderer.textureHeight);
				quadList[5] = new TexturedQuad(new PositionTextureVertex[] {
						positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6
				}, s + depth + width + depth, t + depth, s + depth + width + depth + width, t + depth + height, modelRenderer.textureWidth, modelRenderer.textureHeight);

				for (TexturedQuad texturedQuad : quadList)
				{
					float sc = 0.0625f;
					Vec3 vec3 = texturedQuad.vertexPositions[1].vector3D.subtract(texturedQuad.vertexPositions[0].vector3D);
					Vec3 vec31 = texturedQuad.vertexPositions[1].vector3D.subtract(texturedQuad.vertexPositions[2].vector3D);
					Vec3 vec32 = vec31.crossProduct(vec3).normalize();

					tessellator.setNormal((float)vec32.xCoord, (float)vec32.yCoord, (float)vec32.zCoord);

					for (int i1 = 0; i1 < 4; ++i1)
					{
						PositionTextureVertex positiontexturevertex = texturedQuad.vertexPositions[i1];
						float tu = minu + (maxu - minu) * positiontexturevertex.texturePositionX;
						float tv = minv + (maxv - minv) * positiontexturevertex.texturePositionY;
						tessellator.addVertexWithUV((double)((float)positiontexturevertex.vector3D.xCoord * sc), (double)((float)positiontexturevertex.vector3D.yCoord * sc), (double)((float)positiontexturevertex.vector3D.zCoord * sc), tu, tv);
					}
				}
			}
		}
		tessellator.addTranslation(-(x + 0.5f), -(y + 1.5f), -(z + 0.5f));

		return true;
	}

	private void draw(TexturedQuad texturedQuad, Tessellator tessellator)
	{
		float s = 0.0625f;
		Vec3 vec3 = texturedQuad.vertexPositions[1].vector3D.subtract(texturedQuad.vertexPositions[0].vector3D);
		Vec3 vec31 = texturedQuad.vertexPositions[1].vector3D.subtract(texturedQuad.vertexPositions[2].vector3D);
		Vec3 vec32 = vec31.crossProduct(vec3).normalize();

		tessellator.setNormal((float)vec32.xCoord, (float)vec32.yCoord, (float)vec32.zCoord);

		for (int i = 0; i < 4; ++i)
		{
			PositionTextureVertex positiontexturevertex = texturedQuad.vertexPositions[i];
			tessellator.addVertexWithUV((double)((float)positiontexturevertex.vector3D.xCoord * s), (double)((float)positiontexturevertex.vector3D.yCoord * s), (double)((float)positiontexturevertex.vector3D.zCoord * s), (double)positiontexturevertex.texturePositionX, (double)positiontexturevertex.texturePositionY);
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return false;
	}

	@Override
	public int getRenderId()
	{
		return id;
	}
}
