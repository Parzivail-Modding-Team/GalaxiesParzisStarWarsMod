package com.parzivail.swg.gui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.container.ContainerLightsaberForge;
import com.parzivail.swg.item.lightsaber.LightsaberData;
import com.parzivail.swg.item.lightsaber.LightsaberDescriptor;
import com.parzivail.swg.network.TransactionBroker;
import com.parzivail.swg.render.lightsaber.RenderLightsaber;
import com.parzivail.swg.tile.TileLightsaberForge;
import com.parzivail.swg.transaction.TransactionSetLightsaberDescriptor;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.GuiSlider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiLightsaberForge extends GuiContainer
{
	private static final ResourceLocation guiTexture = Resources.location("textures/container/blasterWorkbench.png");
	private final TileLightsaberForge tile;
	private final EntityPlayer player;

	private LightsaberData lightsaberData;

	private GuiCheckBox cbSaberUnstable;
	private GuiSlider sRed;
	private GuiSlider sGreen;
	private GuiSlider sBlue;
	private GuiButton bSetBladeColor;
	private GuiButton bSetCoreColor;
	private GuiSlider sLength;
	private GuiButton bSetLength;

	public GuiLightsaberForge(EntityPlayer player, InventoryPlayer inventoryPlayer, TileLightsaberForge tile)
	{
		super(new ContainerLightsaberForge(inventoryPlayer, tile));
		this.tile = tile;
		this.player = player;
	}

	@Override
	public void initGui()
	{
		xSize = 256;
		ySize = 241;

		super.initGui();
		buttonList.clear();

		buttonList.add(cbSaberUnstable = new GuiCheckBox(0, guiLeft + 47, guiTop + 5, "Unstable", false));
		buttonList.add(sRed = new GuiSlider(1, guiLeft + 47, guiTop + 20, 128, 20, "R: ", "", 0, 255, 0, false, true));
		buttonList.add(sGreen = new GuiSlider(2, guiLeft + 47, guiTop + 41, 128, 20, "G: ", "", 0, 255, 0, false, true));
		buttonList.add(sBlue = new GuiSlider(3, guiLeft + 47, guiTop + 62, 128, 20, "B: ", "", 0, 255, 0, false, true));
		buttonList.add(bSetBladeColor = new PGuiButton(4, guiLeft + 47, guiTop + 83, 63, 20, "Set Glow"));
		buttonList.add(bSetCoreColor = new PGuiButton(5, guiLeft + 112, guiTop + 83, 63, 20, "Set Core"));
		buttonList.add(sLength = new GuiSlider(6, guiLeft + 47, guiTop + 104, 128, 20, "Length: ", "", 0, 5, 3, true, true));
		buttonList.add(bSetLength = new PGuiButton(7, guiLeft + 176, guiTop + 104, 63, 20, "Set"));

		lightsaberData = null;
		setButtonsEnabled(false);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		if (tile.getLightsaber() != null)
		{
			if (lightsaberData == null)
				lightsaberData = new LightsaberData(tile.getLightsaber());
			LightsaberDescriptor d = lightsaberData.descriptor;

			cbSaberUnstable.setIsChecked(lightsaberData.descriptor.unstable);

			setButtonsEnabled(true);
		}
		else
		{
			if (lightsaberData != null)
				setButtonsEnabled(false);
			lightsaberData = null;
		}
	}

	private void setButtonsEnabled(boolean enabled)
	{
		cbSaberUnstable.enabled = enabled;
		sRed.enabled = enabled;
		sGreen.enabled = enabled;
		sBlue.enabled = enabled;
		bSetBladeColor.enabled = enabled;
		bSetCoreColor.enabled = enabled;
		sLength.enabled = enabled;
		bSetLength.enabled = enabled;
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.id == cbSaberUnstable.id)
		{
			lightsaberData.descriptor.unstable = cbSaberUnstable.isChecked();
			writeDescriptor();
		}
		else if (button.id == bSetBladeColor.id)
		{
			lightsaberData.descriptor.bladeColor = Fx.Util.GetRgb(sRed.getValueInt(), sGreen.getValueInt(), sBlue.getValueInt()) | 0xFF000000;
			writeDescriptor();
		}
		else if (button.id == bSetCoreColor.id)
		{
			lightsaberData.descriptor.coreColor = Fx.Util.GetRgb(sRed.getValueInt(), sGreen.getValueInt(), sBlue.getValueInt()) | 0xFF000000;
			writeDescriptor();
		}
		else if (button.id == bSetLength.id)
		{
			lightsaberData.descriptor.bladeLength = (float)(Math.floor(sLength.getValue() * 10) / 10f);
			writeDescriptor();
		}
	}

	private void writeDescriptor()
	{
		TransactionBroker.dispatch(new TransactionSetLightsaberDescriptor(tile, lightsaberData.descriptor));
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(I18n.format("container.inventory"), 47, ySize - 96 + 2, 4210752);

		if (lightsaberData != null)
		{
			GL.Disable(EnableCap.Texture2D);
			drawRgbPreview(sRed.getValueInt() / 255f, sGreen.getValueInt() / 255f, sBlue.getValueInt() / 255f, true);

			GL.PushMatrix();
			GL.Translate(47, 135, 20);
			GL.Rotate(-90, 0, 0, 1);
			GL.Scale(53);
			RenderLightsaber.renderBlade(3, 0, lightsaberData.descriptor);
			GL.PopMatrix();
			GL.Enable(EnableCap.Texture2D);
		}
		else
		{
			GL.Disable(EnableCap.Texture2D);
			drawRgbPreview(1, 1, 1, false);
			GL.Enable(EnableCap.Texture2D);
		}

		//		ArrayList<String> lines = new ArrayList<>();
		//		lines.add(String.valueOf(mouseX - guiLeft));
		//		lines.add(String.valueOf(mouseY - guiTop));
		//		drawHoveringText(lines, mouseX - guiLeft, mouseY - guiTop);
	}

	private void drawRgbPreview(float r, float g, float b, boolean enabled)
	{
		GL.Color(enabled ? GLPalette.BLACK : GLPalette.GREY);
		Fx.D2.DrawSolidRectangle(179, 29, 64, 64);
		if (enabled)
		{
			GL.Color(r, g, b);
			Fx.D2.DrawSolidRectangle(180, 30, 62, 62);
		}
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(guiTexture);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}
}
