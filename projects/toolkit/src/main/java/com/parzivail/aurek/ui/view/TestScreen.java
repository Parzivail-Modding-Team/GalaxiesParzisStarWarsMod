package com.parzivail.aurek.ui.view;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.aurek.ui.ImguiScreen;
import com.parzivail.aurek.ui.Viewport;
import com.parzivail.pswg.Resources;
import com.parzivail.util.math.MathUtil;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.internal.ImGui;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

public class TestScreen extends ImguiScreen
{
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit.test");

	private final Viewport viewport = new Viewport();

	public TestScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_HOME));
	}

	@Override
	public void tick()
	{
		viewport.tick();
	}

	@Override
	protected void drawBackground(DrawContext context)
	{
		context.fillGradient(0, 0, this.width, this.height, 0xFF000000, 0xFF000011);

		assert this.client != null;
		var tickDelta = client.getTickDelta();

		//		viewport.capture(false, true);
		viewport.pollInput(this.width, this.height);

		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);

		var ms = new MatrixStack();

		ms.push();

		var f0 = (float)client.getWindow().getScaleFactor();
		var f = 1 / f0;
		MathUtil.scalePos(ms, f, f, f);
		viewport.translateAndZoom(ms, tickDelta);
		MathUtil.scalePos(ms, 16, 16, 16);
		MathUtil.scalePos(ms, 10, 10, 10);

		viewport.rotate(ms, tickDelta);
		var immediate = client.getBufferBuilders().getEntityVertexConsumers();

		ms.push();
		ms.translate(-0.5f, -0.5f, -0.5f);
		client.getBlockRenderManager().renderBlockAsEntity(Blocks.FURNACE.getDefaultState(), ms, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
		ms.pop();

		immediate.draw();

		ms.pop();

		ms.push();

		viewport.translate(ms, tickDelta);
		MathUtil.scalePos(ms, 1, -1, -1);
		viewport.rotate(ms, tickDelta);

		ImGuizmo.setOrthographic(true);
		ImGuizmo.setAllowAxisFlip(false);
		ImGuizmo.setEnabled(true);
		ImGuizmo.setDrawList(ImGui.getBackgroundDrawList());

		ImGuizmo.setRect(0, 0, this.width * f0, this.height * f0);
		ImGuizmo.setId(0);

		float[] m = new Matrix4f().get(new float[16]);
		float[] v = ms.peek().getPositionMatrix().get(new float[16]);
		float[] p = new Matrix4f().ortho(0, this.width * f0, this.height * f0, 0, 1, -1).get(new float[16]);

		ImGuizmo.manipulate(v, p, m, Operation.ROTATE, Mode.LOCAL);

		ms.pop();

		//		viewport.draw();
	}

	@Override
	public void process()
	{
		//		if (ImGui.begin("Aurek Toolkit"))
		//		{
		//			ImGui.text("Some Text");
		//		}
		//		ImGui.end();
	}
}
