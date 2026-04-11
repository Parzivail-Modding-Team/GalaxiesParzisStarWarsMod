package dev.pswg.rendering.ptex;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;

/**
 * A runtime sampler-domain {@code ptex} texture service.
 */
public interface PtexSamplerTextureService
{
	/**
	 * Gets the serialized service name used in the {@code ptex} namespace.
	 *
	 * @return The service name.
	 */
	String getServiceName();

	/**
	 * Produces the final image for the requested texture reference.
	 *
	 * @param reference       The parsed texture reference.
	 * @param resourceManager The current resource manager.
	 *
	 * @return The generated image.
	 *
	 * @throws IOException If the source image cannot be read or transformed.
	 */
	NativeImage load(PtexTextureReference reference, ResourceManager resourceManager) throws IOException;
}
