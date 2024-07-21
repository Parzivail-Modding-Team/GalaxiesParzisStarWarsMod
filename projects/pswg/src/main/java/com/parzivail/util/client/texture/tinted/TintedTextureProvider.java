package com.parzivail.util.client.texture.tinted;

import com.parzivail.pswg.Resources;
import com.parzivail.util.client.NativeImageUtil;
import com.parzivail.util.client.texture.CallbackTexture;
import com.parzivail.util.client.texture.TextureProvider;
import com.parzivail.util.data.TintedIdentifier;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TintedTextureProvider extends TextureProvider<TintedIdentifier>
{
	public static final Identifier ROOT = Resources.id("///tinted");

	public TintedTextureProvider(TextureManager textureManager)
	{
		super(ROOT, textureManager);
	}

	public Identifier tint(String textureId, Identifier texture, int color)
	{
		return getId(
				textureId,
				() -> texture,
				() -> new TintedIdentifier(texture.getNamespace(), texture.getPath(), NativeImageUtil.argbToAbgr(color))
		);
	}

	@Override
	protected CallbackTexture createTexture(Identifier destId, TintedIdentifier request, BooleanConsumer callback)
	{
		registerDependencyCallbacks(destId, request);
		return new TintedTexture(request, DefaultSkinHelper.getTexture(), callback);
	}
}
