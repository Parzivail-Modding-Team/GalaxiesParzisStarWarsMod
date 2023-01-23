package com.parzivail.pswgtk.ui;

import com.parzivail.pswgtk.util.AnimatedFloat;
import com.parzivail.pswgtk.util.ImGuiHelper;
import imgui.flag.ImGuiMouseButton;
import imgui.internal.ImGui;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

@Deprecated
public class ViewportController
{
	private static <T> void putNumpadEmu(HashMap<Integer, T> map, int key, T value)
	{
		map.put(key + GLFW.GLFW_KEY_0, value);
		map.put(key + GLFW.GLFW_KEY_KP_0, value);
	}

	private static final HashMap<Integer, Pair<Vec3f, Vec3f>> VIEWPORT_DIRECTION_PRESETS = Util.make(() -> {
		var h = new HashMap<Integer, Pair<Vec3f, Vec3f>>();
		putNumpadEmu(h, 1, new Pair<>(Direction.SOUTH.getUnitVector(), Direction.NORTH.getUnitVector()));
		putNumpadEmu(h, 7, new Pair<>(Direction.UP.getUnitVector(), Direction.DOWN.getUnitVector()));
		putNumpadEmu(h, 3, new Pair<>(Direction.EAST.getUnitVector(), Direction.WEST.getUnitVector()));
		putNumpadEmu(h, 9, new Pair<>(new Vec3f(1, 1, 1), new Vec3f(-1, 1, 1)));
		putNumpadEmu(h, 5, new Pair<>(new Vec3f(1, 1, -1), new Vec3f(-1, 1, -1)));
		return h;
	});

	private final AnimatedFloat yaw = new AnimatedFloat(1, 0.1f, 45);
	private final AnimatedFloat pitch = new AnimatedFloat(1, 0.1f, 60);
	private final AnimatedFloat x = new AnimatedFloat(1, 1, 0);
	private final AnimatedFloat y = new AnimatedFloat(1, 1, 0);
	private final AnimatedFloat zoomExponent = new AnimatedFloat(2, 0.01f, 0);

	private Vec2f prevMousePos = Vec2f.ZERO;

	public ViewportController()
	{
	}

	private void setCameraPosition(Vec3f pos)
	{
		var vec3d = new Vec3d(pos).normalize();
		double d = vec3d.horizontalLength();
		this.yaw.setTarget((float)(MathHelper.atan2(vec3d.x, vec3d.z) * MathHelper.DEGREES_PER_RADIAN));
		this.pitch.setTarget((float)(MathHelper.atan2(vec3d.y, d) * MathHelper.DEGREES_PER_RADIAN));
	}

	public void pollInput(int width, int height)
	{
		var cursorPos = ImGui.getCursorPos();
		ImGui.invisibleButton("viewport_input", width, height);
		ImGui.setCursorPos(cursorPos.x, cursorPos.y);
		var hovered = ImGui.isItemHovered();

		var pos = new Vec2f(ImGui.getMousePosX(), ImGui.getMousePosY());
		var delta = pos.add(prevMousePos.multiply(-1));

		if (hovered)
		{
			if (ImGui.isMouseDragging(ImGuiMouseButton.Left))
			{
				this.pitch.setValue((this.pitch.getValue() + delta.y / 4) % 360);
				this.yaw.setValue((this.yaw.getValue() + delta.x / 4) % 360);
			}
			else if (ImGui.isMouseDragging(ImGuiMouseButton.Right))
			{
				this.y.setValue((this.y.getValue() + delta.y));
				this.x.setValue((this.x.getValue() + delta.x));
			}

			var io = ImGui.getIO();
			this.zoomExponent.setTarget(this.zoomExponent.getTarget() + io.getMouseWheel());
		}

		prevMousePos = pos;

		pollKeyboard();
	}

	private void pollKeyboard()
	{
		for (var cameraPresetEntry : VIEWPORT_DIRECTION_PRESETS.entrySet())
		{
			if (ImGui.isKeyPressed(cameraPresetEntry.getKey()))
			{
				var cameraPreset = cameraPresetEntry.getValue();
				setCameraPosition(ImGuiHelper.isShiftDown() ? cameraPreset.getRight() : cameraPreset.getLeft());
				if (ImGuiHelper.isCtrlDown())
				{
					this.x.setTarget(0);
					this.y.setTarget(0);
				}

				return;
			}
		}

		if (ImGui.isKeyPressed(GLFW.GLFW_KEY_4) || ImGui.isKeyPressed(GLFW.GLFW_KEY_KP_4))
			this.yaw.setTarget(this.yaw.getValue() + 22.5f);
		else if (ImGui.isKeyPressed(GLFW.GLFW_KEY_6) || ImGui.isKeyPressed(GLFW.GLFW_KEY_KP_6))
			this.yaw.setTarget(this.yaw.getValue() - 22.5f);
		else if (ImGui.isKeyPressed(GLFW.GLFW_KEY_8) || ImGui.isKeyPressed(GLFW.GLFW_KEY_KP_8))
			this.pitch.setTarget(this.pitch.getValue() + 22.5f);
		else if (ImGui.isKeyPressed(GLFW.GLFW_KEY_2) || ImGui.isKeyPressed(GLFW.GLFW_KEY_KP_2))
			this.pitch.setTarget(this.pitch.getValue() - 22.5f);
	}

	public void tick()
	{
		pitch.tick();
		yaw.tick();

		x.tick();
		y.tick();

		zoomExponent.tick();
	}

	public void translateAndZoom(MatrixStack ms, Framebuffer framebuffer, float tickDelta)
	{
		ms.translate(framebuffer.textureWidth / 2f + this.x.getValue(tickDelta), (framebuffer.textureHeight / 2f + this.y.getValue(tickDelta)), 50);
		var f = (float)Math.pow(10, zoomExponent.getValue() / 10);
		ms.scale(f, f, 1);
	}

	public void rotate(MatrixStack ms, float tickDelta)
	{
		ms.multiply(new Quaternion(pitch.getValue(tickDelta), 0, 0, true));
		ms.multiply(new Quaternion(0, yaw.getValue(tickDelta), 0, true));
	}
}
