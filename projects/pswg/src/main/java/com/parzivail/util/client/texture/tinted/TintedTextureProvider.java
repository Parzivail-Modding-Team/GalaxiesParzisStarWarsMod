package com.parzivail.util.client.texture.tinted;

import com.parzivail.pswg.Resources;
import com.parzivail.util.client.ColorUtil;
import com.parzivail.util.client.texture.CallbackTexture;
import com.parzivail.util.client.texture.TextureProvider;
import com.parzivail.util.data.TintedIdentifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class TintedTextureProvider extends TextureProvider<TintedIdentifier>
{
	private record EarlyEntry(String textureId, Identifier texture, int color)
	{
	}

	private static final ArrayList<EarlyEntry> earlyQueue = new ArrayList<>();

	public static final Identifier ROOT = Resources.id("///tinted");

	public static Identifier enqueueEarly(String textureId, Identifier texture, int color)
	{
		earlyQueue.add(new EarlyEntry(textureId, texture, color));
		return getCacheId(textureId);
	}

	public TintedTextureProvider(TextureManager textureManager)
	{
		super(ROOT, textureManager);

		for (EarlyEntry entry : earlyQueue)
			bakeTextureSync(getCacheId(entry.textureId), new TintedIdentifier(entry.texture.getNamespace(), entry.texture.getPath(), ColorUtil.argbToAbgr(entry.color)));
	}

	public Identifier tint(String textureId, Identifier texture, int color)
	{
		return getId(
				textureId,
				() -> texture,
				() -> new TintedIdentifier(texture.getNamespace(), texture.getPath(), ColorUtil.argbToAbgr(color))
		);
	}

	@NotNull
	public static Identifier getCacheId(String requestName)
	{
		return new Identifier(ROOT.getNamespace(), ROOT.getPath() + "/" + requestName);
	}

	@Override
	protected CallbackTexture createTexture(Identifier destId, TintedIdentifier request, Consumer<Boolean> callback)
	{
		registerDependencyCallbacks(destId, request);
		return new TintedTexture(request, DefaultSkinHelper.getTexture(), callback);
	}
}
