package com.parzivail.swg.weapon.blastermodule.scope;

import com.parzivail.swg.Resources;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachment;
import com.parzivail.util.math.MathFormat;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.EnumSet;

public abstract class BlasterScope extends BlasterAttachment
{
	public BlasterScope(String name, int price)
	{
		super(Resources.modDot("blaster", "scope", name), price);
	}

	@Override
	public String getInfoText()
	{
		float zL = getZoomLevel();
		return I18n.format(Resources.guiDot("xzoom"), MathFormat.DEC2.format(1 / zL));
	}

	@SideOnly(Side.CLIENT)
	public abstract void draw(ScaledResolution sr, EntityPlayer player, ItemStack stack);

	@Override
	public void drawInfoCard(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
		GL.PushAttrib(EnumSet.of(AttribMask.EnableBit, AttribMask.LineBit));
		GL.Enable(EnableCap.Blend);
		GL.Enable(EnableCap.LineSmooth);
		GL.Enable(EnableCap.PointSmooth);

		//Fx.D2.DrawWireRectangle(-sr.getScaledWidth(), -sr.getScaledHeight() / 2, 2 * sr.getScaledWidth(), sr.getScaledHeight());

		Client.mc.fontRendererObj.drawString("[3D MODEL HERE]", -70, -Client.mc.fontRendererObj.FONT_HEIGHT / 2, GLPalette.BLACK);

		GL.PushMatrix();
		GL.Translate(sr.getScaledWidth() - 25, 0, 0);

		String reticle = I18n.format(Resources.guiDot("reticle"));
		Client.mc.fontRendererObj.drawString(reticle, -Client.mc.fontRendererObj.getStringWidth(reticle) / 2, -25, GLPalette.BLACK);

		GL.Translate(0, 10, 0);
		GL.Disable(EnableCap.Texture2D);
		draw(sr, player, stack);

		GL.PopMatrix();

		GL.PopAttrib();
	}

	public abstract float getZoomLevel();
}
