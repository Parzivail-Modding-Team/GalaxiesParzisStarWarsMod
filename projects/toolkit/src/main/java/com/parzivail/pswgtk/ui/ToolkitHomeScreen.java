package com.parzivail.pswgtk.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.screen.JComponentScreen;
import com.parzivail.pswgtk.swing.TextureBackedContentWrapper;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
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
	private final JComboBox<ItemStack> comboBox;
	private final JSlider slider;

	public ToolkitHomeScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_HOME));

		var panel = new JPanel();

		this.comboBox = new JComboBox<>();
		this.comboBox.addItem(new ItemStack(Items.DIAMOND_SWORD));
		this.comboBox.addItem(new ItemStack(Items.APPLE));
		this.comboBox.addItem(new ItemStack(Items.SPYGLASS));
		this.comboBox.addItem(new ItemStack(Blocks.TALL_GRASS));
		this.comboBox.addItem(new ItemStack(Blocks.SCAFFOLDING));
		this.comboBox.addItem(new ItemStack(Blocks.BIRCH_FENCE));
		this.comboBox.addItem(new ItemStack(Blocks.DRAGON_HEAD));
		this.comboBox.setSelectedIndex(0);

		panel.add(this.comboBox);

		this.slider = new JSlider(-180, 180, 0);
		panel.add(this.slider);

		this.root = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.root.setLeftComponent(panel);

		this.contentPanel = new JPanel();
		this.contentPanel.setBackground(new Color(TextureBackedContentWrapper.MASK_COLOR));
		this.root.setRightComponent(contentPanel);
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

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

		var rsm = RenderSystem.getModelViewStack();
		rsm.push();

		var contentCenter = transformSwingToScreen(getContentTopLeft().add(getContentSize().multiply(0.5f)));

		rsm.translate(contentCenter.x, contentCenter.y, 50);
		rsm.scale(1, -1, 1);
		rsm.scale(100, 100, 100);
		RenderSystem.applyModelViewMatrix();

		var ir = this.client.getItemRenderer();
		var stack = (ItemStack)comboBox.getSelectedItem();

		var ms = new MatrixStack();
		ms.multiply(new Quaternion(this.slider.getValue(), 0, 0, true));
		ms.multiply(new Quaternion(0, (System.currentTimeMillis() % 36000) / 100f, 0, true));

		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		var model = ir.getModel(stack, null, null, 0);
		ir.renderItem(stack, ModelTransformation.Mode.NONE, false, ms, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
		immediate.draw();

		rsm.pop();
		RenderSystem.applyModelViewMatrix();
	}
}