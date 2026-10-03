package dev.pswg.rendering.ptex;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.Galaxies;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

import java.util.HashMap;
import java.util.Map;

/**
 * Codec-backed texture services. Addons may register services during startup.
 */
public final class PtexCodecs
{
	/**
	 * Registers one graph service. Call before any resource loading starts.
	 */
	public static <T extends PtexTextureSpec> void register(Identifier id, Class<T> type, MapCodec<T> codec)
	{
		if (SERVICES.containsKey(id) || TYPES.containsKey(type))
			throw new IllegalArgumentException("Ptex service already registered: " + id);

		SERVICES.put(id, codec);
		TYPES.put(type, id);
	}

	/**
	 * Reports unavailable services through the codec instead of a null failure.
	 */
	private static <T> DataResult<T> service(T value, String error)
	{
		return value == null ? DataResult.error(() -> error) : DataResult.success(value);
	}

	/**
	 * Registered codecs keyed by their namespaced service identifier.
	 */
	private static final Map<Identifier, MapCodec<? extends PtexTextureSpec>> SERVICES = new HashMap<>();

	/**
	 * The matching service name for each Java graph type.
	 */
	private static final Map<Class<?>, Identifier> TYPES = new HashMap<>();

	/**
	 * A recursive graph codec using the same type-dispatch pattern as vanilla assets.
	 */
	public static final Codec<PtexTextureSpec> CODEC = Codec.recursive("Ptex graph", graph -> {
		register(Galaxies.id("source"), SourceTexture.class, RecordCodecBuilder.mapCodec(instance -> instance.group(
				Identifier.CODEC.fieldOf("texture").forGetter(SourceTexture::identifier)
		).apply(instance, SourceTexture::new)));
		register(Galaxies.id("tint"), TintedTexture.class, RecordCodecBuilder.mapCodec(instance -> instance.group(
				ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("color").forGetter(TintedTexture::tintColor),
				graph.fieldOf("input").forGetter(TintedTexture::upstream)
		).apply(instance, TintedTexture::new)));
		register(Galaxies.id("composite"), CompositeTexture.class, RecordCodecBuilder.mapCodec(instance -> instance.group(
				graph.listOf(1, 256).fieldOf("layers").forGetter(CompositeTexture::layers)
		).apply(instance, CompositeTexture::new)));
		return Identifier.CODEC.partialDispatch(
				"type",
				value -> service(TYPES.get(value.getClass()), "Unregistered Ptex graph type"),
				id -> service(SERVICES.get(id), "Unknown Ptex service " + id)
		);
	});

	/**
	 * Prevents construction of this codec catalog.
	 */
	private PtexCodecs()
	{
	}
}
