package com.parzivail.pswgtk.ui;

import com.google.gson.Gson;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.imgui.ImguiScreen;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.ToolkitClient;
import com.parzivail.pswgtk.model.nemi.NemiModel;
import com.parzivail.pswgtk.ui.model.NemiModelProject;
import com.parzivail.pswgtk.ui.model.TabModelController;
import com.parzivail.pswgtk.util.DialogUtil;
import com.parzivail.pswgtk.util.FileUtil;
import com.parzivail.pswgtk.util.LangUtil;
import com.parzivail.util.math.MatrixStackUtil;
import imgui.flag.ImGuiDir;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import imgui.type.ImBoolean;
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
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec2f;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class NemiCompilerScreen extends ImguiScreen
{
	private static final Gson gson = new Gson();
	private static final String I18N_TOOLKIT_NEMI_COMPILER = Resources.screen("nemi_compiler");
	private static final ImInt INT_NULL = new ImInt(0);

	private final TabModelController<NemiModelProject> tabController;
	private final PanelViewportController viewportController = null;

	private boolean setupDock = true;

	private JPanel rootPanel;
	private JTabbedPane openFiles;
	private JMenuBar menuBar;
	private JTree modelTree;
	private JPanel contentPanel;

	private NemiModelProject selectedModel;

	public NemiCompilerScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_NEMI_COMPILER));

		//		var menu = new JMenu("File");
		//		menu.setMnemonic(KeyEvent.VK_F);
		//		menu.add(EventHelper.action(new JMenuItem("Open...", KeyEvent.VK_O), EventHelper.ctrl(KeyEvent.VK_O), this::openModel));
		//		menu.add(new JSeparator());
		//		menu.add(EventHelper.action(new JMenuItem("Export NEM..."), this::exportNem));
		//		menuBar.add(menu);

		//		viewportController = new PanelViewportController(this, contentPanel);

		tabController = new TabModelController<>(openFiles, this::selectedModelChanged);
		//		modelTree.setModel(null);
	}

	private Vec2f getContentTopLeft()
	{
		return new Vec2f(contentPanel.getX(), contentPanel.getY());
	}

	private Vec2f getContentSize()
	{
		return new Vec2f(contentPanel.getWidth(), contentPanel.getHeight());
	}

	private void selectedModelChanged(NemiModelProject nemiModelProject)
	{
		selectedModel = nemiModelProject;
		//		modelTree.setModel(nemiModelProject.getTreeModel());
	}

	@Override
	public void tick()
	{
		//		viewportController.tick();
	}

	protected void renderContent(MatrixStack matrices)
	{
		if (selectedModel == null)
			return;

		assert this.client != null;
		var tickDelta = this.client.getTickDelta();

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

		var rsm = RenderSystem.getModelViewStack();
		rsm.push();

		MatrixStackUtil.scalePos(rsm, 1, -1, 1);
		viewportController.setup(rsm, tickDelta);
		MatrixStackUtil.scalePos(rsm, 16, 16, 16);
		RenderSystem.applyModelViewMatrix();

		var ms = new MatrixStack();
		viewportController.rotate(ms, tickDelta);
		var immediate = client.getBufferBuilders().getEntityVertexConsumers();

		ms.push();
		ms.translate(-0.5f, -1, -0.5f);
		client.getBlockRenderManager().renderBlockAsEntity(Blocks.FURNACE.getDefaultState(), ms, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
		ms.pop();

		ms.multiply(new Quaternion(0, 0, 180, true));
		ms.translate(0, -1.5f, 0);
		selectedModel.getModelPart().render(ms, immediate.getBuffer(RenderLayer.getEntitySolid(ToolkitClient.TEX_DEBUG)), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
		immediate.draw();

		rsm.pop();
		RenderSystem.applyModelViewMatrix();
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

	private void exportNem(ActionEvent e)
	{
		DialogUtil.saveFile("Save Model", "*.nem")
		          .ifPresent(this::saveModel);
	}

	private void saveModel(String path)
	{
		path = FileUtil.ensureExtension(path, ".nem");

		var project = tabController.getSelected();
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
		var flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.MenuBar;
		flags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground;
		flags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.NoDecoration;

		var v = ImGui.getMainViewport();

		ImGui.setNextWindowPos(0, 0);
		ImGui.setNextWindowSize(v.getSizeX(), v.getSizeY());

		ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

		if (ImGui.begin(LangUtil.translate(I18N_TOOLKIT_NEMI_COMPILER), new ImBoolean(true), flags))
		{
			ImGui.popStyleVar();

			var dockspaceId = ImGui.getID("nemi_dockspace");
			ImGui.dockSpace(dockspaceId, 0, 0, ImGuiDockNodeFlags.PassthruCentralNode);

			if (ImGui.beginMenuBar())
			{
				if (ImGui.beginMenu(LangUtil.translate(I18N_TOOLKIT_NEMI_COMPILER)))
				{
					if (ImGui.menuItem("Open..."))
						openModel();

					ImGui.separator();

					if (ImGui.menuItem("Exit"))
						close();

					ImGui.endMenu();
				}

				ImGui.endMenuBar();
			}

			if (setupDock)
			{
				setupDock = false;

				var outId = new ImInt(dockspaceId);
				var dockLeftId = ImGui.dockBuilderSplitNode(dockspaceId, ImGuiDir.Left, 0.3f, INT_NULL, outId);

				ImGui.dockBuilderDockWindow("Model Tree", dockLeftId);
				ImGui.dockBuilderDockWindow("Viewport", outId.get());
				ImGui.dockBuilderFinish(dockspaceId);
			}

			if (ImGui.begin("Model Tree"))
			{
				if (selectedModel == null)
					ImGui.textDisabled("No model selected");
				else
					selectedModel.getTreeModel().render();
			}
			ImGui.end();

			if (ImGui.begin("Viewport"))
			{
				if (ImGui.beginTabBar("open_projects"))
				{
					for (var model : tabController)
					{
						if (ImGui.beginTabItem(model.getTitle()))
						{
							ImGui.text(model.getCompiledModel().toString());
							ImGui.endTabItem();
						}
					}

					ImGui.endTabBar();
				}
			}
			ImGui.end();
		}
		else
			ImGui.popStyleVar();
		ImGui.end();
	}
}
