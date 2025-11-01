package dev.pswg.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.pswg.Gadgets;
import dev.pswg.feature.brewing.MixerScreenHandler;
import dev.pswg.rendering.Drawables;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.Click;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
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
		this.playerInventoryTitleY = 160;
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
	{
		var backgroundX = (this.width - this.backgroundWidth) / 2;
		var backgroundY = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX, backgroundY, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
	}

	@Override
	protected void handledScreenTick()
	{
		if (isMouseHeld() && isHoveringBellow((int)(client.mouse.getX() / client.getWindow().getScaleFactor()), (int)(client.mouse.getY() / client.getWindow().getScaleFactor())) && this.handler.onButtonClick(this.client.player, 0))
			this.client.interactionManager.clickButton(this.handler.syncId, 0);
		super.handledScreenTick();
	}

	@Override
	public boolean mouseClicked(Click click, boolean doubled)
	{
		int backgroundX = (this.width - this.backgroundWidth) / 2;
		int backgroundY = (this.height - this.backgroundHeight) / 2;
		int mouseX = (int)click.x();
		int mouseY = (int)click.y();
		if (within(mouseX, mouseY, 138, 145, 53, 60) && this.handler.onButtonClick(this.client.player, 1))
		{
			this.client.interactionManager.clickButton(this.handler.syncId, 1);
			return true;
		}
		if (within(mouseX, mouseY, 150, 157, 53, 60) && this.handler.onButtonClick(this.client.player, 2))
		{
			this.client.interactionManager.clickButton(this.handler.syncId, 2);
			return true;
		}
		if (within(mouseX, mouseY, 162, 169, 53, 60) && this.handler.onButtonClick(this.client.player, 3))
		{
			this.client.interactionManager.clickButton(this.handler.syncId, 3);
			return true;
		}
		return super.mouseClicked(click, doubled);
	}

	public boolean isMouseHeld()
	{
		return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

	}

	public boolean isKeyPressed(int keyId)
	{

		return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), keyId);
	}

	public boolean isHoveringBellow(int mouseX, int mouseY)
	{
		return within(mouseX, mouseY, 153, 154, 92, 93) || withinX(mouseX, mouseY, 152, 155, 94) || within(mouseX, mouseY, 151, 156, 95, 96) || withinX(mouseX, mouseY, 150, 157, 97) || within(mouseX, mouseY, 149, 158, 98, 100) || withinX(mouseX, mouseY, 150, 150, 101) || withinX(mouseX, mouseY, 157, 157, 101);
	}

	public boolean within(int mouseX, int mouseY, int x1, int x2, int y1, int y2)
	{
		int backgroundX = (this.width - this.backgroundWidth) / 2;
		int backgroundY = (this.height - this.backgroundHeight) / 2;
		return mouseX >= x1 + backgroundX && mouseX <= x2 + backgroundX && mouseY >= y1 + backgroundY && mouseY <= y2 + backgroundY;
	}

	public boolean withinX(int mouseX, int mouseY, int x1, int x2, int y)
	{
		return within(mouseX, mouseY, x1, x2, y, y);
	}

	public boolean withinY(int mouseX, int mouseY, int x, int y1, int y2)
	{
		return within(mouseX, mouseY, x, x, y1, y2);
	}


	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		super.render(context, mouseX, mouseY, delta);

		var backgroundX = (this.width - this.backgroundWidth) / 2;
		var backgroundY = (this.height - this.backgroundHeight) / 2;

		///  MAP
		context.drawTexture(RenderPipelines.GUI_TEXTURED, MAP_TEXTURE, backgroundX + 7, backgroundY + 19, Math.clamp(handler.getMapX() - 64, 0, 512 - 128), Math.clamp(handler.getMapY() - 64, 0, 512 - 128), 128 - 1, 128 - 1, 512, 512);

		///  MARKER
		float markerX = backgroundX + 68 + (Math.min(handler.getMapX(), 64) - 64) + (Math.max(handler.getMapX(), 512 - 64) - 448);
		float markerY = backgroundY + 81 + (Math.min(handler.getMapY(), 64) - 64) + (Math.max(handler.getMapY(), 512 - 64) - 448);
		context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, (int)markerX, (int)markerY, 177, 69, 5, 5, 256, 256);

		///  EFFECT BAR
		for (int i = 0; i < handler.getEffectCount(); i++)
		{
			int color = ColorHelper.fullAlpha(handler.getEffectColor(i));
			color = ColorHelper.getArgb(ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color));
			context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 97 + i * 11, backgroundY + 150, 177, 145, 11, 5, 256, 256, color);
			if (i == 2)
				context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 130, backgroundY + 151, 188, 146, 11, 5, 256, 256, color);
		}
		/// COLOR BAR
		for (int i = 0; i < handler.drinkColors.size(); i++)
		{
			context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 9 + i * 11, backgroundY + 150, 177, 145, 11, 5, 256, 256, handler.getDyeColor(i));
			if (i == 2)
				context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 42, backgroundY + 151, 188, 146, 11, 5, 256, 256, handler.getDyeColor(i));
		}
		/// CONFIRM DRINK BUTTON
		if (!handler.drinkEffects.isEmpty() || handler.isOnEffectCell() || !handler.drinkColors.isEmpty())
		{
			context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 138, backgroundY + 53, 177, 83, 8, 8, 256, 256);
			if (within(mouseX, mouseY, 138, 145, 53, 60))
				context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 138, backgroundY + 53, 177, 75, 8, 8, 256, 256);
		}
		/// ADD EFFECT BUTTON
		if (handler.canAddEffect() && handler.drinkEffects.size() < 3)
		{
			context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 150, backgroundY + 53, 177, 99, 8, 8, 256, 256);
			if (within(mouseX, mouseY, 150, 157, 53, 60))
				context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 150, backgroundY + 53, 177, 91, 8, 8, 256, 256);
		}
		/// CANCEL DRINK BUTTON
		if (!handler.wasReset())
		{
			context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 162, backgroundY + 53, 177, 115, 8, 8, 256, 256);
			if (within(mouseX, mouseY, 162, 169, 53, 60))
				context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 162, backgroundY + 53, 177, 107, 8, 8, 256, 256);
		}

		/// BELLOW BUTTON
		if (handler.getLitTimeRemaining() != 0 && handler.isDrinkContainerPresent())
		{
			context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 149, backgroundY + 92, 177, 47, 10, 10, 256, 256);
			if (isHoveringBellow(mouseX, mouseY))
				context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 149, backgroundY + 92, 177, 27, 10, 10, 256, 256);
		}
		///  BELLOW FILL AMOUNT
		context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 149, backgroundY + 92, 177, 57, 10, handler.getBellowProgress() / 18, 256, 256);
		if (isHoveringBellow(mouseX, mouseY))
			context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 149, backgroundY + 92, 177, 37, 10, handler.getBellowProgress() / 18, 256, 256);

		///  LIT TIME INDICATOR
		float litMod = 14 - (float)(handler.getLitTimeRemaining() * 14) / Math.max(handler.getLitTimeTotal(), 1);
		context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 146, (int)(backgroundY + 106 + litMod), 177, (int)(12 + litMod), 14, handler.getLitTimeRemaining() * 14 / Math.max(handler.getLitTimeTotal(), 1), 256, 256);

		///  PAST POSITIONS TRAIL
		if (handler.getMapX() == 256 && handler.getMapY() == 256)
			previousMousePosition.clear();

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
				//Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

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

				Drawables.fill(context, RenderPipelines.GUI, x1, y1, x2, y2, 0, Colors.WHITE);
				//bufferBuilder.vertex(matrix4f, x1, y1, 0).color(255, 255, 255, 255);
				//bufferBuilder.vertex(matrix4f, x1, y2, 0).color(255, 255, 255, 255);
				//bufferBuilder.vertex(matrix4f, x2, y2, 0).color(255, 255, 255, 255);
				//bufferBuilder.vertex(matrix4f, x2, y1, 0).color(255, 255, 255, 255);

				// TODO: DO I NEED TO DO THIS?
				//RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
				//BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			}
		}
		var pair = new Pair<>((int)handler.getMapX(), (int)handler.getMapY());
		if (previousMousePosition.isEmpty() || (Math.abs(previousMousePosition.getLast().getLeft().intValue() - pair.getLeft().intValue()) + Math.abs(previousMousePosition.getLast().getRight().intValue() - pair.getRight().intValue()) > 3))
			previousMousePosition.add(pair);

		if (previousMousePosition.size() > 60)
			previousMousePosition.removeFirst();
	}
}
