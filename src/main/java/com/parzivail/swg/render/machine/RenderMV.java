package com.parzivail.swg.render.machine;

import com.parzivail.swg.Resources;
import com.parzivail.swg.tile.machine.TileMV;
import com.parzivail.util.block.TileRotatable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderMV extends TileEntitySpecialRenderer
{
	public static ResourceLocation texture = new ResourceLocation(Resources.MODID + ":" + "textures/model/moistureVaporator.png");

	private final ModelMV model;

	public RenderMV()
	{
		model = new ModelMV();
	}

	private void adjustRotatePivotViaMeta(World world, int x, int y, int z)
	{
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tickTime)
	{
		GL11.glPushMatrix();
		TileMV mv = (TileMV)te;
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.8F, (float)z + 0.5F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glScalef(1.5F, 1.5F, 1.5F);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(90 * ((TileRotatable)te).getFacing(), 0, 1, 0);
		float r = (float)((mv.frame + tickTime) / 180f * Math.PI) * 5;
		model.windVane1.rotateAngleY = r;
		model.windVane2.rotateAngleY = r;
		model.windVaneRod1.rotateAngleY = r;
		model.windVaneRod2.rotateAngleY = r;
		model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.05F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
}
