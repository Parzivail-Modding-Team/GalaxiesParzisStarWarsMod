package com.parzivail.aurek.imgui;

import com.parzivail.aurek.ToolkitClient;
import com.parzivail.aurek.imgui.toast.ImguiNotify;
import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImGuiIO;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGui;
import imgui.internal.ImGuiContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImGuiHelper
{
	static
	{
		final boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
		if (isMac)
			glslVersion = "#version 150";
		else
			glslVersion = "#version 130";
	}

	public static final int ImGuiWindowFlags_Tooltip = 1 << 25;

	private static String glslVersion = null;

	private static ImGuiContext context;
	private static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
	private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

	private static ImFont latinFont;
	private static ImFont aurebeshFont;
	private static ImFont iconFont;

	private static boolean frameDrawn = true;

	public static void init()
	{
		var client = MinecraftClient.getInstance();
		var window = client.getWindow();
		var handle = window.getHandle();

		initImgui();

		imGuiGlfw.init(handle, true);
		imGuiGl3.init(glslVersion);
	}

	private static void initImgui()
	{
		context = ImGui.createContext();
		setupDefaultFonts();
	}

	private static byte[] getBytes(String domain, Identifier resourceLocation) throws URISyntaxException, IOException
	{
		var resource = ImGuiHelper.class.getClassLoader().getResource(domain + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath());
		return Files.readAllBytes(Paths.get(resource.toURI()));
	}

	private static void setupDefaultFonts()
	{
		final ImGuiIO io = ImGui.getIO();
		io.setIniFilename(null);
		io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
		io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
		io.setConfigDockingWithShift(true);
		io.setConfigViewportsNoTaskBarIcon(true);

		final ImFontConfig fontConfig = new ImFontConfig();

		try
		{
			latinFont = io.getFonts().addFontFromMemoryTTF(getBytes("assets", ToolkitClient.id("font/inter_v.ttf")), 18, fontConfig);

			fontConfig.setMergeMode(true);
			iconFont = io.getFonts().addFontFromMemoryTTF(getBytes("assets", ToolkitClient.id("font/icons.ttf")), 18, fontConfig, AurekIconFont.ICON_RANGE);
			io.getFonts().build();
		}
		catch (URISyntaxException | IOException e)
		{
			throw new RuntimeException(e);
		}

		fontConfig.destroy();
	}

	public static ImFont getLatinFont()
	{
		return latinFont;
	}

	public static ImFont getAurebeshFont()
	{
		return aurebeshFont;
	}

	public static ImFont getIconFont()
	{
		return iconFont;
	}

	public static void swapBuffers()
	{
		imGuiGl3.renderDrawData(ImGui.getDrawData());
	}

	/**
	 * Method called at the beginning of the main cycle.
	 * It clears OpenGL buffer and starts an ImGui frame.
	 */
	public static void startFrame()
	{
		if (!frameDrawn)
			return;

		imGuiGlfw.newFrame();
		ImGui.newFrame();
		ImGuizmo.beginFrame();

		frameDrawn = false;
	}

	/**
	 * Method called in the end of the main cycle.
	 * It renders ImGui and swaps GLFW buffers to show an updated frame.
	 */
	public static void endFrame()
	{
		if (frameDrawn)
			return;

		ImguiNotify.render();
		ImGui.render();

		if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable))
		{
			ImGui.updatePlatformWindows();
			ImGui.renderPlatformWindowsDefault();
		}

		frameDrawn = true;
	}

	public static boolean isShiftDown()
	{
		return ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
	}

	public static boolean isCtrlDown()
	{
		return ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
	}
}
