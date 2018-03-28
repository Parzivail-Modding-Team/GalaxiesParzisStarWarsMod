package com.parzivail.swg.weapon.blastermodule;

import com.parzivail.util.ui.Fx;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ScopeAcog implements IBlasterScope
{
	@Override
	public void draw(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
		float d = (float)(sr.getScaledHeight_double() / 6);
		GL11.glLineWidth(2);
		Fx.D2.DrawWireCircle(0, 0, d);

		float ohSixD = 0.06f * d;
		Fx.D2.DrawLine(0, d / 3f, 0, d);
		Fx.D2.DrawLine(-2, 2 * d / 3f, 2, 2 * d / 3f);

		GL11.glColor4f(1, 0, 0, 1);
		Fx.D2.DrawLine(-2, d / 3f, 2, d / 3f);
		Fx.D2.DrawLine(-2, d / 6f, 2, d / 6f);

		Fx.D2.DrawLine(0, -ohSixD, 0, d / 3f);
		Fx.D2.DrawLine(0, -ohSixD, ohSixD, 0);
		Fx.D2.DrawLine(-ohSixD, 0, 0, -ohSixD);
	}

	@Override
	public float getZoomLevel()
	{
		return 0.4f;
	}

	@Override
	public String getName()
	{
		return "ACOG";
	}
}
