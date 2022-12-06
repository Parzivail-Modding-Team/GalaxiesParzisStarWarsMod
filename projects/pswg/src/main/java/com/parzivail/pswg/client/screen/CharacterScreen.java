package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.character.SpeciesColorVariable;
import com.parzivail.pswg.character.SpeciesGender;
import com.parzivail.pswg.character.SpeciesVariable;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.client.render.camera.CameraHelper;
import com.parzivail.pswg.client.render.player.PlayerSpeciesModelRenderer;
import com.parzivail.pswg.client.species.SwgSpeciesIcons;
import com.parzivail.pswg.client.species.SwgSpeciesLore;
import com.parzivail.pswg.client.species.SwgSpeciesRenderer;
import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.mixin.EntityRenderDispatcherAccessor;
import com.parzivail.util.client.TextUtil;
import com.parzivail.util.client.screen.blit.*;
import com.parzivail.util.math.ColorUtil;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.MatrixStackUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class CharacterScreen extends Screen
{
	private static class HoveringEntry
	{
		private final BlitRectangle area;
		private final long timeStartHovering;

		public HoveringEntry(BlitRectangle area)
		{
			this.area = area;
			this.timeStartHovering = System.currentTimeMillis();
		}

		public boolean shouldShowTooltip()
		{
			return System.currentTimeMillis() - timeStartHovering > 1000;
		}
	}

	private enum Page
	{
		SPECIES,
		VARIABLES
	}

	public static final String I18N_TITLE = "screen.pswg.character";
	public static final String I18N_CHOOSE_SPECIES = "screen.pswg.character.choose_species";
	public static final String I18N_CHOOSE_OPTION = "screen.pswg.character.choose_option";
	public static final String I18N_NEXT_PAGE = "screen.pswg.character.next_page";
	public static final String I18N_PREVIOUS_PAGE = "screen.pswg.character.previous_page";
	public static final String I18N_CLEAR_SPECIES = "screen.pswg.character.clear_species";
	private static final Identifier OPTIONS_BACKGROUND = new Identifier("textures/gui/options_background.png");
	private static final Identifier BACKGROUND = Resources.id("textures/gui/character/background.png");

	private static final int HALF_WIDTH = 213;
	private static final int HALF_HEIGHT = 120;

	private static final int SPECIES_LIST_PANEL_X = 7;
	private static final int SPECIES_LIST_PANEL_Y = 25;
	private static final int OPTION_LIST_PANEL_X = 260;
	private static final int OPTION_LIST_PANEL_Y = 41;
	private static final int LIST_ROW_HEIGHT = 25;
	private static final int LIST_ROW_CONTENT_HEIGHT = 20;
	private static final int LIST_ROW_CONTENT_CENTERING_OFFSET = (LIST_ROW_HEIGHT - LIST_ROW_CONTENT_HEIGHT) / 2;

	private final BlitRectangle<BlittableAsset> TRANSPARENT_VIEWPORT_BACKGROUND = new BlitRectangle<>(
			new BlittableAsset(16, 251, 25, 25, 512, 512),
			270, 178, 25, 25
	);
	private static final BlittableAsset LIST_BG_PATCH = new BlittableAsset(10, 241, 8, 8, 512, 512);
	private final BlitRectangle<BlittableAsset> LEFT_LIST_CUTOUT = new BlitRectangle<>(
			LIST_BG_PATCH,
			SPECIES_LIST_PANEL_X, SPECIES_LIST_PANEL_Y, 79, 190
	);
	private final BlitRectangle<BlittableAsset> RIGHT_LIST_CUTOUT = new BlitRectangle<>(
			LIST_BG_PATCH,
			OPTION_LIST_PANEL_X, OPTION_LIST_PANEL_Y, 150, 117
	);

	private static final Blittable3Patch SCROLLBAR_TRACK = new Blittable3Patch(
			new BlittableAsset(0, 241, 3, 3, 512, 512),
			new BlittableAsset(5, 241, 3, 8, 512, 512),
			new BlittableAsset(0, 246, 3, 3, 512, 512),
			1, 1
	);

	private static final BlittableAsset SLIDER_TRACK = new BlittableAsset(0, 424, 8, 4, 512, 512);
	private static final Blittable3Patch SLIDER_TRACK_WHITE = new Blittable3Patch(
			new BlittableAsset(0, 430, 4, 4, 512, 512),
			SLIDER_TRACK,
			new BlittableAsset(5, 430, 5, 4, 512, 512),
			2, 2
	);
	private static final Blittable3Patch SLIDER_TRACK_RED = new Blittable3Patch(
			new BlittableAsset(0, 436, 4, 4, 512, 512),
			SLIDER_TRACK,
			new BlittableAsset(5, 436, 5, 4, 512, 512),
			2, 2
	);
	private static final Blittable3Patch SLIDER_TRACK_GREEN = new Blittable3Patch(
			new BlittableAsset(0, 442, 4, 4, 512, 512),
			SLIDER_TRACK,
			new BlittableAsset(5, 442, 5, 4, 512, 512),
			2, 2
	);
	private static final Blittable3Patch SLIDER_TRACK_BLUE = new Blittable3Patch(
			new BlittableAsset(0, 448, 4, 4, 512, 512),
			SLIDER_TRACK,
			new BlittableAsset(5, 448, 5, 4, 512, 512),
			2, 2
	);

	private final BlitRectangle<HoverableBlittableAsset> LEFT_ARROW = new BlitRectangle<>(
			new HoverableBlittableAsset(
					new BlittableAsset(0, 278, 7, 11, 512, 512),
					new BlittableAsset(7, 278, 7, 11, 512, 512)
			),
			126,
			110
	);
	private final BlitRectangle<HoverableBlittableAsset> RIGHT_ARROW = new BlitRectangle<>(
			new HoverableBlittableAsset(
					new BlittableAsset(0, 291, 7, 11, 512, 512),
					new BlittableAsset(7, 291, 7, 11, 512, 512)
			),
			233,
			110
	);
	private final BlitRectangle<HoverableBlittableAsset> NEXT_PAGE_BTN = new BlitRectangle<>(new HoverableBlittableAsset(
			new BlittableAsset(0, 304, 42, 18, 512, 512),
			new BlittableAsset(42, 304, 42, 18, 512, 512)
	), 313, 210);
	private final BlitRectangle<HoverableBlittableAsset> PREV_PAGE_BTN = new BlitRectangle<>(new HoverableBlittableAsset(
			new BlittableAsset(86, 304, 42, 18, 512, 512),
			new BlittableAsset(128, 304, 42, 18, 512, 512)
	), 313, 210);
	private final BlitRectangle<HoverableBlittableAsset> APPLY_BTN = new BlitRectangle<>(new HoverableBlittableAsset(
			new BlittableAsset(0, 324, 42, 18, 512, 512),
			new BlittableAsset(42, 324, 42, 18, 512, 512)
	), 365, 210);
	private final BlitRectangle<HoverableBlittableAsset> CLEAR_BTN = new BlitRectangle<>(new HoverableBlittableAsset(
			new BlittableAsset(86, 324, 42, 18, 512, 512),
			new BlittableAsset(128, 324, 42, 18, 512, 512)
	), 365, 210);
	private final BlitRectangle<HoverableBlittableAsset> RANDOM_BUTTON = new BlitRectangle<>(new HoverableBlittableAsset(
			new BlittableAsset(0, 344, 20, 18, 512, 512),
			new BlittableAsset(20, 344, 20, 18, 512, 512)
	), 128, 210);
	private final BlitRectangle<TogglableBlittableAsset> GENDER_TOGGLE = new BlitRectangle<>(new TogglableBlittableAsset(
			new HoverableBlittableAsset(
					new BlittableAsset(0, 404, 20, 18, 512, 512),
					new BlittableAsset(20, 404, 20, 18, 512, 512)
			),
			new HoverableBlittableAsset(
					new BlittableAsset(40, 404, 20, 18, 512, 512),
					new BlittableAsset(60, 404, 20, 18, 512, 512)
			)
	), 158, 210);
	private final BlitRectangle<HoverableBlittableAsset> SAVE_BUTTON = new BlitRectangle<>(new HoverableBlittableAsset(
			new BlittableAsset(0, 364, 20, 18, 512, 512),
			new BlittableAsset(20, 364, 20, 18, 512, 512)
	), 188, 210);
	private final BlitRectangle<HoverableBlittableAsset> EXPORT_BUTTON = new BlitRectangle<>(new HoverableBlittableAsset(
			new BlittableAsset(0, 384, 20, 18, 512, 512),
			new BlittableAsset(20, 384, 20, 18, 512, 512)
	), 218, 210);

	private final BlitScrollThumb LEFT_SCROLL_THUMB = new BlitScrollThumb(
			new HoverableBlittableAsset(
					new BlittableAsset(0, 251, 7, 15, 512, 512),
					new BlittableAsset(7, 251, 7, 15, 512, 512)
			),
			88, 24, 192
	);
	private final BlitScrollThumb RIGHT_SCROLL_THUMB = new BlitScrollThumb(
			new HoverableBlittableAsset(
					new BlittableAsset(0, 251, 7, 15, 512, 512),
					new BlittableAsset(7, 251, 7, 15, 512, 512)
			),
			415, 41, 117
	);

	private final BlitScrollThumb WHITE_SCROLL_THUMB = new BlitScrollThumb(
			new HoverableBlittableAsset(
					new BlittableAsset(0, 268, 6, 8, 512, 512),
					new BlittableAsset(6, 268, 6, 8, 512, 512)
			),
			270, 163, 140
	);
	private final BlitScrollThumb RED_SCROLL_THUMB = new BlitScrollThumb(
			new HoverableBlittableAsset(
					new BlittableAsset(0, 268, 6, 8, 512, 512),
					new BlittableAsset(6, 268, 6, 8, 512, 512)
			),
			305, 176, 105
	);
	private final BlitScrollThumb GREEN_SCROLL_THUMB = new BlitScrollThumb(
			new HoverableBlittableAsset(
					new BlittableAsset(0, 268, 6, 8, 512, 512),
					new BlittableAsset(6, 268, 6, 8, 512, 512)
			),
			305, 186, 105
	);
	private final BlitScrollThumb BLUE_SCROLL_THUMB = new BlitScrollThumb(
			new HoverableBlittableAsset(
					new BlittableAsset(0, 268, 6, 8, 512, 512),
					new BlittableAsset(6, 268, 6, 8, 512, 512)
			),
			305, 196, 105
	);

	private final Screen parent;

	@SuppressWarnings("rawtypes")
	private final ArrayList<BlitRectangle> blitRectangles = new ArrayList<>();

	@SuppressWarnings("rawtypes")
	private final HashMap<BlitRectangle, Supplier<Text>> hoverText = new HashMap<>();
	private HoveringEntry hoveringEntry = null;

	private Page page = Page.SPECIES;

	private SwgSpecies previewSpecies = null;
	private SpeciesVariable previewVariable = null;
	private String previewVariableValue = null;
	private SpeciesGender previewSpeciesGender = SpeciesGender.MALE;
	private PlayerSpeciesModelRenderer previousRenderer;
	private Identifier previousTexture;
	private boolean canDrag = false;
	private float yaw = 0;

	public CharacterScreen(Screen parent)
	{
		super(Text.translatable(I18N_TITLE));
		this.parent = parent;

		blitRectangles.clear();
		blitRectangles.add(LEFT_ARROW);
		blitRectangles.add(RIGHT_ARROW);
		blitRectangles.add(RANDOM_BUTTON);
		blitRectangles.add(GENDER_TOGGLE);
		blitRectangles.add(NEXT_PAGE_BTN);
		blitRectangles.add(PREV_PAGE_BTN);
		blitRectangles.add(CLEAR_BTN);
		blitRectangles.add(SAVE_BUTTON);
		blitRectangles.add(EXPORT_BUTTON);
		blitRectangles.add(APPLY_BTN);

		hoverText.put(RANDOM_BUTTON, () -> Text.translatable(Resources.I18N_SCREEN_RANDOM));
		hoverText.put(GENDER_TOGGLE, () -> Text.translatable(GENDER_TOGGLE.getBlittable().isToggled() ? Resources.I18N_SCREEN_GENDER_FEMALE : Resources.I18N_SCREEN_GENDER_MALE));
		hoverText.put(NEXT_PAGE_BTN, () -> Text.translatable(I18N_NEXT_PAGE));
		hoverText.put(PREV_PAGE_BTN, () -> Text.translatable(I18N_PREVIOUS_PAGE));
		hoverText.put(APPLY_BTN, () -> Text.translatable(Resources.I18N_SCREEN_APPLY));
		hoverText.put(SAVE_BUTTON, () -> Text.translatable(Resources.I18N_SCREEN_SAVE_PRESET));
		hoverText.put(EXPORT_BUTTON, () -> Text.translatable(Resources.I18N_SCREEN_EXPORT_PRESET));
		hoverText.put(CLEAR_BTN, () -> Text.translatable(I18N_CLEAR_SPECIES));
	}

	private void updateAbility()
	{
	}

	@Override
	protected void init()
	{
		var mc = MinecraftClient.getInstance();
		var components = SwgEntityComponents.getPersistent(mc.player);
		if (components.getSpecies() != null)
			previewSpecies = SwgSpeciesRegistry.deserialize(components.getSpecies().serialize());
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		//		if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT)
		//			return moveToNextVariableOption(keyCode == GLFW.GLFW_KEY_LEFT);

		//		else if (this.speciesListWidget.getSelectedOrNull() != null)
		//			return this.speciesListWidget.keyPressed(keyCode, scanCode, modifiers);
		//		else if (this.speciesVariableListWidget.getSelectedOrNull() != null)
		//			return this.speciesVariableListWidget.keyPressed(keyCode, scanCode, modifiers);
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private boolean mouseClickedPageSpecies(double mouseX, double mouseY, int button)
	{
		if (NEXT_PAGE_BTN.contains((int)mouseX, (int)mouseY))
		{
			page = Page.VARIABLES;
			previewVariable = null;
			previewVariableValue = null;
			LEFT_SCROLL_THUMB.setScroll(0);
			return true;
		}

		if (CLEAR_BTN.contains((int)mouseX, (int)mouseY) && isPlayerSpecies())
		{
			previewSpecies = null;
			applySpecies();
		}

		var allSpecies = SwgSpeciesRegistry.ALL_SPECIES.get();

		var listOverflowSize = Math.max(0, LIST_ROW_HEIGHT * allSpecies.size() - LEFT_LIST_CUTOUT.height);
		var scrollOffset = -(int)(listOverflowSize * LEFT_SCROLL_THUMB.getScroll());

		for (int i = 0, allSpeciesSize = allSpecies.size(); i < allSpeciesSize; i++)
		{
			if (scrollOffset + LIST_ROW_HEIGHT * i < -LIST_ROW_HEIGHT
			    || scrollOffset + LIST_ROW_HEIGHT * i > LEFT_LIST_CUTOUT.height
			    || !listItemContains(SPECIES_LIST_PANEL_X, SPECIES_LIST_PANEL_Y, (int)mouseX, (int)mouseY, scrollOffset + LIST_ROW_HEIGHT * i, LEFT_LIST_CUTOUT.width))
				continue;

			previewVariable = null;
			previewVariableValue = null;
			previewSpecies = SwgSpeciesRegistry.create(allSpecies.get(i).getSlug(), previewSpeciesGender);
			RIGHT_SCROLL_THUMB.setScroll(0);
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	private boolean mouseClickedPageVariables(double mouseX, double mouseY, int button)
	{
		if (button != GLFW.GLFW_MOUSE_BUTTON_1)
			return super.mouseClicked(mouseX, mouseY, button);

		if (APPLY_BTN.contains((int)mouseX, (int)mouseY))
		{
			applySpecies();
			return true;
		}

		if (PREV_PAGE_BTN.contains((int)mouseX, (int)mouseY))
		{
			page = Page.SPECIES;
			previewVariable = null;
			previewVariableValue = null;
			LEFT_SCROLL_THUMB.setScroll(0);
			return true;
		}

		if (WHITE_SCROLL_THUMB.containsHorizontal((int)mouseX, (int)mouseY))
		{
			WHITE_SCROLL_THUMB.setScrolling(true);
			return true;
		}

		if (RED_SCROLL_THUMB.containsHorizontal((int)mouseX, (int)mouseY))
		{
			RED_SCROLL_THUMB.setScrolling(true);
			return true;
		}

		if (GREEN_SCROLL_THUMB.containsHorizontal((int)mouseX, (int)mouseY))
		{
			GREEN_SCROLL_THUMB.setScrolling(true);
			return true;
		}

		if (BLUE_SCROLL_THUMB.containsHorizontal((int)mouseX, (int)mouseY))
		{
			BLUE_SCROLL_THUMB.setScrolling(true);
			return true;
		}

		if (previewSpecies != null)
		{
			var variables = previewSpecies.getVariables();

			var listOverflowSize = Math.max(0, LIST_ROW_HEIGHT * variables.length - LEFT_LIST_CUTOUT.height);
			var scrollOffset = -(int)(listOverflowSize * LEFT_SCROLL_THUMB.getScroll());

			for (int i = 0, allVariablesSize = variables.length; i < allVariablesSize; i++)
			{
				if (scrollOffset + LIST_ROW_HEIGHT * i < -LIST_ROW_HEIGHT
				    || scrollOffset + LIST_ROW_HEIGHT * i > LEFT_LIST_CUTOUT.height
				    || !listItemContains(SPECIES_LIST_PANEL_X, SPECIES_LIST_PANEL_Y, (int)mouseX, (int)mouseY, scrollOffset + LIST_ROW_HEIGHT * i, LEFT_LIST_CUTOUT.width))
					continue;

				previewVariable = variables[i];

				// TODO: choose value player has applied, if applicable
				previewVariableValue = previewSpecies.getVariable(variables[i]);
				RIGHT_SCROLL_THUMB.setScroll(0);

				if (previewVariable instanceof SpeciesColorVariable)
					setSliderColor(Integer.parseUnsignedInt(previewVariableValue, 16));
				else
					setSliderColor(0);
				return true;
			}
		}

		if (previewVariable != null)
		{
			var options = previewVariable.getPossibleValues();

			var listOverflowSize = Math.max(0, LIST_ROW_HEIGHT * options.size() - RIGHT_LIST_CUTOUT.height);
			var scrollOffset = -(int)(listOverflowSize * RIGHT_SCROLL_THUMB.getScroll());

			for (int i = 0, allOptionsSize = options.size(); i < allOptionsSize; i++)
			{
				if (scrollOffset + LIST_ROW_HEIGHT * i < -LIST_ROW_HEIGHT
				    || scrollOffset + LIST_ROW_HEIGHT * i > RIGHT_LIST_CUTOUT.height
				    || !listItemContains(OPTION_LIST_PANEL_X, OPTION_LIST_PANEL_Y, (int)mouseX, (int)mouseY, scrollOffset + LIST_ROW_HEIGHT * i, RIGHT_LIST_CUTOUT.width))
					continue;

				previewVariableValue = options.get(i);
				previewSpecies.setVariable(previewVariable, previewVariableValue);
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if (!canDrag)
			return false;

		yaw -= deltaX;

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	private void setSliderColor(int color)
	{
		WHITE_SCROLL_THUMB.setScroll(((color >> 24) & 0xFF) / 255f);
		RED_SCROLL_THUMB.setScroll(((color >> 16) & 0xFF) / 255f);
		GREEN_SCROLL_THUMB.setScroll(((color >> 8) & 0xFF) / 255f);
		BLUE_SCROLL_THUMB.setScroll((color & 0xFF) / 255f);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (button == GLFW.GLFW_MOUSE_BUTTON_1)
		{
			if (LEFT_SCROLL_THUMB.containsVertical((int)mouseX, (int)mouseY))
			{
				LEFT_SCROLL_THUMB.setScrolling(true);
				return true;
			}

			if (RIGHT_SCROLL_THUMB.containsVertical((int)mouseX, (int)mouseY))
			{
				RIGHT_SCROLL_THUMB.setScrolling(true);
				return true;
			}

			if (GENDER_TOGGLE.contains((int)mouseX, (int)mouseY))
			{
				if (previewSpeciesGender == SpeciesGender.MALE)
					previewSpeciesGender = SpeciesGender.FEMALE;
				else
					previewSpeciesGender = SpeciesGender.MALE;

				GENDER_TOGGLE.getBlittable().setToggled(previewSpeciesGender == SpeciesGender.FEMALE);

				if (previewSpecies != null)
				{
					var variables = previewSpecies.getVariables();
					var newSpecies = SwgSpeciesRegistry.create(previewSpecies.getSlug(), previewSpeciesGender);

					for (var variable : variables)
						newSpecies.setVariable(variable, previewSpecies.getVariable(variable));

					previewSpecies = newSpecies;
				}

				return true;
			}

			if (RANDOM_BUTTON.contains((int)mouseX, (int)mouseY))
			{
				if (previewSpecies != null)
				{
					for (var variable : previewSpecies.getVariables())
					{
						var possibleValues = variable.getPossibleValues();
						if (possibleValues.isEmpty())
						{
							if (variable instanceof SpeciesColorVariable)
							{
								var value = Integer.toHexString(Resources.RANDOM.nextInt());
								previewSpecies.setVariable(variable, value);
								setSliderColor(Integer.parseUnsignedInt(value, 16));
							}
							continue;
						}

						if (possibleValues.contains("none") && Resources.RANDOM.nextFloat() < 0.5)
							previewSpecies.setVariable(variable, "none");
						else
							previewSpecies.setVariable(variable, possibleValues.get(Resources.RANDOM.nextInt(possibleValues.size())));
					}

					if (previewVariable != null)
						previewVariableValue = previewSpecies.getVariable(previewVariable);
				}
				return true;
			}
		}

		if (switch (page)
				{
					case SPECIES -> mouseClickedPageSpecies(mouseX, mouseY, button);
					case VARIABLES -> mouseClickedPageVariables(mouseX, mouseY, button);
				})
			return true;

		canDrag = true;
		return false;
	}

	private boolean mouseReleasedPageSpecies(double mouseX, double mouseY, int button)
	{
		if (button != GLFW.GLFW_MOUSE_BUTTON_1)
			return super.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	private boolean mouseReleasedPageVariables(double mouseX, double mouseY, int button)
	{
		if (WHITE_SCROLL_THUMB.isScrolling() || RED_SCROLL_THUMB.isScrolling() || GREEN_SCROLL_THUMB.isScrolling() || BLUE_SCROLL_THUMB.isScrolling())
		{
			// TODO: alpha
			if (previewSpecies != null && previewVariable instanceof SpeciesColorVariable)
				previewSpecies.setVariable(previewVariable, Integer.toHexString(ColorUtil.packFloatArgb(RED_SCROLL_THUMB.getScroll(), GREEN_SCROLL_THUMB.getScroll(), BLUE_SCROLL_THUMB.getScroll(), WHITE_SCROLL_THUMB.getScroll())));
		}

		WHITE_SCROLL_THUMB.setScrolling(false);
		RED_SCROLL_THUMB.setScrolling(false);
		GREEN_SCROLL_THUMB.setScrolling(false);
		BLUE_SCROLL_THUMB.setScrolling(false);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		LEFT_SCROLL_THUMB.setScrolling(false);
		RIGHT_SCROLL_THUMB.setScrolling(false);

		canDrag = false;

		return switch (page)
				{
					case SPECIES -> mouseReleasedPageSpecies(mouseX, mouseY, button);
					case VARIABLES -> mouseReleasedPageVariables(mouseX, mouseY, button);
				};
	}

	@Override
	public void tick()
	{
		super.tick();
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		if (LEFT_LIST_CUTOUT.contains((int)mouseX, (int)mouseY) || LEFT_SCROLL_THUMB.containsVertical((int)mouseX, (int)mouseY))
		{
			LEFT_SCROLL_THUMB.inputScroll(amount);
			return true;
		}

		if (RIGHT_LIST_CUTOUT.contains((int)mouseX, (int)mouseY) || RIGHT_SCROLL_THUMB.containsVertical((int)mouseX, (int)mouseY))
		{
			RIGHT_SCROLL_THUMB.inputScroll(amount);
			return true;
		}

		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	private void applySpecies()
	{
		var passedData = new PacketByteBuf(Unpooled.buffer());

		if (previewSpecies == null)
			passedData.writeString("minecraft:none");
		else
			passedData.writeString(this.previewSpecies.serialize());

		// TODO: verify species variables on server
		ClientPlayNetworking.send(SwgPackets.C2S.SetOwnSpecies, passedData);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrices);

		var x = width / 2 - HALF_WIDTH;
		var y = height / 2 - HALF_HEIGHT;
		var selectedSpeciesSlug = previewSpecies == null ? SwgSpeciesRegistry.SPECIES_NONE : previewSpecies.getSlug();
		var isSpeciesPage = page == Page.SPECIES;

		LEFT_LIST_CUTOUT.setOrigin(x, y);
		RIGHT_LIST_CUTOUT.setOrigin(x, y);
		LEFT_SCROLL_THUMB.setOrigin(x, y);
		RIGHT_SCROLL_THUMB.setOrigin(x, y);
		WHITE_SCROLL_THUMB.setOrigin(x, y);
		RED_SCROLL_THUMB.setOrigin(x, y);
		GREEN_SCROLL_THUMB.setOrigin(x, y);
		BLUE_SCROLL_THUMB.setOrigin(x, y);
		TRANSPARENT_VIEWPORT_BACKGROUND.setOrigin(x, y);
		blitRectangles.forEach(blitRectangle -> blitRectangle.setOrigin(x, y));

		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		RenderSystem.setShaderTexture(0, BACKGROUND);

		LEFT_LIST_CUTOUT.blit(matrices);
		RIGHT_LIST_CUTOUT.blit(matrices);

		if (isSpeciesPage)
			renderLeftScrollPanelPageSpecies(matrices, mouseX, mouseY, x, y, selectedSpeciesSlug);
		else
		{
			renderLeftScrollPanelPageVariables(matrices, mouseX, mouseY, x, y);
			renderRightScrollPanelPageVariables(matrices, mouseX, mouseY, x, y);
		}

		RenderSystem.setShaderTexture(0, BACKGROUND);
		drawTexture(matrices, x, y, 0, 0, 427, 240, 512, 512);

		GENDER_TOGGLE.x = isSpeciesPage ? 218 : 158;
		SAVE_BUTTON.visible = !isSpeciesPage;
		EXPORT_BUTTON.visible = !isSpeciesPage;
		NEXT_PAGE_BTN.visible = isSpeciesPage;
		CLEAR_BTN.visible = isSpeciesPage && isPlayerSpecies();
		PREV_PAGE_BTN.visible = !isSpeciesPage;
		APPLY_BTN.visible = !isSpeciesPage;

		LEFT_ARROW.visible = false;
		RIGHT_ARROW.visible = false;

		LEFT_SCROLL_THUMB.updateMouseStateVertical(mouseX, mouseY);
		RIGHT_SCROLL_THUMB.updateMouseStateVertical(mouseX, mouseY);
		WHITE_SCROLL_THUMB.updateMouseStateHorizontal(mouseX, mouseY);
		RED_SCROLL_THUMB.updateMouseStateHorizontal(mouseX, mouseY);
		GREEN_SCROLL_THUMB.updateMouseStateHorizontal(mouseX, mouseY);
		BLUE_SCROLL_THUMB.updateMouseStateHorizontal(mouseX, mouseY);
		blitRectangles.forEach(blitRectangle -> blitRectangle.updateMouseState(mouseX, mouseY));

		SCROLLBAR_TRACK.blitVertical(matrices, x + 90, y + 25, 190);
		LEFT_SCROLL_THUMB.blitVertical(matrices);

		var isColorVariable = previewVariable instanceof SpeciesColorVariable scv && scv.getPossibleValues().isEmpty();
		if (!isSpeciesPage)
		{
			SCROLLBAR_TRACK.blitVertical(matrices, x + 417, y + 42, 115);
			RIGHT_SCROLL_THUMB.blitVertical(matrices);

			WHITE_SCROLL_THUMB.setVisible(isColorVariable);
			RED_SCROLL_THUMB.setVisible(isColorVariable);
			GREEN_SCROLL_THUMB.setVisible(isColorVariable);
			BLUE_SCROLL_THUMB.setVisible(isColorVariable);

			if (isColorVariable)
			{
				SLIDER_TRACK_WHITE.blitHorizontal(matrices, x + 270, y + 165, 140);
				SLIDER_TRACK_RED.blitHorizontal(matrices, x + 305, y + 178, 105);
				SLIDER_TRACK_GREEN.blitHorizontal(matrices, x + 305, y + 188, 105);
				SLIDER_TRACK_BLUE.blitHorizontal(matrices, x + 305, y + 198, 105);

				WHITE_SCROLL_THUMB.blitHorizontal(matrices);
				RED_SCROLL_THUMB.blitHorizontal(matrices);
				GREEN_SCROLL_THUMB.blitHorizontal(matrices);
				BLUE_SCROLL_THUMB.blitHorizontal(matrices);
			}
		}
		blitRectangles.forEach(blitRectangle -> blitRectangle.blit(matrices));

		var rsm = RenderSystem.getModelViewStack();
		rsm.push();
		rsm.translate(x + 182, y + 190, 50);
		rsm.multiply(new Quaternionf().rotationX(MathUtil.toRadians(-22)));
		rsm.multiply(new Quaternionf().rotationY(MathUtil.toRadians(-45)));
		drawEntity(rsm, previewSpecies, 0, 0, 80);
		rsm.pop();
		RenderSystem.applyModelViewMatrix();

		this.textRenderer.draw(matrices, this.title, x + 9, y + 9, 0x404040);

		if (!selectedSpeciesSlug.equals(SwgSpeciesRegistry.SPECIES_NONE))
		{
			var speciesHeaderText = Text.translatable(SwgSpeciesRegistry.getTranslationKey(selectedSpeciesSlug));
			var speciesHeaderOffset = -textRenderer.getWidth(speciesHeaderText) / 2f;

			matrices.push();
			matrices.translate(x + 344, y + 15, 0);
			matrices.scale(2, 2, 2);
			textRenderer.draw(matrices, speciesHeaderText, speciesHeaderOffset, 0, 0x000000);
			matrices.pop();

			SwgSpeciesIcons.renderLargeCircle(matrices, (int)(x + 320 + 2 * speciesHeaderOffset), y + 12, selectedSpeciesSlug, false);
		}
		else
		{
			// TODO: draw a header when the player has no species selected?
		}

		if (isSpeciesPage)
		{
			if (previewSpecies != null)
				TextUtil.drawArea(matrices, textRenderer, x + 260, y + 40, 150, Text.translatable(SwgSpeciesLore.DESCRIPTION.createLanguageKey(previewSpecies.getSlug())));
			else
				TextUtil.drawArea(matrices, textRenderer, x + 260, y + 40, 150, Text.translatable(I18N_CHOOSE_SPECIES));
		}
		else
		{
			if (previewVariable == null)
				TextUtil.drawArea(matrices, textRenderer, x + 260, y + 40, 150, Text.translatable(I18N_CHOOSE_OPTION));

			if (isColorVariable)
			{
				RenderSystem.setShaderTexture(0, BACKGROUND);
				TRANSPARENT_VIEWPORT_BACKGROUND.blit(matrices);
				renderColorPreview(x + 270, y + 178, 25, RED_SCROLL_THUMB.getScroll(), GREEN_SCROLL_THUMB.getScroll(), BLUE_SCROLL_THUMB.getScroll(), WHITE_SCROLL_THUMB.getScroll());
			}
		}

		if (hoveringEntry != null)
		{
			if (!hoveringEntry.area.contains(mouseX, mouseY))
				hoveringEntry = null;

			if (hoveringEntry != null && hoverText.containsKey(hoveringEntry.area) && hoveringEntry.shouldShowTooltip())
				renderTooltip(matrices, hoverText.get(hoveringEntry.area).get(), mouseX, mouseY);
		}

		if (hoveringEntry == null)
		{
			blitRectangles
					.stream()
					.filter(blitRectangle -> blitRectangle.contains(mouseX, mouseY))
					.findFirst()
					.ifPresent(rect -> hoveringEntry = new HoveringEntry(rect));
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	private boolean isPlayerSpecies()
	{
		var mc = MinecraftClient.getInstance();
		var components = SwgEntityComponents.getPersistent(mc.player);
		return components.getSpecies() != null;
	}

	private static void renderColorPreview(int x, int y, int size, float r, float g, float b, float a)
	{
		var bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(x, y + size, 0).color(r, g, b, a).texture(0, 1).next();
		bufferBuilder.vertex(x + size, y + size, 0).color(r, g, b, a).texture(1, 1).next();
		bufferBuilder.vertex(x + size, y, 0).color(r, g, b, a).texture(1, 0).next();
		bufferBuilder.vertex(x, y, 0).color(r, g, b, a).texture(0, 0).next();

		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		RenderSystem.enableBlend();
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.disableBlend();
	}

	private void renderLeftScrollPanelPageVariables(MatrixStack matrices, int mouseX, int mouseY, int x, int y)
	{
		if (previewSpecies == null)
		{
			LEFT_SCROLL_THUMB.setVisible(false);
			return;
		}

		var variables = previewSpecies.getVariables();
		LEFT_SCROLL_THUMB.setScrollInputFactor(variables.length);

		var listOverflowSize = Math.max(0, LIST_ROW_HEIGHT * variables.length - LEFT_LIST_CUTOUT.height);
		var scrollOffset = -(int)(listOverflowSize * LEFT_SCROLL_THUMB.getScroll());

		LEFT_SCROLL_THUMB.setVisible(listOverflowSize > 0);

		for (var i = 0; i < variables.length; i++)
		{
			if (scrollOffset + LIST_ROW_HEIGHT * i < -LIST_ROW_HEIGHT || scrollOffset + LIST_ROW_HEIGHT * i > LEFT_LIST_CUTOUT.height)
				continue;

			var entry = variables[i];
			var hovering = entry == previewVariable || (!LEFT_SCROLL_THUMB.isScrolling() && listItemContains(SPECIES_LIST_PANEL_X, SPECIES_LIST_PANEL_Y, mouseX, mouseY, scrollOffset + LIST_ROW_HEIGHT * i, LEFT_LIST_CUTOUT.width));

			var translatedText = Text.translatable(entry.getTranslationKey());
			var wrapped = this.textRenderer.wrapLines(translatedText, 80);
			this.textRenderer.draw(matrices, wrapped.get(0), x + SPECIES_LIST_PANEL_X + 7, y + SPECIES_LIST_PANEL_Y + 6 + scrollOffset + LIST_ROW_CONTENT_CENTERING_OFFSET + LIST_ROW_HEIGHT * i, hovering ? 0xFFFFFF : 0x000000);
		}
	}

	private void renderRightScrollPanelPageVariables(MatrixStack matrices, int mouseX, int mouseY, int x, int y)
	{
		if (previewVariable == null)
		{
			RIGHT_SCROLL_THUMB.setVisible(false);
			return;
		}

		var options = previewVariable.getPossibleValues();
		RIGHT_SCROLL_THUMB.setScrollInputFactor(options.size());

		var listOverflowSize = Math.max(0, LIST_ROW_HEIGHT * options.size() - RIGHT_LIST_CUTOUT.height);
		var scrollOffset = -(int)(listOverflowSize * RIGHT_SCROLL_THUMB.getScroll());

		RIGHT_SCROLL_THUMB.setVisible(listOverflowSize > 0);

		for (var i = 0; i < options.size(); i++)
		{
			if (scrollOffset + LIST_ROW_HEIGHT * i < -LIST_ROW_HEIGHT || scrollOffset + LIST_ROW_HEIGHT * i > RIGHT_LIST_CUTOUT.height)
				continue;

			var entry = options.get(i);
			var hovering = Objects.equals(entry, previewVariableValue) || (!RIGHT_SCROLL_THUMB.isScrolling() && listItemContains(OPTION_LIST_PANEL_X, OPTION_LIST_PANEL_Y, mouseX, mouseY, scrollOffset + LIST_ROW_HEIGHT * i, RIGHT_LIST_CUTOUT.width));

			var translatedText = Text.translatable(previewVariable.getTranslationFor(entry));
			var wrapped = this.textRenderer.wrapLines(translatedText, 150);
			this.textRenderer.draw(matrices, wrapped.get(0), x + OPTION_LIST_PANEL_X + 7, y + OPTION_LIST_PANEL_Y + 6 + scrollOffset + LIST_ROW_CONTENT_CENTERING_OFFSET + LIST_ROW_HEIGHT * i, hovering ? 0xFFFFFF : 0x000000);
		}
	}

	private void renderLeftScrollPanelPageSpecies(MatrixStack matrices, int mouseX, int mouseY, int x, int y, Identifier selectedSpeciesSlug)
	{
		var allSpecies = SwgSpeciesRegistry.ALL_SPECIES.get();
		LEFT_SCROLL_THUMB.setScrollInputFactor(allSpecies.size());

		var listOverflowSize = Math.max(0, LIST_ROW_HEIGHT * allSpecies.size() - LEFT_LIST_CUTOUT.height);
		var scrollOffset = -(int)(listOverflowSize * LEFT_SCROLL_THUMB.getScroll());

		LEFT_SCROLL_THUMB.setVisible(listOverflowSize > 0);

		for (var i = 0; i < allSpecies.size(); i++)
		{
			if (scrollOffset + LIST_ROW_HEIGHT * i < -LIST_ROW_HEIGHT || scrollOffset + LIST_ROW_HEIGHT * i > LEFT_LIST_CUTOUT.height)
				continue;

			var entry = allSpecies.get(i);
			var hovering = entry.getSlug().equals(selectedSpeciesSlug) || (!LEFT_SCROLL_THUMB.isScrolling() && listItemContains(SPECIES_LIST_PANEL_X, SPECIES_LIST_PANEL_Y, mouseX, mouseY, scrollOffset + LIST_ROW_HEIGHT * i, LEFT_LIST_CUTOUT.width));
			SwgSpeciesIcons.renderLargeCircle(matrices, x + SPECIES_LIST_PANEL_X, y + SPECIES_LIST_PANEL_Y + scrollOffset + LIST_ROW_CONTENT_CENTERING_OFFSET + LIST_ROW_HEIGHT * i, entry.getSlug(), hovering);
		}

		for (var i = 0; i < allSpecies.size(); i++)
		{
			if (scrollOffset + LIST_ROW_HEIGHT * i < -LIST_ROW_HEIGHT || scrollOffset + LIST_ROW_HEIGHT * i > LEFT_LIST_CUTOUT.height)
				continue;

			var entry = allSpecies.get(i);
			var hovering = entry.getSlug().equals(selectedSpeciesSlug) || (!LEFT_SCROLL_THUMB.isScrolling() && listItemContains(SPECIES_LIST_PANEL_X, SPECIES_LIST_PANEL_Y, mouseX, mouseY, scrollOffset + LIST_ROW_HEIGHT * i, LEFT_LIST_CUTOUT.width));

			var translatedText = Text.translatable(SwgSpeciesRegistry.getTranslationKey(entry.getSlug()));
			var wrapped = this.textRenderer.wrapLines(translatedText, 60);
			this.textRenderer.draw(matrices, wrapped.get(0), x + SPECIES_LIST_PANEL_X + 27, y + SPECIES_LIST_PANEL_Y + 6 + scrollOffset + LIST_ROW_CONTENT_CENTERING_OFFSET + LIST_ROW_HEIGHT * i, hovering ? 0xFFFFFF : 0x000000);
		}
	}

	private boolean listItemContains(int listX, int listY, int mouseX, int mouseY, int rowY, int rowWidth)
	{
		var x = width / 2 - HALF_WIDTH;
		var y = height / 2 - HALF_HEIGHT;

		return MathUtil.rectContains(
				x + listX,
				y + listY + LIST_ROW_CONTENT_CENTERING_OFFSET + rowY,
				rowWidth,
				LIST_ROW_CONTENT_HEIGHT,
				mouseX, mouseY
		);
	}

	public void drawEntity(MatrixStack matrixStack, SwgSpecies species, int x, int y, int size)
	{
		matrixStack.push();

		AbstractClientPlayerEntity entity = client.player;
		MatrixStackUtil.scalePos(matrixStack, size, size, -size);
		RenderSystem.applyModelViewMatrix();

		var matrixStack2 = new MatrixStack();
		Quaternionf quaternion = new Quaternionf().rotationZ(MathHelper.PI);
		matrixStack2.multiply(quaternion);
		var h = entity.bodyYaw;
		var i = entity.getYaw();
		var j = entity.getPitch();
		var k = entity.prevHeadYaw;
		var l = entity.headYaw;
		entity.bodyYaw = 180 + yaw;
		entity.setYaw(180 + yaw);
		entity.setPitch(0);
		entity.headYaw = entity.getYaw();
		entity.prevHeadYaw = entity.getYaw();

		entity.limbDistance = 0;

		var immediate = client.getBufferBuilders().getEntityVertexConsumers();

		var erda = (EntityRenderDispatcherAccessor)client.getEntityRenderDispatcher();
		var renderers = erda.getModelRenderers();

		DiffuseLighting.method_34742();
		var entityRenderDispatcher = client.getEntityRenderDispatcher();
		entityRenderDispatcher.setRenderShadows(false);
		RenderSystem.runAsFancy(() -> {
			if (species == null)
			{
				entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0, 1, matrixStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE);
			}
			else
			{
				CameraHelper.forcePlayerRender = true;

				var renderer = renderers.get(species.getModel().toString());
				if (renderer instanceof PlayerSpeciesModelRenderer perwm)
				{
					var texture = SwgSpeciesRenderer.getTexture(entity, species);

					if (texture.equals(Client.TEX_TRANSPARENT))
					{
						// Continue rendering previous model while current texture is loading
						if (previousRenderer != null && previousTexture != null)
							previousRenderer.renderWithOverrides(species, previousTexture, entity, 0, 1, matrixStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE);
					}
					else
					{
						perwm.renderWithOverrides(species, texture, entity, 0, 1, matrixStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE);
						previousTexture = texture;
					}

					previousRenderer = perwm;
				}
				else if (renderer != null)
					renderer.render(entity, 1, 1, matrixStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE);

				CameraHelper.forcePlayerRender = false;
			}
		});
		immediate.draw();
		entityRenderDispatcher.setRenderShadows(true);

		immediate.draw();
		entity.bodyYaw = h;
		entity.setYaw(i);
		entity.setPitch(j);
		entity.prevHeadYaw = k;
		entity.headYaw = l;

		matrixStack.pop();

		RenderSystem.applyModelViewMatrix();
		DiffuseLighting.enableGuiDepthLighting();
	}

	@Override
	public void renderBackgroundTexture(int vOffset)
	{
		var tessellator = Tessellator.getInstance();
		var bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		var f = 32.0F;
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0D, this.height, 0.0D).texture(0.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.width, this.height, 0.0D).texture((float)this.width / 32.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.width, 0.0D, 0.0D).texture((float)this.width / 32.0F, (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(0.0D, 0.0D, 0.0D).texture(0.0F, (float)vOffset).color(64, 64, 64, 255).next();
		tessellator.draw();
	}
}
