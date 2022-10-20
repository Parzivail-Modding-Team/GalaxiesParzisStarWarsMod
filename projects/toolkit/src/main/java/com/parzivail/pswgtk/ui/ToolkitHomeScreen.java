package com.parzivail.pswgtk.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.screen.JComponentScreen;
import com.parzivail.pswgtk.swing.TextureBackedContentWrapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ToolkitHomeScreen extends JComponentScreen implements MouseListener
{
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit_home");

	public ToolkitHomeScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_HOME));
	}

	@Override
	protected JComponent buildInterface()
	{
		var panel = new JPanel();

		JTextField tb;
		panel.add(tb = new JTextField(10));
		panel.add(new JButton("Button"));

		var split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(panel);

		var emptyPanel = new JPanel();
		emptyPanel.setBackground(new Color(TextureBackedContentWrapper.MASK_COLOR));
		split.setRightComponent(emptyPanel);

		return split;
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
		rsm.translate(this.width * 0.75, this.height * 0.5, 50);

		// Render MC content
		var matrixStack = new MatrixStack();
		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		this.client.getItemRenderer().renderItem(new ItemStack(Items.GRASS_BLOCK), ModelTransformation.Mode.FIXED, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, matrixStack, immediate, 0);
		immediate.draw();

		rsm.pop();
		RenderSystem.applyModelViewMatrix();
	}
}