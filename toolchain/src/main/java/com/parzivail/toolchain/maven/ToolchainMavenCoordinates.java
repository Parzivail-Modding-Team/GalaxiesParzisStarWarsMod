package com.parzivail.toolchain.maven;

import java.util.List;

/**
 * Shared Maven coordinate bundles used by the PSWG graph.
 */
public final class ToolchainMavenCoordinates
{
	/**
	 * The direct compile dependency used by the framework annotation processor.
	 */
	public static final String JAVAPOET = "com.palantir.javapoet:javapoet:0.5.0";

	/**
	 * The compile-time AutoService annotations used by the framework annotation processor.
	 */
	public static final String AUTO_SERVICE_ANNOTATIONS = "com.google.auto.service:auto-service-annotations:1.1.1";

	/**
	 * The AutoService annotation processor itself.
	 */
	public static final String AUTO_SERVICE = "com.google.auto.service:auto-service:1.1.1";

	/**
	 * The explicit AutoService processor-path closure used for IntelliJ AP configuration.
	 */
	public static final List<String> AUTO_SERVICE_PROCESSOR_PATH = List.of(
		AUTO_SERVICE,
		AUTO_SERVICE_ANNOTATIONS,
		"com.google.auto:auto-common:1.2.1",
		"com.google.guava:guava:32.0.1-jre",
		"com.google.guava:failureaccess:1.0.1",
		"com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava",
		"com.google.code.findbugs:jsr305:3.0.2",
		"org.checkerframework:checker-qual:3.33.0",
		"com.google.errorprone:error_prone_annotations:2.18.0",
		"com.google.j2objc:j2objc-annotations:2.8"
	);

	/**
	 * The JetBrains annotations library used throughout PSWG content modules.
	 */
	public static final String JETBRAINS_ANNOTATIONS = "org.jetbrains:annotations:26.0.2";

	/**
	 * The Fabric API aggregate module used by PSWG content modules at compile time.
	 */
	public static final String FABRIC_API = "net.fabricmc.fabric-api:fabric-api:${fabric_version}";

	/**
	 * The jtoml implementation bundle.
	 */
	public static final String JTOML = "io.github.wasabithumb:jtoml:1.5.0";

	/**
	 * The jtoml compile-time API artifact.
	 */
	public static final String JTOML_API = "io.github.wasabithumb:jtoml-api:1.5.0";

	/**
	 * The jtoml runtime internals artifact.
	 */
	public static final String JTOML_INTERNALS = "io.github.wasabithumb:jtoml-internals:1.5.0";

	/**
	 * Prevents construction.
	 */
	private ToolchainMavenCoordinates()
	{
	}
}
