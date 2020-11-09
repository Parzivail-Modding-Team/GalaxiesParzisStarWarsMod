package com.parzivail.pswg.client.species;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

public class SwgSpeciesModel
{
	public final Identifier identifier;
	public final PlayerEntityModel<AbstractClientPlayerEntity> model;

	public SwgSpeciesModel(Identifier identifier, PlayerEntityModel<AbstractClientPlayerEntity> model)
	{
		this.identifier = identifier;
		this.model = model;
	}
}
