package com.parzivail.aurek.ui.view;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.parzivail.aurek.ui.ImguiScreen;
import com.parzivail.aurek.ui.Viewport;
import com.parzivail.aurek.util.DialogUtil;
import com.parzivail.aurek.util.FileUtil;
import com.parzivail.aurek.util.LangUtil;
import com.parzivail.pswg.Resources;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import imgui.type.ImString;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarBuilderScreen extends ImguiScreen
{
	public static final String I18N_JAR_BUILDER = Resources.screen("toolkit.jar_builder");

	private final Viewport viewport = new Viewport();

	private final ImString editingAuthor = new ImString();

	private final ImString addonId = new ImString();
	private final ImString addonVersion = new ImString();
	private final ImString addonDescription = new ImString();
	private final ArrayList<String> addonAuthors = new ArrayList<>();

	public JarBuilderScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_JAR_BUILDER));
	}

	@Override
	public void tick()
	{
		viewport.tick();
	}

	@Override
	public void process()
	{
		if (ImGui.beginMainMenuBar())
		{
			if (ImGui.beginMenu(LangUtil.translate(I18N_JAR_BUILDER)))
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

		if (ImGui.begin(LangUtil.translate(I18N_JAR_BUILDER), ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground))
		{
			ImGui.text("Addon Metadata");

			if (ImGui.beginTable("metadataInputTable", 2))
			{
				ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 100);
				ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

				ImGui.tableNextColumn();
				ImGui.text("Mod ID");
				ImGui.tableNextColumn();
				ImGui.inputText("##modid", addonId);

				ImGui.tableNextColumn();
				ImGui.text("Version");
				ImGui.tableNextColumn();
				ImGui.inputText("##version", addonVersion);

				ImGui.tableNextColumn();
				ImGui.text("Description");
				ImGui.tableNextColumn();
				ImGui.inputText("##description", addonDescription);

				ImGui.endTable();
			}

			ImGui.separator();

			if (ImGui.beginTable("authorsTable", 2))
			{
				ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 100);
				ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

				ImGui.tableNextColumn();
				ImGui.text("Authors");
				ImGui.tableNextColumn();

				var buttonSize = ImGui.getFrameHeight();
				if (ImGui.beginListBox("##authorList"))
				{
					var authorToRemove = -1;
					for (int i = 0; i < addonAuthors.size(); i++)
					{
						String author = addonAuthors.get(i);

						if (ImGui.button("-##removeAuthor%s".formatted(i), buttonSize, buttonSize))
							authorToRemove = i;

						ImGui.sameLine();
						ImGui.text(author);
					}
					if (authorToRemove != -1)
						addonAuthors.remove(authorToRemove);

					if (addonAuthors.isEmpty())
						ImGui.textDisabled("Add an author using the text box below.");

					ImGui.endListBox();
				}

				ImGui.tableNextColumn();
				ImGui.text("Add Author");
				ImGui.tableNextColumn();

				ImGui.inputText("##addAuthor", editingAuthor);
				var addAuthorTextboxEdited = ImGui.isItemDeactivatedAfterEdit();
				if (addAuthorTextboxEdited)
					ImGui.setKeyboardFocusHere(-1);

				ImGui.sameLine();

				if (ImGui.button("+##addAuthor", buttonSize, buttonSize) || addAuthorTextboxEdited)
				{
					addonAuthors.add(editingAuthor.get());
					editingAuthor.clear();
				}

				ImGui.endTable();
			}

			ImGui.separator();

			if (ImGui.button("Compile Addon"))
				compileAddon(FabricModJson.universal(addonId.get(), addonVersion.get(), addonDescription.get(), List.of("parzivail")));
		}
		ImGui.end();
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
					ZipOutputStream zip = new ZipOutputStream(fs);
					PrintWriter zipText = new PrintWriter(zip))
			{
				ZipEntry ze = new ZipEntry("fabric.mod.json");
				zip.putNextEntry(ze);

				var gson = new GsonBuilder().create();

				var jw = new JsonWriter(zipText);
				jw.setIndent("\t");
				gson.toJson(fmj, FabricModJson.class, jw);
				jw.flush();

				zip.closeEntry();
			}
		}
		catch (Exception e)
		{
			// TODO: warnings, NOTIFIER, etc
			e.printStackTrace();
		}
	}

	public record FabricModJson(int schemaVersion, String id, String version, String description, List<String> authors, String environment, Map<String, String> depends,
	                            Map<String, String> recommends)
	{
		public static FabricModJson universal(String id, String version, String description, List<String> authors)
		{
			authors = new ArrayList<>(authors);
			authors.add("PSWG Team");

			var depends = new HashMap<String, String>();

			var container = FabricLoader.getInstance().getModContainer(Resources.MODID).orElseThrow(() -> new RuntimeException("Could not get own mod container"));
			if (!FabricLoader.getInstance().isDevelopmentEnvironment() && container.getMetadata().getVersion() instanceof SemanticVersion ownVersion)
				depends.put(Resources.MODID, String.format(">=%s", ownVersion));
			else
				depends.put(Resources.MODID, "*");

			var recommends = new HashMap<String, String>();
			recommends.put("aurek", "*");

			return new FabricModJson(1, id, version, description, authors, "*", depends, recommends);
		}
	}
}
