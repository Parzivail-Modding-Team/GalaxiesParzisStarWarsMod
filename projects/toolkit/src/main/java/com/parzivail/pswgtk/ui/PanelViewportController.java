package com.parzivail.pswgtk.ui;

import com.parzivail.pswgtk.swing.EventHelper;
import com.parzivail.pswgtk.swing.TextureBackedContentWrapper;
import com.parzivail.pswgtk.util.AnimatedFloat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

public class PanelViewportController implements MouseMotionListener
{
	private static <T> void putNumpadEmu(HashMap<Integer, T> map, int key, T value)
	{
		map.put(key, value);
		map.put(key + KeyEvent.VK_NUMPAD0, value);
	}

	private static final HashMap<Integer, Pair<Vec3f, Vec3f>> VIEWPORT_DIRECTION_PRESETS = Util.make(() -> {
		var h = new HashMap<Integer, Pair<Vec3f, Vec3f>>();
		putNumpadEmu(h, KeyEvent.VK_1, new Pair<>(Direction.SOUTH.getUnitVector(), Direction.NORTH.getUnitVector()));
		putNumpadEmu(h, KeyEvent.VK_7, new Pair<>(Direction.UP.getUnitVector(), Direction.DOWN.getUnitVector()));
		putNumpadEmu(h, KeyEvent.VK_3, new Pair<>(Direction.EAST.getUnitVector(), Direction.WEST.getUnitVector()));
		putNumpadEmu(h, KeyEvent.VK_9, new Pair<>(new Vec3f(1, 1, 1), new Vec3f(-1, 1, 1)));
		putNumpadEmu(h, KeyEvent.VK_5, new Pair<>(new Vec3f(1, 1, -1), new Vec3f(-1, 1, -1)));
		return h;
	});

	private final Screen screen;
	private final JPanel panel;
	private final AnimatedFloat yaw = new AnimatedFloat(1, 0.1f, 45);
	private final AnimatedFloat pitch = new AnimatedFloat(1, 0.1f, 60);
	private final AnimatedFloat x = new AnimatedFloat(1, 1, 0);
	private final AnimatedFloat y = new AnimatedFloat(1, 1, 0);
	private final AnimatedFloat zoomExponent = new AnimatedFloat(2, 0.01f, 0);

	private Vec2f prevMousePos = Vec2f.ZERO;

	public PanelViewportController(Screen screen, JPanel panel)
	{
		this.screen = screen;
		this.panel = panel;
		panel.setFocusable(true);
		panel.setBackground(new Color(TextureBackedContentWrapper.MASK_COLOR));
		panel.addMouseMotionListener(this);
		panel.addMouseWheelListener(e -> this.zoomExponent.setTarget(this.zoomExponent.getTarget() - e.getWheelRotation()));
		EventHelper.press(panel, this::contentPanelPressed);
		EventHelper.keyPressed(panel, this::contentPanelKeyPressed);
	}

	private void contentPanelKeyPressed(KeyEvent e)
	{
		var c = e.getKeyCode();
		var cameraPreset = VIEWPORT_DIRECTION_PRESETS.get(c);
		if (cameraPreset != null)
		{
			setCameraPosition(e.isShiftDown() ? cameraPreset.getRight() : cameraPreset.getLeft());
			if (e.isControlDown())
			{
				this.x.setTarget(0);
				this.y.setTarget(0);
			}
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

	private void contentPanelPressed(MouseEvent e)
	{
		prevMousePos = new Vec2f(e.getXOnScreen(), e.getYOnScreen());
		this.panel.requestFocus();
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
		var pos = new Vec2f(e.getXOnScreen(), e.getYOnScreen());
		var delta = pos.add(prevMousePos.multiply(-1));

		if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0)
		{
			this.pitch.setValue((this.pitch.getValue() + delta.y / 4) % 360);
			this.yaw.setValue((this.yaw.getValue() + delta.x / 4) % 360);
		}
		else if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0)
		{
			this.y.setValue((this.y.getValue() + delta.y));
			this.x.setValue((this.x.getValue() + delta.x));
		}

		prevMousePos = pos;
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
	}

	public Vec2f getContentTopLeft()
	{
		return new Vec2f(panel.getX(), panel.getY());
	}

	public Vec2f getContentSize()
	{
		return new Vec2f(panel.getWidth(), panel.getHeight());
	}

	public void tick()
	{
		pitch.tick();
		yaw.tick();

		x.tick();
		y.tick();

		zoomExponent.tick();
	}

	public Vec2f transformSwingToScreen(Vec2f point)
	{
		var client = MinecraftClient.getInstance();
		return new Vec2f(
				(int)(screen.width * point.x / client.getWindow().getFramebufferWidth()),
				(int)(screen.height * point.y / client.getWindow().getFramebufferHeight())
		);
	}

	public void setup(MatrixStack ms, float tickDelta)
	{
		var windowX = x.getValue(tickDelta);
		var windowY = y.getValue(tickDelta);

		var contentTopLeft = getContentTopLeft();
		var contentCenter = transformSwingToScreen(contentTopLeft.add(getContentSize().multiply(0.5f)).add(new Vec2f(windowX, windowY)));

		ms.translate(contentCenter.x, -contentCenter.y, 50);
		var f = (float)Math.pow(10, zoomExponent.getValue() / 10);
		ms.scale(f, f, 1);
	}

	public void rotate(MatrixStack ms, float tickDelta)
	{
		ms.multiply(new Quaternion(pitch.getValue(tickDelta), 0, 0, true));
		ms.multiply(new Quaternion(0, yaw.getValue(tickDelta), 0, true));
	}
}
