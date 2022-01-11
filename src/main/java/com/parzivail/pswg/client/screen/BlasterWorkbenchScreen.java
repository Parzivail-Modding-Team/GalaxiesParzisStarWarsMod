package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.pswg.screen.BlasterWorkbenchScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BlasterWorkbenchScreen extends HandledScreen<BlasterWorkbenchScreenHandler> implements ScreenHandlerListener
{
	private static final Identifier TEXTURE = Resources.id("textures/gui/container/blaster_workbench.png");

	private ItemStack blaster = ItemStack.EMPTY;

	public BlasterWorkbenchScreen(BlasterWorkbenchScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
		backgroundWidth = 256;
		backgroundHeight = 241;
	}

	protected void init()
	{
		super.init();

		this.playerInventoryTitleX = 48;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;

		this.handler.addListener(this);
	}

	public void removed()
	{
		super.removed();
		this.handler.removeListener(this);
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);


		if (blaster.getItem() instanceof BlasterItem)
		{
			var bd = BlasterItem.getBlasterDescriptor(MinecraftClient.getInstance().world, blaster);
			var bt = new BlasterTag(blaster.getOrCreateNbt());

			var textY = this.y + 20;

			this.textRenderer.draw(matrices, Text.of("ID"), this.x + 50, textY, 0);
			this.textRenderer.draw(matrices, Text.of("Bitmask"), this.x + 140, textY, 0);
			this.textRenderer.draw(matrices, Text.of("Mutex"), this.x + 190, textY, 0);
			textY += 1.5 * this.textRenderer.fontHeight;

			this.textRenderer.draw(matrices, Text.of("Possible:"), this.x + 40, textY, 0);
			textY += 1.5 * this.textRenderer.fontHeight;

			for (var e : bd.attachmentMap.entrySet())
			{
				this.textRenderer.draw(matrices, Text.of(e.getValue().id), this.x + 50, textY, 0);
				this.textRenderer.draw(matrices, Text.of(String.format("%6s", Integer.toBinaryString(e.getKey())).replace(' ', '0')), this.x + 140, textY, 0);
				this.textRenderer.draw(matrices, Text.of(String.format("%6s", Integer.toBinaryString(e.getValue().mutex)).replace(' ', '0')), this.x + 190, textY, 0);
				textY += this.textRenderer.fontHeight;
			}

			textY += 0.5 * this.textRenderer.fontHeight;
			this.textRenderer.draw(matrices, Text.of("Attached:"), this.x + 40, textY, 0);
			this.textRenderer.draw(matrices, Text.of(String.format("%6s", Integer.toBinaryString(bt.attachmentBitmask)).replace(' ', '0')), this.x + 140, textY, 0x808080);
			textY += 1.5 * this.textRenderer.fontHeight;

			for (var e : bd.attachmentMap.entrySet())
			{
				if ((bt.attachmentBitmask & e.getKey()) == 0)
					continue;

				this.textRenderer.draw(matrices, Text.of(e.getValue().id), this.x + 50, textY, 0);
				this.textRenderer.draw(matrices, Text.of(String.format("%6s", Integer.toBinaryString(e.getKey())).replace(' ', '0')), this.x + 140, textY, 0);
				this.textRenderer.draw(matrices, Text.of(String.format("%6s", Integer.toBinaryString(e.getValue().mutex)).replace(' ', '0')), this.x + 190, textY, 0);
				textY += this.textRenderer.fontHeight;
			}
		}
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack)
	{
		switch (slotId)
		{
			case 0 -> {
				blaster = stack.copy();
				onBlasterChanged();
			}
		}
	}

	private void onBlasterChanged()
	{
	}

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int property, int value)
	{
	}

	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		var i = (this.width - this.backgroundWidth) / 2;
		var j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}
}
