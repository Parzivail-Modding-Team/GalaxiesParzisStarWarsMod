package com.parzivail.pswg.mixin;

import net.minecraft.entity.ai.brain.task.BreedTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BreedTask.class)
public interface BreedTaskAccessor
{
	@Accessor
	long getBreedTime();
}
