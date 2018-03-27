package com.parzivail.swg.weapon.blastermodule;

import com.parzivail.util.ui.Fx;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ScopeSpitfire implements IBlasterScope
{
	@Override
	public void draw(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
		float d = (float)(sr.getScaledHeight_double() / 6);
		GL11.glLineWidth(2);
		Fx.D2.DrawWireCircle(0, 0, d);

		float ohSixD = 0.06f * d;

		Fx.D2.DrawLine(0, -ohSixD, 0, d);
		Fx.D2.DrawLine(-ohSixD, 0, ohSixD, 0);
		Fx.D2.DrawLine(-0.035f * d, ohSixD, 0.035f * d, ohSixD);
		Fx.D2.DrawLine(-0.025f * d, ohSixD * 2, 0.025f * d, ohSixD * 2);
		Fx.D2.DrawLine(-0.0185f * d, ohSixD * 3, 0.0185f * d, ohSixD * 3);

		Fx.D2.DrawWireArc(0, 0, d / 4, -130, -50);
		Fx.D2.DrawWireArc(0, 0, d / 4, -220, -140);
		Fx.D2.DrawWireArc(0, 0, d / 4, -310, -230);
	}
}
