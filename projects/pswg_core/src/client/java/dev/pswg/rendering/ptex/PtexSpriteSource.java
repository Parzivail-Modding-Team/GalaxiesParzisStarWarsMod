package dev.pswg.rendering.ptex;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import dev.pswg.Galaxies;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Generates Ptex sprites before vanilla stitches and bakes the block atlas.
 */
public final class PtexSpriteSource implements SpriteSource
{
	/**
	 * Stateless atlas source used by assets/minecraft/atlases/blocks.json.
	 */
	public static final PtexSpriteSource INSTANCE = new PtexSpriteSource();

	/**
	 * Public codec registered with Fabric's SpriteSourceRegistry.
	 */
	public static final MapCodec<PtexSpriteSource> CODEC = MapCodec.unit(INSTANCE);

	/**
	 * Prevents construction of additional stateless sources.
	 */
	private PtexSpriteSource()
	{
	}

	/**
	 * Adds only atlas-capable graphs. Direct images keep vanilla animation metadata.
	 */
	@Override
	public void run(ResourceManager manager, Output output)
	{
		for (var file : PtexDefinition.FILES.listMatchingResources(manager).keySet())
		{
			var id = PtexDefinition.FILES.fileToId(file);
			try
			{
				var definition = PtexDefinition.read(manager, id);
				if (!definition.atlas())
					continue;

				var sprite = PtexDefinition.spriteId(id);

				if (definition.graph() instanceof SourceTexture source)
				{
					output.add(sprite, manager.getResource(source.identifier()).orElseThrow(() -> new IOException("Missing texture " + source.identifier())));
				}
				else
				{
					output.add(sprite, loader -> {
						try
						{
							var image = generate(definition.graph(), manager);
							return new SpriteContents(sprite, new FrameSize(image.getWidth(), image.getHeight()), image);
						}
						catch (Exception exception)
						{
							Galaxies.LOGGER.error("Could not generate Ptex sprite {}", id, exception);
							return null;
						}
					});
				}
			}
			catch (Exception exception)
			{
				Galaxies.LOGGER.error("Could not load Ptex sprite {}", id, exception);
			}
		}
	}

	/**
	 * Gets the registered codec.
	 */
	@Override
	public MapCodec<PtexSpriteSource> codec()
	{
		return CODEC;
	}

	/**
	 * Builds an image using the supplied resource manager, with no TextureManager
	 * or client singleton. The returned copy belongs to the atlas loader.
	 */
	public static NativeImage generate(PtexTextureSpec graph, ResourceManager manager) throws IOException
	{
		var textures = new HashMap<PtexTextureSpec, AsyncTexture>();
		var resolver = new PtexTextureResolver()
		{
			@Override
			public AsyncTexture load(PtexTextureSpec spec)
			{
				var existing = textures.get(spec);
				if (existing != null)
					return existing;

				var texture = spec.createTexture(this);
				textures.put(spec, texture);
				return texture;
			}

			@Override
			public AsyncTexture loadSource(Identifier id)
			{
				try (var stream = manager.getResource(id).orElseThrow(() -> new IOException("Missing texture " + id)).open())
				{
					return new AsyncTexture(CompletableFuture.completedFuture(NativeImage.read(stream)), null);
				}
				catch (IOException exception)
				{
					return new AsyncTexture(CompletableFuture.failedFuture(exception), null);
				}
			}

			@Override
			public AsyncTexture transform(PtexTextureSpec upstream, PtexTextureTransform transform, String description)
			{
				return new AsyncTexture(load(upstream).getFuture().thenApply(transform::apply), null);
			}
		};
		try
		{
			return PtexSamplerTextureManager.copyImage(resolver.load(graph).getBlocking());
		}
		finally
		{
			textures.values().forEach(AsyncTexture::close);
		}
	}
}
