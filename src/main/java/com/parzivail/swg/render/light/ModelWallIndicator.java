package com.parzivail.swg.render.light;

import com.parzivail.swg.tile.light.TileWallIndicator;
import com.parzivail.util.entity.EntityTilePassthrough;
import com.parzivail.util.render.ShaderModelRenderer;
import com.parzivail.util.ui.Fx.Util;
import com.parzivail.util.ui.ShaderHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelWallIndicator - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelWallIndicator extends ModelBase
{
	public ModelRenderer wallMount;
	public ModelRenderer grill;
	public ModelRenderer grill_1;
	public ModelRenderer bulb;

	public ModelWallIndicator()
	{
		textureWidth = 64;
		textureHeight = 32;
		wallMount = new ModelRenderer(this, 22, 0);
		wallMount.setRotationPoint(-2.5F, 13.5F, 7.5F);
		wallMount.addBox(0.0F, 0.0F, 0.0F, 5, 5, 1, 0.0F);
		bulb = new ShaderModelRenderer(this, 0, 9, ShaderHelper.glowSolid);
		bulb.setRotationPoint(1.0F, 1.0F, -5.3F);
		bulb.addBox(0.0F, 0.0F, 0.0F, 3, 3, 6, 0.0F);
		grill = new ModelRenderer(this, 0, 0);
		grill.setRotationPoint(0.5F, 2.0F, -6.0F);
		grill.addBox(0.0F, 0.0F, 0.0F, 4, 1, 6, 0.0F);
		grill_1 = new ModelRenderer(this, 0, 20);
		grill_1.setRotationPoint(2.0F, 0.5F, -5.8F);
		grill_1.addBox(0.0F, 0.0F, 0.0F, 1, 4, 6, 0.0F);
		wallMount.addChild(bulb);
		wallMount.addChild(grill);
		wallMount.addChild(grill_1);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		if (entity instanceof EntityTilePassthrough && ((EntityTilePassthrough)entity).tileEntity instanceof TileWallIndicator)
		{
			TileWallIndicator console = (TileWallIndicator)((EntityTilePassthrough)entity).tileEntity;

			float frame = console.frame + f5;
			float h = (float)(1 / Math.E);
			float g = (float)(1 / (Math.E - h));
			float breath = (float)((Math.exp(Math.sin(frame / 16f)) - h) * g);

			((ShaderModelRenderer)bulb).color = Util.GetRgb((int)(breath * 255), 0, 0);
		}
		wallMount.render(f5);
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
