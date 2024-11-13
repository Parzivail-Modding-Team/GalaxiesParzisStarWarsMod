package dev.pswg.mixin.client.accessors;

import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.BiConsumer;

@Mixin(ItemModelGenerator.class)
public interface ItemModelGeneratorAccessor
{
	@Accessor("modelCollector")
	BiConsumer<Identifier, ModelSupplier> getModelCollector();
}
