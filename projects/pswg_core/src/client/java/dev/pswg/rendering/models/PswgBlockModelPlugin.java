package dev.pswg.rendering.models;

import dev.pswg.mixin.client.accesors.MultiPartModelUnbakedAccessor;
import dev.pswg.mixin.client.accesors.SimpleCachedUnbakedRootAccessor;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.dispatch.WeightedVariants;
import net.minecraft.client.renderer.block.dispatch.multipart.MultiPartModel;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds connected-texture selection. G3D geometry uses the separate unbaked-model hook.
 */
public final class PswgBlockModelPlugin implements ModelLoadingPlugin
{
	/**
	 * Replaces only variants that explicitly request a connected-texture model.
	 */
	private static BlockStateModel.Unbaked transform(BlockStateModel.Unbaked model)
	{
		if (model instanceof SingleVariant.Unbaked(net.minecraft.client.renderer.block.dispatch.Variant variant))
		{
			var id = variant.modelLocation();
			if (ConnectedTextureUnbaked.isPillarConnectingModel(id))
				return new ConnectedTextureUnbaked(id, true);
			if (ConnectedTextureUnbaked.isCubeConnectingModel(id))
				return new ConnectedTextureUnbaked(id, false);
		}

		if (model instanceof WeightedVariants.Unbaked(WeightedList<BlockStateModel.Unbaked> entries1))
		{
			List<Weighted<BlockStateModel.Unbaked>> entries = new ArrayList<>();
			for (var entry : entries1.unwrap())
				entries.add(new Weighted<>(transform(entry.value()), entry.weight()));
			return new WeightedVariants.Unbaked(WeightedList.of(entries));
		}

		return model;
	}

	/**
	 * Keeps multipart conditions and cached roots while adapting their child variants.
	 */
	private static BlockStateModel.UnbakedRoot transformRoot(BlockStateModel.UnbakedRoot root)
	{
		if (root instanceof MultiPartModel.Unbaked multipart)
		{
			var accessor = (MultiPartModelUnbakedAccessor)multipart;
			List<MultiPartModel.Selector<BlockStateModel.Unbaked>> selectors = new ArrayList<>();
			for (var selector : accessor.getSelectors())
				selectors.add(selector.with(transform(selector.model())));
			return new MultiPartModel.Unbaked(selectors);
		}

		if (root instanceof BlockStateModel.SimpleCachedUnbakedRoot cached)
		{
			var original = ((SimpleCachedUnbakedRootAccessor)cached).getContents();
			var transformed = transform(original);
			if (transformed != original)
				return transformed.asRoot();
		}

		return root;
	}

	/**
	 * The module's shared connected-texture plugin.
	 */
	public static final PswgBlockModelPlugin INSTANCE = new PswgBlockModelPlugin();

	/**
	 * Registers the public Fabric blockstate selection hook for each reload.
	 */
	@Override
	public void initialize(Context context)
	{
		context.modifyBlockModelOnLoad().register((root, loadContext) -> transformRoot(root));
	}
}
