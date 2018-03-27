package com.parzivail.swg.weapon.blastermodule;

import com.parzivail.util.ui.Fx;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ScopeRingReticle implements IBlasterScope
{
	@Override
	public void draw(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
		float d = (float)(sr.getScaledHeight_double() / 6);
		GL11.glLineWidth(2);
		Fx.D2.DrawWireCircle(0, 0, d);

		Fx.D2.DrawLine(-d, 0, -d / 2, 0);
		Fx.D2.DrawLine(d, 0, d / 2, 0);

		Fx.D2.DrawLine(0, -d, 0, -d / 2);
		Fx.D2.DrawLine(0, d, 0, d / 2);

		GL11.glLineWidth(1);

		Fx.D2.DrawLine(-d / 2, 0, d / 2, 0);
		Fx.D2.DrawLine(0, -d / 2, 0, d / 2);
	}
}
