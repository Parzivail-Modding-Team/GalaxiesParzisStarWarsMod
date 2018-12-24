package com.parzivail.swg.gui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.container.ContainerLightsaberForge;
import com.parzivail.swg.item.lightsaber.LightsaberData;
import com.parzivail.swg.network.TransactionBroker;
import com.parzivail.swg.tile.TileLightsaberForge;
import com.parzivail.swg.transaction.TransactionSetLightsaberDescriptor;
import cpw.mods.fml.client.config.GuiCheckBox;
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
