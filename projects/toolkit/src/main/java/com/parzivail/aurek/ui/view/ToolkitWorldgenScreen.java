package com.parzivail.aurek.ui.view;

import com.parzivail.aurek.render.ChunkedWorldMesh;
import com.parzivail.aurek.ui.ImguiScreen;
import com.parzivail.aurek.ui.ViewportController;
import com.parzivail.aurek.ui.WorldgenControls;
import com.parzivail.aurek.world.GeneratingBlockRenderView;
import com.parzivail.pswg.Resources;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

import javax.swing.*;

public class ToolkitWorldgenScreen extends ImguiScreen
{
	private static final String I18N_TOOLKIT_WORLDGEN = Resources.screen("toolkit_worldgen");

	private final JSplitPane root;
	private final JPanel contentPanel;

	private final GeneratingBlockRenderView world;
	private final ViewportController viewportController;

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

		var worldgenControls = new WorldgenControls(this.mesh);

		this.root = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.root.setLeftComponent(worldgenControls.getRoot());

		this.contentPanel = new JPanel();
		viewportController = new ViewportController();
		this.root.setRightComponent(contentPanel);
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
		super.tick();

		viewportController.tick();
	}

	protected void renderContent(MatrixStack matrices)
	{
		//		assert this.client != null;
		//		var tickDelta = this.client.getTickDelta();
		//
		//		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		//
		//		var rsm = RenderSystem.getModelViewStack();
		//		rsm.push();
		//
		//		rsm.scale(1, -1, 1);
		//		viewportController.setup(rsm, tickDelta);
		//		RenderSystem.applyModelViewMatrix();
		//
		//		var dim = mesh.getDimensions();
		//
		//		var ms = new MatrixStack();
		//		ms.multiplyPositionMatrix(RenderSystem.getModelViewMatrix());
		//		viewportController.rotate(ms, tickDelta);
		//
		//		ms.translate(-dim.getX() / 2f, -dim.getY() / 2f, -dim.getZ() / 2f);
		//
		//		this.mesh.render(ms);
		//
		//		rsm.pop();
		//		RenderSystem.applyModelViewMatrix();
		//
		//		var contentTopLeftMc = viewportController.transformSwingToScreen(viewportController.getContentTopLeft());
		//
		//		matrices.push();
		//		matrices.translate(contentTopLeftMc.x, contentTopLeftMc.y, 1000);
		//		textRenderer.draw(matrices, client.fpsDebugString, 10, 10, 0xFFFFFF);
		//		matrices.pop();
	}

	@Override
	public void process()
	{

	}
}
