package com.parzivail.aurek.ui.view;

import com.google.gson.Gson;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.aurek.ToolkitClient;
import com.parzivail.aurek.imgui.ImGuiHelper;
import com.parzivail.aurek.model.p3di.P3diModel;
import com.parzivail.aurek.ui.ImguiScreen;
import com.parzivail.aurek.ui.Viewport;
import com.parzivail.aurek.ui.model.P3diModelProject;
import com.parzivail.aurek.ui.model.TabModelController;
import com.parzivail.aurek.util.DialogUtil;
import com.parzivail.aurek.util.FileUtil;
import com.parzivail.aurek.util.LangUtil;
import com.parzivail.p3d.P3dModel;
import com.parzivail.p3d.P3dObject;
import com.parzivail.pswg.client.render.entity.EnergyRenderer;
import com.parzivail.util.client.ImmediateBuffer;
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
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class P3diCompilerScreen extends ImguiScreen
{
	private enum UiAction
	{
		None(0),
		OpenModel(GLFW.GLFW_KEY_O),
		ExportModel(GLFW.GLFW_KEY_E),
		ExportRig(GLFW.GLFW_KEY_R);

		private final int keybind;

		UiAction(int keybind)
		{
			this.keybind = keybind;
		}
	}

	private static final Gson gson = new Gson();
	private static final String I18N_TOOLKIT_P3DI_COMPILER = ToolkitClient.toolLang("p3di_compiler");

	private final TabModelController<P3diModelProject> tabController;
	private final Viewport viewport = new Viewport();

	public P3diCompilerScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_P3DI_COMPILER));

		tabController = new TabModelController<>();
	}

	@Override
	public void tick()
	{
		viewport.tick();
	}

	private void openModel()
	{
		DialogUtil.openFile("Open Model", "P3Di Models (*.p3di)", false, "*.p3di")
		          .ifPresent(paths -> openModel(paths[0]));
	}

	private void openModel(String path)
	{
		if (path.endsWith(".p3di"))
			openP3di(path);
	}

	private void exportP3d(P3diModelProject project, boolean writeVertexData)
	{
		DialogUtil.saveFile("Save Model", writeVertexData ? "*.p3d" : "*.p3dr")
		          .ifPresent(path -> saveModel(path, project, writeVertexData));
	}

	private void saveModel(String path, P3diModelProject project, boolean writeVertexData)
	{
		path = FileUtil.ensureExtension(path, writeVertexData ? ".p3d" : ".p3dr");

		try
		{
			var file = new File(path);
			file.delete();
			project.getModel().compile(file, writeVertexData);
		}
		catch (Exception e)
		{
			// TODO: warnings, NOTIFIER, etc
			e.printStackTrace();
		}
	}

	private void openP3di(String path)
	{
		try (Reader reader = Files.newBufferedReader(Path.of(path)))
		{
			tabController.add(new P3diModelProject(path, gson.fromJson(reader, P3diModel.class)));
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
			if (ImGui.beginMenu(LangUtil.translate(I18N_TOOLKIT_P3DI_COMPILER)))
			{
				if (ImGui.menuItem("Open...", "Ctrl+O"))
					action = UiAction.OpenModel;

				if (ImGui.menuItem("Export P3D...", "Ctrl+E"))
					action = UiAction.ExportModel;

				if (ImGui.menuItem("Export P3D Rig...", "Ctrl+R"))
					action = UiAction.ExportRig;

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

		if (ImGui.begin(LangUtil.translate(I18N_TOOLKIT_P3DI_COMPILER), ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground))
		{
			ImGui.popStyleVar();

			ImGuiHelper.leftSplitDockspace("p3di_dockspace", "Model Tree", "Viewport");

			P3diModelProject selectedProject = null;

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
				case ExportModel ->
				{
					if (selectedProject != null)
						exportP3d(selectedProject, true);
				}
				case ExportRig ->
				{
					if (selectedProject != null)
						exportP3d(selectedProject, false);
				}
			}
		}
		else
			ImGui.popStyleVar();
		ImGui.end();
	}

	private void renderTab(P3diModelProject model)
	{
		if (model.getCompiledModel() == null)
		{
			ImGui.textDisabled("No preview available");
			return;
		}

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
		var immediate2 = client.getBufferBuilders().getEffectVertexConsumers();

		ms.push();
		ms.translate(-0.5f, -1, -0.5f);
		client.getBlockRenderManager().renderBlockAsEntity(Blocks.FURNACE.getDefaultState(), ms, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
		ms.pop();

		model.getCompiledModel().render(ms, immediate, null, null, (vcp, t, o) -> vcp.getBuffer(RenderLayer.getEntitySolid(ToolkitClient.TEX_DEBUG)), LightmapTextureManager.MAX_LIGHT_COORDINATE, 1, 255, 255, 255, 255);
		immediate.draw();

		ImmediateBuffer.A.init(immediate2.getBuffer(EnergyRenderer.LAYER_ENERGY), ms.peek(), 1, 1, 1, 1, OverlayTexture.DEFAULT_UV, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		for (var entry : model.getCompiledModel().transformables().entrySet())
		{
			ms.push();
			var socket = entry.getValue();

			if (socket instanceof P3dObject)
				continue;

			var m = ms.peek().getPositionMatrix();
			model.getCompiledModel().transformToSocket(m, socket.name, null, 0, P3dModel::identityTransformer);

			ImmediateBuffer.A.setMatrices(ms.peek());
			ImmediateBuffer.A.invertCull(true);

			var thickness = 0.005f;

			ImmediateBuffer.A.setColor(0xFFFFFFFF);
			ImmediateBuffer.A.drawSolidBoxSkew(thickness, 0, thickness, 0, 0, -thickness, 0);

			ImmediateBuffer.A.setColor(0xFF00FF00);
			ImmediateBuffer.A.drawSolidBoxSkew(thickness, 0, 1, 0, 0, thickness, 0);

			ms.multiply(new Quaternionf().rotationX(MathHelper.PI / 2));

			ImmediateBuffer.A.setColor(0xFF0000FF);
			ImmediateBuffer.A.drawSolidBoxSkew(thickness, 0, 1, 0, 0, thickness, 0);

			ms.multiply(new Quaternionf().rotationZ(-MathHelper.PI / 2));

			ImmediateBuffer.A.setColor(0xFFFF0000);
			ImmediateBuffer.A.drawSolidBoxSkew(thickness, 0, 1, 0, 0, thickness, 0);

			ImmediateBuffer.A.invertCull(false);

			ms.pop();
		}

		immediate2.draw();

		viewport.draw();
	}
}
