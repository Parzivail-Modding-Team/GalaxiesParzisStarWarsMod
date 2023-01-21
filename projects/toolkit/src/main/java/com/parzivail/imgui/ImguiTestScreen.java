package com.parzivail.imgui;

import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.ToolkitClient;
import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImguiTestScreen extends ImguiScreen
{
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit_home");

	private Screen parent;

	private ImFont latinFont;
	private ImFont aurebeshFont;
	private ImFont iconFont;

	public ImguiTestScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_HOME));
	}

	public static byte[] getBytes(String domain, Identifier resourceLocation) throws URISyntaxException, IOException
	{
		return Files.readAllBytes(Paths.get(
				ToolkitClient.class.getClassLoader().getResource(domain + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath()).toURI()
		));
	}

	@Override
	protected void initImgui()
	{
		super.initImgui();

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
			aurebeshFont = io.getFonts().addFontFromMemoryTTF(getBytes("assets", ToolkitClient.id("font/aurebesh.ttf")), 18, fontConfig);
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
	public void process()
	{
		var flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.MenuBar;
		flags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground;
		flags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.NoDecoration;

		var v = ImGui.getMainViewport();

		ImGui.setNextWindowPos(0, 0);
		ImGui.setNextWindowSize(v.getSizeX(), v.getSizeY());

		ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

		if (ImGui.begin("Aurek Toolkit", new ImBoolean(true), flags))
		{
			ImGui.popStyleVar();

			var dockspaceId = ImGui.getID("MyDockspace");
			ImGui.dockSpace(dockspaceId, 0, 0, ImGuiDockNodeFlags.PassthruCentralNode);

			if (ImGui.beginMenuBar())
			{
				if (ImGui.beginMenu("Aurek"))
				{
					if (ImGui.menuItem("Exit"))
						close();

					ImGui.endMenu();
				}

				ImGui.endMenuBar();
			}

			if (ImGui.begin("Small Window", ImGuiWindowFlags.AlwaysAutoResize))
			{
				ImGui.text("Hello, World!");
				ImGui.pushFont(iconFont);
				ImGui.text(AurekIconFont.pswg_micro_logo);
				ImGui.text(AurekIconFont.discord);
				ImGui.text(AurekIconFont.fabric);
				ImGui.text(AurekIconFont.curseforge);
				ImGui.text(AurekIconFont.modrinth);
				ImGui.popFont();
				ImGui.pushFont(aurebeshFont);
				ImGui.text("Hello, World!");
				ImGui.popFont();
			}
			ImGui.end();

			ImGui.showDemoWindow();
		}
		else
			ImGui.popStyleVar();
		ImGui.end();
	}
}
