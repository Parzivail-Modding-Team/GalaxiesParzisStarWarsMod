package com.parzivail.pswgtk.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.render.ChunkedWorldMesh;
import com.parzivail.pswgtk.screen.JComponentScreen;
import com.parzivail.pswgtk.swing.EventHelper;
import com.parzivail.pswgtk.swing.TextureBackedContentWrapper;
import com.parzivail.pswgtk.world.ParametricBlockRenderView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockRenderView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class ToolkitHomeScreen extends JComponentScreen implements MouseMotionListener
{
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit_home");

	private final JSplitPane root;
	private final JPanel contentPanel;
	private final JButton rebuildButton;

	private final BlockRenderView world;
	private final ChunkedWorldMesh mesh;

	private Vec2f prevMousePos = Vec2f.ZERO;

	private float yaw = 45;
	private float pitch = 60;
	private int zoomExponent = 0;

	public ToolkitHomeScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_HOME));

		this.mesh = new ChunkedWorldMesh(this.world = new ParametricBlockRenderView(this::getBlockState), ChunkPos.ORIGIN, new ChunkPos(8, 8), 0, 128);

		var panel = new JPanel();

		panel.add(this.rebuildButton = new JButton("Rebuild"));

		// TODO: https://github.com/gliscowo/worldmesher/pull/4
		EventHelper.click(this.rebuildButton, this::rebuildClicked);

		this.root = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.root.setLeftComponent(panel);

		this.contentPanel = new JPanel();
		this.contentPanel.setBackground(new Color(TextureBackedContentWrapper.MASK_COLOR));
		this.contentPanel.addMouseMotionListener(this);
		this.contentPanel.addMouseWheelListener(e -> this.zoomExponent += e.getWheelRotation());
		EventHelper.press(this.contentPanel, mouseEvent -> prevMousePos = new Vec2f(mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen()));
		this.root.setRightComponent(contentPanel);
	}

	private BlockState getBlockState(BlockPos pos)
	{
		var x = pos.getX() - 64;
		var y = pos.getY() - 64;
		var z = pos.getZ() - 64;
		var R = 48;
		var r = 16;
		if (Math.pow(R - Math.sqrt(x * x + z * z), 2) + y * y <= r * r)
			return Blocks.STONE.getDefaultState();
		return Blocks.AIR.getDefaultState();
	}

	@Override
	protected JComponent getRootComponent()
	{
		return root;
	}

	private Vec2f getContentTopLeft()
	{
		return new Vec2f(contentPanel.getX(), contentPanel.getY());
	}

	private Vec2f getContentSize()
	{
		return new Vec2f(contentPanel.getWidth(), contentPanel.getHeight());
	}

	private void rebuildClicked(MouseEvent e)
	{
		mesh.scheduleRebuild();
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0)
		{
			var pos = new Vec2f(e.getXOnScreen(), e.getYOnScreen());
			var delta = pos.add(prevMousePos.multiply(-1));

			this.pitch += delta.y / 2;
			this.yaw += delta.x / 2;

			this.pitch %= 360;
			this.yaw %= 360;

			prevMousePos = pos;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{

	}

	@Override
	protected void renderContent(MatrixStack matrices)
	{
		assert this.client != null;

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

		var rsm = RenderSystem.getModelViewStack();
		rsm.push();

		var contentTopLeft = getContentTopLeft();
		var contentTopLeftMc = transformSwingToScreen(contentTopLeft);
		var contentCenter = transformSwingToScreen(contentTopLeft.add(getContentSize().multiply(0.5f)));

		rsm.translate(contentCenter.x, contentCenter.y, 50);
		rsm.scale(1, -1, 1);
		var f = (float)Math.pow(10, zoomExponent / 10.0);
		rsm.scale(f, f, 1);
		RenderSystem.applyModelViewMatrix();

		var ms = new MatrixStack();
		ms.multiplyPositionMatrix(RenderSystem.getModelViewMatrix());
		ms.multiply(new Quaternion(pitch, 0, 0, true));
		ms.multiply(new Quaternion(0, yaw, 0, true));

		var dim = mesh.getDimensions();
		ms.translate(-dim.getX() / 2f, -dim.getY() / 2f, -dim.getZ() / 2f);

		this.mesh.render(ms);

		rsm.pop();
		RenderSystem.applyModelViewMatrix();

		matrices.push();
		matrices.translate(contentTopLeftMc.x, contentTopLeftMc.y, 1000);
		textRenderer.draw(matrices, client.fpsDebugString, 10, 10, 0xFFFFFF);
		textRenderer.draw(matrices, String.format("%s ms", client.getLastFrameDuration()), 10, 10 + textRenderer.fontHeight, 0xFFFFFF);
		matrices.pop();
	}
}