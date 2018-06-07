package com.parzivail.swg.render.console;

import com.parzivail.swg.Resources;
import com.parzivail.swg.tile.console.TileConsoleHoth3;
import com.parzivail.util.entity.EntityTilePassthrough;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderConsoleHothCurved3 extends TileEntitySpecialRenderer
{
	public static ResourceLocation texture = Resources.location("textures/model/consoleHothCurved3.png");

	private final ModelBase model;

	public RenderConsoleHothCurved3()
	{
		this.model = new ModelConsoleHothCurved3();
	}

	private void adjustRotatePivotViaMeta(World world, int x, int y, int z)
	{
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tickTime)
	{
		GL11.glPushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		GL11.glTranslated(x + 0.5f, y + 1.5f, z + 0.5f);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL.Scale(1.25f);
		GL11.glRotatef(90 * ((TileConsoleHoth3)te).getFacing(), 0, 1, 0);
		this.model.render(new EntityTilePassthrough(te), 0, 0, 0, 0.0F, 0.0F, 0.05F);
		GL11.glPopMatrix();
	}
}
