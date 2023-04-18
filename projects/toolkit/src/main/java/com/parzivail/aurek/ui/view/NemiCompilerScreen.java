package com.parzivail.aurek.ui.view;

import com.google.gson.Gson;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.aurek.ToolkitClient;
import com.parzivail.aurek.imgui.ImGuiHelper;
import com.parzivail.aurek.model.nemi.NemiModel;
import com.parzivail.aurek.ui.ImguiScreen;
import com.parzivail.aurek.ui.Viewport;
import com.parzivail.aurek.ui.model.NemiModelProject;
import com.parzivail.aurek.ui.model.TabModelController;
import com.parzivail.aurek.util.DialogUtil;
import com.parzivail.aurek.util.FileUtil;
import com.parzivail.aurek.util.LangUtil;
import com.parzivail.util.math.MathUtil;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.text.Text;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class NemiCompilerScreen extends ImguiScreen
{
	private enum UiAction
	{
		None(0),
		OpenModel(GLFW.GLFW_KEY_O),
		ExportNem(GLFW.GLFW_KEY_E),
		ExportJava(GLFW.GLFW_KEY_J);

		private final int keybind;

		UiAction(int keybind)
		{
			this.keybind = keybind;
		}
	}

	private static final Gson gson = new Gson();
	private static final String I18N_TOOLKIT_NEMI_COMPILER = ToolkitClient.toolLang("nemi_compiler");

	private final TabModelController<NemiModelProject> tabController;
	private final Viewport viewport = new Viewport();

	public NemiCompilerScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_NEMI_COMPILER));

		tabController = new TabModelController<>();
	}

	@Override
	public void tick()
	{
		viewport.tick();
	}

	private void openModel()
	{
		DialogUtil.openFile("Open Model", "NEM Models (*.nemi, *.nem)", false, "*.nemi", "*.nem")
		          .ifPresent(paths -> openModel(paths[0]));
	}

	private void openModel(String path)
	{
		if (path.endsWith(".nem"))
			openNem(path);
		else if (path.endsWith(".nemi"))
			openNemi(path);
	}

	private void exportNem(NemiModelProject project)
	{
		DialogUtil.saveFile("Save Model", "*.nem")
		          .ifPresent(path -> saveNem(path, project));
	}

	private void exportJava(NemiModelProject project)
	{
		DialogUtil.saveFile("Save Model", "*.java")
		          .ifPresent(path -> saveJava(path, project));
	}

	private void saveNem(String path, NemiModelProject project)
	{
		path = FileUtil.ensureExtension(path, ".nem");

		var nem = project.getCompiledModel();

		try
		{
			var file = new File(path);
			file.delete();
			NbtIo.write(nem, file);
			ToolkitClient.NOTIFIER.success("NEM Compiler", String.format("Exported NEM model as \"%s\"", path));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ToolkitClient.NOTIFIER.error("NEM Compiler", "Failed to export model", e);
		}
	}

	private void saveJava(String path, NemiModelProject project)
	{
		path = FileUtil.ensureExtension(path, ".bb.java");

		var nem = project.getCompiledModel();

		try
		{
			var file = new File(path);
			file.delete();

			try (var pw = new PrintWriter(file))
			{
				pw.println(String.format("public class %s", project.getTitle()));
				pw.println(String.format("public %s", project.getTitle()));

				var tex = nem.getCompound("tex");
				pw.println(String.format("texWidth = %s;", tex.getInt("w")));
				pw.println(String.format("texHeight = %s;", tex.getInt("h")));

				var parts = nem.getCompound("parts");
				for (var key : parts.getKeys())
					printParts(pw, null, key, parts.getCompound(key));
			}

			ToolkitClient.NOTIFIER.success("NEM Compiler", String.format("Exported Blockbench Java model as \"%s\"", path));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ToolkitClient.NOTIFIER.error("NEM Compiler", "Failed to export model", e);
		}
	}

	private void printParts(PrintWriter pw, String parent, String name, NbtCompound part)
	{
		pw.println(String.format("%s = new ModelRenderer(this);", name));

		var pos = part.getCompound("pos");
		pw.println(String.format("%s.setPos(%sF, %sF, %sF);", name, pos.getFloat("x"), pos.getFloat("y"), pos.getFloat("z")));

		if (parent != null)
			pw.println(String.format("%s.addChild(%s);", parent, name));

		var rot = part.getCompound("rot");
		pw.println(String.format(
				"setRotationAngle(%s, %sF, %sF, %sF);",
				name,
				rot.getFloat("pitch"),
				rot.getFloat("yaw"),
				rot.getFloat("roll")
		));

		var groupTex = part.getCompound("tex");

		for (var boxEl : part.getList("cuboids", NbtElement.COMPOUND_TYPE))
		{
			var box = (NbtCompound)boxEl;

			var tex = box.getCompound("tex");
			var boxPos = box.getCompound("pos");
			var size = box.getCompound("size");
			var expand = box.getCompound("expand");

			pw.println(String.format(
					"%s.texOffs(%s, %s).addBox(%sF, %sF, %sF, %sF, %sF, %sF, %sF, %s);",
					name,
					groupTex.getInt("u") + tex.getInt("u"), groupTex.getInt("v") + tex.getInt("v"),
					boxPos.getFloat("x"), boxPos.getFloat("y"), boxPos.getFloat("z"),
					size.getInt("x"), size.getInt("y"), size.getInt("z"),
					expand.getFloat("x"),
					groupTex.getBoolean("mirrored") ^ tex.getBoolean("mirrored")
			));
		}

		if (!part.contains("children"))
			return;

		var children = part.getCompound("children");
		for (var key : children.getKeys())
			printParts(pw, name, key, children.getCompound(key));
	}

	private void openNemi(String path)
	{
		try (Reader reader = Files.newBufferedReader(Path.of(path)))
		{
			tabController.add(new NemiModelProject(path, gson.fromJson(reader, NemiModel.class)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void openNem(String path)
	{
		try
		{
			var nbt = NbtIo.read(new File(path));
			if (nbt == null)
				return;
			tabController.add(new NemiModelProject(path, nbt));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void process()
	{
		var action = UiAction.None;

		if (ImGui.beginMainMenuBar())
		{
			if (ImGui.beginMenu(LangUtil.translate(I18N_TOOLKIT_NEMI_COMPILER)))
			{
				if (ImGui.menuItem("Open...", "Ctrl+O"))
					action = UiAction.OpenModel;

				if (ImGui.menuItem("Export NEM...", "Ctrl+E"))
					action = UiAction.ExportNem;

				if (ImGui.menuItem("Export Blockbench...", "Ctrl+J"))
					action = UiAction.ExportJava;

				ImGui.separator();

				if (ImGui.menuItem("Exit"))
					close();

				ImGui.endMenu();
			}

			ImGui.endMainMenuBar();
		}

		for (var shortcut : UiAction.values())
			if (ImGuiHelper.isCtrlDown() && ImGui.isKeyDown(shortcut.keybind))
				action = shortcut;

		var v = ImGui.getMainViewport();
		ImGui.setNextWindowPos(v.getWorkPosX(), v.getWorkPosY());
		ImGui.setNextWindowSize(v.getWorkSizeX(), v.getWorkSizeY());

		ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

		if (ImGui.begin(LangUtil.translate(I18N_TOOLKIT_NEMI_COMPILER), ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground))
		{
			ImGui.popStyleVar();

			ImGuiHelper.leftSplitDockspace("nemi_dockspace", "Model Tree", "Viewport");

			NemiModelProject selectedProject = null;

			if (ImGui.begin("Viewport", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollWithMouse))
				selectedProject = tabController.render(this::renderTab);
			ImGui.end();

			if (ImGui.begin("Model Tree", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove))
			{
				if (selectedProject == null)
					ImGui.textDisabled("No project selected");
				else
					selectedProject.getTreeModel().render();
			}
			ImGui.end();

			switch (action)
			{
				case OpenModel -> openModel();
				case ExportNem ->
				{
					if (selectedProject != null)
						exportNem(selectedProject);
				}
				case ExportJava ->
				{
					if (selectedProject != null)
						exportJava(selectedProject);
				}
			}
		}
		else
			ImGui.popStyleVar();
		ImGui.end();
	}

	private void renderTab(NemiModelProject model)
	{
		assert this.client != null;
		var tickDelta = client.getTickDelta();

		viewport.capture(false, true);

		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);

		var ms = new MatrixStack();

		var f = 1 / (float)client.getWindow().getScaleFactor();
		MathUtil.scalePos(ms, f, f, f);
		viewport.translateAndZoom(ms, tickDelta);
		MathUtil.scalePos(ms, 16, 16, 16);
		MathUtil.scalePos(ms, 10, 10, 10);

		viewport.rotate(ms, tickDelta);
		var immediate = client.getBufferBuilders().getEntityVertexConsumers();

		ms.push();
		ms.translate(-0.5f, -1, -0.5f);
		client.getBlockRenderManager().renderBlockAsEntity(Blocks.FURNACE.getDefaultState(), ms, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
		ms.pop();

		ms.multiply(new Quaternionf().rotationZ(MathUtil.fPI));
		ms.translate(0, -1.5f, 0);
		model.getModelPart().render(ms, immediate.getBuffer(RenderLayer.getEntitySolid(ToolkitClient.TEX_DEBUG)), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
		immediate.draw();

		viewport.draw();
	}
}
