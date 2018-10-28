package com.parzivail.swg.render.light;

import com.parzivail.swg.Resources;
import com.parzivail.util.block.TileRotatable;
import com.parzivail.util.entity.EntityTilePassthrough;
import com.parzivail.util.ui.Fx.D3;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderCeilingLight2 extends TileEntitySpecialRenderer
{
	public static ResourceLocation texture = new ResourceLocation(Resources.MODID + ":" + "textures/model/hothCeilingLight2.png");

	private final ModelBase model;

	public RenderCeilingLight2()
	{
		model = new ModelCeilingLight2();
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

		if (te instanceof TileRotatable)
			GL11.glRotatef(90 * ((TileRotatable)te).getFacing(), 0, 1, 0);

		GL11.glPushMatrix();
		GL.Scale(1.25f);

		model.render(te == null ? null : new EntityTilePassthrough(te), 0, 0, 0, 0.0F, 0.0F, 0.05F);
		GL11.glPopMatrix();

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		if (te != null)
		{

			int i = 1;
			boolean flag = false;
			while (i <= 2)
			{
				if (te.getWorld() == null || !te.getWorld().isAirBlock(te.xCoord, te.yCoord + i, te.zCoord))
				{
					flag = true;
					break;
				}
				i++;
			}

			if (flag)
			{
				GL11.glColor4f(0, 0, 0, 1);
				GL11.glLineWidth((float)(30 / Math.sqrt(x * x + y * y + z * z)));
				D3.DrawLine(0, 0.5f, 0.4f, 0, -i + 1, 0.4f);
				D3.DrawLine(0, 0.5f, -0.4f, 0, -i + 1, -0.4f);
			}
		}
		GL11.glPopAttrib();

		GL11.glPopMatrix();
	}
}

