package com.parzivail.swg.render.light;

import com.parzivail.util.render.ShaderModelRenderer;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.ShaderHelper;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelFloorLightDome - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelFloorLightDome extends ModelBase
{
	public ModelRenderer stem;
	public ModelRenderer stemRing;
	public ModelRenderer reflectorFillet;
	public ModelRenderer reflector;
	public ModelRenderer domeProtector;
	public ModelRenderer bulb;

	public ModelFloorLightDome()
	{
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.stemRing = new ModelRenderer(this, 34, 9);
		this.stemRing.setRotationPoint(-0.5F, 2.0F, -0.5F);
		this.stemRing.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
		this.stem = new ModelRenderer(this, 34, 24);
		this.stem.setRotationPoint(-1.0F, 20.0F, -1.0F);
		this.stem.addBox(0.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
		this.domeProtector = new ModelRenderer(this, 0, 12);
		this.domeProtector.setRotationPoint(-2.5F, -8.001F, -2.5F);
		this.domeProtector.addBox(0.0F, 0.0F, 0.0F, 7, 6, 7, 0.0F);
		this.reflector = new ModelRenderer(this, 0, 0);
		this.reflector.setRotationPoint(-3.0F, -2.0F, -3.0F);
		this.reflector.addBox(0.0F, 0.0F, 0.0F, 8, 2, 8, 0.0F);
		this.reflectorFillet = new ModelRenderer(this, 34, 0);
		this.reflectorFillet.setRotationPoint(-2.0F, 0.0F, -2.0F);
		this.reflectorFillet.addBox(0.0F, 0.0F, 0.0F, 6, 1, 6, 0.0F);
		this.bulb = new ShaderModelRenderer(this, 34, 15, ShaderHelper.glowSolid);
		this.bulb.setRotationPoint(-0.5F, -3.0F, -0.5F);
		this.bulb.addBox(0.0F, -3.0F, 0.0F, 3, 4, 3, 0.0F);
		this.stem.addChild(this.stemRing);
		this.stem.addChild(this.reflector);
		this.stem.addChild(this.reflectorFillet);
		this.stem.addChild(this.bulb);
		this.stem.addChild(this.domeProtector);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		((ShaderModelRenderer)bulb).color = GLPalette.WHITE;
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.CullFace);
		GL.Enable(EnableCap.Blend);
		this.stem.render(f5);
		GL.PopAttrib();
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
