package com.parzivail.nem.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ModelPart.class)
@Environment(EnvType.CLIENT)
public interface ModelPartAccessor
{
	static ModelPartAccessor of(Object o)
	{
		return (ModelPartAccessor)o;
	}

	@Final
	@Accessor
	Map<String, ModelPart> getChildren();
}
