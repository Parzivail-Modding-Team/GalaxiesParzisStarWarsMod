package com.parzivail.util.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.util.ParziUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;

import java.io.IOException;
import java.util.function.Consumer;

public abstract class CallbackTexture extends ResourceTexture
{
	private static final ThreadExecutor<Runnable> dummyExecutor = new ThreadExecutor<Runnable>("pswg:dummy")
	{
		@Override
		protected Runnable createTask(Runnable runnable)
		{
			return runnable;
		}

		@Override
		protected boolean canExecute(Runnable task)
		{
			return false;
		}

		@Override
		protected Thread getThread()
		{
			return null;
		}

		@Override
		public void execute(Runnable runnable)
		{
			runnable.run();
		}
	};

	public static boolean FORCE_SYNCHRONOUS = false;

	protected final Consumer<Boolean> completionCallback;
	protected boolean isLoaded;
	protected NativeImage image;

	public CallbackTexture(Identifier location, Consumer<Boolean> completionCallback)
	{
		super(location);
		this.completionCallback = completionCallback;
	}

	protected void complete(NativeImage image)
	{
		var minecraft = MinecraftClient.getInstance();
		(FORCE_SYNCHRONOUS ? dummyExecutor : minecraft).execute(() -> {
			this.isLoaded = true;
			if (!RenderSystem.isOnRenderThread())
			{
				RenderSystem.recordRenderCall(() -> this.complete(image));
			}
			else
			{
				if (image != null)
				{
					this.upload(image, false, false);
					completionCallback.accept(true);
				}
				else
					completionCallback.accept(false);

				this.image = image;
			}
		});
	}

	public NativeImage getImage()
	{
		return image;
	}

	private void upload(NativeImage image, boolean blur, boolean clamp)
	{
		TextureUtil.prepareImage(this.getGlId(), 0, image.getWidth(), image.getHeight());
		image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), blur, clamp, false, false);
	}

	@Override
	public void load(ResourceManager manager) throws IOException
	{
		(FORCE_SYNCHRONOUS ? dummyExecutor : MinecraftClient.getInstance()).execute(() -> {
			if (!this.isLoaded)
			{
				try
				{
					super.load(manager);
				}
				catch (IOException var3)
				{
					ParziUtil.LOG.warn("Failed to load texture: %s", this.location);
					var3.printStackTrace();
				}

				this.isLoaded = true;
			}
		});

		complete(generateImage(manager));
	}

	protected abstract NativeImage generateImage(ResourceManager manager) throws IOException;
}
