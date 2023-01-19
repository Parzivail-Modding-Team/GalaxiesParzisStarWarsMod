package com.parzivail.imgui;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

public abstract class ImguiScreen extends Screen
{
	private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
	private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

	private String glslVersion = null;

	/**
	 * Pointer to the native GLFW window.
	 */
	protected long handle;

	protected ImguiScreen(Text title)
	{
		super(title);
	}

	@Override
	protected void init()
	{
		if (handle == 0)
		{
			initWindow();
			initImGui();
			imGuiGlfw.init(handle, true);
			imGuiGl3.init(glslVersion);
		}
	}

	/**
	 * Method to dispose all used application resources and destroy its window.
	 */
	protected void dispose()
	{
		imGuiGl3.dispose();
		imGuiGlfw.dispose();
		disposeImGui();
	}

	/**
	 * Method to create and initialize GLFW window.
	 */
	protected void initWindow()
	{
		decideGlGlslVersions();

		handle = GLFW.glfwGetCurrentContext();

		if (handle == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");
	}

	private void decideGlGlslVersions()
	{
		final boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
		if (isMac)
			glslVersion = "#version 150";
		else
			glslVersion = "#version 130";
	}

	/**
	 * Method to initialize Dear ImGui context. Could be overridden to do custom Dear ImGui setup before application start.
	 */
	protected void initImGui()
	{
		ImGui.createContext();
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
	 * Main application loop.
	 */
	public void run()
	{
		while (!GLFW.glfwWindowShouldClose(handle))
			runFrame();
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

	/**
	 * Method to destroy Dear ImGui context.
	 */
	protected void disposeImGui()
	{
		ImGui.destroyContext();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.fillGradient(matrices, 0, 0, this.width, this.height, 0xFF000000, 0xFF000000);

		if (handle != 0)
			this.runFrame();
	}

	@Override
	public void close()
	{
		super.close();

		dispose();
		handle = 0;
	}
}
