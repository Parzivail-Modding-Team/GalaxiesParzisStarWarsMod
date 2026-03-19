package dev.pswg.rendering.models;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * An item model backed by GQB geometry.
 */
public class GqbItemModel implements ItemModel
{
	private final List<ItemTintSource> _tints;
	private final QuadCollection _quads;
	private final Supplier<Vector3fc[]> _extents;
	private final ModelRenderProperties _properties;
	private final Matrix4fc _transformation;

	/**
	 * Creates a new GQB-backed item model.
	 *
	 * @param tints          The tint sources for the model.
	 * @param quads          The baked quad collection.
	 * @param properties     The resolved render properties.
	 * @param transformation The local transformation to apply.
	 */
	public GqbItemModel(
			List<ItemTintSource> tints,
			QuadCollection quads,
			ModelRenderProperties properties,
			Matrix4fc transformation
	)
	{
		_tints = tints;
		_quads = quads;
		_properties = properties;
		_transformation = transformation;
		_extents = new MemoizedSupplier<>(() -> computeExtents(quads.getAll()));
	}

	@Override
	public void update(
			ItemStackRenderState output,
			ItemStack item,
			ItemModelResolver resolver,
			ItemDisplayContext displayContext,
			@Nullable ClientLevel level,
			@Nullable ItemOwner owner,
			int seed
	)
	{
		output.appendModelIdentityElement(this);
		var layer = output.newLayer();

		if (item.hasFoil())
		{
			var foilType = hasSpecialAnimatedTexture(item) ? ItemStackRenderState.FoilType.SPECIAL : ItemStackRenderState.FoilType.STANDARD;
			layer.setFoilType(foilType);
			output.setAnimated();
			output.appendModelIdentityElement(foilType);
		}

		if (!_tints.isEmpty())
		{
			IntList tintLayers = layer.tintLayers();

			for (var tintSource : _tints)
			{
				int tint = tintSource.calculate(item, level, owner == null ? null : owner.asLivingEntity());
				tintLayers.add(tint);
				output.appendModelIdentityElement(tint);
			}
		}

		layer.setExtents(_extents);
		layer.setLocalTransform(_transformation);
		_properties.applyToLayer(layer, displayContext);
		layer.prepareQuadList().addAll(_quads.getAll());

		if (_quads.hasMaterialFlag(2))
		{
			output.setAnimated();
		}
	}

	/**
	 * Computes the set of model extents from the given quads.
	 *
	 * @param quads The baked quads in this model.
	 *
	 * @return The unique positions occupied by the model.
	 */
	public static Vector3fc[] computeExtents(List<BakedQuad> quads)
	{
		Set<Vector3fc> result = new HashSet<>();

		for (var quad : quads)
		{
			for (int vertex = 0; vertex < 4; vertex++)
			{
				result.add(quad.position(vertex));
			}
		}

		return result.toArray(Vector3fc[]::new);
	}

	private static boolean hasSpecialAnimatedTexture(ItemStack itemStack)
	{
		return itemStack.is(ItemTags.COMPASSES) || itemStack.is(Items.CLOCK);
	}

	/**
	 * A tiny memoizing supplier to avoid adding Guava here.
	 *
	 * @param <T> The supplied type.
	 */
	private static final class MemoizedSupplier<T> implements Supplier<T>
	{
		private final Supplier<T> _delegate;
		private @Nullable T _value;

		private MemoizedSupplier(Supplier<T> delegate)
		{
			_delegate = delegate;
		}

		@Override
		public T get()
		{
			if (_value == null)
			{
				_value = _delegate.get();
			}

			return _value;
		}
	}
}
