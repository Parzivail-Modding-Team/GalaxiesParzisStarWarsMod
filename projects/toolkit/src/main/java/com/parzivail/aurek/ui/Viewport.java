package com.parzivail.aurek.ui;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.aurek.imgui.ImGuiHelper;
import com.parzivail.aurek.render.TextureFramebuffer;
import com.parzivail.aurek.util.AnimatedFloat;
import com.parzivail.util.math.MathUtil;
import imgui.flag.ImGuiMouseButton;
import imgui.internal.ImGui;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class Viewport
{
	private static <T> void putNumpadEmu(Int2ObjectOpenHashMap<T> map, int key, T value)
	{
		map.put(key + GLFW.GLFW_KEY_0, value);
		map.put(key + GLFW.GLFW_KEY_KP_0, value);
	}

	private static final Int2ObjectOpenHashMap<Pair<Vector3f, Vector3f>> VIEWPORT_DIRECTION_PRESETS = Util.make(() -> {
		var h = new Int2ObjectOpenHashMap<Pair<Vector3f, Vector3f>>();
		putNumpadEmu(h, 1, new Pair<>(Direction.SOUTH.getUnitVector(), Direction.NORTH.getUnitVector()));
		putNumpadEmu(h, 7, new Pair<>(Direction.UP.getUnitVector(), Direction.DOWN.getUnitVector()));
		putNumpadEmu(h, 3, new Pair<>(Direction.EAST.getUnitVector(), Direction.WEST.getUnitVector()));
		putNumpadEmu(h, 9, new Pair<>(new Vector3f(1, 1, 1), new Vector3f(-1, 1, 1)));
		putNumpadEmu(h, 5, new Pair<>(new Vector3f(1, 1, -1), new Vector3f(-1, 1, -1)));
		return h;
	});

	private final AnimatedFloat yaw = new AnimatedFloat(1, 0.1f, 45);
	private final AnimatedFloat pitch = new AnimatedFloat(1, 0.1f, 60);
	private final AnimatedFloat x = new AnimatedFloat(1, 1, 0);
	private final AnimatedFloat y = new AnimatedFloat(1, 1, 0);
	private final AnimatedFloat zoomExponent = new AnimatedFloat(2, 0.01f, 0);

	private final TextureFramebuffer renderTarget = new TextureFramebuffer(true);

	private Vec2f prevMousePos = Vec2f.ZERO;
	private int previousFbo = 0;

	public void capture(boolean setViewport, boolean keepFbo)
	{
		if (keepFbo)
			previousFbo = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);

		renderTarget.resizeIfRequired((int)ImGui.getContentRegionAvailX(), (int)ImGui.getContentRegionAvailY());

		pollInput(renderTarget.textureWidth, renderTarget.textureHeight);

		renderTarget.beginWrite(setViewport);

		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT, false);
	}

	public void draw()
	{
		GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, previousFbo);
		ImGui.image(renderTarget.getColorAttachment(), renderTarget.textureWidth, renderTarget.textureHeight, 0, 1, 1, 0);
	}

	public void pollInput(int viewportWidth, int viewportHeight)
	{
		var cursorPos = ImGui.getCursorPos();
		ImGui.invisibleButton("viewport_input", viewportWidth, viewportHeight);
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

		if (ImGui.isItemFocused())
			pollKeyboard();
	}

	private void pollKeyboard()
	{
		for (var cameraPresetEntry : VIEWPORT_DIRECTION_PRESETS.int2ObjectEntrySet())
		{
			if (ImGui.isKeyPressed(cameraPresetEntry.getIntKey()))
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

	public void translateAndZoom(MatrixStack ms, float tickDelta)
	{
		translate(ms, tickDelta);
		zoom(ms, tickDelta);
	}

	public void zoom(MatrixStack ms, float tickDelta)
	{
		var f = (float)Math.pow(10, zoomExponent.getValue() / 10);
		MathUtil.scalePos(ms, f, -f, 1);
	}

	public void translate(MatrixStack ms, float tickDelta)
	{
		ms.translate(renderTarget.textureWidth / 2f + this.x.getValue(tickDelta), renderTarget.textureHeight / 2f + this.y.getValue(tickDelta), 50);
	}

	public void rotate(MatrixStack ms, float tickDelta)
	{
		ms.multiply(new Quaternionf().rotationX(MathUtil.toRadians(pitch.getValue(tickDelta))));
		ms.multiply(new Quaternionf().rotationY(MathUtil.toRadians(yaw.getValue(tickDelta))));
	}

	private void setCameraPosition(Vector3f pos)
	{
		var vec3d = new Vec3d(pos).normalize();
		double d = vec3d.horizontalLength();
		this.yaw.setTarget((float)(MathHelper.atan2(vec3d.x, vec3d.z) * MathHelper.DEGREES_PER_RADIAN));
		this.pitch.setTarget((float)(MathHelper.atan2(vec3d.y, d) * MathHelper.DEGREES_PER_RADIAN));
	}
}
