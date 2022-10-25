package com.parzivail.pswgtk.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.screen.JComponentScreen;
import com.parzivail.pswgtk.swing.ClickListener;
import com.parzivail.pswgtk.swing.TextureBackedContentWrapper;
import com.parzivail.pswgtk.world.ParametricBlockRenderView;
import io.wispforest.worldmesher.WorldMesh;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockRenderView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ToolkitHomeScreen extends JComponentScreen
{
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit_home");

	private final JSplitPane root;
	private final JPanel contentPanel;
	private final JButton rebuildButton;

	private final BlockRenderView world;
	private final WorldMesh mesh;

	public ToolkitHomeScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_HOME));

		this.mesh = new WorldMesh.Builder(this.world = new ParametricBlockRenderView(this::getBlockState), BlockPos.ORIGIN, new BlockPos(256, 256, 256))
				.build();

		var panel = new JPanel();

		panel.add(this.rebuildButton = new JButton("Rebuild"));

		// TODO: https://github.com/gliscowo/worldmesher/pull/4
		ClickListener.add(this.rebuildButton, this::rebuildClicked);

		this.root = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.root.setLeftComponent(panel);

		this.contentPanel = new JPanel();
		this.contentPanel.setBackground(new Color(TextureBackedContentWrapper.MASK_COLOR));
		this.root.setRightComponent(contentPanel);
	}

	private BlockState getBlockState(BlockPos pos)
	{
		var x = pos.getX() - 128;
		var y = pos.getY() - 128;
		var z = pos.getZ() - 128;
		var R = 96;
		var r = 32;
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
	protected void renderContent()
	{
		assert this.client != null;

		if (!mesh.canRender())
			return;

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

		var rsm = RenderSystem.getModelViewStack();
		rsm.push();

		var contentCenter = transformSwingToScreen(getContentTopLeft().add(getContentSize().multiply(0.5f)));

		rsm.translate(contentCenter.x, contentCenter.y, 50);
		rsm.scale(1, -1, 1);
		var f = 1;
		rsm.scale(f, f, f);
		//		rsm.scale(100, 100, 100);
		RenderSystem.applyModelViewMatrix();

		//		var ir = this.client.getItemRenderer();
		//		var stack = (ItemStack)comboBox.getSelectedItem();

		var ms = new MatrixStack();
		ms.multiplyPositionMatrix(RenderSystem.getModelViewMatrix());
		ms.multiply(new Quaternion(30, 0, 0, true));
		ms.multiply(new Quaternion(0, (System.currentTimeMillis() % 36000) / 100f, 0, true));

		ms.translate(-128, -128, -128);

		//		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		//		var model = ir.getModel(stack, null, null, 0);
		//		ir.renderItem(stack, ModelTransformation.Mode.NONE, false, ms, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
		//		immediate.draw();

		this.mesh.render(ms);

		rsm.pop();
		RenderSystem.applyModelViewMatrix();
	}
}