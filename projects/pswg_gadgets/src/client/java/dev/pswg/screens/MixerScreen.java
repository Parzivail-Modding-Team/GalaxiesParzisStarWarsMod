package dev.pswg.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.pswg.Gadgets;
import dev.pswg.feature.brewing.MixerScreenHandler;
import dev.pswg.rendering.Drawables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MixerScreen extends AbstractContainerScreen<MixerScreenHandler>
{
	private static final Identifier TEXTURE = Gadgets.id("textures/gui/container/mixer.png");
	private static final Identifier MAP_TEXTURE = Gadgets.id("textures/gui/misc/brewing_map.png");
	private List<Tuple<Integer, Integer>> previousMousePosition = new ArrayList<>();
	public static HashMap<Holder<MobEffect>, Tuple<Integer, Integer>> ICON_MAP = new HashMap<>();

	public MixerScreen(MixerScreenHandler handler, Inventory inventory, Component title)
	{
		super(handler, inventory, title, 175, 255);
		this.inventoryLabelX = 8;
		this.inventoryLabelY = 160;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta)
	{
		super.extractBackground(context, mouseX, mouseY, delta);
		var backgroundX = (this.width - this.imageWidth) / 2;
		var backgroundY = (this.height - this.imageHeight) / 2;
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX, backgroundY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
	}

	@Override
	protected void containerTick()
	{
		if (isMouseHeld() && isHoveringBellow((int)(minecraft.mouseHandler.xpos() / minecraft.getWindow().getGuiScale()), (int)(minecraft.mouseHandler.ypos() / minecraft.getWindow().getGuiScale())) && this.menu.clickMenuButton(this.minecraft.player, 0))
			this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
		super.containerTick();
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled)
	{
		int backgroundX = (this.width - this.imageWidth) / 2;
		int backgroundY = (this.height - this.imageHeight) / 2;
		int mouseX = (int)click.x();
		int mouseY = (int)click.y();
		if (within(mouseX, mouseY, 138, 145, 53, 60) && this.menu.clickMenuButton(this.minecraft.player, 1))
		{
			this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
			return true;
		}
		if (within(mouseX, mouseY, 150, 157, 53, 60) && this.menu.clickMenuButton(this.minecraft.player, 2))
		{
			this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 2);
			return true;
		}
		if (within(mouseX, mouseY, 162, 169, 53, 60) && this.menu.clickMenuButton(this.minecraft.player, 3))
		{
			this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 3);
			return true;
		}
		return super.mouseClicked(click, doubled);
	}

	public boolean isMouseHeld()
	{
		return GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().handle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

	}

	public boolean isKeyPressed(int keyId)
	{

		return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), keyId);
	}

	public boolean isHoveringBellow(int mouseX, int mouseY)
	{
		return within(mouseX, mouseY, 153, 154, 92, 93) || withinX(mouseX, mouseY, 152, 155, 94) || within(mouseX, mouseY, 151, 156, 95, 96) || withinX(mouseX, mouseY, 150, 157, 97) || within(mouseX, mouseY, 149, 158, 98, 100) || withinX(mouseX, mouseY, 150, 150, 101) || withinX(mouseX, mouseY, 157, 157, 101);
	}

	public boolean within(int mouseX, int mouseY, int x1, int x2, int y1, int y2)
	{
		int backgroundX = (this.width - this.imageWidth) / 2;
		int backgroundY = (this.height - this.imageHeight) / 2;
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
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta)
	{
		super.extractRenderState(context, mouseX, mouseY, delta);

		var backgroundX = (this.width - this.imageWidth) / 2;
		var backgroundY = (this.height - this.imageHeight) / 2;

		///  MAP
		context.blit(RenderPipelines.GUI_TEXTURED, MAP_TEXTURE, backgroundX + 7, backgroundY + 19, Math.clamp(menu.getMapX() - 64, 0, 512 - 128), Math.clamp(menu.getMapY() - 64, 0, 512 - 128), 128 - 1, 128 - 1, 512, 512);

		///  MARKER
		float markerX = backgroundX + 68 + (Math.min(menu.getMapX(), 64) - 64) + (Math.max(menu.getMapX(), 512 - 64) - 448);
		float markerY = backgroundY + 81 + (Math.min(menu.getMapY(), 64) - 64) + (Math.max(menu.getMapY(), 512 - 64) - 448);
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, (int)markerX, (int)markerY, 177, 69, 5, 5, 256, 256);

		///  EFFECT BAR
		for (int i = 0; i < menu.getEffectCount(); i++)
		{
			int color = ARGB.opaque(menu.getEffectColor(i));
			color = ARGB.color(ARGB.red(color), ARGB.green(color), ARGB.blue(color));
			if (i == 0)
				context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 96, backgroundY + 151, 176, 146, 11, 5, 256, 256, color);
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 97 + i * 11, backgroundY + 150, 177, 145, 11, 5, 256, 256, color);
			if (i == 2)
				context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 130, backgroundY + 151, 188, 146, 11, 5, 256, 256, color);
		}
		/// COLOR BAR
		for (int i = 0; i < menu.drinkColors.size(); i++)
		{
			if (i == 0)
				context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 8, backgroundY + 151, 176, 146, 11, 5, 256, 256, menu.getDyeColor(i));
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 9 + i * 11, backgroundY + 150, 177, 145, 11, 5, 256, 256, menu.getDyeColor(i));
			if (i == 2)
				context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 42, backgroundY + 151, 188, 146, 11, 5, 256, 256, menu.getDyeColor(i));
		}
		/// FOOD BAR
		for (int i = 0; i < menu.drinkFoods.size(); i++)
		{
			if (i == 0)
				context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 52, backgroundY + 151, 176, 146, 11, 5, 256, 256, menu.getFoodColor(i));
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 53 + i * 11, backgroundY + 150, 177, 145, 11, 5, 256, 256, menu.getFoodColor(i));
			if (i == 2)
				context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 86, backgroundY + 151, 188, 146, 11, 5, 256, 256, menu.getFoodColor(i));
		}
		/// CONFIRM DRINK BUTTON
		if (!menu.drinkEffects.isEmpty() || menu.isOnEffectCell() || !menu.drinkColors.isEmpty())
		{
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 138, backgroundY + 53, 177, 83, 8, 8, 256, 256);
			if (within(mouseX, mouseY, 138, 145, 53, 60))
				context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 138, backgroundY + 53, 177, 75, 8, 8, 256, 256);
		}
		/// ADD EFFECT BUTTON
		if (menu.canAddEffect() && menu.drinkEffects.size() < 3)
		{
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 150, backgroundY + 53, 177, 99, 8, 8, 256, 256);
			if (within(mouseX, mouseY, 150, 157, 53, 60))
				context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 150, backgroundY + 53, 177, 91, 8, 8, 256, 256);
		}
		/// CANCEL DRINK BUTTON
		if (!menu.wasReset())
		{
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 162, backgroundY + 53, 177, 115, 8, 8, 256, 256);
			if (within(mouseX, mouseY, 162, 169, 53, 60))
				context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 162, backgroundY + 53, 177, 107, 8, 8, 256, 256);
		}

		/// BELLOW BUTTON
		if (menu.getLitTimeRemaining() != 0 && menu.isDrinkContainerPresent())
		{
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 149, backgroundY + 92, 177, 47, 10, 10, 256, 256);
			if (isHoveringBellow(mouseX, mouseY))
				context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 149, backgroundY + 92, 177, 27, 10, 10, 256, 256);
		}
		///  BELLOW FILL AMOUNT
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 149, backgroundY + 92, 177, 57, 10, menu.getBellowProgress() / 18, 256, 256);
		if (isHoveringBellow(mouseX, mouseY))
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 149, backgroundY + 92, 177, 37, 10, menu.getBellowProgress() / 18, 256, 256);

		///  LIT TIME INDICATOR
		float litMod = 14 - (float)(menu.getLitTimeRemaining() * 14) / Math.max(menu.getLitTimeTotal(), 1);
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX + 146, (int)(backgroundY + 106 + litMod), 177, (int)(12 + litMod), 14, menu.getLitTimeRemaining() * 14 / Math.max(menu.getLitTimeTotal(), 1), 256, 256);

		///  PAST POSITIONS TRAIL
		if (menu.getMapX() == 256 && menu.getMapY() == 256)
			previousMousePosition.clear();

		for (int i = 1; i < previousMousePosition.size(); i++)
		{
			int x = previousMousePosition.get(i).getA();
			int y = previousMousePosition.get(i).getB();

			float x1 = markerX - menu.getMapX() + x + 4;
			float y1 = markerY - menu.getMapY() + y + 4;
			float x2 = markerX - menu.getMapX() + x + 3;
			float y2 = markerY - menu.getMapY() + y + 3;

			if (Math.abs(menu.getMapX() - x) < 63 + (64 - Math.min(menu.getMapX(), 64)) && Math.abs(menu.getMapY() - y) < 63 + (64 - Math.min(menu.getMapY(), 64)))
			{
				Drawables.fill(context, RenderPipelines.GUI, Math.max(x1, x2), Math.max(y1, y2), Math.min(x1, x2), Math.min(y1, y2), CommonColors.WHITE);
			}
		}
		var pair = new Tuple<>((int)menu.getMapX(), (int)menu.getMapY());
		if (previousMousePosition.isEmpty() || (Math.abs(previousMousePosition.getLast().getA().intValue() - pair.getA().intValue()) + Math.abs(previousMousePosition.getLast().getB().intValue() - pair.getB().intValue()) > 3))
			previousMousePosition.add(pair);

		if (previousMousePosition.size() > 60)
			previousMousePosition.removeFirst();
	}
}
