package com.parzivail.swg.render.console;

import com.parzivail.swg.tile.console.TileWallControlPanelTall;
import com.parzivail.util.entity.EntityTilePassthrough;
import com.parzivail.util.render.ShaderModelRenderer;
import com.parzivail.util.ui.ShaderHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelWallControlPanelTall - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelWallControlPanelTall extends ModelBase
{
	public ModelRenderer wallPlate;
	public ModelRenderer panel;
	public ModelRenderer panel_1;
	public ModelRenderer screen;
	public ModelRenderer light;
	public ModelRenderer light_1;
	public ModelRenderer light_2;
	public ModelRenderer light_3;
	public ModelRenderer light_4;
	public ModelRenderer light_5;
	public ModelRenderer light_6;
	public ModelRenderer light_7;

	public ModelWallControlPanelTall()
	{
		textureWidth = 64;
		textureHeight = 32;
		light_4 = new ShaderModelRenderer(this, 12, 25, ShaderHelper.glowSolid);
		light_4.setRotationPoint(0.5F, 2.8F, 0.2F);
		light_4.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		light_6 = new ShaderModelRenderer(this, 22, 4, ShaderHelper.glowSolid);
		light_6.setRotationPoint(0.5F, 3.8F, 0.2F);
		light_6.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		light = new ShaderModelRenderer(this, 12, 9, ShaderHelper.glowSolid);
		light.setRotationPoint(0.5F, 0.8F, 0.2F);
		light.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		light_5 = new ShaderModelRenderer(this, 22, 0, ShaderHelper.glowSolid);
		light_5.setRotationPoint(1.5F, 2.8F, 0.2F);
		light_5.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		light_3 = new ShaderModelRenderer(this, 12, 21, ShaderHelper.glowSolid);
		light_3.setRotationPoint(1.5F, 1.8F, 0.2F);
		light_3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		panel = new ModelRenderer(this, 0, 15);
		panel.setRotationPoint(0.5F, 0.5F, -1.0F);
		panel.addBox(0.0F, 0.0F, 0.0F, 3, 6, 1, 0.0F);
		setRotateAngle(panel, 0.16580627893946132F, 0.0F, 0.0F);
		light_2 = new ShaderModelRenderer(this, 12, 17, ShaderHelper.glowSolid);
		light_2.setRotationPoint(0.5F, 1.8F, 0.2F);
		light_2.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		light_1 = new ShaderModelRenderer(this, 12, 13, ShaderHelper.glowSolid);
		light_1.setRotationPoint(1.5F, 0.8F, 0.2F);
		light_1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		screen = new ModelRenderer(this, 0, 24);
		screen.setRotationPoint(0.5F, 0.8F, -0.3F);
		screen.addBox(0.0F, 0.0F, 0.0F, 2, 4, 1, 0.0F);
		light_7 = new ShaderModelRenderer(this, 22, 8, ShaderHelper.glowSolid);
		light_7.setRotationPoint(1.5F, 3.8F, 0.2F);
		light_7.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		wallPlate = new ModelRenderer(this, 0, 0);
		wallPlate.setRotationPoint(-2.0F, 10.0F, 7.0F);
		wallPlate.addBox(0.0F, 0.0F, 0.0F, 4, 12, 1, 0.0F);
		panel_1 = new ModelRenderer(this, 12, 0);
		panel_1.setRotationPoint(0.5F, 11.5F, 0.0F);
		panel_1.addBox(0.0F, 0.0F, 0.0F, 3, 6, 1, 0.0F);
		setRotateAngle(panel_1, 2.975786374650332F, 0.0F, 0.0F);
		panel_1.addChild(light_4);
		panel_1.addChild(light_6);
		panel_1.addChild(light);
		panel_1.addChild(light_5);
		panel_1.addChild(light_3);
		wallPlate.addChild(panel);
		panel_1.addChild(light_2);
		panel_1.addChild(light_1);
		panel.addChild(screen);
		panel_1.addChild(light_7);
		wallPlate.addChild(panel_1);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		if (entity instanceof EntityTilePassthrough && ((EntityTilePassthrough)entity).tileEntity instanceof TileWallControlPanelTall)
		{
			TileWallControlPanelTall console = (TileWallControlPanelTall)((EntityTilePassthrough)entity).tileEntity;

			((ShaderModelRenderer)light).color = console.color1;
			((ShaderModelRenderer)light_1).color = console.color2;
			((ShaderModelRenderer)light_2).color = console.color3;
			((ShaderModelRenderer)light_3).color = console.color4;
			((ShaderModelRenderer)light_4).color = console.color5;
			((ShaderModelRenderer)light_5).color = console.color6;
			((ShaderModelRenderer)light_6).color = console.color7;
			((ShaderModelRenderer)light_7).color = console.color8;
		}
		wallPlate.render(f5);
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
