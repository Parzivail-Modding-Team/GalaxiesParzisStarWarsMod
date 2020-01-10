package com.parzivail.util.jsonpipeline;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class Submap implements ISubmap
{
	public static final ISubmap X1 = new Submap(16, 16, 0, 0);

	public static final ISubmap[][] X2 = new ISubmap[][] {
			{ new Submap(8, 8, 0, 0), new Submap(8, 8, 8, 0) }, { new Submap(8, 8, 0, 8), new Submap(8, 8, 8, 8) }
	};
	public static final ISubmap[][] X4 = new ISubmap[][] {
			{ new Submap(4, 4, 0, 0), new Submap(4, 4, 4, 0), new Submap(4, 4, 8, 0), new Submap(4, 4, 12, 0) },
			{ new Submap(4, 4, 0, 4), new Submap(4, 4, 4, 4), new Submap(4, 4, 8, 4), new Submap(4, 4, 12, 4) },
			{ new Submap(4, 4, 0, 8), new Submap(4, 4, 4, 8), new Submap(4, 4, 8, 8), new Submap(4, 4, 12, 8) },
			{ new Submap(4, 4, 0, 12), new Submap(4, 4, 4, 12), new Submap(4, 4, 8, 12), new Submap(4, 4, 12, 12) },
			};
	private static final float DIV3 = 16 / 3f;
	public static final ISubmap[][] X3 = new ISubmap[][] {
			{ new Submap(DIV3, DIV3, 0, 0), new Submap(DIV3, DIV3, DIV3, 0), new Submap(DIV3, DIV3, DIV3 * 2, 0) }, {
			new Submap(DIV3, DIV3, 0, DIV3), new Submap(DIV3, DIV3, DIV3, DIV3), new Submap(DIV3, DIV3, DIV3 * 2, DIV3)
	}, {
					new Submap(DIV3, DIV3, 0, DIV3 * 2),
					new Submap(DIV3, DIV3, DIV3, DIV3 * 2),
					new Submap(DIV3, DIV3, DIV3 * 2, DIV3 * 2)
			},
			};
	private static final float FACTOR = 16f;
	private final float width, height;
	private final float xOffset, yOffset;
	private final SubmapNormalized normalized = new SubmapNormalized(this);

	public Submap(float width, float height, float xOffset, float yOffset)
	{
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	@Override
	public float getInterpolatedU(TextureAtlasSprite sprite, float u)
	{
		return sprite.getInterpolatedU(getXOffset() + u / getWidth());
	}

	@Override
	public float getInterpolatedV(TextureAtlasSprite sprite, float v)
	{
		return sprite.getInterpolatedV(getYOffset() + v / getWidth());
	}

	@Override
	public float[] toArray()
	{
		return new float[] { getXOffset(), getYOffset(), getXOffset() + getWidth(), getYOffset() + getHeight() };
	}

	@Override
	public SubmapNormalized normalize()
	{
		return normalized;
	}

	@Override
	public ISubmap relativize()
	{
		return this;
	}

	@Override
	public float getWidth()
	{
		return width;
	}

	@Override
	public float getHeight()
	{
		return height;
	}

	@Override
	public float getXOffset()
	{
		return xOffset;
	}

	@Override
	public float getYOffset()
	{
		return yOffset;
	}

	private static class SubmapNormalized implements ISubmap
	{
		private final ISubmap parent;

		private SubmapNormalized(ISubmap parent)
		{
			this.parent = parent;
		}

		@Override
		public float getXOffset()
		{
			return parent.getXOffset() / FACTOR;
		}

		@Override
		public float getYOffset()
		{
			return parent.getYOffset() / FACTOR;
		}

		@Override
		public float getWidth()
		{
			return parent.getWidth() / FACTOR;
		}

		@Override
		public float getHeight()
		{
			return parent.getHeight() / FACTOR;
		}

		@Override
		public ISubmap relativize()
		{
			return parent;
		}

		@Override
		public ISubmap normalize()
		{
			return this;
		}

		@Override
		public float getInterpolatedU(TextureAtlasSprite sprite, float u)
		{
			return parent.getInterpolatedU(sprite, u);
		}

		@Override
		public float getInterpolatedV(TextureAtlasSprite sprite, float v)
		{
			return parent.getInterpolatedV(sprite, v);
		}

		@Override
		public float[] toArray()
		{
			return parent.toArray();
		}
	}
}
