package com.parzivail.swg.weapon.blastermodule;

import com.parzivail.util.ui.Fx;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ScopeRedDot implements IBlasterScope
{
	@Override
	public void draw(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
		GL11.glColor4f(1, 0, 0, 1);
		Fx.D2.DrawPoint(0, 0, 5);
	}

	@Override
	public float getZoomLevel()
	{
		return 1;
	}

	@Override
	public String getName()
	{
		return "Red Dot";
	}
}
