package com.parzivail.pswg.client.species;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public record SwgSpeciesModel(Identifier identifier, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> model)
{
}
