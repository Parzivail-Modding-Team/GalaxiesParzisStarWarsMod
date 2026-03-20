# PSWG Toolchain Design Brief

## Purpose

This document defines the first-draft architecture for the bespoke PSWG toolchain.

The toolchain will live inside the tracked PSWG repository under `toolchain/` and will become the authoritative development model over time. The immediate goal is narrower: define a clean graph-definition architecture and reach a first executable milestone where the toolchain can launch a vanilla Minecraft client with Mojang metadata and assets, without yet loading PSWG modules or Fabric mod content.

## First Milestone

The first concrete success criterion is:

- resolve Mojang version metadata directly
- resolve and provision vanilla client libraries
- resolve and provision vanilla assets
- assemble a correct vanilla client launch command
- launch a vanilla client successfully from the Java toolchain

Out of scope for this first milestone:

- Fabric Loader integration
- PSWG module injection
- Mixin support
- IntelliJ project generation
- datagen
- packaging

## Design Principles

- The toolchain is PSWG-specific, not a general external build system.
- The authoritative graph is defined in Java, not in Gradle and not in a generic declarative file.
- Definition concerns and execution concerns must remain separate.
- IntelliJ should be as unaware of the toolchain as practical.
- The existing Gradle workflow remains available during migration for parity testing.

## High-Level Structure

The `toolchain/` project should be a standalone runnable Java project.

Suggested first-draft package layout:

```text
toolchain/
  src/main/java/dev/pswg/toolchain/
    Main.java
    cli/
    definition/
    model/
    path/
    mojang/
    runtime/
    util/
```

Suggested responsibilities:

- `cli`
  Command parsing and user-facing entrypoints.
- `definition`
  PSWG-specific graph definition classes.
- `model`
  Passive graph data structures consumed by other subsystems.
- `path`
  Module-local path helpers and path value objects.
- `mojang`
  Mojang metadata, version manifest, asset index, and library resolution.
- `runtime`
  Launch command assembly and process execution.
- `util`
  Shared filesystem, JSON, hashing, and diagnostic helpers.

## Definition Model

The authoritative graph should be declared through Java classes that describe modules and produce passive model objects.

The preferred pattern is:

- inheritance for module family defaults
- a local builder/spec object for concise module declaration
- no execution behavior in module definition classes

### Core Types

Suggested core types:

- `BuildDefinition`
  Top-level PSWG graph definition entrypoint.
- `ModuleDefinition`
  Abstract module definition base class.
- `ModuleSpec`
  Passive accumulator used by a definition to declare a module.
- `BuildGraph`
  Fully materialized graph assembled from module specs.
- `ModulePaths`
  Module-local path builder/value helper.

### Example Shape

The intended pattern is roughly:

```java
public abstract class ModuleDefinition
{
	public final ModuleSpec define()
	{
		ModuleSpec spec = new ModuleSpec(getId(), paths());
		applyDefaults(spec);
		configure(spec);
		return spec;
	}

	public abstract String getId();

	protected ModulePaths paths()
	{
		return new ModulePaths("projects/" + getId());
	}

	protected void applyDefaults(ModuleSpec spec)
	{
	}

	protected abstract void configure(ModuleSpec spec);
}
```

```java
public abstract class ClientCommonFabricModuleDefinition extends ModuleDefinition
{
	@Override
	protected void applyDefaults(ModuleSpec spec)
	{
		spec.mainSources(spec.paths().mainJava());
		spec.clientSources(spec.paths().clientJava());
		spec.mainResources(spec.paths().mainResources());
		spec.clientResources(spec.paths().clientResources());
		spec.fabricModJson(spec.paths().mainResource("fabric.mod.json"));
	}
}
```

```java
public final class PswgCoreDefinition extends ClientCommonFabricModuleDefinition
{
	@Override
	public String getId()
	{
		return "pswg_core";
	}

	@Override
	protected void configure(ModuleSpec spec)
	{
		spec.dependency("framework");
		spec.annotationProcessor("framework-generator");
		spec.generatedSources(spec.paths().generatedAnnotationProcessorMain());
		spec.datagenOutput(spec.paths().generatedDatagen());
		spec.mixin(spec.paths().mainResource("pswg.mixins.json"));
		spec.mixin(spec.paths().mainResource("errorman.mixins.json"));
		spec.mixin(spec.paths().clientResource("pswg.client.mixins.json"));
	}
}
```

## Path Model

Raw path strings should be minimized.

The preferred path model is:

- each module owns a `ModulePaths` helper rooted at its project path
- shared conventional paths are derived from that root
- module definitions pass typed path values or path helper results into `ModuleSpec`

Example conventions to support early:

- `src/main/java`
- `src/client/java`
- `src/main/resources`
- `src/client/resources`
- `build/generated/sources/annotationProcessor/java/main`
- `src/main/generated`

## Execution Boundary

The boundary between definition and execution is strict.

Definition classes may:

- describe modules
- describe source/resource/generated path layout
- describe dependencies and logical launch inputs

Definition classes may not:

- resolve Maven artifacts
- download metadata
- inspect IntelliJ files
- launch Java processes
- package jars

Those behaviors belong in consuming subsystems such as `mojang`, `runtime`, later `intellij`, later `fabric`, and later `packaging`.

## Initial Build Graph Scope

For the first milestone, the graph only needs enough structure to support future expansion and the immediate vanilla launcher effort.

Initial implementation scope:

- define top-level `BuildDefinition`
- support a minimal project-level runtime configuration
- establish `ModuleDefinition`, `ModuleSpec`, and `ModulePaths`
- include `pswg_core` as the first modeled PSWG module
- do not require full modeling of `pswg_blasters`, `pswg_gadgets`, `pswg_entrypoint`, `toolkit`, or `mara` yet

This means the graph work should be shaped for future module coverage, but the first executable runtime path does not need full repo modeling on day one.

## Vanilla Runtime Scope

The vanilla launcher milestone should build a narrow runtime pipeline:

1. resolve the Mojang version manifest
2. resolve the selected version metadata
3. resolve asset index metadata
4. provision required libraries and client jar
5. provision assets
6. assemble the vanilla classpath
7. generate the launch arguments
8. launch the client

Suggested first commands:

- `toolchain vanilla resolve`
- `toolchain vanilla assets`
- `toolchain vanilla launch`

The exact command names are less important than keeping the pipeline observable and debuggable.

## Migration Notes

The bespoke toolchain should be developed in parallel with the current Gradle setup until parity checkpoints are available.

Immediate parity targets:

- Mojang metadata resolution
- library resolution
- asset resolution
- successful vanilla client launch

Later parity targets:

- Fabric loader launch
- PSWG module runtime injection
- annotation processor behavior
- datagen behavior
- aggregated packaging

## Deferred Areas

These areas are intentionally deferred from the first implementation slice:

- `pswg_entrypoint` aggregation behavior
- STORE/glob packaging rules
- IntelliJ project emission
- Fabric loader runtime integration
- Mixin and refmap support
- dedicated server support
- `toolkit` and `mara` project-specific handling

## Immediate Next Step

Implement the `toolchain/` project skeleton and the minimum Java model needed by `pswg-12y`, while keeping the definition model reusable for later PSWG module integration.
