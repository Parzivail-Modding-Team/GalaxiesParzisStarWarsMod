package com.parzivail.swg.render.player;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ModelBipedWrapper extends PModelBipedBase
{
	private final ModelBiped modelBiped;

	public ModelBipedWrapper(ModelBiped modelBiped)
	{
		this.modelBiped = modelBiped;
	}

	@Override
	public ModelRenderer getHead()
	{
		return modelBiped.bipedHead;
	}

	@Override
	public ModelRenderer getBody()
	{
		return modelBiped.bipedBody;
	}

	@Override
	public ModelRenderer getArmLeft()
	{
		return modelBiped.bipedLeftArm;
	}

	@Override
	public ModelRenderer getArmRight()
	{
		return modelBiped.bipedRightArm;
	}

	@Override
	public ModelRenderer getLegLeft()
	{
		return modelBiped.bipedLeftLeg;
	}

	@Override
	public ModelRenderer getLegRight()
	{
		return modelBiped.bipedRightLeg;
	}

	@Override
	public ModelRenderer getHeadgear()
	{
		return modelBiped.bipedHeadwear;
	}
}
