package com.parzivail.imgui;

import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.ToolkitClient;
import com.parzivail.pswgtk.util.LangUtil;
import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.*;
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
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit.home");

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
		var resource = ToolkitClient.class.getClassLoader().getResource(domain + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath());
		return Files.readAllBytes(Paths.get(resource.toURI()));
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

			fontConfig.setMergeMode(true);
			iconFont = io.getFonts().addFontFromMemoryTTF(getBytes("assets", ToolkitClient.id("font/icons.ttf")), 18, fontConfig, AurekIconFont.ICON_RANGE);

			fontConfig.setMergeMode(false);
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
				// TODO: add aurek logo to icon font
				// TODO: scale down discord logo
				if (ImGui.beginMenu(AurekIconFont.pswg_micro_logo + " Aurek"))
				{
					if (ImGui.menuItem("Exit"))
						close();

					ImGui.endMenu();
				}

				ImGui.endMenuBar();
			}

			if (ImGui.begin("Tools"))
			{
				var tableFlags = ImGuiTableFlags.Resizable | ImGuiTableFlags.Reorderable | ImGuiTableFlags.Hideable | ImGuiTableFlags.ScrollY
				                 | ImGuiTableFlags.RowBg | ImGuiTableFlags.BordersOuter | ImGuiTableFlags.BordersV | ImGuiTableFlags.NoBordersInBody;
				if (ImGui.beginTable("available_tools", 3, tableFlags))
				{
					ImGui.tableSetupColumn("Tool", ImGuiTableColumnFlags.WidthFixed);
					ImGui.tableSetupColumn("Actions", ImGuiTableColumnFlags.WidthFixed);
					ImGui.tableSetupColumn("Description", ImGuiTableColumnFlags.WidthStretch);
					ImGui.tableSetupScrollFreeze(0, 1);
					ImGui.tableHeadersRow();

					for (var category : ToolkitClient.TOOLS.entrySet())
					{
						var name = category.getKey();
						var tools = category.getValue();

						ImGui.pushID(name);

						ImGui.tableNextRow();
						ImGui.tableNextColumn();

						var expanded = ImGui.treeNodeEx(LangUtil.translate(name), ImGuiTreeNodeFlags.SpanFullWidth);
						ImGui.tableNextColumn();
						ImGui.tableNextColumn();

						if (expanded)
						{
							for (var tool : tools)
							{
								ImGui.pushID(name);
								ImGui.tableNextRow();

								ImGui.tableNextColumn();
								ImGui.treeNodeEx(LangUtil.translate(tool.getTitle()), ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.Bullet | ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.SpanFullWidth);

								ImGui.tableNextColumn();
								if (ImGui.button("Run"))
								{
									// Run
								}

								ImGui.tableNextColumn();
								ImGui.textWrapped(LangUtil.translate(tool.getDescription()));

								ImGui.popID();
							}

							ImGui.treePop();
						}

						ImGui.popID();
					}

					ImGui.endTable();
				}
			}
			ImGui.end();

			ImGui.showDemoWindow();
		}
		else
			ImGui.popStyleVar();
		ImGui.end();
	}
}
