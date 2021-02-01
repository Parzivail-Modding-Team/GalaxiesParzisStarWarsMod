package com.parzivail.pswg.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class SpeciesArgumentType implements ArgumentType<Identifier>
{
	@Override
	public Identifier parse(StringReader stringReader) throws CommandSyntaxException
	{
		return Identifier.fromCommandInput(stringReader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		return context.getSource() instanceof CommandSource ? CommandSource.suggestIdentifiers(SwgSpeciesRegistry.SPECIES.stream(), builder) : Suggestions.empty();
	}
}

