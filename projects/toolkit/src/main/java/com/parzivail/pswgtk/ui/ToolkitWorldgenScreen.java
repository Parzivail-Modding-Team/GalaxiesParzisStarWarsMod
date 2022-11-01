package com.parzivail.pswgtk.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.render.ChunkedWorldMesh;
import com.parzivail.pswgtk.screen.JComponentScreen;
import com.parzivail.pswgtk.swing.EventHelper;
import com.parzivail.pswgtk.swing.TextureBackedContentWrapper;
import com.parzivail.pswgtk.util.AnimatedFloat;
import com.parzivail.pswgtk.world.ProceduralBlockRenderView;
import com.parzivail.util.noise.OpenSimplex2F;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockRenderView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

public class ToolkitWorldgenScreen extends JComponentScreen implements MouseMotionListener
{
	private static <T> void putNumpadEmu(HashMap<Integer, T> map, int key, T value)
	{
		map.put(key, value);
		map.put(key + KeyEvent.VK_NUMPAD0, value);
	}

	public static final String I18N_TOOLKIT_WORLDGEN = Resources.screen("toolkit_worldgen");

	private static final HashMap<Integer, Pair<Vec3f, Vec3f>> VIEWPORT_DIRECTION_PRESETS = Util.make(() -> {
		var h = new HashMap<Integer, Pair<Vec3f, Vec3f>>();
		putNumpadEmu(h, KeyEvent.VK_1, new Pair<>(Direction.SOUTH.getUnitVector(), Direction.NORTH.getUnitVector()));
		putNumpadEmu(h, KeyEvent.VK_7, new Pair<>(Direction.UP.getUnitVector(), Direction.DOWN.getUnitVector()));
		putNumpadEmu(h, KeyEvent.VK_3, new Pair<>(Direction.EAST.getUnitVector(), Direction.WEST.getUnitVector()));
		putNumpadEmu(h, KeyEvent.VK_9, new Pair<>(new Vec3f(1, 1, 1), new Vec3f(-1, 1, 1)));
		putNumpadEmu(h, KeyEvent.VK_5, new Pair<>(new Vec3f(1, 1, -1), new Vec3f(-1, 1, -1)));
		return h;
	});

	private final JSplitPane root;
	private final JPanel contentPanel;

	private final BlockRenderView world;

	private final AnimatedFloat yaw = new AnimatedFloat(1, 0.1f, 45);
	private final AnimatedFloat pitch = new AnimatedFloat(1, 0.1f, 60);

	private ChunkedWorldMesh mesh;
	private Vec2f prevMousePos = Vec2f.ZERO;
	private int zoomExponent = 0;

	public ToolkitWorldgenScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_WORLDGEN));

		this.world = new ProceduralBlockRenderView(this::getBlockState);
		createMesh(16, 0, 128);

		var worldgenControls = new WorldgenControls(this.mesh);

		this.root = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.root.setLeftComponent(worldgenControls.getRoot());

		this.contentPanel = new JPanel();
		this.contentPanel.setFocusable(true);
		this.contentPanel.setBackground(new Color(TextureBackedContentWrapper.MASK_COLOR));
		this.contentPanel.addMouseMotionListener(this);
		this.contentPanel.addMouseWheelListener(e -> this.zoomExponent += e.getWheelRotation());
		EventHelper.press(this.contentPanel, this::contentPanelPressed);
		EventHelper.keyPressed(this.contentPanel, this::contentPanelKeyPressed);
		this.root.setRightComponent(contentPanel);
	}

	private void createMesh(int chunkSideLength, int minY, int maxY)
	{
		if (this.mesh != null)
			this.mesh.close();
		this.mesh = new ChunkedWorldMesh(this.world, ChunkPos.ORIGIN, new ChunkPos(chunkSideLength, chunkSideLength), minY, maxY);
		this.mesh.populateRenderMap();
	}

	private final OpenSimplex2F noise = new OpenSimplex2F(0);

	private BlockState getBlockState(BlockPos pos)
	{
		//		var x = pos.getX() - 64 + 0.5;
		//		var y = pos.getY() - 64 + 0.5;
		//		var z = pos.getZ() - 64 + 0.5;

		var x = pos.getX();
		var y = pos.getY();
		var z = pos.getZ();

		var d = this.mesh.getDimensions();
		if (x < 0 || x >= d.getX() || z < 0 || z >= d.getZ() || y < 0 || y >= d.getY())
			return Blocks.AIR.getDefaultState();

		if (mesh.getSlice().shouldChunkBeEmpty(pos.getX() >> 4, pos.getZ() >> 4))
			return Blocks.AIR.getDefaultState();

		//		var height = noise.noise2(x / 100f, z / 100f) + noise.noise2(x / 20f, z / 20f) / 1.5f;
		var height = noise.noise2(x / 100f, z / 100f) + noise.noise2(x / 50f, z / 50f) / 2;
		height *= 5;
		height += 32;
		height = (int)height;
		if (y == height)
			return Blocks.GRASS_BLOCK.getDefaultState();
		else if (y == 0)
			return Blocks.BEDROCK.getDefaultState();
		else if (y < height && Math.abs(y - height) < 10)
			return Blocks.DIRT.getDefaultState();
		else if (y < height)
			return Blocks.STONE.getDefaultState();
		//		var R = 48;
		//		var r = 16;
		//		if (Math.pow(R - Math.sqrt(x * x + z * z), 2) + y * y <= r * r)
		//		{
		//			if (z == 0.5 && x > 0)
		//				return Blocks.RED_WOOL.getDefaultState();
		//			if (x == 0.5 && z > 0)
		//				return Blocks.BLUE_WOOL.getDefaultState();
		//			return Blocks.STONE.getDefaultState();
		//		}
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public void removed()
	{
		if (this.mesh != null)
			this.mesh.close();

		super.removed();
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

	private void contentPanelPressed(MouseEvent mouseEvent)
	{
		prevMousePos = new Vec2f(mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen());
		this.contentPanel.requestFocus();
	}

	private void contentPanelKeyPressed(KeyEvent e)
	{
		var c = e.getKeyCode();
		var cameraPreset = VIEWPORT_DIRECTION_PRESETS.get(c);
		if (cameraPreset != null)
			setCameraPosition(e.isShiftDown() ? cameraPreset.getRight() : cameraPreset.getLeft());
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

	private void setCameraPosition(Vec3f pos)
	{
		var vec3d = new Vec3d(pos).normalize();
		double d = vec3d.horizontalLength();
		this.yaw.setTarget((float)(MathHelper.atan2(vec3d.x, vec3d.z) * MathHelper.DEGREES_PER_RADIAN));
		this.pitch.setTarget((float)(MathHelper.atan2(vec3d.y, d) * MathHelper.DEGREES_PER_RADIAN));
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