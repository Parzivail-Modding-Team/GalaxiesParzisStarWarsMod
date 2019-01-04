package com.parzivail.util.render.pipeline;

import com.parzivail.util.block.IBlockMetadataRotate;
import com.parzivail.util.block.INameProvider;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.GL;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class JsonBlockRenderer extends JsonModelRenderer implements ISimpleBlockRenderingHandler
{
	protected final int id;

	public <T extends Block & INameProvider> JsonBlockRenderer(T block, ResourceLocation modelLocation)
	{
		super(modelLocation);
		id = block.getName().hashCode();
		block.setTextureName(translateTextureName(model.textures.get("particle")));
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		GL.PushAttrib(AttribMask.EnableBit);
		RenderHelper.disableStandardItemLighting();
		if (!compiled)
		{
			displayList = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(displayList, GL11.GL_COMPILE);

			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			drawBlock(null, 0, 0, 0, block, ModelRotation.X0_Y270, MAX_BRIGHTNESS);
			tessellator.draw();

			GL11.glEndList();

			compiled = true;
		}
		else
			GL11.glCallList(displayList);
		GL.PopAttrib();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		Tessellator tessellator = Tessellator.instance;
		tessellator.addTranslation(x, y, z);
		int brightness = block.getMixedBrightnessForBlock(world, x, y, z);

		ModelRotation rotation = ModelRotation.X0_Y0;

		if (block instanceof IBlockMetadataRotate)
		{
			float angle = world.getBlockMetadata(x, y, z) * 45 + 180;
			rotation = new ModelRotation(0, (int)angle);
		}

		drawBlock(world, x, y, z, block, rotation, brightness);

		tessellator.addTranslation(-x, -y, -z);
		return true;
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
