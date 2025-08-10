package dev.pswg.screens;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsScreenHandlerTypes;
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
		this.backgroundHeight = 198;
		this.backgroundWidth = 175;
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
	{
		var i = (this.width - this.backgroundWidth) / 2;
		var j = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
		//context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 00.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 216, 176);
	}
}
