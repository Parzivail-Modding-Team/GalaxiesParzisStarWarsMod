package com.parzivail.pswg.compat.rei.categories;

import com.parzivail.pswg.compat.rei.displays.MoistureVaporatorDisplay;
import com.parzivail.pswg.compat.rei.plugins.GalaxiesREICategories;
import com.parzivail.pswg.compat.rei.plugins.GalaxiesREIClientPlugin;
import com.parzivail.pswg.container.SwgBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.text.DecimalFormat;
import java.util.List;

@Environment(EnvType.CLIENT)
public class MoistureVaporatorCategory implements DisplayCategory<MoistureVaporatorDisplay>
{
	@Override
	public Renderer getIcon()
	{
		return EntryStacks.of(SwgBlocks.MoistureVaporator.Gx8);
	}

	@Override
	public Text getTitle()
	{
		return new TranslatableText("category.pswg.vaporator");
	}

	@Override
	public List<Widget> setupDisplay(MoistureVaporatorDisplay display, Rectangle bounds)
	{
		// TODO: make animated
		var recipePos = bounds.getLocation();
		recipePos.translate(15, 15);
		var inputSlotPos = recipePos.clone();
		inputSlotPos.translate(1, 10);
		var outputSlotPos = recipePos.clone();
		outputSlotPos.translate(99, 10);
		var duration = display.getDuration();
		var df = new DecimalFormat("###.##");
		return List.of(
				Widgets.createRecipeBase(bounds),
				Widgets.createTexturedWidget(REIRuntime.getInstance().isDarkThemeEnabled() ? GalaxiesREIClientPlugin.DISPLAY_TEXTURE_DARK : GalaxiesREIClientPlugin.DISPLAY_TEXTURE, bounds.getX() + 15, bounds.getY() + 15, 120, 34),
				Widgets.createLabel(new Point(bounds.x + bounds.width - 5, bounds.y + 5),
				                    new TranslatableText("category.pswg.vaporator.time", df.format(duration / 20d))).noShadow().rightAligned().color(0xFF404040, 0xFFBBBBBB),
				Widgets.createSlot(inputSlotPos).markInput().entries(display.getInputEntries().get(0)),
				Widgets.createSlot(outputSlotPos).disableBackground().markOutput().entries(display.getOutputEntries().get(0))
		);
	}

	@Override
	public int getDisplayHeight()
	{
		// Texture width is 120, display width is 150, 15 padding on each side
		// Texture height is 34, with 15 padding on each side, 64 display height
		return 64;
	}

	@Override
	public CategoryIdentifier<? extends MoistureVaporatorDisplay> getCategoryIdentifier()
	{
		return GalaxiesREICategories.MOISTURE_VAPORATOR;
	}
}
