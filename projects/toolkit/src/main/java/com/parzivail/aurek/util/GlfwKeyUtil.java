package com.parzivail.aurek.util;

import org.lwjgl.glfw.GLFW;

import java.awt.event.KeyEvent;
import java.util.HashMap;

public class GlfwKeyUtil
{
	private static final HashMap<Integer, Integer> glfwToAwtKeyMap = new HashMap<>();

	static
	{
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_ENTER, KeyEvent.VK_ENTER);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_BACKSPACE, KeyEvent.VK_BACK_SPACE);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_TAB, KeyEvent.VK_TAB);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_LEFT_SHIFT, KeyEvent.VK_SHIFT);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_RIGHT_SHIFT, KeyEvent.VK_SHIFT);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_LEFT_CONTROL, KeyEvent.VK_CONTROL);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_RIGHT_CONTROL, KeyEvent.VK_CONTROL);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_LEFT_ALT, KeyEvent.VK_ALT);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_RIGHT_ALT, KeyEvent.VK_ALT);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_PAUSE, KeyEvent.VK_PAUSE);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_CAPS_LOCK, KeyEvent.VK_CAPS_LOCK);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_ESCAPE, KeyEvent.VK_ESCAPE);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_PAGE_UP, KeyEvent.VK_PAGE_UP);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_PAGE_DOWN, KeyEvent.VK_PAGE_DOWN);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_END, KeyEvent.VK_END);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_HOME, KeyEvent.VK_HOME);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_LEFT, KeyEvent.VK_LEFT);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_UP, KeyEvent.VK_UP);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_RIGHT, KeyEvent.VK_RIGHT);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_DOWN, KeyEvent.VK_DOWN);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_DELETE, KeyEvent.VK_DELETE);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_NUM_LOCK, KeyEvent.VK_NUM_LOCK);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_SCROLL_LOCK, KeyEvent.VK_SCROLL_LOCK);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F1, KeyEvent.VK_F1);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F2, KeyEvent.VK_F2);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F3, KeyEvent.VK_F3);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F4, KeyEvent.VK_F4);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F5, KeyEvent.VK_F5);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F6, KeyEvent.VK_F6);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F7, KeyEvent.VK_F7);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F8, KeyEvent.VK_F8);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F9, KeyEvent.VK_F9);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F10, KeyEvent.VK_F10);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F11, KeyEvent.VK_F11);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F12, KeyEvent.VK_F12);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F13, KeyEvent.VK_F13);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F14, KeyEvent.VK_F14);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F15, KeyEvent.VK_F15);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F16, KeyEvent.VK_F16);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F17, KeyEvent.VK_F17);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F18, KeyEvent.VK_F18);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F19, KeyEvent.VK_F19);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F20, KeyEvent.VK_F20);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F21, KeyEvent.VK_F21);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F22, KeyEvent.VK_F22);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F23, KeyEvent.VK_F23);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_F24, KeyEvent.VK_F24);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_PRINT_SCREEN, KeyEvent.VK_PRINTSCREEN);
		glfwToAwtKeyMap.put(GLFW.GLFW_KEY_INSERT, KeyEvent.VK_INSERT);
	}

	public static int getAwtGet(int glfwKey)
	{
		// TODO: will this incorrectly map some non-alphanumeric but non-modifier characters to VK_UNDEFINED?
		if (glfwKey >= KeyEvent.VK_0 && glfwKey <= KeyEvent.VK_9 || glfwKey >= KeyEvent.VK_A && glfwKey <= KeyEvent.VK_Z || (glfwKey & 0x01000000) != 0)
			return glfwKey;

		return glfwToAwtKeyMap.getOrDefault(glfwKey, KeyEvent.VK_UNDEFINED);
	}
}
