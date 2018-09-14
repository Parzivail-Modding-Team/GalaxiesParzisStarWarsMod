package com.parzivail.swg.gui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.container.ContainerNothing;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.GLPalette;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import org.lwjgl.opengl.GL11;

public class GuiPersonalDatapad extends GuiContainer
{
	private static final ResourceLocation guiTexture = Resources.location("textures/container/personalDatapad.png");
	private final EntityPlayer player;
	private int attachmentIdx;

	public GuiPersonalDatapad(InventoryPlayer inventoryPlayer)
	{
		super(new ContainerNothing());

		// We have to use this awful hack because the EntityPlayer that's provided to
		// the Gui through the InventoryPlayer is a strictly client-based player instance
		// and isn't the real one.
		player = (EntityPlayer)DimensionManager.getWorld(inventoryPlayer.player.dimension).getEntityByID(inventoryPlayer.player.getEntityId());
	}

	@Override
	public void initGui()
	{
		xSize = 256;
		ySize = 241;

		super.initGui();
		buttonList.clear();
	}

	private int getPlayerMoneyBalance()
	{
		PswgExtProp props = PswgExtProp.get(player);
		if (props == null)
			return 0;
		return props.getCreditBalance();
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(I18n.format(Resources.guiDot("datapad.logotype")), 47, 139, 0x0D0D0D);

		String s = String.format("$%d", getPlayerMoneyBalance());
		Client.frAurebesh.drawString(s, 7, 7, GLPalette.ANALOG_GREEN);
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
