package com.parzivail.swg.gui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.container.ContainerBlasterWorkbench;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachment;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachments;
import com.parzivail.tile.TileBlasterWorkbench;
import com.parzivail.util.ui.FixedResolution;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiBlasterWorkbench extends GuiContainer
{
	private static final ResourceLocation guiTexture = Resources.location("textures/container/blasterWorkbench.png");
	private TileBlasterWorkbench tile;

	private List<BlasterAttachment> attachmentsInTab;
	private int attachmentIdx;

	private GuiButton bScopes;
	private GuiButton bBarrels;
	private GuiButton bGrips;
	private GuiButton bPrev;
	private GuiButton bNext;
	private GuiButton bEquip;
	private GuiButton bBuy;

	private static String textEquip = I18n.format(Resources.guiDot("equip"));
	private static String textEquipped = I18n.format(Resources.guiDot("equipped"));

	public GuiBlasterWorkbench(InventoryPlayer inventoryPlayer, TileBlasterWorkbench tile)
	{
		super(new ContainerBlasterWorkbench(inventoryPlayer, tile));
		this.tile = tile;
	}

	@Override
	public void initGui()
	{
		this.xSize = 256;
		this.ySize = 241;

		super.initGui();
		this.buttonList.clear();

		attachmentsInTab = BlasterAttachments.SCOPES;

		this.buttonList.add(bScopes = new PGuiButton(0, this.guiLeft + 40, this.guiTop + 6, 50, 20, I18n.format(Resources.guiDot("scopes"))));
		this.buttonList.add(bBarrels = new PGuiButton(1, this.guiLeft + 104, this.guiTop + 6, 50, 20, I18n.format(Resources.guiDot("barrels"))));
		this.buttonList.add(bGrips = new PGuiButton(2, this.guiLeft + 166, this.guiTop + 6, 50, 20, I18n.format(Resources.guiDot("grips"))));

		this.buttonList.add(bPrev = new PGuiButton(3, this.guiLeft + 40, this.guiTop + 61, 10, 20, "◀"));
		this.buttonList.add(bNext = new PGuiButton(4, this.guiLeft + 206, this.guiTop + 61, 10, 20, "▶"));

		this.buttonList.add(bEquip = new PGuiButton(5, this.guiLeft + 40, this.guiTop + 115, 50, 20, textEquip));
		this.buttonList.add(bBuy = new PGuiButton(5, this.guiLeft + 166, this.guiTop + 115, 50, 20, I18n.format(Resources.guiDot("buy"))));

		bEquip.enabled = false;

		checkHasBlaster();
	}

	private void setAttachmentsInTab(List<BlasterAttachment> attachmentsInTab)
	{
		this.attachmentsInTab = attachmentsInTab;
		this.attachmentIdx = 0;
	}

	private void nextAttachment()
	{
		this.attachmentIdx++;
		this.attachmentIdx %= this.attachmentsInTab.size();
	}

	private void prevAttachment()
	{
		this.attachmentIdx--;
		if (attachmentIdx < 0)
			attachmentIdx = this.attachmentsInTab.size() - 1;
	}

	public BlasterAttachment getSelectedAttachment()
	{
		return attachmentsInTab.get(attachmentIdx);
	}

	private void checkBuyOrEquip()
	{
		int money = getPlayerMoneyBalance();
		BlasterAttachment attachment = getSelectedAttachment();
		boolean alreadyOwns = BlasterAttachments.doesPlayerOwn(Client.mc.thePlayer, attachment);
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
		return 0;
	}

	private void equipSelectedItem()
	{
	}

	private void buySelectedItem()
	{
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
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 47, this.ySize - 96 + 2, 4210752);

		BlasterAttachment a = getSelectedAttachment();

		GL.PushMatrix();
		GL.Translate(this.xSize / 2, this.ySize - 170, 0);
		a.drawInfoCard(new FixedResolution(Client.mc, (int)(this.xSize / 3.4f), this.ySize / 2), Client.mc.thePlayer, tile.getBlaster());
		GL.PopMatrix();

		PswgExtProp props = PswgExtProp.get(Client.mc.thePlayer);
		if (props != null)
			fontRendererObj.drawString(String.format("$%s", props.creditBalance), 10, this.ySize - 200, GLPalette.ELECTRIC_BLUE);

		String s = a.name;
		fontRendererObj.drawString(s, this.xSize / 2 - fontRendererObj.getStringWidth(s) / 2, this.ySize - 212, GLPalette.ELECTRIC_BLUE);
		s = a.getInfoText();
		fontRendererObj.drawString(s, this.xSize / 2 - fontRendererObj.getStringWidth(s) / 2, this.ySize - 135, 0x404040);
		s = String.format("$%s", a.price);
		Client.frAurebesh.drawString(s, this.xSize / 2 - Client.frAurebesh.getStringWidth(s) / 2, this.ySize - 120, canAffordSelectedItem() ? 0x404040 : GLPalette.ANALOG_RED);
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(guiTexture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
}