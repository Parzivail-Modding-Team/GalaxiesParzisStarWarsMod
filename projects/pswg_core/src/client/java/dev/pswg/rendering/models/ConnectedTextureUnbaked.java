package dev.pswg.rendering.models;

import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

public final class ConnectedTextureUnbaked implements BlockStateModel.Unbaked
{
	private final Identifier baseId;

	public ConnectedTextureUnbaked(Identifier connectingId)
	{
		String path = connectingId.getPath();

		if (!path.endsWith("connecting_model"))
		{
			throw new IllegalArgumentException("Connected texture model must end in connecting_model: " + connectingId);
		}

		this.baseId = Identifier.fromNamespaceAndPath(connectingId.getNamespace(), path.substring(0, path.lastIndexOf('/')));
	}

	public static boolean isConnectingModel(Identifier modelId)
	{
		return modelId.getPath().endsWith("connecting_model");
	}

	@Override
	public BlockStateModel bake(ModelBaker modelBaker)
	{
		Block block = getBlock();

		Map<ConnectedTextureModel.Face, Map<ConnectedTextureModel.TextureQuadrant, BlockStateModelPart>> noneModels = bakeQuadrantModels(modelBaker, "none");
		Map<ConnectedTextureModel.Face, Map<ConnectedTextureModel.TextureQuadrant, BlockStateModelPart>> verticalModels = bakeQuadrantModels(modelBaker, "vertical");
		Map<ConnectedTextureModel.Face, Map<ConnectedTextureModel.TextureQuadrant, BlockStateModelPart>> horizontalModels = bakeQuadrantModels(modelBaker, "horizontal");
		Map<ConnectedTextureModel.Face, Map<ConnectedTextureModel.TextureQuadrant, BlockStateModelPart>> cornerModels = bakeQuadrantModels(modelBaker, "corner");
		Map<ConnectedTextureModel.Face, Map<ConnectedTextureModel.TextureQuadrant, BlockStateModelPart>> centerModels = bakeCenterModels(modelBaker);

		BlockStateModel fallback = bakeFallback(modelBaker, noneModels);

		return new ConnectedTextureModel(block, fallback, noneModels, verticalModels, horizontalModels, cornerModels, centerModels);
	}

	private Map<ConnectedTextureModel.Face, Map<ConnectedTextureModel.TextureQuadrant, BlockStateModelPart>> bakeQuadrantModels(ModelBaker modelBaker, String suffix)
	{
		Map<ConnectedTextureModel.Face, Map<ConnectedTextureModel.TextureQuadrant, BlockStateModelPart>> result = new EnumMap<>(ConnectedTextureModel.Face.class);

		for (ConnectedTextureModel.Face face : ConnectedTextureModel.Face.values())
		{
			Map<ConnectedTextureModel.TextureQuadrant, BlockStateModelPart> faceModels = new EnumMap<>(ConnectedTextureModel.TextureQuadrant.class);
			for (ConnectedTextureModel.TextureQuadrant quadrant : ConnectedTextureModel.TextureQuadrant.values())
			{
				Identifier modelId = modelId(quadrant, suffix);
				faceModels.put(quadrant, bakeVariant(modelBaker, modelId, face));
			}

			result.put(face, faceModels);
		}

		return result;
	}

	private Map<ConnectedTextureModel.Face, Map<ConnectedTextureModel.TextureQuadrant, BlockStateModelPart>> bakeCenterModels(ModelBaker modelBaker)
	{
		Map<ConnectedTextureModel.Face, Map<ConnectedTextureModel.TextureQuadrant, BlockStateModelPart>> result = new EnumMap<>(ConnectedTextureModel.Face.class);

		for (ConnectedTextureModel.Face face : ConnectedTextureModel.Face.values())
		{
			Map<ConnectedTextureModel.TextureQuadrant, BlockStateModelPart> quadrants = new EnumMap<>(ConnectedTextureModel.TextureQuadrant.class);

			for (ConnectedTextureModel.TextureQuadrant quadrant : ConnectedTextureModel.TextureQuadrant.values())
				quadrants.put(quadrant, bakeVariant(modelBaker, modelId(quadrant, "center"), face));

			result.put(face, quadrants);
		}

		return result;
	}

	private BlockStateModelPart bakeVariant(ModelBaker modelBaker, Identifier modelId, ConnectedTextureModel.Face face)
	{
		Variant variant = new Variant(modelId);

		variant = switch (face)
		{
			case NORTH -> variant;
			case SOUTH -> variant.withYRot(Quadrant.R180);
			case EAST -> variant.withYRot(Quadrant.R270);
			case WEST -> variant.withYRot(Quadrant.R90);
			case UP -> variant.withXRot(Quadrant.R90);
			case DOWN -> variant.withXRot(Quadrant.R270);
		};

		return variant.bake(modelBaker);
	}

	private BlockStateModel bakeFallback(ModelBaker modelBaker, Map<ConnectedTextureModel.Face, Map<ConnectedTextureModel.TextureQuadrant, BlockStateModelPart>> noneModels)
	{
		BlockStateModelPart north = noneModels.get(ConnectedTextureModel.Face.NORTH).get(ConnectedTextureModel.TextureQuadrant.TOP_LEFT);

		return new BlockStateModel()
		{
			@Override
			public void collectParts(RandomSource random, java.util.List<BlockStateModelPart> parts)
			{
				parts.add(north);
			}

			@Override
			public Material.Baked particleMaterial()
			{
				return north.particleMaterial();
			}

			@Override
			public int materialFlags()
			{
				return north.materialFlags();
			}

			@Override
			public void emitQuads(QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, Predicate<Direction> cullTest)
			{
				north.emitQuads(emitter, cullTest);
			}
		};
	}

	private Block getBlock()
	{
		String path = baseId.getPath();

		String prefix = "block/connected/";

		if (!path.startsWith(prefix))
		{
			throw new IllegalArgumentException("Invalid connected texture model path: " + baseId);
		}

		String blockPath = path.substring(prefix.length());

		Identifier blockId = Identifier.fromNamespaceAndPath(baseId.getNamespace(), blockPath);

		return BuiltInRegistries.BLOCK.getOptional(blockId).orElseThrow(() -> new IllegalStateException("Unable to find connected texture block: " + blockId));
	}

	@Override
	public void resolveDependencies(ResolvableModel.Resolver resolver)
	{
		for (ConnectedTextureModel.TextureQuadrant quadrant : ConnectedTextureModel.TextureQuadrant.values())
		{
			resolver.markDependency(modelId(quadrant, "none"));
			resolver.markDependency(modelId(quadrant, "vertical"));
			resolver.markDependency(modelId(quadrant, "horizontal"));
			resolver.markDependency(modelId(quadrant, "corner"));
			resolver.markDependency(modelId(quadrant, "center"));
		}

		String blockKey =  baseId.getPath().substring(baseId.getPath().lastIndexOf('/') + 1, baseId.getPath().length());
		resolver.markDependency(Identifier.fromNamespaceAndPath(baseId.getNamespace(), baseId.getPath() + "/" + blockKey +"_connecting_model"));
	}

	private Identifier modelId(ConnectedTextureModel.TextureQuadrant quadrant, String suffix)
	{
		return Identifier.fromNamespaceAndPath(baseId.getNamespace(), baseId.getPath() + "/" + quadrant.name().toLowerCase() + "_" + suffix);
	}
}