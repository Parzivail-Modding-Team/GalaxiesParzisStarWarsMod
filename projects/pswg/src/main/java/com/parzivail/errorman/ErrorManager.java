package com.parzivail.errorman;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.crash.CrashReport;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.util.Arrays;

public class ErrorManager
{
	/*
	POST https://api.rollbar.com/api/1/item/
	X-Rollbar-Access-Token: <client token>
	Accept: application/json
	Content-Type: application/json

{
	"data": {
		"environment": "<Minecraft version>",
		"platform": "client",
		"code_version": "<PSWG version>",
		"language": "java",
		"request": {
			"mods": {
				"<modid>": "<version>",
			}
		},
		"body": {
			"trace_chain": [
				{
					"exception": {
						"class": "Caused By",
						"message": "Message"
					},
					"frames": [
						{
							"filename": "Pswg.java",
							"lineno": 789,
							"method": "load"
						}
					]
				},
				{
					"exception": {
						"class": "Outer Error",
						"message": "Message"
					},
					"frames": [
						{
							"filename": "MinecraftClient2.java",
							"lineno": 24,
							"method": "start"
						}
					]
				}
			]
		}
	}
}
	 */

	public static void onCrash(CrashReport report)
	{
		if (FabricLoader.getInstance().isDevelopmentEnvironment())
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

		if (!atFault)
			return;

		var message = "PSWG has crashed! Send this and future crash reports to the developers?";
		if (TinyFileDialogs.tinyfd_messageBox("PSWG Error", message, "yesno", "error", true))
			dispatchError(report);
	}

	private static void dispatchError(CrashReport report)
	{
		// TODO
	}
}
