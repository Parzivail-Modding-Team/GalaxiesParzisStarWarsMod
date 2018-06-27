package com.parzivail.swg.render.ladder;

import com.parzivail.swg.Resources;
import com.parzivail.util.block.TileRotatable;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderLadder extends TileEntitySpecialRenderer
{
	public static ResourceLocation texture = new ResourceLocation(Resources.MODID + ":" + "textures/model/ladder.png");
	public static ResourceLocation textureTop = new ResourceLocation(Resources.MODID + ":" + "textures/model/ladderTop.png");

	private final ModelBase model;
	private final ModelBase modelTop;

	public RenderLadder()
	{
		model = new ModelLadder();
		modelTop = new ModelLadderTop();
	}

	private void adjustRotatePivotViaMeta(World world, int x, int y, int z)
	{
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tickTime)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5f, y + 1.5f, z + 0.5f);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(90 * ((TileRotatable)te).getFacing(), 0, 1, 0);
		GL.Scale(1.25f);
		if (te.getWorld().getBlock(te.xCoord, te.yCoord + 1, te.zCoord) == Blocks.air)
		{
			Minecraft.getMinecraft().renderEngine.bindTexture(textureTop);
			modelTop.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.05F);
		}
		else
		{
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
			model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.05F);
		}
		GL11.glPopMatrix();
	}
}
