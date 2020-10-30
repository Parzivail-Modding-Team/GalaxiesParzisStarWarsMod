package com.parzivail.pswg.mixin;

import com.parzivail.pswg.container.SwgRecipeType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientRecipeBook.class)
@Environment(EnvType.CLIENT)
public class ClientRecipeBookMixin
{
	@Inject(method = "getGroupForRecipe", at = @At(value = "HEAD"), cancellable = true)
	private static void getGroupForRecipe(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir)
	{
		RecipeType<?> type = recipe.getType();
		if (SwgRecipeType.RECIPE_TYPES.contains(type))
		{
			cir.setReturnValue(RecipeBookGroup.UNKNOWN);
		}
	}
}
