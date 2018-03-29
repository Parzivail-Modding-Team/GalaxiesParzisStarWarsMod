package com.parzivail.swg.weapon.blastermodule.scope;

import com.parzivail.util.ui.Fx;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ScopeSpitfire extends BlasterScope
{
	public ScopeSpitfire()
	{
		super("spitfire");
	}

	@Override
	public void draw(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
		GL11.glColor4f(0.1f, 0.1f, 0.1f, 1);

		float d = (float)(sr.getScaledHeight_double() / 6);
		GL11.glLineWidth(2);
		Fx.D2.DrawWireCircle(0, 0, d);

		float lineDist = 0.08f * d;

		Fx.D2.DrawLine(0, -lineDist, 0, d);
		Fx.D2.DrawLine(-lineDist, 0, lineDist, 0);
		Fx.D2.DrawLine(-0.04f * d, lineDist, 0.04f * d, lineDist);
		Fx.D2.DrawLine(-0.03f * d, lineDist * 2, 0.03f * d, lineDist * 2);
		Fx.D2.DrawLine(-0.02f * d, lineDist * 3, 0.02f * d, lineDist * 3);

		Fx.D2.DrawWireArc(0, 0, d / 3, -130, -50);
		Fx.D2.DrawWireArc(0, 0, d / 3, -220, -140);
		Fx.D2.DrawWireArc(0, 0, d / 3, -310, -230);
	}

	@Override
	public float getZoomLevel()
	{
		return 0.4f;
	}
}
