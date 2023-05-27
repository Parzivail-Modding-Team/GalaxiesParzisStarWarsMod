package com.parzivail.aurek.ui.view;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.aurek.imgui.ImGuiHelper;
import com.parzivail.aurek.render.ChunkedWorldMesh;
import com.parzivail.aurek.ui.ImguiScreen;
import com.parzivail.aurek.ui.Viewport;
import com.parzivail.aurek.util.LangUtil;
import com.parzivail.aurek.world.GeneratingBlockRenderView;
import com.parzivail.pswg.Resources;
import com.parzivail.util.math.MathUtil;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

public class ToolkitWorldgenScreen extends ImguiScreen
{
	private static final String I18N_TOOLKIT_WORLDGEN = Resources.screen("toolkit_worldgen");

	private final GeneratingBlockRenderView world;
	private final Viewport viewport = new Viewport();

	private ChunkedWorldMesh mesh;

	public ToolkitWorldgenScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_WORLDGEN));

		var sideLengthInChunks = 16;
		var min = ChunkPos.ORIGIN;
		var max = new ChunkPos(sideLengthInChunks - 1, sideLengthInChunks - 1);
		var minY = 0;
		var maxY = 128;

		this.world = new GeneratingBlockRenderView(min, max, minY, maxY);
		this.mesh = new ChunkedWorldMesh(this.world, min, max, minY, maxY);

		this.mesh.scheduleRegererate();
	}

	@Override
	public void removed()
	{
		if (this.mesh != null)
			this.mesh.close();

		super.removed();
	}

	@Override
	public void tick()
	{
		viewport.tick();
	}

	protected void renderContent()
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

		immediate.draw();

		var dim = mesh.getDimensions();
		ms.translate(-dim.getX() / 2f, -dim.getY() / 2f, -dim.getZ() / 2f);

		this.mesh.render(ms);

		viewport.draw();
	}

	@Override
	public void process()
	{
		if (ImGui.beginMainMenuBar())
		{
			if (ImGui.beginMenu(LangUtil.translate(I18N_TOOLKIT_WORLDGEN)))
			{
				ImGui.separator();

				if (ImGui.menuItem("Exit"))
					close();

				ImGui.endMenu();
			}

			ImGui.endMainMenuBar();
		}

		var v = ImGui.getMainViewport();
		ImGui.setNextWindowPos(v.getWorkPosX(), v.getWorkPosY());
		ImGui.setNextWindowSize(v.getWorkSizeX(), v.getWorkSizeY());

		ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

		if (ImGui.begin(LangUtil.translate(I18N_TOOLKIT_WORLDGEN), ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground))
		{
			ImGui.popStyleVar();

			ImGuiHelper.leftSplitDockspace("worldgen_dockspace", "Controls", "Viewport");

			if (ImGui.begin("Viewport", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollWithMouse))
			{
				renderContent();
			}
			ImGui.end();

			if (ImGui.begin("Controls", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove))
			{
				if (ImGui.button("Regenerate"))
					this.mesh.scheduleRegererate();
			}
			ImGui.end();
		}
		else
			ImGui.popStyleVar();
		ImGui.end();
	}
}
