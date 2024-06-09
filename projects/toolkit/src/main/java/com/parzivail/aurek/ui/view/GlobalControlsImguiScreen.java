package com.parzivail.aurek.ui.view;

import com.parzivail.aurek.ui.ImguiScreen;
import com.parzivail.aurek.util.DialogUtil;
import com.parzivail.pswg.Resources;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import imgui.type.ImBoolean;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class GlobalControlsImguiScreen extends ImguiScreen
{
	private static final String I18N_TOOLKIT_GLOBAL_CONTROLS = Resources.screen("global_controls");

	public GlobalControlsImguiScreen()
	{
		super(null, Text.translatable(I18N_TOOLKIT_GLOBAL_CONTROLS));
	}

	@Override
	public void process()
	{
		if (client == null || client.player == null || client.player.getMainHandStack() == null)
			return;

		var open = new ImBoolean(true);

		if (ImGui.begin("Aurek", open, ImGuiWindowFlags.AlwaysAutoResize))
		{
			if (ImGui.button("Take Panorama"))
			{
				var result = DialogUtil.saveFile("Save Panorama", "*.png");
				if (result.isPresent())
				{
					var path = new File(result.get()).getParentFile();
					client.takePanorama(path, 4096, 4096);
				}
			}
		}
		ImGui.end();

		if (!open.get() || ImGui.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
			client.setScreen(null);
	}

	@Override
	protected void drawBackground(DrawContext context)
	{
	}

	@Override
	public boolean shouldPause()
	{
		return false;
	}
}
