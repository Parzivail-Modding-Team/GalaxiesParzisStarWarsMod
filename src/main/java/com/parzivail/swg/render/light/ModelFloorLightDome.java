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
import org.lwjgl.opengl.GL11;

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
		textureWidth = 64;
		textureHeight = 32;
		stemRing = new ModelRenderer(this, 34, 9);
		stemRing.setRotationPoint(-0.5F, 2.0F, -0.5F);
		stemRing.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
		stem = new ModelRenderer(this, 34, 24);
		stem.setRotationPoint(-1.0F, 20.0F, -1.0F);
		stem.addBox(0.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
		domeProtector = new ModelRenderer(this, 0, 12);
		domeProtector.setRotationPoint(-2.5F, -8.001F, -2.5F);
		domeProtector.addBox(0.0F, 0.0F, 0.0F, 7, 6, 7, 0.0F);
		reflector = new ModelRenderer(this, 0, 0);
		reflector.setRotationPoint(-3.0F, -2.0F, -3.0F);
		reflector.addBox(0.0F, 0.0F, 0.0F, 8, 2, 8, 0.0F);
		reflectorFillet = new ModelRenderer(this, 34, 0);
		reflectorFillet.setRotationPoint(-2.0F, 0.0F, -2.0F);
		reflectorFillet.addBox(0.0F, 0.0F, 0.0F, 6, 1, 6, 0.0F);
		bulb = new ShaderModelRenderer(this, 34, 15, ShaderHelper.glowSolid);
		bulb.setRotationPoint(-0.5F, -3.0F, -0.5F);
		bulb.addBox(0.0F, -3.0F, 0.0F, 3, 4, 3, 0.0F);
		stem.addChild(stemRing);
		stem.addChild(reflector);
		stem.addChild(reflectorFillet);
		stem.addChild(bulb);
		stem.addChild(domeProtector);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		((ShaderModelRenderer)bulb).color = GLPalette.WHITE;
		GL.PushAttrib(AttribMask.EnableBit);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL.Disable(EnableCap.CullFace);
		GL.Enable(EnableCap.Blend);
		stem.render(f5);
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
