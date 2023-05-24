package com.parzivail.aurek.ui.view;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.parzivail.aurek.ToolkitClient;
import com.parzivail.aurek.imgui.AurekIconFont;
import com.parzivail.aurek.imgui.ImGuiHelper;
import com.parzivail.aurek.ui.ImguiScreen;
import com.parzivail.aurek.util.DialogUtil;
import com.parzivail.aurek.util.FileUtil;
import com.parzivail.aurek.util.LangUtil;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.features.lightsabers.client.LightsaberItemRenderer;
import com.parzivail.pswg.features.lightsabers.data.LightsaberBladeType;
import com.parzivail.util.math.ColorUtil;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import io.netty.buffer.Unpooled;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AddonBuilder extends ImguiScreen
{
	private enum FeatureType
	{
		Lightsaber("Lightsaber"),
		Blaster("Blaster"),
		Species("Species");

		private final String name;

		FeatureType(String name)
		{
			this.name = name;
		}
	}

	private interface IAddonFeature
	{
		FeatureType getType();

		String getName();

		String getId();

		void serialize(String domain, ZipOutputStream zip, PrintWriter writer);
	}

	private static class LightsaberAddonFeature implements IAddonFeature
	{
		public final ImString name;
		public final ImString id;
		public final ImBoolean unstable;
		public final float[] defaultColor;
		private final ImInt bladeType = new ImInt();

		private final HashMap<String, ImFloat> bladeCoefs = new HashMap<>();

		private String hiltModelFilename = null;
		private P3dModel hiltModel = null;

		public LightsaberAddonFeature(String name, String id, int defaultColor, boolean unstable)
		{
			this.name = new ImString(name, 64);
			this.id = new ImString(id, 64);
			this.defaultColor = new float[] {
					ColorUtil.hsvGetH(defaultColor),
					ColorUtil.hsvGetS(defaultColor),
					ColorUtil.hsvGetV(defaultColor)
			};
			this.unstable = new ImBoolean(unstable);
		}

		public void setHilt(String filename, P3dModel model)
		{
			this.hiltModelFilename = filename;
			this.hiltModel = model;
			bladeCoefs.clear();
		}

		public P3dModel getHiltModel()
		{
			return hiltModel;
		}

		public String getHiltModelFilename()
		{
			return hiltModelFilename;
		}

		public ImFloat getBladeCoef(String socketId)
		{
			if (!bladeCoefs.containsKey(socketId))
				bladeCoefs.put(socketId, new ImFloat(1));

			return bladeCoefs.get(socketId);
		}

		@Override
		public FeatureType getType()
		{
			return FeatureType.Lightsaber;
		}

		@Override
		public String getName()
		{
			return this.name.get();
		}

		@Override
		public String getId()
		{
			return this.id.get();
		}

		@Override
		public void serialize(String domain, ZipOutputStream zip, PrintWriter writer)
		{
			var buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeInt(1); // Schema version
			buf.writeString(domain);
			buf.writeString(getName());
			buf.writeString(getId());
			buf.writeBoolean(unstable.get());
			buf.writeString(LightsaberBladeType.values()[bladeType.get()].getId());
			buf.writeInt(ColorUtil.packHsv(this.defaultColor[0], this.defaultColor[1], this.defaultColor[2]));

			buf.writeInt(bladeCoefs.size());
			for (var entry : bladeCoefs.entrySet())
			{
				buf.writeString(entry.getKey());
				buf.writeFloat(entry.getValue().get());
			}

			FileUtil.zip(zip, buf, "src/main/resources/data/pswg/lightsabers/%s.%s.pswg_lightsaber".formatted(this.id.get(), domain));

			if (hiltModelFilename != null)
				FileUtil.zip(
						zip,
						hiltModelFilename,
						"src/main/resources/assets/%s/models/item/lightsaber/%s.p3d".formatted(domain, this.id.get())
				);
		}
	}

	private static class AddonProject
	{
		private final ImString editingAuthor = new ImString();

		private final ImString addonId;
		private final ImString addonVersion = new ImString("1.0.0", 32);
		private final ImString addonDescription = new ImString("Adds new features to Galaxies: Parzi's Star Wars Mod", 256);
		private final ArrayList<String> addonAuthors = new ArrayList<>();

		private final ArrayList<IAddonFeature> addonFeatures = new ArrayList<>();

		public AddonProject()
		{
			var profile = MinecraftClient.getInstance().getSession().getProfile();
			addonId = new ImString("%s-pswg-addon".formatted(profile.getName()), 32);

			addonAuthors.add(profile.getName());
		}

		public String getId(String path)
		{
			return "%s:%s".formatted(addonId.get(), path);
		}
	}

	public static final String I18N_ADDON_BUILDER = Resources.screen("toolkit.addon_builder");

	private AddonProject currentProject = new AddonProject();

	private IAddonFeature currentEditingFeature = null;

	private static final String[] featureTypes = Arrays.stream(FeatureType.values())
	                                                   .map(featureType -> featureType.name)
	                                                   .toArray(String[]::new);
	private final ImInt selectedFeatureType = new ImInt();

	private static final String[] lightsaberBladeTypes = Arrays.stream(LightsaberBladeType.values())
	                                                           .map(featureType -> LangUtil.translate(featureType.getLangKey()))
	                                                           .toArray(String[]::new);

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
						project.addonFeatures.add(currentEditingFeature = new LightsaberAddonFeature("The Heavysaber", "heavysaber", ColorUtil.packHsv(0.62f, 1, 1), false));
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
						if (ImGui.button("%s##edit%s".formatted(AurekIconFont.greasepencil, i), frameSize, frameSize))
							currentEditingFeature = feature;
						ImGui.sameLine();
						if (ImGui.button("%s##delete%s".formatted(AurekIconFont.x, i), frameSize, frameSize))
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
				if (currentEditingFeature instanceof LightsaberAddonFeature lightsaberAddonFeature)
					renderLightsaberFeatureEditor(lightsaberAddonFeature);
			}

			ImGui.endChild();

			ImGui.endTable();
		}
	}

	private void renderLightsaberFeatureEditor(LightsaberAddonFeature feature)
	{
		if (ImGui.beginTable("lightsaberMetadataInputTable", 2))
		{
			ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 100);
			ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

			ImGui.tableNextColumn();
			ImGui.text("Name");
			ImGui.tableNextColumn();
			ImGui.inputText("##saberName", feature.name);

			ImGui.tableNextColumn();
			ImGui.text("ID");
			ImGui.tableNextColumn();
			ImGui.inputText("##saberId", feature.id);

			ImGui.tableNextColumn();
			ImGui.text("Unstable");
			ImGui.tableNextColumn();
			ImGui.checkbox("##saberUnstable", feature.unstable);

			ImGui.tableNextColumn();
			ImGui.text("Blade Type");
			ImGui.tableNextColumn();
			ImGui.combo("##saberBladeType", feature.bladeType, lightsaberBladeTypes);

			ImGui.tableNextColumn();
			ImGui.text("Hilt Model");
			ImGui.tableNextColumn();

			if (ImGui.button("Choose File"))
			{
				DialogUtil.openFile("Open Model", "P3D Models (*.p3d)", false, "*.p3d")
				          .ifPresent(paths -> {
					          var loadedModel = loadP3dModel(paths[0]);
					          if (loadedModel != null)
						          feature.setHilt(paths[0], loadedModel);
				          });
			}
			ImGui.sameLine();
			if (feature.getHiltModelFilename() != null)
				ImGui.textWrapped(feature.getHiltModelFilename());
			else
				ImGui.textDisabled("No file selected");

			ImGui.tableNextColumn();
			ImGui.text("Blades");
			ImGui.tableNextColumn();

			if (feature.getHiltModel() != null)
			{
				if (ImGui.beginTable("bladeTable", 2))
				{
					ImGui.tableSetupColumn("Blade Socket", ImGuiTableColumnFlags.WidthStretch);
					ImGui.tableSetupColumn("Blade Length", ImGuiTableColumnFlags.WidthFixed, 200);

					ImGui.tableHeadersRow();

					var foundBlades = 0;
					for (var socket : feature.getHiltModel().transformables().values())
					{
						if (!LightsaberItemRenderer.isBladeSocket(socket))
							continue;

						foundBlades++;
						ImGui.tableNextColumn();
						ImGui.text(socket.name);
						ImGui.tableNextColumn();
						ImGui.setNextItemWidth(-1);
						ImGui.inputFloat("##bladeLength_%s".formatted(socket.name), feature.getBladeCoef(socket.name));
					}

					if (foundBlades == 0)
					{
						ImGui.tableNextColumn();
						ImGui.textColored(0xFF0000FF, "No blade sockets found! Is this model formatted correctly?");
					}

					ImGui.endTable();
				}
			}
			else
				ImGui.textDisabled("No model loaded");

			ImGui.tableNextColumn();
			ImGui.text("Blade Color");
			ImGui.tableNextColumn();

			var availWidth = ImGui.getContentRegionAvailX();
			ImGui.setNextItemWidth(Math.min(availWidth, 300));
			ImGui.colorPicker3("##bladeColor", feature.defaultColor, ImGuiColorEditFlags.InputHSV);

			ImGui.endTable();
		}
	}

	private static P3dModel loadP3dModel(String path)
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
						if (ImGui.button("-##removeAuthor%s".formatted(i), buttonSize, buttonSize))
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

				for (var feature : currentProject.addonFeatures)
					feature.serialize(currentProject.addonId.get(), zip, zipText);
			}
		}
		catch (Exception e)
		{
			ToolkitClient.NOTIFIER.error("Compilation error", "Error compiling addon", e);
		}
	}

	public record AurekGeneratorMetadata(String generatorVersion)
	{
	}

	public record FabricModJson(int schemaVersion, String id, String version, String description, List<String> authors, String environment, Map<String, String> depends,
	                            Map<String, String> recommends, Map<String, Object> custom)
	{
		public static FabricModJson universal(String id, String version, String description, List<String> authors)
		{
			authors = new ArrayList<>(authors);
			authors.add("PSWG Team");

			var depends = new HashMap<String, String>();

			var pswgContainer = FabricLoader.getInstance().getModContainer(Resources.MODID).orElseThrow(() -> new RuntimeException("Could not get PSWG mod container"));
			if (!FabricLoader.getInstance().isDevelopmentEnvironment() && pswgContainer.getMetadata().getVersion() instanceof SemanticVersion pswgVersion)
				depends.put(Resources.MODID, String.format(">=%s", pswgVersion));
			else
				depends.put(Resources.MODID, "*");

			var recommends = new HashMap<String, String>();
			recommends.put("aurek", "*");

			var custom = new HashMap<String, Object>();

			var ownContainer = FabricLoader.getInstance().getModContainer(ToolkitClient.MODID).orElseThrow(() -> new RuntimeException("Could not get own mod container"));
			var ownVersion = ownContainer.getMetadata().getVersion();
			custom.put("aurek", new AurekGeneratorMetadata(ownVersion instanceof SemanticVersion v ? v.toString() : "unknown"));

			return new FabricModJson(1, id, version, description, authors, "*", depends, recommends, custom);
		}
	}
}
