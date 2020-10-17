package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.screen.MoistureVaporatorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;

public class MoistureVaporatorBlockEntity extends InventoryBlockEntity implements NamedScreenHandlerFactory, Tickable
{
	// TODO: lengthen
	public static final int TIMER_LENGTH = 200;

	protected final PropertyDelegate propertyDelegate;
	private int collectionTimer;

	public MoistureVaporatorBlockEntity()
	{
		super(SwgBlocks.MoistureVaporator.Gx8BlockEntityType, 1);
		this.propertyDelegate = new PropertyDelegate()
		{
			public int get(int index)
			{
				switch (index)
				{
					case 0:
						return MoistureVaporatorBlockEntity.this.collectionTimer;
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
				}
			}

			public int size()
			{
				return 1;
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
		return super.toTag(tag);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		collectionTimer = tag.getInt("collectionTimer");
		super.fromTag(state, tag);
	}

	@Override
	public void tick()
	{
		if (this.world != null && !this.world.isClient)
		{
			ItemStack stack = getStack(0);
			if (stack.getCount() == 1 && stack.getItem() == Items.BUCKET)
			{
				if (collectionTimer <= 0)
				{
					collectionTimer = TIMER_LENGTH;

					setStack(0, new ItemStack(Items.WATER_BUCKET));
				}

				this.collectionTimer--;

				this.markDirty();
			}
			else if (collectionTimer != TIMER_LENGTH)
			{
				this.collectionTimer = TIMER_LENGTH;

				this.markDirty();
			}
		}
	}
}
