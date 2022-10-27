package com.parzivail.pswgtk.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.render.ChunkedWorldMesh;
import com.parzivail.pswgtk.screen.JComponentScreen;
import com.parzivail.pswgtk.swing.EventHelper;
import com.parzivail.pswgtk.swing.TextureBackedContentWrapper;
import com.parzivail.pswgtk.util.AnimatedFloat;
import com.parzivail.pswgtk.world.ParametricBlockRenderView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockRenderView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

public class ToolkitHomeScreen extends JComponentScreen implements MouseMotionListener
{
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit_home");

	private static final HashMap<Integer, Direction> VIEWPORT_CAMERA_PRESETS = Util.make(() -> {
		var h = new HashMap<Integer, Direction>();
		h.put(KeyEvent.VK_1, Direction.SOUTH);
		h.put(KeyEvent.VK_NUMPAD1, Direction.SOUTH);
		h.put(KeyEvent.VK_7, Direction.UP);
		h.put(KeyEvent.VK_NUMPAD7, Direction.UP);
		h.put(KeyEvent.VK_3, Direction.EAST);
		h.put(KeyEvent.VK_NUMPAD3, Direction.EAST);
		return h;
	});

	private final JSplitPane root;
	private final JPanel contentPanel;

	private final BlockRenderView world;
	private final ChunkedWorldMesh mesh;

	private final AnimatedFloat yaw = new AnimatedFloat(1.5f, 0.1f, 45);
	private final AnimatedFloat pitch = new AnimatedFloat(1.5f, 0.1f, 60);

	private Vec2f prevMousePos = Vec2f.ZERO;
	private int zoomExponent = 0;

	public ToolkitHomeScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_HOME));

		this.mesh = new ChunkedWorldMesh(this.world = new ParametricBlockRenderView(this::getBlockState), ChunkPos.ORIGIN, new ChunkPos(8, 8), 0, 128);

		var panel = new JPanel();

		// TODO: https://github.com/gliscowo/worldmesher/pull/4
		panel.add(EventHelper.click(new JButton("Rebuild"), this::rebuildClicked));

		this.root = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.root.setLeftComponent(panel);

		this.contentPanel = new JPanel();
		this.contentPanel.setFocusable(true);
		this.contentPanel.setBackground(new Color(TextureBackedContentWrapper.MASK_COLOR));
		this.contentPanel.addMouseMotionListener(this);
		this.contentPanel.addMouseWheelListener(e -> this.zoomExponent += e.getWheelRotation());
		EventHelper.press(this.contentPanel, this::contentPanelPressed);
		EventHelper.keyPressed(this.contentPanel, this::contentPanelKeyPressed);
		this.root.setRightComponent(contentPanel);
	}

	private BlockState getBlockState(BlockPos pos)
	{
		var x = pos.getX() - 64 + 0.5;
		var y = pos.getY() - 64 + 0.5;
		var z = pos.getZ() - 64 + 0.5;
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

	private void contentPanelPressed(MouseEvent mouseEvent)
	{
		prevMousePos = new Vec2f(mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen());
		this.contentPanel.requestFocus();
	}

	private void contentPanelKeyPressed(KeyEvent e)
	{
		var c = e.getKeyCode();
		var cameraPreset = VIEWPORT_CAMERA_PRESETS.get(c);
		if (cameraPreset != null)
		{
			var scale = e.isShiftDown() ? -1 : 1;

			var vec3d = new Vec3d(cameraPreset.getUnitVector()).multiply(scale);
			double d = vec3d.horizontalLength();
			this.yaw.setTarget((float)(MathHelper.atan2(vec3d.x, vec3d.z) * MathHelper.DEGREES_PER_RADIAN));
			this.pitch.setTarget((float)(MathHelper.atan2(vec3d.y, d) * MathHelper.DEGREES_PER_RADIAN));
		}
		else
		{
			if (c == KeyEvent.VK_4 || c == KeyEvent.VK_NUMPAD4)
				this.yaw.setTarget(this.yaw.getValue() + 22.5f);
			else if (c == KeyEvent.VK_6 || c == KeyEvent.VK_NUMPAD6)
				this.yaw.setTarget(this.yaw.getValue() - 22.5f);
			else if (c == KeyEvent.VK_8 || c == KeyEvent.VK_NUMPAD8)
				this.pitch.setTarget(this.pitch.getValue() + 22.5f);
			else if (c == KeyEvent.VK_2 || c == KeyEvent.VK_NUMPAD2)
				this.pitch.setTarget(this.pitch.getValue() - 22.5f);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0)
		{
			var pos = new Vec2f(e.getXOnScreen(), e.getYOnScreen());
			var delta = pos.add(prevMousePos.multiply(-1));

			this.pitch.setValue((this.pitch.getValue() + delta.y / 4) % 360);
			this.yaw.setValue((this.yaw.getValue() + delta.x / 4) % 360);

			prevMousePos = pos;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{

	}

	@Override
	public void tick()
	{
		super.tick();

		this.pitch.tick();
		this.yaw.tick();
	}

	@Override
	protected void renderContent(MatrixStack matrices)
	{
		assert this.client != null;
		var tickDelta = this.client.getTickDelta();

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
		ms.multiply(new Quaternion(pitch.getValue(tickDelta), 0, 0, true));
		ms.multiply(new Quaternion(0, yaw.getValue(tickDelta), 0, true));

		var dim = mesh.getDimensions();
		ms.translate(-dim.getX() / 2f, -dim.getY() / 2f, -dim.getZ() / 2f);

		this.mesh.render(ms);

		rsm.pop();
		RenderSystem.applyModelViewMatrix();

		matrices.push();
		matrices.translate(contentTopLeftMc.x, contentTopLeftMc.y, 1000);
		textRenderer.draw(matrices, client.fpsDebugString, 10, 10, 0xFFFFFF);
		matrices.pop();
	}
}