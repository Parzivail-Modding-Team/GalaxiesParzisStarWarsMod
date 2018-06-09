package com.parzivail.swg.render.light;

import com.parzivail.swg.tile.light.TileWallIndicatorCluster;
import com.parzivail.util.entity.EntityTilePassthrough;
import com.parzivail.util.render.ShaderModelRenderer;
import com.parzivail.util.ui.ShaderHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelWallIndicatorCluster - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelWallIndicatorCluster extends ModelBase
{
	public ModelRenderer wallMount;
	public ModelRenderer wallMount_1;
	public ModelRenderer wallMount_2;
	public ModelRenderer grill;
	public ModelRenderer grill_1;
	public ModelRenderer bulb;
	public ModelRenderer grill_2;
	public ModelRenderer grill_3;
	public ModelRenderer bulb_1;
	public ModelRenderer grill_4;
	public ModelRenderer grill_5;
	public ModelRenderer bulb_2;

	public ModelWallIndicatorCluster()
	{
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.grill_1 = new ModelRenderer(this, 0, 20);
		this.grill_1.setRotationPoint(2.0F, 0.5F, -5.8F);
		this.grill_1.addBox(0.0F, 0.0F, 0.0F, 1, 4, 6, 0.0F);
		this.grill_4 = new ModelRenderer(this, 0, 0);
		this.grill_4.setRotationPoint(0.5F, 2.0F, -6.0F);
		this.grill_4.addBox(0.0F, 0.0F, 0.0F, 4, 1, 6, 0.0F);
		this.grill_5 = new ModelRenderer(this, 0, 20);
		this.grill_5.setRotationPoint(2.0F, 0.5F, -5.8F);
		this.grill_5.addBox(0.0F, 0.0F, 0.0F, 1, 4, 6, 0.0F);
		this.bulb_1 = new ShaderModelRenderer(this, 0, 9, ShaderHelper.glowSolid);
		this.bulb_1.setRotationPoint(1.0F, 1.0F, -5.3F);
		this.bulb_1.addBox(0.0F, 0.0F, 0.0F, 3, 3, 6, 0.0F);
		this.bulb_2 = new ShaderModelRenderer(this, 0, 9, ShaderHelper.glowSolid);
		this.bulb_2.setRotationPoint(1.0F, 1.0F, -5.3F);
		this.bulb_2.addBox(0.0F, 0.0F, 0.0F, 3, 3, 6, 0.0F);
		this.grill_3 = new ModelRenderer(this, 0, 20);
		this.grill_3.setRotationPoint(2.0F, 0.5F, -5.8F);
		this.grill_3.addBox(0.0F, 0.0F, 0.0F, 1, 4, 6, 0.0F);
		this.wallMount_1 = new ModelRenderer(this, 22, 0);
		this.wallMount_1.setRotationPoint(-5.5F, 16.5F, 7.5F);
		this.wallMount_1.addBox(0.0F, 0.0F, 0.0F, 5, 5, 1, 0.0F);
		this.grill_2 = new ModelRenderer(this, 0, 0);
		this.grill_2.setRotationPoint(0.5F, 2.0F, -6.0F);
		this.grill_2.addBox(0.0F, 0.0F, 0.0F, 4, 1, 6, 0.0F);
		this.bulb = new ShaderModelRenderer(this, 0, 9, ShaderHelper.glowSolid);
		this.bulb.setRotationPoint(1.0F, 1.0F, -5.3F);
		this.bulb.addBox(0.0F, 0.0F, 0.0F, 3, 3, 6, 0.0F);
		this.wallMount = new ModelRenderer(this, 22, 0);
		this.wallMount.setRotationPoint(-2.5F, 10.5F, 7.5F);
		this.wallMount.addBox(0.0F, 0.0F, 0.0F, 5, 5, 1, 0.0F);
		this.wallMount_2 = new ModelRenderer(this, 22, 0);
		this.wallMount_2.setRotationPoint(0.5F, 16.5F, 7.5F);
		this.wallMount_2.addBox(0.0F, 0.0F, 0.0F, 5, 5, 1, 0.0F);
		this.grill = new ModelRenderer(this, 0, 0);
		this.grill.setRotationPoint(0.5F, 2.0F, -6.0F);
		this.grill.addBox(0.0F, 0.0F, 0.0F, 4, 1, 6, 0.0F);
		this.wallMount.addChild(this.grill_1);
		this.wallMount_2.addChild(this.grill_4);
		this.wallMount_2.addChild(this.grill_5);
		this.wallMount_1.addChild(this.bulb_1);
		this.wallMount_2.addChild(this.bulb_2);
		this.wallMount_1.addChild(this.grill_3);
		this.wallMount_1.addChild(this.grill_2);
		this.wallMount.addChild(this.bulb);
		this.wallMount.addChild(this.grill);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		if (entity instanceof EntityTilePassthrough && ((EntityTilePassthrough)entity).tileEntity instanceof TileWallIndicatorCluster)
		{
			TileWallIndicatorCluster console = (TileWallIndicatorCluster)((EntityTilePassthrough)entity).tileEntity;

			((ShaderModelRenderer)bulb).color = console.color1;
			((ShaderModelRenderer)bulb_1).color = console.color2;
			((ShaderModelRenderer)bulb_2).color = console.color3;
		}
		this.wallMount_1.render(f5);
		this.wallMount.render(f5);
		this.wallMount_2.render(f5);
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
