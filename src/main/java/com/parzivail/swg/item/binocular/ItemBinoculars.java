package com.parzivail.swg.item.binocular;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.ICustomCrosshair;
import com.parzivail.swg.item.ILeftClickInterceptor;
import com.parzivail.swg.item.PItem;
import com.parzivail.swg.item.binocular.data.BinocularData;
import com.parzivail.swg.item.binocular.data.BinocularDescriptor;
import com.parzivail.util.common.AnimatedValue;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemBinoculars extends PItem implements ICustomCrosshair, ILeftClickInterceptor
{
	private final BinocularDescriptor descriptor;

	private AnimatedValue avZoomLevel;

	public ItemBinoculars(BinocularDescriptor descriptor)
	{
		super("binoc." + descriptor.name);
		this.descriptor = descriptor;
		this.maxStackSize = 1;

		avZoomLevel = new AnimatedValue(0, 500);
	}

	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
	}

	@Override
	public boolean isFull3D()
	{
		return true;
	}

	@Override
	public boolean shouldUsePrecisionMovement(ItemStack stack, World world, EntityPlayer player)
	{
		BinocularData bd = new BinocularData(stack);
		return bd.isZooming;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List text, boolean advancedItemTooltips)
	{
		BinocularData bd = new BinocularData(stack);

		text.add(String.format("%s: %sx", I18n.format(Resources.guiDot("zoomLevel")), bd.zoomLevel));
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		BinocularData bd = new BinocularData(stack);

		bd.isZooming = !bd.isZooming;

		bd.serialize(stack.stackTagCompound);
		return stack;
	}

	@Override
	public float getZoomLevel(ItemStack stack, World world, EntityPlayer player)
	{
		BinocularData bd = new BinocularData(stack);
		float z = bd.zoomLevel / 4f;
		return z + (1 - z) * descriptor.maxZoom;
	}

	@Override
	public void drawCrosshair(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
		BinocularData bd = new BinocularData(stack);

		float size = 2;

		if (bd.isZooming)
		{
			//			GL.Enable(EnableCap.LineSmooth);
			//			GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
			//			bd.getScope().draw(sr, player, stack);
		}
		else
		{
			//			float onePixel = 1f / sr.getScaleFactor();
			//
			//			GL11.glLineWidth(4);
			//			GL11.glColor4f(0, 0, 0, 1);
			//			Fx.D2.DrawLine(0, expansion - onePixel, 0, size + expansion + onePixel);
			//			Fx.D2.DrawLine(0, -expansion + onePixel, 0, -size - expansion - onePixel);
			//			Fx.D2.DrawLine(expansion - onePixel, 0, size + expansion + onePixel, 0);
			//			Fx.D2.DrawLine(-expansion + onePixel, 0, -size - expansion - onePixel, 0);
			//
			//			GL11.glLineWidth(2);
			//			GL11.glColor4f(1, 1, 1, 1);
			//			Fx.D2.DrawLine(0, expansion, 0, size + expansion);
			//			Fx.D2.DrawLine(0, -expansion, 0, -size - expansion);
			//			Fx.D2.DrawLine(expansion, 0, size + expansion, 0);
			//			Fx.D2.DrawLine(-expansion, 0, -size - expansion, 0);
		}

		GL.Enable(EnableCap.Texture2D);
		GL.PushMatrix();
		//		GL.Translate(sr.getScaledWidth_double() / 2, sr.getScaledHeight_double() / 2, 0);
		//
		//		String remaining = String.format("%s", bd.shotsRemaining);
		//		int w = mc.fontRendererObj.getStringWidth(remaining);
		//		int h = mc.fontRendererObj.FONT_HEIGHT;
		//
		//		GL.Translate(-w - 1, -h - 1, 0);
		//		mc.fontRendererObj.drawString(remaining, 0, 0, 0xFFFFFF);

		GL.PopMatrix();
	}

	@Override
	public void onItemLeftClick(ItemStack stack, World world, EntityPlayer player)
	{
		BinocularData bd = new BinocularData(stack);

		bd.zoomLevel--;
		if (bd.zoomLevel < 0)
			bd.zoomLevel = 4;

		bd.serialize(stack.stackTagCompound);
	}
}