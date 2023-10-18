package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgRecipeType;
import com.parzivail.pswg.recipe.VaporatorRecipe;
import com.parzivail.pswg.screen.MoistureVaporatorScreenHandler;
import com.parzivail.util.blockentity.InventoryBlockEntity;
import com.parzivail.util.item.ItemUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MoistureVaporatorBlockEntity extends InventoryBlockEntity implements NamedScreenHandlerFactory, RecipeInputProvider
{
	protected final PropertyDelegate propertyDelegate;
	private int collectionTimer;
	private int collectionTimerLength;

	public MoistureVaporatorBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.MoistureVaporator.Gx8BlockEntityType, pos, state, 2);
		this.propertyDelegate = new PropertyDelegate()
		{
			@Override
			public int get(int index)
			{
				return switch (index)
						{
							case 0 -> MoistureVaporatorBlockEntity.this.collectionTimer;
							case 1 -> MoistureVaporatorBlockEntity.this.collectionTimerLength;
							default -> 0;
						};
			}

			@Override
			public void set(int index, int value)
			{
				switch (index)
				{
					case 0:
						MoistureVaporatorBlockEntity.this.collectionTimer = value;
					case 1:
						MoistureVaporatorBlockEntity.this.collectionTimerLength = value;
				}
			}

			@Override
			public int size()
			{
				return 2;
			}
		};
	}

	@Override
	public Text getDisplayName()
	{
		return Text.translatable(Resources.container("moisture_vaporator_gx8"));
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new MoistureVaporatorScreenHandler(syncId, inv, this, this.propertyDelegate);
	}

	@Override
	public void writeNbt(NbtCompound tag)
	{
		tag.putInt("collectionTimer", collectionTimer);
		tag.putInt("collectionTimerLength", collectionTimerLength);
		super.writeNbt(tag);
	}

	@Override
	public void readNbt(NbtCompound tag)
	{
		collectionTimer = tag.getInt("collectionTimer");
		collectionTimerLength = tag.getInt("collectionTimerLength");
		super.readNbt(tag);
	}

	protected int getHydrateTime()
	{
		assert this.world != null;
		return this.world.getRecipeManager().getFirstMatch(SwgRecipeType.Vaporator, this, this.world).map(RecipeEntry::value).map(VaporatorRecipe::getDuration).orElse(200);
	}

	private boolean isHydratable(ItemStack stack)
	{
		assert this.world != null;
		var recipeOptional = this.world.getRecipeManager().getFirstMatch(SwgRecipeType.Vaporator, new SimpleInventory(stack), this.world);
		return recipeOptional.map(RecipeEntry::value).filter(this::canAcceptOutput).isPresent();
	}

	private boolean canAcceptOutput(VaporatorRecipe recipe)
	{
		var outputStack = getStack(1);
		if (outputStack.isEmpty())
			return true;

		var resultStack = recipe.getResult(world.getRegistryManager());

		if (resultStack.getCount() + outputStack.getCount() > outputStack.getMaxCount())
			return false;

		return ItemUtil.areStacksEqualIgnoreCount(resultStack, outputStack);
	}

	private VaporatorRecipe hydrate(ItemStack stack)
	{
		assert this.world != null;
		return this.world.getRecipeManager().getFirstMatch(SwgRecipeType.Vaporator, new SimpleInventory(stack), this.world).orElseThrow(RuntimeException::new).value();
	}

	public static <T extends BlockEntity> void serverTick(World world, BlockPos blockPos, BlockState blockState, T be)
	{
		if (!(be instanceof MoistureVaporatorBlockEntity t))
			return;

		var stack = t.getStack(0);
		if (t.isHydratable(stack))
		{
			var recipe = t.hydrate(stack);

			if (t.collectionTimerLength == -1)
			{
				t.collectionTimer = t.collectionTimerLength = recipe.getDuration();
			}
			else if (t.collectionTimer <= 0)
			{
				var outputStack = t.getStack(1).copy();
				var resultStack = recipe.getResult(world.getRegistryManager());

				stack.decrement(1);
				t.setStack(0, stack.copy());

				if (outputStack.isEmpty())
					t.setStack(1, resultStack.copy());
				else
				{
					if (!ItemUtil.areStacksEqualIgnoreCount(outputStack, resultStack) || resultStack.getCount() + outputStack.getCount() > outputStack.getMaxCount())
						throw new RuntimeException("Result and output itemstacks cannot combine!");

					outputStack.setCount(resultStack.getCount() + outputStack.getCount());
					t.setStack(1, outputStack);
				}
				t.collectionTimerLength = -1;
			}

			t.collectionTimer--;

			t.markDirty();
		}
		else if (t.collectionTimerLength != -1)
		{
			t.collectionTimer = -1;
			t.collectionTimerLength = -1;

			t.markDirty();
		}
	}

	@Override
	public void provideRecipeInputs(RecipeMatcher finder)
	{

	}
}
