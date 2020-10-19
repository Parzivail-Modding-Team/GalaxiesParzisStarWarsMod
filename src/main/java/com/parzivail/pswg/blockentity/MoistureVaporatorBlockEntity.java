package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgRecipeType;
import com.parzivail.pswg.recipe.VaporatorRecipe;
import com.parzivail.pswg.screen.MoistureVaporatorScreenHandler;
import com.parzivail.util.item.ItemUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;

import java.util.Optional;

public class MoistureVaporatorBlockEntity extends InventoryBlockEntity implements NamedScreenHandlerFactory, Tickable
{
	protected final PropertyDelegate propertyDelegate;
	private int collectionTimer;
	private int collectionTimerLength;

	public MoistureVaporatorBlockEntity()
	{
		super(SwgBlocks.MoistureVaporator.Gx8BlockEntityType, 2);
		this.propertyDelegate = new PropertyDelegate()
		{
			public int get(int index)
			{
				switch (index)
				{
					case 0:
						return MoistureVaporatorBlockEntity.this.collectionTimer;
					case 1:
						return MoistureVaporatorBlockEntity.this.collectionTimerLength;
					default:
						return 0;
				}
			}

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

			public int size()
			{
				return 2;
			}
		};
	}

	@Override
	public Text getDisplayName()
	{
		return new TranslatableText(Resources.container("moisture_vaporator_gx8"));
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new MoistureVaporatorScreenHandler(syncId, inv, this, this.propertyDelegate);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag)
	{
		tag.putInt("collectionTimer", collectionTimer);
		tag.putInt("collectionTimerLength", collectionTimerLength);
		return super.toTag(tag);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		collectionTimer = tag.getInt("collectionTimer");
		collectionTimerLength = tag.getInt("collectionTimerLength");
		super.fromTag(state, tag);
	}

	protected int getHydrateTime()
	{
		return this.world.getRecipeManager().getFirstMatch(SwgRecipeType.Vaporator, this, this.world).map(VaporatorRecipe::getDuration).orElse(200);
	}

	private boolean isHydratable(ItemStack stack)
	{
		Optional<VaporatorRecipe> recipeOptional = this.world.getRecipeManager().getFirstMatch(SwgRecipeType.Vaporator, new SimpleInventory(stack), this.world);
		if (!recipeOptional.isPresent())
			return false;

		return canAcceptOutput(recipeOptional.get());
	}

	private boolean canAcceptOutput(VaporatorRecipe recipe)
	{
		ItemStack outputStack = getStack(1);
		if (outputStack.isEmpty())
			return true;

		ItemStack resultStack = recipe.getOutput();

		if (resultStack.getCount() + outputStack.getCount() > outputStack.getMaxCount())
			return false;

		return ItemUtil.areStacksEqualIgnoreCount(resultStack, outputStack);
	}

	private VaporatorRecipe hydrate(ItemStack stack)
	{
		return this.world.getRecipeManager().getFirstMatch(SwgRecipeType.Vaporator, new SimpleInventory(stack), this.world).orElseThrow(RuntimeException::new);
	}

	@Override
	public void tick()
	{
		if (this.world != null && !this.world.isClient)
		{
			ItemStack stack = getStack(0);
			if (isHydratable(stack))
			{
				VaporatorRecipe recipe = hydrate(stack);

				if (collectionTimerLength == -1)
				{
					collectionTimer = collectionTimerLength = recipe.getDuration();
				}
				else if (collectionTimer <= 0)
				{
					ItemStack outputStack = getStack(1).copy();
					ItemStack resultStack = recipe.getOutput();

					stack.decrement(1);
					setStack(0, stack.copy());

					if (outputStack.isEmpty())
						setStack(1, resultStack.copy());
					else
					{
						if (!ItemUtil.areStacksEqualIgnoreCount(outputStack, resultStack) || resultStack.getCount() + outputStack.getCount() > outputStack.getMaxCount())
							throw new RuntimeException("Result and output itemstacks cannot combine!");

						outputStack.setCount(resultStack.getCount() + outputStack.getCount());
						setStack(1, outputStack);
					}
					collectionTimerLength = -1;
				}

				collectionTimer--;

				markDirty();
			}
			else if (collectionTimerLength != -1)
			{
				collectionTimer = 0;
				collectionTimerLength = -1;

				markDirty();
			}
		}
	}
}
