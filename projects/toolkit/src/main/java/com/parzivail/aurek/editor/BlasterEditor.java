package com.parzivail.aurek.editor;

import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.rits.cloning.Cloner;
import imgui.internal.ImGui;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class BlasterEditor implements IDirectItemEditor
{
	private static final Cloner CLONER = new Cloner();
	private static Map<Identifier, BlasterDescriptor> originalPresets = null;

	private BlasterEditor()
	{
	}

	@Override
	public void process(MinecraftClient client, ItemStack stack)
	{
		if (originalPresets == null)
		{
			originalPresets = new HashMap<>();

			for (var entry : PswgContent.getBlasterPresets().entrySet())
				// We directly modify the blaster descriptor in memory, so a deep clone is required to enable "reset to default"
				originalPresets.put(entry.getKey(), CLONER.shallowClone(entry.getValue()));
		}

		if (!(stack.getItem() instanceof BlasterItem blaster))
			return;

		if (ImGui.beginTabBar("scopes"))
		{
			if (ImGui.beginTabItem("Global Properties"))
			{
				var bd = BlasterItem.getBlasterDescriptor(stack, false);

				ImGui.text(String.format("Identifier: %s", bd.id.toString()));

				var f = new ImFloat(bd.damage);
				if (ImGui.inputFloat("Damage", f, 0.5f))
					bd.damage = f.get();

				f.set(bd.range);
				if (ImGui.inputFloat("Range", f, 1))
					bd.range = f.get();

				f.set(bd.weight);
				if (ImGui.inputFloat("Weight", f, 0.5f))
					bd.weight = f.get();

				f.set(bd.boltHue);
				if (ImGui.inputFloat("Bolt Hue", f, 0.05f))
					bd.boltHue = f.get();

				f.set(bd.boltLength);
				if (ImGui.inputFloat("Bolt Length", f, 0.25f))
					bd.boltLength = f.get();

				f.set(bd.boltRadius);
				if (ImGui.inputFloat("Bolt Radius", f, 0.25f))
					bd.boltRadius = f.get();

				var i = new ImInt(bd.magazineSize);
				if (ImGui.inputInt("Magazine Size", i))
					bd.magazineSize = i.get();

				var iPtr = new int[1];
				iPtr[0] = bd.automaticRepeatTime;
				if (ImGui.sliderInt("Auto Repeat Time", iPtr, 1, 100))
					bd.automaticRepeatTime = iPtr[0];

				iPtr[0] = bd.burstRepeatTime;
				if (ImGui.sliderInt("Burst Repeat Time", iPtr, 1, 100))
					bd.burstRepeatTime = iPtr[0];

				iPtr[0] = bd.burstSize;
				if (ImGui.sliderInt("Burst Size", iPtr, 1, 10))
					bd.burstSize = iPtr[0];

				iPtr[0] = bd.burstGap;
				if (ImGui.sliderInt("Burst Gap", iPtr, 1, 10))
					bd.burstGap = iPtr[0];

				iPtr[0] = bd.quickdrawDelay;
				if (ImGui.sliderInt("Quickdraw Delay", iPtr, 1, 100))
					bd.quickdrawDelay = iPtr[0];

				if (ImGui.button("Discard Changes"))
					CLONER.copyPropertiesOfInheritedClass(originalPresets.get(bd.id), bd);

				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Instance Properties"))
			{
				var bt = new BlasterTag(stack.getOrCreateNbt());

				bt.serializeAsSubtag(stack);
				ImGui.endTabItem();
			}

			ImGui.endTabBar();
		}
	}

	public static void register()
	{
		EDITORS.put(BlasterItem.class, new BlasterEditor());
	}
}
