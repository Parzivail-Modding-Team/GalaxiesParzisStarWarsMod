package com.parzivail.swg.item.binocular;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.PItem;
import com.parzivail.swg.item.binocular.data.BinocularData;
import com.parzivail.swg.item.binocular.data.BinocularDescriptor;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.common.AnimatedValue;
import com.parzivail.util.item.IGuiOverlay;
import com.parzivail.util.item.ILeftClickInterceptor;
import com.parzivail.util.item.IScreenShader;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.ui.Fx.D2;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.ShaderHelper;
import com.parzivail.util.ui.TextUtil;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ItemBinoculars extends PItem implements IGuiOverlay, ILeftClickInterceptor, IScreenShader
{
	private final BinocularDescriptor descriptor;

	private final AnimatedValue avZoomLevel;

	public ItemBinoculars(BinocularDescriptor descriptor)
	{
		super("binoc." + descriptor.name);
		this.descriptor = descriptor;
		maxStackSize = 1;

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

		//text.add(String.format("%s: %sx", I18n.format(Resources.guiDot("zoomLevel")), bd.zoomLevel));
		text.add(TextUtil.graveToSection(String.format("%s: `r[`e%s`r]", I18n.format(Resources.guiDot("zoom")), Resources.getKeyName(Client.mc.gameSettings.keyBindAttack))));
		text.add(TextUtil.graveToSection(String.format("%s: `r[`e%s`r]", I18n.format(Resources.guiDot("use")), Resources.getKeyName(Client.mc.gameSettings.keyBindUseItem))));
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote)
			ShaderHelper.tareTimer();

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
	public boolean doesDrawOverlay(EntityPlayer player, ItemStack item)
	{
		return true;
	}

	@Override
	public void drawOverlay(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
		BinocularData bd = new BinocularData(stack);

		GL.Color(GLPalette.BLACK);
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.CullFace);
		GL.Enable(EnableCap.Blend);

		float zoom = avZoomLevel.animateTo((5 - bd.zoomLevel) * 100);
		float zoomRound = MathUtil.roundToNearest(zoom, 25);
		float diff = (zoomRound - zoom);

		GL.PushMatrix();
		GL.Translate(0, 0, 100);
		if (bd.isZooming)
		{
			GL.TessBeginPolygon(null);
			GL.TessNextContour();

			GL.Vertex2(-sr.getScaledWidth_double() / 2, -sr.getScaledHeight_double() / 2);
			GL.Vertex2(sr.getScaledWidth_double() / 2, -sr.getScaledHeight_double() / 2);
			GL.Vertex2(sr.getScaledWidth_double() / 2, sr.getScaledHeight_double() / 2);
			GL.Vertex2(-sr.getScaledWidth_double() / 2, sr.getScaledHeight_double() / 2);

			GL.TessNextContour();

			D2.BufferRoundRectangle(-160, -80, 320, 160, 15, 15, 15, 15, 15f);

			GL.TessNextContour();
			GL.Vertex2(-160, -8);
			GL.Vertex2(-130, -1);
			GL.Vertex2(-130, 1);
			GL.Vertex2(-160, 8);

			GL.TessNextContour();
			GL.Vertex2(160, -8);
			GL.Vertex2(130, -1);
			GL.Vertex2(130, 1);
			GL.Vertex2(160, 8);

			GL.TessNextContour();
			GL.Vertex2(70, 80);
			GL.Vertex2(60, 70);
			GL.Vertex2(-60, 70);
			GL.Vertex2(-70, 80);

			GL.TessNextContour();
			D2.BufferRoundRectangle(-40, 75, 80, 12, 3, 3, 3, 3, 15f);

			GL.TessEndPolygon();

			GL.Color(GLPalette.VERY_DARK_GREEN);
			GL.Begin(PrimitiveType.TriangleFan);
			D2.BufferRoundRectangle(-40, 75, 80, 12, 3, 3, 3, 3, 15f);
			GL.End();

			GL.Color(GLPalette.DARK_YELLOW_GREEN);
			D2.RoundRectangle(-20, 76, 40, 10, 3, 3, 3, 3, PrimitiveType.TriangleFan);
		}
		else
		{
			GL11.glLineWidth(4);
			GL11.glColor4f(0, 0, 0, 1);
			D2.DrawWireArc(0, 0, 6, -25, 85);
			D2.DrawWireArc(0, 0, 6, 95, 205);
			D2.DrawWireArc(0, 0, 6, 215, 325);

			GL11.glLineWidth(2);
			GL11.glColor4f(1, 1, 1, 1);
			D2.DrawWireArc(0, 0, 6, -25, 85);
			D2.DrawWireArc(0, 0, 6, 95, 205);
			D2.DrawWireArc(0, 0, 6, 215, 325);
		}
		GL.PopMatrix();

		if (bd.isZooming)
		{
			GL.Enable(EnableCap.Texture2D);
			GL.PushMatrix();

			String s = getMeterCenteredOn((int)zoomRound);
			String s1 = getMeterCenteredOnBefore((int)zoomRound);

			int centerWidth = Client.mc.fontRendererObj.getStringWidth(String.valueOf((int)zoomRound)) / 2;
			int width = Client.mc.fontRendererObj.getStringWidth(s1);

			int w = width - centerWidth;

			GL.Enable(EnableCap.ScissorTest);
			GL.Translate(-w + diff / 50f * Client.mc.fontRendererObj.getStringWidth(" - " + zoomRound + " - "), 77, 101);

			GL.Scissor(-40 + sr.getScaledWidth() / 2, 75 + sr.getScaledHeight() / 2, 80, 12);
			Client.mc.fontRendererObj.drawString(s, 0, 0, GLPalette.DARK_YELLOW_GREEN);

			GL.Scissor(-20 + sr.getScaledWidth() / 2, 75 + sr.getScaledHeight() / 2, 40, 12);
			Client.mc.fontRendererObj.drawString(s, 0, 0, GLPalette.CORAL_PINK);

			GL.PopMatrix();
		}
		GL.PopAttrib();
	}

	@Override
	public boolean shouldHideHand(EntityPlayer player, ItemStack item)
	{
		BinocularData bd = new BinocularData(item);
		return bd.isZooming;
	}

	private String getMeterCenteredOn(int center)
	{
		return (center - 50) + " - " + (center - 25) + " - " + center + " - " + (center + 25) + " - " + (center + 50);
	}

	private String getMeterCenteredOnBefore(int center)
	{
		return (center - 50) + " - " + (center - 25) + " - " + center;
	}

	@Override
	public boolean onItemLeftClick(ItemStack stack, World world, EntityPlayer player)
	{
		BinocularData bd = new BinocularData(stack);
		if (!bd.isZooming)
			return false;

		if (world.isRemote)
			ShaderHelper.tareTimer();

		bd.zoomLevel--;
		if (bd.zoomLevel < 0)
			bd.zoomLevel = 4;

		bd.serialize(stack.stackTagCompound);

		return true;
	}

	@Override
	public boolean isLeftClickRepeatable()
	{
		return false;
	}

	@Override
	public int requestShader(EntityPlayer player, ItemStack stack)
	{
		BinocularData bd = new BinocularData(stack);
		return bd.isZooming ? ShaderHelper.vhs : 0;
	}
}
