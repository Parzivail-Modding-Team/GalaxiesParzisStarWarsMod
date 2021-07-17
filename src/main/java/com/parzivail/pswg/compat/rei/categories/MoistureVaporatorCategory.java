package com.parzivail.pswg.compat.rei.categories;

import com.parzivail.pswg.compat.rei.displays.MoistureVaporatorDisplay;
import com.parzivail.pswg.compat.rei.plugins.GalaxiesREICategories;
import com.parzivail.pswg.container.SwgBlocks;
import it.unimi.dsi.fastutil.ints.IntList;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.registry.display.TransferDisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

@Environment(EnvType.CLIENT)
public class MoistureVaporatorCategory implements TransferDisplayCategory<MoistureVaporatorDisplay>
{
	// While this is scheduled for removal, we still need to override it.
	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void renderRedSlots(MatrixStack matrices, List<Widget> widgets, Rectangle bounds, MoistureVaporatorDisplay display, IntList redSlots)
	{
		// TODO
	}

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
	public CategoryIdentifier<? extends MoistureVaporatorDisplay> getCategoryIdentifier()
	{
		return GalaxiesREICategories.MOISTURE_VAPORATOR;
	}
}
