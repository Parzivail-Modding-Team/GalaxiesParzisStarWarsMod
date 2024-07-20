package com.parzivail.errorman;

import com.google.gson.Gson;
import com.parzivail.errorman.model.*;
import com.parzivail.pswg.Resources;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.client.model.compat.FmlCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.minecraft.util.crash.CrashReport;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class ErrorManager
{
	public static final Lumberjack LOG = new Lumberjack("pswg:errorman");

	private static final Gson GSON = new Gson();
	private static final URI ROLLBAR_API_ENDPOINT = URI.create("https://api.rollbar.com/api/1/item/");
	private static final String ROLLBAR_CLIENT_TOKEN = "d9b378407e67416bad536678502410d1";

	public static void onCrash(CrashReport report)
	{
		LOG.warn("Error manager triggered");

		// Do not ask to report errors if the user has disabled it
		if (!Resources.CONFIG.get().askToSendCrashReports)
			return;

		LOG.warn("Crash report system enabled");

		// Do not report errors if a new version of PSWG is known
		if (Resources.REMOTE_VERSION != null)
			return;

		LOG.warn("Running on latest version");

		// Do not ask to report errors in development environments
		if (FabricLoader.getInstance().isDevelopmentEnvironment())
			return;

		LOG.warn("Not in a development environment");

		// Do not report errors if Forge is loaded in any capacity
		if (FmlCompat.isForge())
			return;

		var cause = report.getCause();
		var atFault = false;

		var depth = 0;
		while (cause != null && depth < 10)
		{
			var trace = cause.getStackTrace();

			if (Arrays.stream(trace).map(StackTraceElement::getClassName).anyMatch(s -> s.startsWith("com.parzivail")))
			{
				atFault = true;
				break;
			}

			cause = cause.getCause();

			// Prevent infinite loops for poorly-formed
			// exception objects
			depth++;
		}

		LOG.warn("PSWG at fault: %s", atFault);

		// Do not ask to report errors if PSWG's package
		// isn't directly referenced in the stack trace
		if (!atFault)
			return;

		getConsentToDispatch(report);
	}

	private static void getConsentToDispatch(CrashReport report)
	{
		LOG.warn("Getting consent to dispatch error");

		// Do not report errors without the user's consent
		var message = "PSWG has crashed! Send this crash report to the developers?";
		if (TinyFileDialogs.tinyfd_messageBox("PSWG Error", message, "yesno", "error", true))
		{
			try
			{
				dispatchError(report);
			}
			catch (Throwable t)
			{
				LOG.error("Failed to dispatch error");
				t.printStackTrace();
			}
		}
		else
			LOG.warn("No consent, abandoning report");
	}

	private static void dispatchError(CrashReport report)
	{
		LOG.warn("Dispatching error");

		var modSet = new HashMap<String, String>();

		String pswgVersion = "<unknown>";
		for (var mod : FabricLoader.getInstance().getAllMods())
		{
			var meta = mod.getMetadata();

			if (meta.getId().equals(Resources.MODID))
				pswgVersion = meta.getVersion().getFriendlyString();

			// Exclude Fabric API modules
			var lifecycle = meta.getCustomValue("fabric-api:module-lifecycle");
			if (lifecycle != null)
				continue;

			var mo = mod.getOrigin();

			// Exclude nested mods
			if (mo.getKind() == ModOrigin.Kind.NESTED)
				continue;

			// Exclude mods without paths
			var paths = mo.getPaths();
			if (paths.isEmpty())
				continue;

			var filename = paths.get(0).getFileName().toString();
			modSet.put(meta.getId(), meta.getVersion().getFriendlyString() + '!' + filename);
		}

		LOG.warn("Found %s mods (pswg: %s)", modSet.size(), pswgVersion);

		// Do not report errors if the version field is
		// known but unpopulated
		if ("${version}".equals(pswgVersion))
			return;

		LOG.warn("Creating stack trace");

		var traceStack = new Stack<RollbarTrace>();

		var cause = report.getCause();
		var depth = 0;
		while (cause != null && depth < 10)
		{
			var exc = new RollbarException(cause.getClass().getName(), cause.getMessage());

			var trace = cause.getStackTrace();
			var frames = new RollbarFrame[trace.length];

			for (var i = 0; i < trace.length; i++)
				frames[trace.length - i - 1] = new RollbarFrame(trace[i].getFileName(), trace[i].getClassName(), trace[i].getMethodName(), trace[i].getLineNumber());

			traceStack.push(new RollbarTrace(exc, frames));

			cause = cause.getCause();

			// Prevent infinite loops for poorly-formed
			// exception objects
			depth++;
		}

		LOG.warn("Stack trace contains %s entries", traceStack.size());

		var traceChain = new RollbarTrace[traceStack.size()];
		var i = 0;
		while (!traceStack.empty())
			traceChain[i++] = traceStack.pop();

		LOG.warn("Creating Rollbar request");

		var indexOfDelimiter = pswgVersion.indexOf('+');
		if (indexOfDelimiter < 0)
			indexOfDelimiter = pswgVersion.length();

		var request = new RollbarRequest(new RollbarData(
				pswgVersion.contains("-dev-") ? "dev" : "prod",
				pswgVersion.substring(0, indexOfDelimiter),
				"client",
				"java",
				new RollbarBody(traceChain),
				new RollbarRequestInfo(modSet)
		));
		var requestJson = GSON.toJson(request);

		LOG.warn("Posting error to Rollbar");

		try (var httpclient = HttpClient.newBuilder().connectTimeout(Duration.ofMinutes(1)).build())
		{
			LOG.warn("Executing request to Rollbar");

			var response = httpclient.send(HttpRequest.newBuilder()
			                                          .uri(ROLLBAR_API_ENDPOINT)
			                                          .POST(HttpRequest.BodyPublishers.ofString(requestJson))
			                                          .setHeader("X-Rollbar-Access-Token", ROLLBAR_CLIENT_TOKEN)
			                                          .setHeader("Accept", "application/json")
			                                          .setHeader("Content-Type", "application/json")
			                                          .build(), HttpResponse.BodyHandlers.ofString());

			LOG.warn("Executed post");

			LOG.warn("Response entity: %s", response);

			if (response.body() != null)
			{
				LOG.warn("Reading entity stream");

				var rollbarResponse = GSON.fromJson(response.body(), RollbarResponse.class);

				LOG.warn("Response: %s", rollbarResponse);

				var section = report.addElement("PSWG Crash Submission Details");
				section.add("Status", rollbarResponse.err());
				if (rollbarResponse.result() != null)
					section.add("Report ID", rollbarResponse.result().uuid());
				else if (rollbarResponse.message() != null)
					section.add("Error Message", rollbarResponse.message());
			}
		}
		catch (Throwable t)
		{
			LOG.error("Failed to post error");
			t.printStackTrace();
		}
	}
}
