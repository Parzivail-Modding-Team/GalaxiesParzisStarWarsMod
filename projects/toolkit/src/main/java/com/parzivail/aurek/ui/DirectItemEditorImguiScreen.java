package com.parzivail.aurek.ui;

import com.parzivail.aurek.editor.IDirectItemEditor;
import com.parzivail.aurek.imgui.ImguiScreen;
import com.parzivail.pswg.Resources;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import imgui.type.ImBoolean;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class DirectItemEditorImguiScreen extends ImguiScreen
{
	private static final String I18N_TOOLKIT_DIRECT_EDITOR = Resources.screen("direct_item_editor");

	public DirectItemEditorImguiScreen()
	{
		super(null, Text.translatable(I18N_TOOLKIT_DIRECT_EDITOR));
	}

	@Override
	public void process()
	{
		if (client == null || client.player == null || client.player.getMainHandStack() == null)
			return;

		var stack = client.player.getMainHandStack();

		var itemClass = stack.getItem().getClass();

		var open = new ImBoolean(true);

		if (ImGui.begin("Direct Item Editor", open, ImGuiWindowFlags.AlwaysAutoResize))
		{
			if (!IDirectItemEditor.EDITORS.containsKey(itemClass))
			{
				ImGui.text(String.format("No editor defined for %s", itemClass.getTypeName()));
			}
			else
				IDirectItemEditor.EDITORS.get(itemClass).process(client, stack);
		}
		ImGui.end();

		if (!open.get() || ImGui.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
			client.setScreen(null);
	}

	@Override
	protected void drawBackground(MatrixStack matrices)
	{
	}

	@Override
	public boolean shouldPause()
	{
		return false;
	}
}
