package com.parzivail.aurek.ui.view.addonbuilder;

import com.google.gson.GsonBuilder;
import com.parzivail.aurek.ToolkitClient;
import com.parzivail.aurek.imgui.AurekIconFont;
import com.parzivail.aurek.imgui.ImGuiHelper;
import com.parzivail.aurek.ui.ImguiScreen;
import com.parzivail.aurek.util.DialogUtil;
import com.parzivail.aurek.util.FileUtil;
import com.parzivail.aurek.util.LangUtil;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.Resources;
import com.parzivail.util.math.ColorUtil;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import imgui.type.ImInt;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.ZipOutputStream;

public class AddonBuilder extends ImguiScreen
{
	public static final String I18N_ADDON_BUILDER = Resources.screen("toolkit.addon_builder");

	private AddonProject currentProject = new AddonProject();

	private IAddonFeature currentEditingFeature = null;

	private static final String[] featureTypes = Arrays.stream(FeatureType.values())
	                                                   .map(FeatureType::getName)
	                                                   .toArray(String[]::new);
	private final ImInt selectedFeatureType = new ImInt();

	public AddonBuilder(Screen parent)
	{
		super(parent, Text.translatable(I18N_ADDON_BUILDER));
	}

	@Override
	public void process()
	{
		if (ImGui.beginMainMenuBar())
		{
			if (ImGui.beginMenu(LangUtil.translate(I18N_ADDON_BUILDER)))
			{
				//				if (ImGui.menuItem("Open...", "Ctrl+O"))
				//					action = P3diCompilerScreen.UiAction.OpenModel;
				//
				//				if (ImGui.menuItem("Export P3D...", "Ctrl+E"))
				//					action = P3diCompilerScreen.UiAction.ExportModel;
				//
				//				if (ImGui.menuItem("Export P3D Rig...", "Ctrl+R"))
				//					action = P3diCompilerScreen.UiAction.ExportRig;
				//
				//				ImGui.separator();

				if (ImGui.menuItem("Exit"))
					close();

				ImGui.endMenu();
			}

			ImGui.endMainMenuBar();
		}

		var v = ImGui.getMainViewport();
		ImGui.setNextWindowPos(v.getWorkPosX(), v.getWorkPosY());
		ImGui.setNextWindowSize(v.getWorkSizeX(), v.getWorkSizeY());

		if (ImGui.begin(LangUtil.translate(I18N_ADDON_BUILDER), ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground))
		{
			if (ImGui.beginTabBar("featureTabs"))
			{
				if (ImGui.beginTabItem("Metadata"))
				{
					renderMetadataForm(currentProject);
					ImGui.endTabItem();
				}

				if (ImGui.beginTabItem("Features"))
				{
					renderFeaturesForm(currentProject);
					ImGui.endTabItem();
				}

				ImGui.endTabBar();
			}
		}
		ImGui.end();
	}

	private void renderFeaturesForm(AddonProject project)
	{
		var innerSize = ImGui.getContentRegionAvail();
		if (ImGui.beginTable("featureTable", 2, ImGuiTableFlags.Resizable | ImGuiTableFlags.NoHostExtendY, innerSize.x, innerSize.y))
		{
			ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 400);
			ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

			ImGui.tableNextColumn();

			var frameSize = ImGui.getFrameHeight();

			ImGui.combo("##featureTypes", selectedFeatureType, featureTypes);
			ImGui.sameLine();
			if (ImGui.button("New", -1, frameSize))
			{
				var featureType = FeatureType.values()[selectedFeatureType.get()];
				switch (featureType)
				{
					case Lightsaber ->
					{
						project.addonFeatures.add(currentEditingFeature = new LightsaberAddonFeature("Jedi Bob", "bricksaber", ColorUtil.packHsv(0.37f, 1, 1), false));
					}
				}
			}

			if (ImGui.beginListBox("##featureList", -1, -1))
			{
				if (project.addonFeatures.isEmpty())
					ImGui.textDisabled("No features yet, add one above.");
				else if (ImGui.beginTable("featuresList", 4))
				{
					ImGui.tableSetupColumn("icon[]", ImGuiTableColumnFlags.WidthFixed, frameSize);
					ImGui.tableSetupColumn("featureName[]", ImGuiTableColumnFlags.WidthStretch);
					ImGui.tableSetupColumn("featureId[]", ImGuiTableColumnFlags.WidthStretch);
					ImGui.tableSetupColumn("featureActions[]", ImGuiTableColumnFlags.WidthFixed, -1);

					ImGuiHelper.loopWithDelete(project.addonFeatures, (delete, i, feature) ->
					{
						ImGui.tableNextColumn();
						ImGui.text(AurekIconFont.file_3d);
						ImGui.tableNextColumn();
						ImGui.text(feature.getName());
						ImGui.tableNextColumn();
						ImGui.textDisabled(feature.getId().toString());
						ImGui.tableNextColumn();
						if (ImGui.button(AurekIconFont.greasepencil + "##edit" + i, frameSize, frameSize))
							currentEditingFeature = feature;
						ImGui.sameLine();
						if (ImGui.button(AurekIconFont.panel_close + "##delete" + i, frameSize, frameSize))
						{
							// TODO: ask first
							delete.run();
						}
					});

					ImGui.endTable();
				}
				ImGui.endListBox();
			}

			ImGui.tableNextColumn();

			if (currentEditingFeature != null)
			{
				ImGui.text(currentEditingFeature.getName());
				ImGui.sameLine();
				ImGui.textDisabled(currentEditingFeature.getId());
				ImGui.separator();
			}

			if (ImGui.beginChild("featureEditor"))
			{
				if (currentEditingFeature != null)
					currentEditingFeature.getType().getFormRenderer().render(this, currentEditingFeature);
			}
			ImGui.endChild();

			ImGui.endTable();
		}
	}

	public static P3dModel loadP3dModel(String path)
	{
		try (var in = new FileInputStream(path))
		{
			return P3dModel.read(in, true);
		}
		catch (FileNotFoundException e)
		{
			ToolkitClient.NOTIFIER.error("Error loading model", "Target file is inaccessible", e);
			return null;
		}
		catch (IOException e)
		{
			ToolkitClient.NOTIFIER.error("Error loading model", "Failed to read model", e);
			return null;
		}
	}

	private void renderMetadataForm(AddonProject project)
	{
		if (ImGui.beginChild("metadataEditor"))
		{
			if (ImGui.beginTable("metadataInputTable", 2))
			{
				ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 100);
				ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

				ImGui.tableNextColumn();
				ImGui.text("Mod ID");
				ImGui.tableNextColumn();
				ImGui.inputText("##modid", project.addonId);

				ImGui.tableNextColumn();
				ImGui.text("Version");
				ImGui.tableNextColumn();
				ImGui.inputText("##version", project.addonVersion);

				ImGui.tableNextColumn();
				ImGui.text("Description");
				ImGui.tableNextColumn();
				ImGui.inputText("##description", project.addonDescription);

				ImGui.tableNextColumn();
				ImGui.text("Authors");
				ImGui.tableNextColumn();

				var buttonSize = ImGui.getFrameHeight();
				if (ImGui.beginListBox("##authorList"))
				{
					ImGuiHelper.loopWithDelete(project.addonAuthors, (delete, i, author) ->
					{
						if (ImGui.button("-##removeAuthor" + i, buttonSize, buttonSize))
							delete.run();

						ImGui.sameLine();
						ImGui.text(author);
					});

					if (project.addonAuthors.isEmpty())
						ImGui.textDisabled("Add an author using the text box below.");

					ImGui.endListBox();
				}

				ImGui.tableNextColumn();
				ImGui.text("Add Author");
				ImGui.tableNextColumn();

				ImGui.inputText("##addAuthor", project.editingAuthor);
				var addAuthorTextboxEdited = ImGui.isItemDeactivatedAfterEdit();
				if (addAuthorTextboxEdited)
					ImGui.setKeyboardFocusHere(-1);

				ImGui.sameLine();

				if (ImGui.button("+##addAuthor", buttonSize, buttonSize) || addAuthorTextboxEdited)
				{
					project.addonAuthors.add(project.editingAuthor.get());
					project.editingAuthor.clear();
				}

				ImGui.tableNextColumn();
				ImGui.tableNextColumn();

				if (ImGui.button("Compile Addon"))
					compileAddon(FabricModJson.universal(project.addonId.get(), project.addonVersion.get(), project.addonDescription.get(), project.addonAuthors));

				ImGui.endTable();
			}
		}
		ImGui.endChild();
	}

	private void compileAddon(FabricModJson fmj)
	{
		DialogUtil.saveFile("Save Addon Jar", "*.jar")
		          .ifPresent(path -> compileAddon(path, fmj));
	}

	private void compileAddon(String path, FabricModJson fmj)
	{
		path = FileUtil.ensureExtension(path, ".jar");

		try
		{
			var file = new File(path);
			file.delete();

			try (
					FileOutputStream fs = new FileOutputStream(path);
					ZipOutputStream zip = new ZipOutputStream(fs))
			{
				var domain = currentProject.addonId.get();

				var gson = new GsonBuilder().create();

				FileUtil.zip(zip, gson, fmj, "fabric.mod.json");

				var lang = new HashMap<String, String>();

				for (var feature : currentProject.addonFeatures)
				{
					feature.serialize(domain, zip);
					feature.appendLanguageKeys(domain, lang);
				}

				// TODO: allow selection of default localization ID
				FileUtil.zip(zip, gson, lang, "assets/" + domain + "/lang/en_us.json");
			}
		}
		catch (Exception e)
		{
			ToolkitClient.NOTIFIER.error("Compilation error", "Error compiling addon", e);
		}
	}
}
