package com.parzivail.pswgtk.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.screen.JComponentScreen;
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ToolkitHomeScreen extends JComponentScreen implements MouseListener
{
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit_home");

	private final JSplitPane root;
	private final JPanel contentPanel;
	private final JButton rebuildButton;

	private final WorldMesh mesh;

	public ToolkitHomeScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_HOME));

		var panel = new JPanel();

		panel.add(this.rebuildButton = new JButton("Rebuild"));
		this.rebuildButton.addMouseListener(this);

		this.root = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.root.setLeftComponent(panel);

		this.contentPanel = new JPanel();
		this.contentPanel.setBackground(new Color(TextureBackedContentWrapper.MASK_COLOR));
		this.root.setRightComponent(contentPanel);

		mesh = new WorldMesh.Builder(new ParametricBlockRenderView(this::getBlockState), BlockPos.ORIGIN, new BlockPos(32, 32, 32))
				.build();
	}

	private BlockState getBlockState(BlockPos pos)
	{
		var x = pos.getX() - 16;
		var y = pos.getY() - 16;
		var z = pos.getZ() - 16;
		var R = 12;
		var r = 6;
		if (Math.pow(R - Math.sqrt(x * x + z * z), 2) + y * y <= r * r)
			return Blocks.FIRE.getDefaultState();
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

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getSource() == rebuildButton)
		{
			// TODO: if you render a set of blocks that use one render layer,
			//  delete those blocks, add new blocks which use a different render
			//  layer, and scheduleRebuild, the original blocks remain
			mesh.scheduleRebuild();
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
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
		var f = 6;
		rsm.scale(f, f, f);
		//		rsm.scale(100, 100, 100);
		RenderSystem.applyModelViewMatrix();

		//		var ir = this.client.getItemRenderer();
		//		var stack = (ItemStack)comboBox.getSelectedItem();

		var ms = new MatrixStack();
		ms.multiplyPositionMatrix(RenderSystem.getModelViewMatrix());
		ms.multiply(new Quaternion(30, 0, 0, true));
		ms.multiply(new Quaternion(0, (System.currentTimeMillis() % 36000) / 100f, 0, true));

		ms.translate(-16, -16, -16);

		//		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		//		var model = ir.getModel(stack, null, null, 0);
		//		ir.renderItem(stack, ModelTransformation.Mode.NONE, false, ms, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
		//		immediate.draw();

		this.mesh.render(ms);

		rsm.pop();
		RenderSystem.applyModelViewMatrix();
	}
}