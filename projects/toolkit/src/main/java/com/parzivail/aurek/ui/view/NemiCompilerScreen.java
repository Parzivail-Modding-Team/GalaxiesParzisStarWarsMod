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
import com.parzivail.util.math.MatrixStackUtil;
import imgui.flag.ImGuiDir;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import imgui.type.ImInt;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtIo;
import net.minecraft.text.Text;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class NemiCompilerScreen extends ImguiScreen
{
	private static final Gson gson = new Gson();
	private static final String I18N_TOOLKIT_NEMI_COMPILER = ToolkitClient.toolLang("nemi_compiler");
	private static final ImInt INT_NULL = new ImInt(0);

	private final TabModelController<NemiModelProject> tabController;
	private final Viewport viewport = new Viewport();

	private boolean firstFrame = true;

	public NemiCompilerScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_NEMI_COMPILER));

		//		var menu = new JMenu("File");
		//		menu.setMnemonic(KeyEvent.VK_F);
		//		menu.add(EventHelper.action(new JMenuItem("Open...", KeyEvent.VK_O), EventHelper.ctrl(KeyEvent.VK_O), this::openModel));
		//		menu.add(new JSeparator());
		//		menu.add(EventHelper.action(new JMenuItem("Export NEM..."), this::exportNem));
		//		menuBar.add(menu);

		tabController = new TabModelController<>();
	}

	@Override
	public void tick()
	{
		viewport.tick();
	}

	//	@Override
	//	protected void renderContent(MatrixStack matrices)
	//	{
	//		if (selectedModel == null)
	//			return;
	//
	//		assert this.client != null;
	//		var tickDelta = this.client.getTickDelta();
	//
	//		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
	//
	//		var rsm = RenderSystem.getModelViewStack();
	//		rsm.push();
	//
	//		MatrixStackUtil.scalePos(rsm, 1, -1, 1);
	//		viewportController.setup(rsm, tickDelta);
	//		MatrixStackUtil.scalePos(rsm, 16, 16, 16);
	//		RenderSystem.applyModelViewMatrix();
	//
	//		var ms = new MatrixStack();
	//		viewportController.rotate(ms, tickDelta);
	//		var immediate = client.getBufferBuilders().getEntityVertexConsumers();
	//
	//		ms.push();
	//		ms.translate(-0.5f, -1, -0.5f);
	//		client.getBlockRenderManager().renderBlockAsEntity(Blocks.FURNACE.getDefaultState(), ms, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
	//		ms.pop();
	//
	//		ms.multiply(new Quaternionf().rotationZ(MathHelper.PI));
	//		ms.translate(0, -1.5f, 0);
	//		selectedModel.getModelPart().render(ms, immediate.getBuffer(RenderLayer.getEntitySolid(ToolkitClient.TEX_DEBUG)), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
	//		immediate.draw();
	//
	//		rsm.pop();
	//		RenderSystem.applyModelViewMatrix();
	//	}

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
		          .ifPresent(path -> saveModel(path, project));
	}

	private void saveModel(String path, NemiModelProject project)
	{
		path = FileUtil.ensureExtension(path, ".nem");

		var nem = project.getCompiledModel();

		try
		{
			var file = new File(path);
			file.delete();
			NbtIo.write(nem, file);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
		boolean shouldExportProject = false;

		if (ImGui.beginMainMenuBar())
		{
			if (ImGui.beginMenu(LangUtil.translate(I18N_TOOLKIT_NEMI_COMPILER)))
			{
				if (ImGui.menuItem("Open...", "Ctrl+O"))
					openModel();

				if (ImGui.menuItem("Export NEM...", "Ctrl+E"))
					shouldExportProject = true;

				ImGui.separator();

				if (ImGui.menuItem("Exit"))
					close();

				ImGui.endMenu();
			}

			ImGui.endMainMenuBar();
		}

		if (ImGuiHelper.isCtrlDown() && ImGui.isKeyDown(GLFW.GLFW_KEY_O))
			openModel();
		if (ImGuiHelper.isCtrlDown() && ImGui.isKeyDown(GLFW.GLFW_KEY_E))
			shouldExportProject = true;

		var v = ImGui.getMainViewport();
		ImGui.setNextWindowPos(v.getWorkPosX(), v.getWorkPosY());
		ImGui.setNextWindowSize(v.getWorkSizeX(), v.getWorkSizeY());

		ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

		if (ImGui.begin(LangUtil.translate(I18N_TOOLKIT_NEMI_COMPILER), ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground))
		{
			ImGui.popStyleVar();

			var dockspaceId = ImGui.getID("nemi_dockspace");
			ImGui.dockSpace(dockspaceId);

			if (firstFrame)
			{
				firstFrame = false;

				var outId = new ImInt(dockspaceId);
				var dockLeftId = ImGui.dockBuilderSplitNode(dockspaceId, ImGuiDir.Left, 0.3f, INT_NULL, outId);

				ImGui.dockBuilderDockWindow("Model Tree", dockLeftId);
				ImGui.dockBuilderDockWindow("Viewport", outId.get());
				ImGui.dockBuilderFinish(dockspaceId);
			}

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

			if (shouldExportProject && selectedProject != null)
				exportNem(selectedProject);
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
		MatrixStackUtil.scalePos(ms, f, f, f);
		viewport.translateAndZoom(ms, tickDelta);
		MatrixStackUtil.scalePos(ms, 16, 16, 16);
		MatrixStackUtil.scalePos(ms, 10, 10, 10);

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
