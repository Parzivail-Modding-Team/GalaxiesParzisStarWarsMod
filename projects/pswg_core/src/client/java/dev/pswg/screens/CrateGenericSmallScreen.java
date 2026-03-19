package dev.pswg.screens;

import dev.pswg.Galaxies;
import dev.pswg.blockEntity.screenHandler.CrateGenericSmallScreenHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrateGenericSmallScreen extends AbstractContainerScreen<CrateGenericSmallScreenHandler>
{
	private static final ResourceLocation TEXTURE = Galaxies.id("textures/gui/container/crate_5x3.png");

	public CrateGenericSmallScreen(CrateGenericSmallScreenHandler handler, Inventory inventory, Component title)
	{
		super(handler, inventory, title);
		imageWidth = 176;
		imageHeight = 168;
	}

	@Override
	protected void init()
	{
		super.init();

		this.inventoryLabelY = this.imageHeight - 94;
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(context, mouseX, mouseY, delta);
		super.render(context, mouseX, mouseY, delta);
		this.renderTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY)
	{
		var i = (this.width - this.imageWidth) / 2;
		var j = (this.height - this.imageHeight) / 2;
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
	}
}
