package com.parzivail.pswgtk.util;

import imgui.internal.ImGui;
import org.lwjgl.glfw.GLFW;

public class ImGuiHelper
{
	public static boolean isShiftDown()
	{
		return ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
	}

	public static boolean isCtrlDown()
	{
		return ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
	}
}
