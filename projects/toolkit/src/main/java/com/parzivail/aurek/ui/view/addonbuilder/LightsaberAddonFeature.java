package com.parzivail.aurek.ui.view.addonbuilder;

import com.parzivail.aurek.imgui.ImGuiHelper;
import com.parzivail.aurek.util.FileUtil;
import com.parzivail.aurek.util.LangUtil;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.features.lightsabers.LightsaberItem;
import com.parzivail.pswg.features.lightsabers.client.LightsaberItemRenderer;
import com.parzivail.pswg.features.lightsabers.data.LightsaberBladeType;
import com.parzivail.util.data.PacketByteBufHelper;
import com.parzivail.util.math.ColorUtil;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.internal.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

public class LightsaberAddonFeature implements IAddonFeature
{
	public enum NameType
	{
		Owner("Owner"),
		Title("Title");

		public final String title;

		NameType(String title)
		{
			this.title = title;
		}
	}

	public static final int SCHEMA_VERSION = 1;
	public static final String[] lightsaberNameTypes = Arrays.stream(NameType.values())
	                                                         .map(nameType -> nameType.title)
	                                                         .toArray(String[]::new);
	public static final String[] lightsaberBladeTypes = Arrays.stream(LightsaberBladeType.values())
	                                                          .map(featureType -> LangUtil.translate(featureType.getLangKey()))
	                                                          .toArray(String[]::new);

	public final ImString id;
	public final ImString name;
	public final ImInt nameType = new ImInt();
	public final ImBoolean unstable;
	public final float[] defaultColor;
	public final ImInt bladeType = new ImInt();

	private final HashMap<String, ImFloat> bladeCoefs = new HashMap<>();

	private String hiltTextureFilename = null;

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

	public NameType getNameType()
	{
		return NameType.values()[nameType.get()];
	}

	@Override
	public void serialize(String namespace, ZipOutputStream zip)
	{
		var buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(SCHEMA_VERSION);
		buf.writeString(namespace);
		buf.writeString(getId());

		var hasOwner = getNameType() == NameType.Owner;
		var ownerName = hasOwner ? getName() : null;
		PacketByteBufHelper.writeNullable(buf, ownerName, PacketByteBuf::writeString);

		buf.writeBoolean(unstable.get());
		buf.writeString(LightsaberBladeType.values()[bladeType.get()].getId());
		buf.writeInt(ColorUtil.packHsv(this.defaultColor[0], this.defaultColor[1], this.defaultColor[2]));

		buf.writeInt(bladeCoefs.size());
		for (var entry : bladeCoefs.entrySet())
		{
			buf.writeString(entry.getKey());
			buf.writeFloat(entry.getValue().get());
		}

		FileUtil.zip(zip, buf, "data/pswg/lightsabers/%s.%s.pswg_lightsaber".formatted(this.id.get(), namespace));

		if (hiltModelFilename != null)
			FileUtil.zip(
					zip,
					hiltModelFilename,
					"assets/%s/models/item/lightsaber/%s.p3d".formatted(namespace, this.id.get())
			);

		if (hiltTextureFilename != null)
			FileUtil.zip(
					zip,
					hiltTextureFilename,
					"assets/%s/textures/item/model/lightsaber/%s.png".formatted(namespace, this.id.get())
			);
	}

	@Override
	public void appendLanguageKeys(String namespace, Map<String, String> languageKeys)
	{
		var hasTitle = getNameType() == NameType.Title;
		if (hasTitle)
			languageKeys.put(LightsaberItem.getTranslationKey(Identifier.of(namespace, getId())), getName());
	}

	public static void renderForm(AddonBuilder context, IAddonFeature feature)
	{
		if (!(feature instanceof LightsaberAddonFeature lightsaber))
			return;

		if (ImGui.beginTable("lightsaberMetadataInputTable", 2))
		{
			ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 100);
			ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

			ImGui.tableNextColumn();
			ImGui.text("ID");
			ImGui.tableNextColumn();
			ImGui.inputText("##saberId", lightsaber.id);

			ImGui.tableNextColumn();
			ImGui.text("Name Type");
			ImGui.tableNextColumn();
			ImGui.combo("##saberNameType", lightsaber.nameType, LightsaberAddonFeature.lightsaberNameTypes);

			ImGui.tableNextColumn();
			switch (lightsaber.getNameType())
			{
				case Owner -> ImGui.text("Owner Name");
				case Title -> ImGui.text("Title");
				default -> ImGui.text("Name");
			}
			ImGui.tableNextColumn();
			ImGui.inputText("##saberName", lightsaber.name);

			ImGui.tableNextColumn();
			ImGui.text("Unstable");
			ImGui.tableNextColumn();
			ImGui.checkbox("##saberUnstable", lightsaber.unstable);

			ImGui.tableNextColumn();
			ImGui.text("Blade Type");
			ImGui.tableNextColumn();
			ImGui.combo("##saberBladeType", lightsaber.bladeType, LightsaberAddonFeature.lightsaberBladeTypes);

			ImGui.tableNextColumn();
			ImGui.text("Hilt Texture");
			ImGui.tableNextColumn();

			ImGuiHelper.filePicker(
					"texturePicker",
					() -> lightsaber.hiltTextureFilename,
					(path) -> lightsaber.hiltTextureFilename = path,
					"Open Texture",
					"PNG Images (*.png)",
					"*.png"
			);

			ImGui.tableNextColumn();
			ImGui.text("Hilt Model");
			ImGui.tableNextColumn();

			ImGuiHelper.filePicker(
					"modelPicker",
					lightsaber::getHiltModelFilename,
					(path) -> {
						var loadedModel = AddonBuilder.loadP3dModel(path);
						if (loadedModel != null)
							lightsaber.setHilt(path, loadedModel);
					},
					"Open Model",
					"P3D Models (*.p3d)",
					"*.p3d"
			);

			ImGui.tableNextColumn();
			ImGui.text("Blades");
			ImGui.tableNextColumn();

			if (lightsaber.getHiltModel() != null)
			{
				if (ImGui.beginTable("bladeTable", 2))
				{
					ImGui.tableSetupColumn("Blade Socket", ImGuiTableColumnFlags.WidthStretch);
					ImGui.tableSetupColumn("Blade Length", ImGuiTableColumnFlags.WidthFixed, 200);

					ImGui.tableHeadersRow();

					var foundBlades = 0;
					for (var socket : lightsaber.getHiltModel().transformables().values())
					{
						if (!LightsaberItemRenderer.isBladeSocket(socket))
							continue;

						foundBlades++;
						ImGui.tableNextColumn();
						ImGui.text(socket.name);
						ImGui.tableNextColumn();
						ImGui.setNextItemWidth(-1);
						ImGui.inputFloat("##bladeLength_%s".formatted(socket.name), lightsaber.getBladeCoef(socket.name));
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
			ImGui.colorPicker3("##bladeColor", lightsaber.defaultColor, ImGuiColorEditFlags.InputHSV);

			ImGui.endTable();
		}
	}
}
