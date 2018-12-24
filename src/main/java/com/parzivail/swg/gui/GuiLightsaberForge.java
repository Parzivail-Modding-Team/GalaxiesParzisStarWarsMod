package com.parzivail.swg.gui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.container.ContainerLightsaberForge;
import com.parzivail.swg.item.lightsaber.LightsaberData;
import com.parzivail.swg.item.lightsaber.LightsaberDescriptor;
import com.parzivail.swg.network.TransactionBroker;
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

		buttonList.add(cbSaberUnstable = new GuiCheckBox(0, guiLeft + 47, guiTop + 15, "Unstable", false));
		buttonList.add(sRed = new GuiSlider(1, guiLeft + 47, guiTop + 30, 128, 20, "R: ", "", 0, 255, 0, false, true));
		buttonList.add(sGreen = new GuiSlider(2, guiLeft + 47, guiTop + 51, 128, 20, "G: ", "", 0, 255, 0, false, true));
		buttonList.add(sBlue = new GuiSlider(3, guiLeft + 47, guiTop + 72, 128, 20, "B: ", "", 0, 255, 0, false, true));
		buttonList.add(bSetBladeColor = new GuiButton(4, guiLeft + 47, guiTop + 93, 63, 20, "Set Blade"));
		buttonList.add(bSetCoreColor = new GuiButton(5, guiLeft + 113, guiTop + 93, 63, 20, "Set Core"));

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
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		//		if (button.id == bScopes.id)
		//			setAttachmentsInTab(BlasterAttachments.SCOPES);
		if (button.id == cbSaberUnstable.id)
		{
			lightsaberData.descriptor.unstable = cbSaberUnstable.isChecked();
			writeDescriptor();
		}
		else if (button.id == bSetBladeColor.id)
		{
			lightsaberData.descriptor.bladeColor = Fx.Util.GetRgb(sRed.getValueInt(), sGreen.getValueInt(), sBlue.getValueInt());
			writeDescriptor();
		}
		else if (button.id == bSetCoreColor.id)
		{
			lightsaberData.descriptor.coreColor = Fx.Util.GetRgb(sRed.getValueInt(), sGreen.getValueInt(), sBlue.getValueInt());
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
			fontRendererObj.drawString("sabers!", 47, 5, 4210752);
		}

		GL.Disable(EnableCap.Texture2D);
		GL.Color(GLPalette.BLACK);
		Fx.D2.DrawSolidRectangle(179, 29, 64, 64);
		GL.Color(sRed.getValueInt() / 255f, sGreen.getValueInt() / 255f, sBlue.getValueInt() / 255f);
		Fx.D2.DrawSolidRectangle(180, 30, 62, 62);
		GL.Enable(EnableCap.Texture2D);

		//		ArrayList<String> lines = new ArrayList<>();
		//		lines.add(String.valueOf(mouseX - guiLeft));
		//		lines.add(String.valueOf(mouseY - guiTop));
		//		drawHoveringText(lines, mouseX - guiLeft, mouseY - guiTop);
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
