package com.parzivail.aurek.ui.view;

import com.parzivail.aurek.ToolkitClient;
import com.parzivail.aurek.imgui.AurekIconFont;
import com.parzivail.aurek.ui.ImguiScreen;
import com.parzivail.aurek.util.LangUtil;
import com.parzivail.pswg.Resources;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import imgui.type.ImBoolean;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ToolkitHomeScreen extends ImguiScreen
{
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit.home");

	public ToolkitHomeScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_HOME));
	}

	@Override
	public void process(float tickDelta)
	{
		var flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.MenuBar;
		flags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground;
		flags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.NoDecoration;

		var v = ImGui.getMainViewport();

		ImGui.setNextWindowPos(0, 0);
		ImGui.setNextWindowSize(v.getSizeX(), v.getSizeY());

		if (ImGui.begin("Aurek Toolkit", new ImBoolean(true), flags))
		{
			if (ImGui.beginMenuBar())
			{
				if (ImGui.beginMenu(AurekIconFont.aurek + " Aurek"))
				{
					if (ImGui.menuItem("Exit"))
						close();

					ImGui.endMenu();
				}

				ImGui.endMenuBar();
			}

			var tableFlags = ImGuiTableFlags.Resizable | ImGuiTableFlags.Reorderable | ImGuiTableFlags.Hideable | ImGuiTableFlags.ScrollY
			                 | ImGuiTableFlags.RowBg | ImGuiTableFlags.BordersOuter | ImGuiTableFlags.BordersV | ImGuiTableFlags.NoBordersInBody;
			if (ImGui.beginTable("available_tools", 3, tableFlags))
			{
				ImGui.tableSetupColumn("Tool", ImGuiTableColumnFlags.WidthFixed, 200);
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
							ImGui.pushID(tool.getTitle());
							ImGui.tableNextRow();

							ImGui.tableNextColumn();
							ImGui.treeNodeEx(LangUtil.translate(tool.getTitle()), ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.Bullet | ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.SpanFullWidth);

							ImGui.tableNextColumn();
							if (ImGui.button("Run"))
								client.setScreen(tool.getScreen());

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
	}
}
