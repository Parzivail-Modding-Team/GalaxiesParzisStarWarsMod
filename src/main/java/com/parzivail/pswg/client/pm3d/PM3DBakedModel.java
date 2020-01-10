package com.parzivail.pswg.client.pm3d;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class PM3DBakedModel implements BakedModel, FabricBakedModel
{
	private Mesh mesh;
	private ModelTransformation transformation;

	public PM3DBakedModel(Mesh mesh, ModelTransformation transformation)
	{
		this.mesh = mesh;
		this.transformation = transformation;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState blockState, Direction direction, Random random)
	{
		List<BakedQuad>[] bakedQuads = ModelHelper.toQuadLists(mesh);
		return bakedQuads[direction == null ? 6 : direction.getId()];
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context)
	{
		if (mesh != null)
		{
			context.meshConsumer().accept(mesh);
		}
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context)
	{
		if (mesh != null)
		{
			context.meshConsumer().accept(mesh);
		}
	}

	@Override
	public boolean useAmbientOcclusion()
	{
		return true;
	}

	@Override
	public boolean hasDepthInGui()
	{
		return true;
	}

	@Override
	public boolean isBuiltin()
	{
		return false;
	}

	@Override
	public Sprite getSprite()
	{
		return new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("missingno")).getSprite();
	}

	@Override
	public ModelTransformation getTransformation()
	{
		return transformation;
	}

	@Override
	public ModelItemPropertyOverrideList getItemPropertyOverrides()
	{
		return ModelItemPropertyOverrideList.EMPTY;
	}

	@Override
	public boolean isVanillaAdapter()
	{
		return false;
	}
}
