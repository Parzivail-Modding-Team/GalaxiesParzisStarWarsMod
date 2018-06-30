package com.parzivail.swg.gui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.container.ContainerBlasterWorkbench;
import com.parzivail.swg.item.blaster.data.BlasterAttachment;
import com.parzivail.swg.item.blaster.data.BlasterAttachments;
import com.parzivail.swg.network.TransactionRegistry;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.tile.TileBlasterWorkbench;
import com.parzivail.swg.transaction.TransactionDeductCredits;
import com.parzivail.swg.transaction.TransactionEquipAttachment;
import com.parzivail.swg.transaction.TransactionUnlockAttachment;
import com.parzivail.util.ui.FixedResolution;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiBlasterWorkbench extends GuiContainer
{
	private static final ResourceLocation guiTexture = Resources.location("textures/container/blasterWorkbench.png");
	private static final String textEquip = I18n.format(Resources.guiDot("equip"));
	private static final String textEquipped = I18n.format(Resources.guiDot("equipped"));
	private final TileBlasterWorkbench tile;
	private final EntityPlayer player;
	private List<BlasterAttachment> attachmentsInTab;
	private int attachmentIdx;
	private GuiButton bScopes;
	private GuiButton bBarrels;
	private GuiButton bGrips;
	private GuiButton bPrev;
	private GuiButton bNext;
	private GuiButton bEquip;
	private GuiButton bBuy;

	public GuiBlasterWorkbench(InventoryPlayer inventoryPlayer, TileBlasterWorkbench tile)
	{
		super(new ContainerBlasterWorkbench(inventoryPlayer, tile));
		this.tile = tile;

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

		attachmentsInTab = BlasterAttachments.SCOPES;

		buttonList.add(bScopes = new PGuiButton(0, guiLeft + 40, guiTop + 6, 50, 15, I18n.format(Resources.guiDot("scopes"))));
		buttonList.add(bBarrels = new PGuiButton(1, guiLeft + 104, guiTop + 6, 50, 15, I18n.format(Resources.guiDot("barrels"))));
		buttonList.add(bGrips = new PGuiButton(2, guiLeft + 166, guiTop + 6, 50, 15, I18n.format(Resources.guiDot("grips"))));

		buttonList.add(bPrev = new PGuiButton(3, guiLeft + 40, guiTop + 61, 10, 20, "<"));
		buttonList.add(bNext = new PGuiButton(4, guiLeft + 206, guiTop + 61, 10, 20, ">"));

		buttonList.add(bEquip = new PGuiButton(5, guiLeft + 40, guiTop + 115, 50, 15, textEquip));
		buttonList.add(bBuy = new PGuiButton(6, guiLeft + 166, guiTop + 115, 50, 15, I18n.format(Resources.guiDot("buy"))));

		bEquip.enabled = false;

		checkHasBlaster();
	}

	private void setAttachmentsInTab(List<BlasterAttachment> attachmentsInTab)
	{
		this.attachmentsInTab = attachmentsInTab;
		attachmentIdx = 0;
	}

	private void nextAttachment()
	{
		attachmentIdx++;
		attachmentIdx %= attachmentsInTab.size();
	}

	private void prevAttachment()
	{
		attachmentIdx--;
		if (attachmentIdx < 0)
			attachmentIdx = attachmentsInTab.size() - 1;
	}

	public BlasterAttachment getSelectedAttachment()
	{
		return attachmentsInTab.get(attachmentIdx);
	}

	private void checkBuyOrEquip()
	{
		int money = getPlayerMoneyBalance();
		BlasterAttachment attachment = getSelectedAttachment();
		boolean alreadyOwns = BlasterAttachments.doesPlayerOwn(player, attachment);
		boolean equipped = BlasterAttachments.isEquipped(tile.getBlaster(), attachment);
		bEquip.displayString = textEquip;
		if (alreadyOwns)
		{
			bBuy.enabled = false;
			if (equipped)
			{
				bEquip.enabled = false;
				bEquip.displayString = textEquipped;
			}
			else
				bEquip.enabled = true;
			return;
		}

		bEquip.enabled = false;
		bBuy.enabled = money >= attachment.price;
	}

	private void checkHasBlaster()
	{
		if (tile == null || tile.getBlaster() == null)
		{
			bEquip.enabled = bBuy.enabled = false;
			return;
		}

		checkBuyOrEquip();
	}

	private boolean canAffordSelectedItem()
	{
		return getPlayerMoneyBalance() >= getSelectedAttachment().price;
	}

	private int getPlayerMoneyBalance()
	{
		PswgExtProp props = PswgExtProp.get(player);
		if (props == null)
			return 0;
		return props.getCreditBalance();
	}

	private void equipSelectedItem()
	{
		BlasterAttachment s = getSelectedAttachment();
		if (s == null)
			return;

		TransactionRegistry.dispatch(new TransactionEquipAttachment(tile, s));
	}

	private void buySelectedItem()
	{
		BlasterAttachment s = getSelectedAttachment();
		if (s == null)
			return;

		TransactionRegistry.dispatch(new TransactionDeductCredits(player, s.price), new TransactionUnlockAttachment(player, s));
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.id == bScopes.id)
			setAttachmentsInTab(BlasterAttachments.SCOPES);
		else if (button.id == bBarrels.id)
			setAttachmentsInTab(BlasterAttachments.BARRELS);
		else if (button.id == bGrips.id)
			setAttachmentsInTab(BlasterAttachments.GRIPS);
		else if (button.id == bNext.id)
			nextAttachment();
		else if (button.id == bPrev.id)
			prevAttachment();
		else if (button.id == bBuy.id)
			buySelectedItem();
		else if (button.id == bEquip.id)
			equipSelectedItem();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		checkHasBlaster();
		fontRendererObj.drawString(I18n.format("container.inventory"), 47, ySize - 96 + 2, 4210752);

		BlasterAttachment a = getSelectedAttachment();

		GL.PushMatrix();
		GL.Translate(xSize / 2, ySize - 170, 0);
		a.drawInfoCard(new FixedResolution(Client.mc, (int)(xSize / 3.4f), ySize / 2), player, tile.getBlaster());
		GL.PopMatrix();

		String s = a.localizedName;
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, ySize - 212, GLPalette.ELECTRIC_BLUE);
		s = a.getInfoText();
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, ySize - 135, 0x404040);
		s = String.format("$%d", a.price);
		Client.frAurebesh.drawString(s, xSize / 2 - Client.frAurebesh.getStringWidth(s) / 2, ySize - 120, canAffordSelectedItem() ? 0x404040 : GLPalette.ANALOG_RED);

		s = String.format("$%d", getPlayerMoneyBalance());
		Client.frAurebesh.drawString(s, xSize / 2 - Client.frAurebesh.getStringWidth(s) / 2, ySize - 105, GLPalette.ELECTRIC_BLUE);
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
