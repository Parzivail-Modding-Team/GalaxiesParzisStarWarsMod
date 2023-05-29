package com.parzivail.aurek.ui.view;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.aurek.imgui.AurekIconFont;
import com.parzivail.aurek.imgui.ImGuiHelper;
import com.parzivail.aurek.render.ChunkedWorldMesh;
import com.parzivail.aurek.ui.ImguiScreen;
import com.parzivail.aurek.ui.Viewport;
import com.parzivail.aurek.util.LangUtil;
import com.parzivail.aurek.world.GeneratingBlockRenderView;
import com.parzivail.pswg.Resources;
import com.parzivail.util.math.MathUtil;
import imgui.flag.ImGuiInputTextFlags;
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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;

public class ToolkitWorldgenScreen extends ImguiScreen
{
	private static final String I18N_TOOLKIT_WORLDGEN = Resources.screen("toolkit_worldgen");
	private static final Random SEED_RANDOM = Random.createLocal();

	private final GeneratingBlockRenderView world;
	private final Viewport viewport = new Viewport();

	private ChunkedWorldMesh mesh;

	private final ImInt seed = new ImInt();
	private final ImBoolean activeX = new ImBoolean();
	private final ImBoolean reverseX = new ImBoolean();
	private final ImInt sliceX = new ImInt();
	private final ImBoolean activeZ = new ImBoolean();
	private final ImBoolean reverseZ = new ImBoolean();
	private final ImInt sliceZ = new ImInt();

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

		// TODO: why?
		ms.translate(0, 0, -5000);

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
				ImGui.beginDisabled(this.mesh.isBusy());

				if (ImGui.button("Generate"))
					this.mesh.scheduleRegererate();

				ImGui.sameLine();

				if (this.mesh.isBusy())
					ImGui.text("Regenerating...");
				else
					ImGui.text("Ready");

				ImGui.separator();

				if (ImGui.inputInt("##seed", seed, 1, 10, ImGuiInputTextFlags.EnterReturnsTrue))
					this.mesh.setSeed(seed.get());

				ImGui.sameLine();

				var size = ImGui.getFrameHeight();
				if (ImGui.button(AurekIconFont.file_refresh, size, size))
				{
					seed.set(SEED_RANDOM.nextInt());
					this.mesh.setSeed(seed.get());
				}

				ImGui.separator();

				ImGui.text("X Slice");

				if (ImGui.checkbox("Active##x", activeX))
					this.mesh.getSlice().setActiveX(activeX.get());

				ImGui.sameLine();
				ImGui.beginDisabled(!activeX.get());
				if (ImGui.checkbox("Reverse##x", reverseX))
					this.mesh.getSlice().setReverseX(reverseX.get());
				if (ImGui.inputInt("Value##x", sliceX, 1, 10, ImGuiInputTextFlags.EnterReturnsTrue))
					this.mesh.getSlice().setValueX(sliceX.get());
				ImGui.endDisabled();

				ImGui.separator();

				ImGui.text("Z Slice");

				if (ImGui.checkbox("Active##z", activeZ))
					this.mesh.getSlice().setActiveZ(activeZ.get());

				ImGui.sameLine();
				ImGui.beginDisabled(!activeZ.get());
				if (ImGui.checkbox("Reverse##z", reverseZ))
					this.mesh.getSlice().setReverseZ(reverseZ.get());
				if (ImGui.inputInt("Value##z", sliceZ, 1, 10, ImGuiInputTextFlags.EnterReturnsTrue))
					this.mesh.getSlice().setValueZ(sliceZ.get());
				ImGui.endDisabled();

				ImGui.endDisabled();
			}
			ImGui.end();
		}
		else
			ImGui.popStyleVar();
		ImGui.end();
	}
}
