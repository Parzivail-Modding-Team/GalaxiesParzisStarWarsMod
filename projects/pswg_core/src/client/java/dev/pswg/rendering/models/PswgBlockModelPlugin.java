package dev.pswg.rendering.models;

import dev.pswg.mixin.client.accesors.MultiPartModelUnbakedAccessor;
import dev.pswg.mixin.client.accesors.SimpleCachedUnbakedRootAccessor;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.WeightedVariants;
import net.minecraft.client.renderer.block.dispatch.multipart.MultiPartModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;

import java.util.ArrayList;
import java.util.List;

public final class PswgBlockModelPlugin
		implements ModelLoadingPlugin
{

    @Override
    public void initialize(Context context) {
        context.modifyBlockModelOnLoad().register(
                (root, loadContext) -> transformRoot(root)
        );
    }

    private static BlockStateModel.Unbaked transform(BlockStateModel.Unbaked model) {
        if (model instanceof SingleVariant.Unbaked single) {
            return transformSingle(single);
        }

        if (model instanceof WeightedVariants.Unbaked weighted) {
            return transformWeighted(weighted);
        }

        return model;
    }

    private static BlockStateModel.Unbaked transformSingle(SingleVariant.Unbaked single) {
        Variant variant = single.variant();
        Identifier modelId = variant.modelLocation();
        var geometry = GalaxiesModelBakery.getGeometry(modelId);

        if (geometry.isEmpty()) {
            return single;
        }

        if (!(geometry.get() instanceof GalaxiesModelBakery.GQuadGeometry gqb)) {
            return single;
        }

        return new GqbSingleVariantUnbaked(modelId, variant.modelState(), gqb);
    }

    private static BlockStateModel.Unbaked transformWeighted(WeightedVariants.Unbaked weighted) {
        List<Weighted<BlockStateModel.Unbaked>> result = new ArrayList<>();

        for (Weighted<BlockStateModel.Unbaked> entry : weighted.entries().unwrap()) {
            result.add(new Weighted<>(transform(entry.value()), entry.weight()));
        }

        return new WeightedVariants.Unbaked(
		        WeightedList.of(result)
        );
    }

    private static MultiPartModel.Unbaked transformMultipart(MultiPartModel.Unbaked multipart) {
        var accessor = (MultiPartModelUnbakedAccessor) multipart;

        List<MultiPartModel.Selector<BlockStateModel.Unbaked>> selectors = new ArrayList<>();

        for (var selector : accessor.getSelectors()) {
            selectors.add(selector.with(transform(selector.model())));
        }

        return new MultiPartModel.Unbaked(selectors);
    }

    private static BlockStateModel.UnbakedRoot transformRoot(BlockStateModel.UnbakedRoot root) {
        if (root instanceof MultiPartModel.Unbaked multipart) {
            return transformMultipart(multipart);
        }

        if (root instanceof BlockStateModel.SimpleCachedUnbakedRoot cached) {
            BlockStateModel.Unbaked original = ((SimpleCachedUnbakedRootAccessor)cached).getContents();
            BlockStateModel.Unbaked transformed = transform(original);

            if (transformed != original) {
                return transformed.asRoot();
            }
        }

        return root;
    }
}
