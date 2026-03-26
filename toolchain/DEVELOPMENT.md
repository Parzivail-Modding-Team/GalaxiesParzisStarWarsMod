# Development Notes

## Logical Overview

- Gradle:
  Builds and runs the standalone `toolchain/` project itself.
- Toolchain:
  Loads `../toolchain.toml`, then owns IntelliJ metadata generation and Fabric runtime bundle generation.
- IntelliJ:
  Owns compilation of PSWG modules for normal development runs.

The toolchain still reads a small amount of repo-owned version metadata from `gradle.properties`, but Gradle no longer
owns the development runtime shape.

The toolchain writes and maintains a few key outputs in the tracked repository:

- `.idea/modules.xml`
- `.idea/compiler.xml`
- `.idea/misc.xml`
- `.idea/modules/projects/...`
- `.idea/modules/launch/fabric/...`
- `.idea/libraries/...`
- `.idea/runConfigurations/Fabric_Client_*.xml`
- `.idea/runConfigurations/Fabric_Server_*.xml`
- `.idea/runConfigurations/Fabric_Datagen_*.xml`

It also writes the generated runtime bundle under `toolchain/work/instances/...`.

The tracked host repo supplies its project structure through `toolchain.toml` at the
repo root. That file is the intended boundary between the reusable toolchain engine and one host
project's module graph.

Keep `toolchain.toml` ergonomic. Prefer the shortest clear form:

- `fabric_split_sources` for normal split-source Fabric modules
- singular keys like `dependency` or `annotation_processor` when only one item is present
- resource-relative `main_mixins` / `client_mixins` instead of full `src/...` paths
- `generated_sources = true`, `generated_client_sources = true`, and `datagen = true` when using standard roots
- string dependency shorthand `<notation> @ <repository>` for one-off Maven coordinates

## Advanced Commands

Low-level commands still exist for inspection and diagnosis:

```bash
./toolchain.sh idea sync-project
./toolchain.sh fabric prepare-dev --environment client --module pswg_entrypoint
./toolchain.sh fabric prepare-dev --environment server --module pswg_entrypoint
./toolchain.sh fabric prepare-datagen --module pswg_core
./toolchain.sh fabric inspect-dev --environment client
./toolchain.sh fabric inspect-dev --environment server
./toolchain.sh mojang manifest
```

These are useful when debugging the toolchain itself.

## Updating Fabric DLI

The PSWG development workflow launches Fabric client and Fabric server through
`net.fabricmc.devlaunchinjector.Main`. When Fabric
Loader, Loom, or the dev-launch-injector changes, treat the update as a contract check across a
few focused surfaces:

- Version pins:
  `toolchain/src/main/java/com/parzivail/toolchain/fabric/FabricRuntimeResolver.java`
  owns the pinned `DEV_LAUNCH_INJECTOR_VERSION` and related Fabric runtime helper versions.
  Compare these against the vendored Loom runtime catalog before changing them.
- Contract inspection:
  `./toolchain.sh fabric inspect-dev --environment <client|server>` reports the current
  Loom-side defaults for one environment, including the DLI main class, `fabric.dli.main`,
  `fabric.dli.env`, and the generated launch config path. Run this for both environments first when
  upgrading.
- Launch assembly:
  `toolchain/src/main/java/com/parzivail/toolchain/fabric/FabricDevLaunchService.java` is the authoritative
  implementation of PSWG's direct-DLI workflow. If DLI changes required JVM properties, launch-config
  section names, or runtime main-class behavior, update this service first.
- Generated launch artifacts:
  the DLI contract is serialized into
  `toolchain/src/main/resources/com/parzivail/toolchain/templates/fabric-dev-launch.cfg`,
  `toolchain/src/main/resources/com/parzivail/toolchain/templates/intellij-run-config.xml`,
  `.idea/modules/launch/fabric/...`, and `.idea/runConfigurations/Fabric_Client_*.xml`.
  The generated outputs include `.idea/runConfigurations/Fabric_Server_*.xml`.
  Keep those generated outputs aligned with the runtime contract after any DLI update.
- Runtime classpath shaping:
  the generated IntelliJ launch module intentionally mirrors the prepared runtime classpath exactly.
  If a DLI update changes bootstrap jars, classpath ordering, or the handoff from DLI to Knot, review
  the launch-module generation and IntelliJ `classpathModifications` handling in
  `FabricDevLaunchService` before changing broader IntelliJ metadata. Verify both the prepended
  Fabric/module classpath and the vanilla baseline classpath continue to flow into the generated
  launch module; dedicated server launches now depend on bundled bootstrap libraries from Mojang's
  `META-INF/libraries.list`.

Common failure signals:

- `ClassNotFoundException: net.fabricmc.devlaunchinjector.Main`
  usually means the launch-only DLI jar fell off the generated IntelliJ runtime classpath.
- `SHA-384 digest error` followed by widespread mixin target misses
  usually means IntelliJ compile-time jars leaked into the direct runtime classpath, not a DLI ABI
  break.
- dedicated server startup fails with missing `joptsimple`, `brigadier`, or `datafixerupper`
  usually means the generated launch module no longer includes the vanilla baseline classpath or the
  Mojang bundled server libraries were not unpacked from `META-INF/libraries.list`.
- Missing or renamed `fabric.dli.*` properties
  usually means the DLI contract changed and both the inspector and launch-service property writers
  need to be updated together.

Recommended update loop:

1. inspect the current Loom contract with `fabric inspect-dev`
2. update runtime version pins in `FabricRuntimeResolver`
3. adjust `FabricDevLaunchService` and the launch templates to match the new DLI contract
4. run `./toolchain.sh dev setup-intellij` and compare the generated client and server run
   configs against the inspected Loom contract
5. verify one real IntelliJ debug launch for both environments before changing unrelated toolchain
   code

## Updating Datagen Workflow

PSWG datagen is intentionally simpler than Loom's general-purpose model:

- the toolchain only generates client-derived datagen runs
- the datagen runtime classpath is an aggregate PSWG closure rooted at the graph's development
  module
- the generated IntelliJ run configurations fence output ownership by setting both
  `fabric-api.datagen.modid` and `fabric-api.datagen.output-dir`

Contract owners:

- `toolchain/src/main/java/com/parzivail/toolchain/fabric/FabricDataGenerationService.java`
- `toolchain/src/main/java/com/parzivail/toolchain/model/ModuleSpec.java`
- `toolchain.toml`

Sensitive areas:

- `ModuleSpec.fabricModId` and `ModuleSpec.datagenOutput`
- the aggregate datagen launch-module classpath
- the generated `Fabric_Datagen_*.xml` run configurations

Common failure signals:

- datagen writes into the wrong module's `src/main/generated`
  usually means the generated run config lost either the `fabric-api.datagen.modid` or
  `fabric-api.datagen.output-dir` property.
- a downstream module's datagen run cannot see upstream data or code
  usually means the aggregate datagen launch no longer includes the full modeled PSWG dependency
  closure.
- datagen starts behaving differently from Loom after a Fabric API update
  usually means the Fabric API datagen properties or client-inheritance assumptions changed.

Recommended update loop:

1. compare Loom's current datagen setup against `FabricApiDataGeneration`
2. regenerate with `fabric prepare-datagen` or `dev setup-intellij`
3. inspect the generated `Fabric_Datagen_*.xml` files for the target module id and output dir
4. verify at least one real datagen run for an upstream module and one downstream module

## Updating IntelliJ Metadata Generation

The toolchain writes IntelliJ project files directly instead of relying on Gradle import. That makes
the workflow predictable, but it also means the toolchain depends on IntelliJ's current XML contract.

- Contract owners:
  `toolchain/src/main/java/com/parzivail/toolchain/intellij/IntelliJProjectSyncService.java`,
  `toolchain/src/main/java/com/parzivail/toolchain/fabric/FabricDevLaunchService.java`,
  and the run-config / launch-config templates under `toolchain/src/main/resources/...`.
- Sensitive areas:
  `modules.xml`, `.iml` root-manager structure, `compiler.xml` annotation-processor profiles,
  `misc.xml` project output settings, and run-config features like `classpathModifications`.
- What to compare:
  when updating IntelliJ or changing the supported IDE workflow, regenerate metadata and compare the
  generated `.idea/...` files against what IntelliJ rewrites after a successful open/build cycle.

Common failure signals:

- modules compile but IntelliJ does not see them in the right source set
  usually means `.iml` source/resource root modeling drifted.
- IntelliJ rebuild only emits `toolchain` or ignores PSWG modules
  usually means module registration or compiler-profile ownership drifted.
- generated run config opens but launches with the wrong classpath
  usually means the Application XML contract changed, especially around module binding or
  `classpathModifications`.

Recommended update loop:

1. regenerate with `dev setup-intellij`
2. reload IntelliJ and let indexing/build finish
3. compare generated `.idea` files before and after IntelliJ touches them
4. verify one rebuild and one debug launch for both client and server before changing unrelated
   metadata generation

## Updating IntelliJ Source Attachments

IntelliJ library metadata also owns source attachments for generated and downloaded libraries.

- Contract owners:
  `toolchain/src/main/java/com/parzivail/toolchain/source/MinecraftSourcesGenerator.java`,
  `toolchain/src/main/java/com/parzivail/toolchain/source/SourceAttachmentResolver.java`,
  `toolchain/src/main/java/com/parzivail/toolchain/intellij/IntelliJProjectSyncService.java`,
  `toolchain/src/main/java/com/parzivail/toolchain/fabric/FabricDevLaunchService.java`,
  and `toolchain/src/main/java/com/parzivail/toolchain/fabric/FabricDataGenerationService.java`.
- Sensitive areas:
  Vineflower API compatibility, optional Parchment metadata availability, Maven `sources`
  classifier resolution, and the current-platform launch-library generation path under
  `.idea/libraries/...`.
- What to compare:
  use `vendor/vinediff` for the general Minecraft decompile-plus-Parchment approach and the
  vendored Loom sources for how Fabric expects Minecraft sources to line up with transformed jars.

Common failure signals:

- IntelliJ can navigate into a library jar but shows decompiled bytecode instead of source
  usually means the generated `.idea/libraries/*.xml` entry lost its `SOURCES` root.
- Minecraft compile jars attach cleanly but Fabric or Mojang dependency jars do not
  usually mean Maven `sources` classifier resolution drifted for cached library paths.
- source attachments work for one platform's generated launch libraries but not another's
  usually means only the current host platform's launch-library XML was regenerated.

Recommended update loop:

1. regenerate with `dev setup-intellij`
2. inspect a transformed Minecraft library XML and one Fabric launch-library XML under `.idea/libraries`
3. confirm cached `*-sources.jar` files exist under `toolchain/work/cache/...`
4. verify IntelliJ opens attached source for one Minecraft class and one Fabric class

## Updating Mojang Metadata And Runtime Resolution

The vanilla runtime bundle depends on Mojang's version manifest, version metadata, asset index, and
library download schema remaining compatible with the toolchain models.

- Contract owners:
  `toolchain/src/main/java/com/parzivail/toolchain/mojang/MojangMetadataClient.java`,
  the `toolchain/src/main/java/com/parzivail/toolchain/mojang/model/...` records,
  and `toolchain/src/main/java/com/parzivail/toolchain/runtime/VanillaLaunchService.java`.
- Sensitive areas:
  version-manifest fields, `downloads.client/server`, library artifact paths, rule evaluation,
  asset index aliases, the client logging config descriptor, and bundled dedicated-server metadata
  such as `META-INF/versions.list`, `META-INF/main-class`, and `META-INF/libraries.list`.
- What to compare:
  use `mojang manifest`, `mojang version`, and `mojang runtime` to inspect the current live payloads
  before changing model classes or launch assembly logic.

Common failure signals:

- download or JSON parse failures against otherwise reachable Mojang endpoints
  usually mean the launcher metadata schema changed.
- runtime launch builds but missing libraries or natives fail at startup
  usually mean library `downloads` or Mojang rule handling drifted.
- dedicated server launch resolves metadata but fails with missing bootstrap classes
  usually mean bundled server-library extraction or server-classpath assembly drifted.
- missing assets or broken logging config on launch
  usually mean asset-index or logging-file handling changed.

Recommended update loop:

1. inspect live metadata with the `mojang ...` commands
2. update the Mojang model records and client parsing together
3. regenerate the runtime bundle with `dev setup-intellij`
4. verify one real game launch before touching unrelated launch code

## Updating Compile-Time Minecraft Transform Parity

IntelliJ compiles PSWG modules against toolchain-generated Minecraft jars, not raw Mojang jars. That
compile-time parity layer depends on Fabric/Loom class-tweaker behavior and local
`loom:injected_interfaces` metadata staying compatible.

- Contract owners:
  `toolchain/src/main/java/com/parzivail/toolchain/intellij/IntelliJMinecraftJarTransformer.java` and
  `toolchain/src/main/java/com/parzivail/toolchain/intellij/IntelliJDependencyResolver.java`.
- Sensitive areas:
  class-tweaker discovery from dependency jars, local `fabric.mod.json` custom metadata,
  transformed-jar cache keys, and the split between common/server and client compile jars.
- What to compare:
  use the vendored Loom sources as the primary reference when Fabric changes interface-injection,
  access-widener, or compile-time transformation behavior.

Common failure signals:

- common or client source sets suddenly lose Minecraft symbols that should exist
  usually mean the transformed compile jars no longer match the expected environment split.
- IntelliJ compiles, but runtime explodes with widespread mixin target or digest errors
  usually mean transformed compile jars leaked into the real runtime classpath.
- local interface injections stop appearing in IntelliJ
  usually mean `loom:injected_interfaces` handling drifted from Loom's current behavior.

Recommended update loop:

1. compare the vendored Loom behavior with the current transformer assumptions
2. update the transformer and dependency resolver together
3. regenerate IntelliJ metadata and rebuild the affected source sets
4. verify both editor compilation and one actual debug run
