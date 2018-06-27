package com.parzivail.swg.render.console;

import com.parzivail.swg.tile.console.TileWallControlPanel;
import com.parzivail.util.entity.EntityTilePassthrough;
import com.parzivail.util.render.ShaderModelRenderer;
import com.parzivail.util.ui.ShaderHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelWallControlPanel - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelWallControlPanel extends ModelBase
{
	public ModelRenderer base;
	public ModelRenderer button;
	public ModelRenderer button_1;
	public ModelRenderer button_2;
	public ModelRenderer button_3;
	public ModelRenderer button_4;
	public ModelRenderer button_5;
	public ModelRenderer screen;

	public ModelWallControlPanel()
	{
		textureWidth = 64;
		textureHeight = 32;
		button = new ShaderModelRenderer(this, 12, 8, ShaderHelper.glowSolid);
		button.setRotationPoint(0.5F, 0.5F, -0.3F);
		button.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		screen = new ModelRenderer(this, 0, 8);
		screen.setRotationPoint(3.5F, 0.5F, -0.3F);
		screen.addBox(0.0F, 0.0F, 0.0F, 4, 4, 1, 0.0F);
		button_4 = new ShaderModelRenderer(this, 0, 27, ShaderHelper.glowSolid);
		button_4.setRotationPoint(2.0F, 2.0F, -0.3F);
		button_4.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		button_3 = new ShaderModelRenderer(this, 0, 23, ShaderHelper.glowSolid);
		button_3.setRotationPoint(2.0F, 0.5F, -0.3F);
		button_3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		base = new ModelRenderer(this, 0, 0);
		base.setRotationPoint(-4.0F, 13.0F, 7.0F);
		base.addBox(0.0F, 0.0F, 0.0F, 8, 5, 1, 0.0F);
		button_2 = new ShaderModelRenderer(this, 0, 19, ShaderHelper.glowSolid);
		button_2.setRotationPoint(0.5F, 3.5F, -0.3F);
		button_2.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		button_1 = new ShaderModelRenderer(this, 0, 15, ShaderHelper.glowSolid);
		button_1.setRotationPoint(0.5F, 2.0F, -0.3F);
		button_1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		button_5 = new ShaderModelRenderer(this, 6, 15, ShaderHelper.glowSolid);
		button_5.setRotationPoint(2.0F, 3.5F, -0.3F);
		button_5.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		base.addChild(button);
		base.addChild(screen);
		base.addChild(button_4);
		base.addChild(button_3);
		base.addChild(button_2);
		base.addChild(button_1);
		base.addChild(button_5);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		if (entity instanceof EntityTilePassthrough && ((EntityTilePassthrough)entity).tileEntity instanceof TileWallControlPanel)
		{
			TileWallControlPanel console = (TileWallControlPanel)((EntityTilePassthrough)entity).tileEntity;

			((ShaderModelRenderer)button).color = console.color1;
			((ShaderModelRenderer)button_1).color = console.color2;
			((ShaderModelRenderer)button_2).color = console.color3;
			((ShaderModelRenderer)button_3).color = console.color4;
			((ShaderModelRenderer)button_4).color = console.color5;
			((ShaderModelRenderer)button_5).color = console.color6;
		}
		base.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
