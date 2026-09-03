package dev.pswg.mixin.client.accesors;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockStateModel.SimpleCachedUnbakedRoot.class)
public interface SimpleCachedUnbakedRootAccessor
{
	@Accessor("contents")
	BlockStateModel.Unbaked getContents();
}
