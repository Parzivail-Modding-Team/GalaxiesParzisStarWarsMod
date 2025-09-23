package dev.pswg.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.pswg.Gadgets;
import dev.pswg.feature.brewing.MixerScreenHandler;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class MixerScreen extends HandledScreen<MixerScreenHandler>
{
	private static final Identifier TEXTURE = Gadgets.id("textures/gui/container/mixer.png");
	private static final Identifier MAP_TEXTURE = Gadgets.id("textures/gui/misc/brewing_map.png");
	private List<Pair<Integer, Integer>> previousMousePosition = new ArrayList<>();

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

		var backgroundX = (this.width - this.backgroundWidth) / 2;
		var backgroundY = (this.height - this.backgroundHeight) / 2;

		context.drawTexture(RenderLayer::getGuiTextured, MAP_TEXTURE, backgroundX + 6, backgroundY + 19, Math.clamp(handler.getMapX() - 64, 0, 512 - 128), Math.clamp(handler.getMapY() - 64, 0, 512 - 128), 128, 128, 512, 512);

		float markerX = backgroundX + 67 + (Math.min(handler.getMapX(), 64) - 64) + (Math.max(handler.getMapX(), 512 - 64) - 448);
		float markerY = backgroundY + 81 + (Math.min(handler.getMapY(), 64) - 64) + (Math.max(handler.getMapY(), 512 - 64) - 448);
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, (int)markerX, (int)markerY, 177, 61, 5, 5, 256, 256);


		float litMod = 14 - (float)(handler.getLitTimeRemaining() * 14) / Math.max(handler.getLitTimeTotal(), 1);
		if (handler.getLitTimeRemaining() != 0)
		{
			context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, backgroundX + 143, backgroundY + 95, 176, 43, 10, 17, 256, 256);
			if (mouseX > 142 + backgroundX && mouseX < 153 + backgroundX && mouseY > 94 + backgroundY && mouseY < 112 + backgroundY)
				context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, backgroundX + 143, backgroundY + 95, 176, 26, 10, 17, 256, 256);
		}
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, backgroundX + 145, backgroundY + 98, 177, 0, 6, handler.getBellowProgress() / 8, 256, 256);
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, backgroundX + 141, (int)(backgroundY + 115 + litMod), 177, (int)(12 + litMod), 14, handler.getLitTimeRemaining() * 14 / Math.max(handler.getLitTimeTotal(), 1), 256, 256);

		if (handler.getMapX() == 256 && handler.getMapY() == 256)
			previousMousePosition.clear();
		;

		for (int i = 1; i < previousMousePosition.size(); i++)
		{
			int x = previousMousePosition.get(i).getLeft();
			int y = previousMousePosition.get(i).getRight();

			float x1 = markerX - handler.getMapX() + x + 4;
			float y1 = markerY - handler.getMapY() + y + 4;
			float x2 = markerX - handler.getMapX() + x + 3;
			float y2 = markerY - handler.getMapY() + y + 3;

			if (Math.abs(handler.getMapX() - x) < 63 + (64 - Math.min(handler.getMapX(), 64)) && Math.abs(handler.getMapY() - y) < 63 + (64 - Math.min(handler.getMapY(), 64)))
			{
				//context.fill(x1, y1, x2, y2, -1);
				Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

				if (x1 < x2)
				{
					float tmp = x1;
					x1 = x2;
					x2 = tmp;
				}

				if (y1 < y2)
				{
					float tmp = y1;
					y1 = y2;
					y2 = tmp;
				}

				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

				bufferBuilder.vertex(matrix4f, x1, y1, 0).color(255, 255, 255, 255);
				bufferBuilder.vertex(matrix4f, x1, y2, 0).color(255, 255, 255, 255);
				bufferBuilder.vertex(matrix4f, x2, y2, 0).color(255, 255, 255, 255);
				bufferBuilder.vertex(matrix4f, x2, y1, 0).color(255, 255, 255, 255);

				RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
				BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			}
		}
		var pair = new Pair<>((int)handler.getMapX(), (int)handler.getMapY());
		if (previousMousePosition.isEmpty() || (Math.abs(previousMousePosition.getLast().getLeft().intValue() - pair.getLeft().intValue()) + Math.abs(previousMousePosition.getLast().getRight().intValue() - pair.getRight().intValue()) > 3))
			previousMousePosition.add(pair);

		if (previousMousePosition.size() > 60)
			previousMousePosition.removeFirst();
	}
}
