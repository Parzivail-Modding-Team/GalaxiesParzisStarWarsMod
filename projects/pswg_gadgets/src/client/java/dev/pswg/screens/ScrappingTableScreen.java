package dev.pswg.screens;

import dev.pswg.Gadgets;
import dev.pswg.feature.scrapping.ScrappingTableScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ScrappingTableScreen extends HandledScreen<ScrappingTableScreenHandler>
{
	private static final Identifier TEXTURE = Gadgets.id("textures/gui/scrapping_table.png");


	public ScrappingTableScreen(ScrappingTableScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
		this.backgroundHeight = 213;
		this.backgroundWidth = 175;
		this.playerInventoryTitleX = 8;
		this.playerInventoryTitleY = 118;
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
	{
		var backgroundX = (this.width - this.backgroundWidth) / 2;
		var backgroundY = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, backgroundX, backgroundY, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);

		for (int i = 0; i < 3; i++)
		{
			if (mouseX > 31 + backgroundX && mouseX < 41 + backgroundX && mouseY > 48 + i * 23 + backgroundY && mouseY < 58 + i * 22 + backgroundY)
			{
				context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 31 + backgroundX, 48 + i * 22 + backgroundY, 177, 29, 10, 10, 256, 256);
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		Gadgets.LOGGER.info("x: " + mouseX + " y: " + mouseY + " button: " + button + " xMinLoc: " + (30 + ((this.width - this.backgroundWidth) / 2)) + " yMinLoc: " + (70 + ((this.height - this.backgroundHeight) / 2)));
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		super.render(context, mouseX, mouseY, delta);
	}
}
