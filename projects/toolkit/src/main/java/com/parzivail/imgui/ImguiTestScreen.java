package com.parzivail.imgui;

import com.parzivail.pswg.Resources;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ImguiTestScreen extends ImguiScreen
{
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit_home");

	private Screen parent;

	private final ImString str = new ImString(5);
	private final float[] flt = new float[1];
	private int count;

	public ImguiTestScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_HOME));
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

		ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0);
		ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

		if (ImGui.begin("Aurek Toolkit", new ImBoolean(true), flags))
		{
			ImGui.popStyleVar(2);

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
			}
			ImGui.end();
		}
		else
			ImGui.popStyleVar(2);
		ImGui.end();
	}
}
