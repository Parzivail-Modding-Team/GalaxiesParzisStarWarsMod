package dev.pswg.block;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.GrenadeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NerveGasGrenadeBlock extends GrenadeBlock{
	private static final int MAX_CLUSTER_SIZE = 3;

    public NerveGasGrenadeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public Item getItem() {
        return GadgetsItems.NERVE_GAS_GRENADE_ITEM;
    }

    @Override
    public EntityType<? extends GrenadeEntity> getEntityType() {
        return GadgetsEntities.NERVE_GAS_GRENADE_ENTITY;
    }

	@Override
	public int getMaxClusterSize()
	{
		return 3;
	}

	@Override
	public IntegerProperty getClusterSizeProperty()
	{
		return GrenadeBlock.CLUSTER_SIZE_3;
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		return getShape(state, level, pos, context);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		var size = state.getValue(getClusterSizeProperty());

		var shape = switch (size)
		{
			case 3 -> getTripleCollisionShape();
			case 2 -> getDoubleCollisionShape();
			default -> getSingleCollisionShape();
		};

		return super.getCollisionShape(state, level, pos, context);
	}

	@Override
    public VoxelShape getSingleShape()
    {
        return Shapes.or(
                Shapes.box(0.4375, 0, 0.4375, 0.5625, 0.125, 0.5625),
                Shapes.box(0.46875, 0.125, 0.46875, 0.53125, 0.375, 0.53125)
        );
    }
	public VoxelShape getSingleCollisionShape(){
		return getSingleShape();
	}

	@Override
    public VoxelShape getDoubleShape()
    {
        return Shapes.or(
                Shapes.box(0.46875, 0, 0.46875, 0.59375, 0.125, 0.59375),
                Shapes.box(0.5, 0.125, 0.5, 0.5625, 0.375, 0.5625)
        );
    }

	public VoxelShape getDoubleCollisionShape(){
		VoxelShape shape = Shapes.empty();
		shape = Shapes.join(shape, Shapes.box(0.5, 0.125, 0.5, 0.5625, 0.375, 0.5625), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.3125, 0, 0.3125, 0.625, 0.125, 0.625), BooleanOp.OR);

		return shape;
	}

    @Override
    public VoxelShape getTripleShape()
    {
	    return Shapes.or(
			    Shapes.box(0.46875, 0, 0.46875, 0.59375, 0.125, 0.59375),
			    Shapes.box(0.5, 0.125, 0.5, 0.5625, 0.375, 0.5625)
	    );
    }

	public VoxelShape getTripleCollisionShape(){
		VoxelShape shape = Shapes.empty();
		shape = Shapes.join(shape, Shapes.box(0.5, 0.25, 0.5, 0.5625, 0.375, 0.5625), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.3125, 0, 0.3125, 0.625, 0.25, 0.625), BooleanOp.OR);

		return shape;
	}

}
