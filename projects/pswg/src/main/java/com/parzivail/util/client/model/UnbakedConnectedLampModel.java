package com.parzivail.util.client.model;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Function;

public class UnbakedConnectedLampModel extends ConnectedTextureModel.Unbaked
{
	private final SpriteIdentifier litBorderSprite;
	private ConnectedTextureModel cachedBakedModel;
	private final boolean hConnect;
	private final boolean vConnect;
	private final boolean lConnect;
	private final EnumSet<Direction> capDirections;
	private final SpriteIdentifier sprite;
	private final SpriteIdentifier borderSprite;
	private final SpriteIdentifier capSprite;
	private final Function<Function<SpriteIdentifier, Sprite>, ConnectedTextureModel> baker;

	public UnbakedConnectedLampModel(boolean hConnect, boolean vConnect, boolean lConnect, EnumSet<Direction> capDirections, SpriteIdentifier sprite, SpriteIdentifier borderSprite, SpriteIdentifier capSprite, SpriteIdentifier litBorderSprite)
	{
		super(hConnect, vConnect, lConnect, capDirections, sprite, borderSprite, capSprite);
		this.litBorderSprite = litBorderSprite;
		baker = spriteLoader -> new ConnectedLampModel(hConnect, vConnect, lConnect, capDirections, spriteLoader.apply(sprite), spriteLoader.apply(borderSprite), capSprite == null ? null : spriteLoader.apply(capSprite), spriteLoader.apply(litBorderSprite));
		this.hConnect = hConnect;
		this.vConnect = vConnect;
		this.lConnect = lConnect;
		this.capDirections = capDirections;
		this.sprite = sprite;
		this.borderSprite = borderSprite;
		this.capSprite = capSprite;
	}

	@Override
	public ClonableUnbakedModel copy()
	{
		return new UnbakedConnectedLampModel(hConnect, vConnect, lConnect, capDirections.clone(), sprite, borderSprite, capSprite, litBorderSprite);
	}
}
