package dev.pswg.mixin.client.accesors;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.multipart.MultiPartModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(MultiPartModel.Unbaked.class)
public interface MultiPartModelUnbakedAccessor
{
	@Accessor("selectors")
	List<MultiPartModel.Selector<BlockStateModel.Unbaked>> getSelectors();
}
