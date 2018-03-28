package com.parzivail.swg.weapon.blastermodule;

import com.parzivail.util.ui.Fx;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ScopeReflex implements IBlasterScope
{
	@Override
	public void draw(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
		GL11.glColor4f(1, 0, 0, 1);

		float d = 3;
		GL11.glLineWidth(2);
		Fx.D2.DrawWireCircle(0, 0, d);
		Fx.D2.DrawPoint(0, 0, 5);

		Fx.D2.DrawLine(0, d, 0, d + 1.5f);
		Fx.D2.DrawLine(0, -d, 0, -d - 1.5f);
		Fx.D2.DrawLine(d, 0, d + 1.5f, 0);
		Fx.D2.DrawLine(-d, 0, -d - 1.5f, 0);
	}

	@Override
	public float getZoomLevel()
	{
		return 1;
	}

	@Override
	public String getName()
	{
		return "Reflex";
	}
}
