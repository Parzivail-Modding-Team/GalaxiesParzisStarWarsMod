package com.parzivail.datagen.tarkin2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class Tarkin2
{
	private static final Gson GSON = new GsonBuilder().create();

	public static void main() throws Exception
	{
		var project = "pswg";
		var config = getConfiguration(project)
				.orElseThrow(() -> new Exception("Could not find project! Make sure the current working directory is the \"run\" directory within the repository!"));
	}

	private static Optional<TarkinConfiguration> getConfiguration(String project) throws IOException
	{
		var currentDirectory = Paths.get("").toAbsolutePath();
		if (!currentDirectory.getFileName().toString().equals("run"))
			return Optional.empty();

		var resourcesDir = currentDirectory
				.getParent()
				.resolve("projects")
				.resolve(project)
				.resolve("src")
				.resolve("main")
				.resolve("resources");

		var fmj = resourcesDir.resolve("fabric.mod.json");
		if (!Files.exists(fmj))
			return Optional.empty();

		try (var reader = Files.newBufferedReader(fmj))
		{
			var element = GSON.fromJson(reader, JsonObject.class);
			var id = element.get("id").getAsString();

			return Optional.of(new TarkinConfiguration(id, resourcesDir));
		}
	}
}
