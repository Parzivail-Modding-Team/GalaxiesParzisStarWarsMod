package dev.pswg.screens;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import dev.pswg.feature.brewing.MixerScreenHandler;
import dev.pswg.feature.scrapping.table.ScrappingTableScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MixerScreen extends HandledScreen<MixerScreenHandler>
{
	private static final Identifier TEXTURE = Gadgets.id("textures/gui/container/mixer.png");
	private static final Identifier MAP_TEXTURE = Gadgets.id("textures/gui/misc/brewing_map.png");

	public MixerScreen(MixerScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
		this.backgroundHeight = 255;
		this.backgroundWidth = 175;
		this.playerInventoryTitleX = 8;
		this.playerInventoryTitleY = 152;
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
	{
		var backgroundX = (this.width - this.backgroundWidth) / 2;
		var backgroundY = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, backgroundX, backgroundY, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		int backgroundX = (this.width - this.backgroundWidth) / 2;
		int backgroundY = (this.height - this.backgroundHeight) / 2;

		if (mouseX > 143 + backgroundX && mouseX < 152 + backgroundX && mouseY > 95 + backgroundY && mouseY < 111 + backgroundY && this.handler.onButtonClick(this.client.player, 0))
		{
			this.client.interactionManager.clickButton(this.handler.syncId, 0);
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		super.render(context, mouseX, mouseY, delta);
	}
}
