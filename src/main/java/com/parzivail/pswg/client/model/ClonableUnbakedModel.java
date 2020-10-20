package com.parzivail.pswg.client.model;

import net.minecraft.client.render.model.UnbakedModel;

public abstract class ClonableUnbakedModel implements UnbakedModel
{
	public abstract ClonableUnbakedModel copy();
}
