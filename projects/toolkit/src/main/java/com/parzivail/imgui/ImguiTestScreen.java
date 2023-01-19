package com.parzivail.imgui;

import com.parzivail.pswg.Resources;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ImguiTestScreen extends ImguiScreen
{
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit_home");

	private Screen parent;

	private final ImString str = new ImString(5);
	private final float[] flt = new float[1];
	private int count;

	public ImguiTestScreen(Screen parent)
	{
		super(Text.translatable(I18N_TOOLKIT_HOME));
		this.parent = parent;
	}

	@Override
	public void process()
	{
		if (ImGui.begin("Demo", ImGuiWindowFlags.AlwaysAutoResize))
		{
			ImGui.text("Hello, World!");

			if (ImGui.button("Save"))
				count++;

			ImGui.sameLine();
			ImGui.text(String.valueOf(count));
			ImGui.inputText("string", str, ImGuiInputTextFlags.CallbackResize);
			ImGui.text("Result: " + str.get());
			ImGui.sliderFloat("float", flt, 0, 1);
			ImGui.separator();
			ImGui.text("Extra");
		}
		ImGui.end();
	}
}
