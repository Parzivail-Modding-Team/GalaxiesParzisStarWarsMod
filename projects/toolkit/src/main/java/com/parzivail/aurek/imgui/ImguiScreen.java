package com.parzivail.aurek.imgui;

import com.parzivail.aurek.ToolkitClient;
import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public abstract class ImguiScreen extends Screen
{
	private static String glslVersion = null;

	private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
	private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

	protected static long handle;

	protected ImFont latinFont;
	protected ImFont aurebeshFont;
	protected ImFont iconFont;

	private Optional<Screen> nextScreen = Optional.empty();

	static
	{
		final boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
		if (isMac)
			glslVersion = "#version 150";
		else
			glslVersion = "#version 130";
	}

	private Screen parent;

	protected ImguiScreen(Screen parent, Text title)
	{
		super(title);
		this.parent = parent;
	}

	public static byte[] getBytes(String domain, Identifier resourceLocation) throws URISyntaxException, IOException
	{
		var resource = ToolkitClient.class.getClassLoader().getResource(domain + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath());
		return Files.readAllBytes(Paths.get(resource.toURI()));
	}

	@Override
	protected void init()
	{
		if (handle == 0)
		{
			handle = GLFW.glfwGetCurrentContext();

			if (handle == MemoryUtil.NULL)
				throw new RuntimeException("Failed to create the GLFW window");

			initImgui();
		}

		imGuiGlfw.init(handle, true);
		imGuiGl3.init(glslVersion);
	}

	protected void initImgui()
	{
		ImGui.createContext();
		setupDefaultFonts();
	}

	protected void setupDefaultFonts()
	{
		final ImGuiIO io = imgui.internal.ImGui.getIO();
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

	@Override
	public void close()
	{
		setScreen(parent);
	}

	protected void setScreen(Screen screen)
	{
		nextScreen = Optional.ofNullable(screen);
	}

	/**
	 * Method to dispose all used application resources and destroy its window.
	 */
	protected void dispose()
	{
		handle = 0;

		imGuiGl3.dispose();
		imGuiGlfw.dispose();
		ImGui.destroyContext();
	}

	/**
	 * Method called every frame, before calling {@link #process()} method.
	 */
	protected void preProcess()
	{
	}

	/**
	 * Method called every frame, after calling {@link #process()} method.
	 */
	protected void postProcess()
	{
	}

	/**
	 * Method used to run the next frame.
	 */
	protected void runFrame()
	{
		startFrame();
		preProcess();
		process();
		postProcess();
		endFrame();
	}

	/**
	 * Method to be overridden by user to provide main application logic.
	 */
	public abstract void process();

	/**
	 * Method called at the beginning of the main cycle.
	 * It clears OpenGL buffer and starts an ImGui frame.
	 */
	protected void startFrame()
	{
		imGuiGlfw.newFrame();
		ImGui.newFrame();
	}

	/**
	 * Method called in the end of the main cycle.
	 * It renders ImGui and swaps GLFW buffers to show an updated frame.
	 */
	protected void endFrame()
	{
		ImGui.render();
		imGuiGl3.renderDrawData(ImGui.getDrawData());

		if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable))
		{
			ImGui.updatePlatformWindows();
			ImGui.renderPlatformWindowsDefault();
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		drawBackground(matrices);

		if (handle != 0)
		{
			if (nextScreen.isPresent())
				client.setScreen(nextScreen.get());
			else
				this.runFrame();
		}
	}

	protected void drawBackground(MatrixStack matrices)
	{
		this.fillGradient(matrices, 0, 0, this.width, this.height, 0xFF464A55, 0xFF1A283E);
	}
}
