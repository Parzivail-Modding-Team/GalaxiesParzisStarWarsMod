package dev.pswg.feature.scrapping;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ScrappingTableScreenHandler extends AbstractRecipeScreenHandler
{
	private final Inventory inventory;
	private final PlayerInventory playerInventory;
	protected final World world;

	public ScrappingTableScreenHandler(ScreenHandlerType<?> screenHandlerType, int syncId, PlayerInventory playerInventory, Inventory inventory)
	{
		super(screenHandlerType, syncId);
		this.inventory = inventory;
		this.playerInventory = playerInventory;
		this.world = playerInventory.player.getWorld();
		/// Cutter
		this.addSlot(new Slot(inventory, 0, 45, 45));
		///  Spanner
		this.addSlot(new Slot(inventory, 1, 45, 67));
		///  Calibrator
		this.addSlot(new Slot(inventory, 2, 45, 89));
		///  Input
		this.addSlot(new Slot(inventory, 3, 120, 27));
		/// Output
		this.addSlot(new Slot(inventory, 4, 168, 53));
		this.addSlot(new Slot(inventory, 5, 168, 79));
		this.addSlot(new Slot(inventory, 6, 189, 53));
		this.addSlot(new Slot(inventory, 7, 189, 79));

		this.addPlayerSlots(playerInventory, 8, 84);
	}

	@Override
	public PostFillAction fillInputSlots(boolean craftAll, boolean creative, RecipeEntry<?> recipe, ServerWorld world, PlayerInventory inventory)
	{
		return PostFillAction.NOTHING;
	}

	@Override
	public void populateRecipeFinder(RecipeFinder finder)
	{

	}

	@Override
	public RecipeBookType getCategory()
	{
		return null;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return this.inventory.canPlayerUse(player);
	}
}
