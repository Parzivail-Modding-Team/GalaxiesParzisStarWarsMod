package dev.pswg.data;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Collection;

/**
 * A resource reloader that can be registered with the resource manager.
 */
public interface RegisterableResourceReloader extends PreparableReloadListener
{
	/**
	 * Registers a client reloader with optional listener ordering.
	 *
	 * @param id           The reloader identifier.
	 * @param reloader     The listener instance to register.
	 * @param dependencies The listener ids that must run first.
	 */
	private static void registerClientReloader(Identifier id, PreparableReloadListener reloader, Collection<Identifier> dependencies)
	{
		var resourceLoader = ResourceLoader.get(PackType.CLIENT_RESOURCES);
		resourceLoader.registerReloadListener(id, reloader);

		for (var dependency : dependencies)
		{
			resourceLoader.addListenerOrdering(id, dependency);
		}
	}

	/**
	 * Registers this reloader with the resource manager.
	 */
	default void register()
	{
		registerClientReloader(getId(), this, getDependencies());
	}

	/**
	 * Gets the identifier of this reloader.
	 */
	Identifier getId();

	/**
	 * Gets the dependencies of this reloader.
	 */
	Collection<Identifier> getDependencies();
}
