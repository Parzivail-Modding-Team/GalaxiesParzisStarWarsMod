# Development Notes

## Logical Overview

- Gradle:
  Builds and runs the standalone `toolchain/` project itself.
- Toolchain:
  Owns PSWG graph definition, IntelliJ metadata generation, and Fabric runtime bundle generation.
- IntelliJ:
  Owns compilation of PSWG modules for normal development runs.

The toolchain still reads a small amount of repo-owned version metadata from `gradle.properties`, but Gradle no longer owns the development runtime shape.

The toolchain writes and maintains a few key outputs in the tracked repository:

- `.idea/modules.xml`
- `.idea/compiler.xml`
- `.idea/misc.xml`
- `.idea/modules/projects/...`
- `.idea/modules/launch/fabric/...`
- `.idea/libraries/...`
- `.idea/runConfigurations/Fabric_Client_*.xml`

It also writes the generated runtime bundle under `toolchain/work/instances/...`.

## Advanced Commands

Low-level commands still exist for inspection and diagnosis:

```bash
./gradlew run --args="idea sync-pswg"
./gradlew run --args="fabric prepare-dev --module pswg_entrypoint"
./gradlew run --args="fabric inspect-dev"
./gradlew run --args="mojang manifest"
```

These are useful when debugging the toolchain itself.

## Updating Fabric DLI

The supported PSWG development workflow launches Fabric through `net.fabricmc.devlaunchinjector.Main`
instead of through Loom's older bootstrap path. When Fabric Loader, Loom, or the dev-launch-injector
changes, treat the update as a contract check across a few focused surfaces:

- Version pins:
  `toolchain/src/main/java/dev/pswg/toolchain/fabric/FabricRuntimeResolver.java`
  currently owns the pinned `DEV_LAUNCH_INJECTOR_VERSION` and related Fabric runtime helper versions.
  Compare these against the vendored Loom runtime catalog before changing them.
- Contract inspection:
  `./gradlew run --args="fabric inspect-dev"` reports the current Loom-side defaults, including the
  DLI main class, `fabric.dli.main`, `fabric.dli.env`, and the generated launch config path. Run this
  first when upgrading.
- Launch assembly:
  `toolchain/src/main/java/dev/pswg/toolchain/fabric/FabricDevLaunchService.java` is the authoritative
  implementation of PSWG's direct-DLI workflow. If DLI changes required JVM properties, launch-config
  section names, or runtime main-class behavior, update this service first.
- Generated launch artifacts:
  the DLI contract is serialized into
  `toolchain/src/main/resources/dev/pswg/toolchain/templates/fabric-dev-launch.cfg`,
  `toolchain/src/main/resources/dev/pswg/toolchain/templates/intellij-run-config.xml`,
  `.idea/modules/launch/fabric/...`, and `.idea/runConfigurations/Fabric_Client_*.xml`.
  Keep those generated outputs aligned with the runtime contract after any DLI update.
- Runtime classpath shaping:
  the generated IntelliJ launch module intentionally mirrors the prepared runtime classpath exactly.
  If a DLI update changes bootstrap jars, classpath ordering, or the handoff from DLI to Knot, review
  the launch-module generation and IntelliJ `classpathModifications` handling in
  `FabricDevLaunchService` before changing broader IntelliJ metadata.

Common failure signals:

- `ClassNotFoundException: net.fabricmc.devlaunchinjector.Main`
  usually means the launch-only DLI jar fell off the generated IntelliJ runtime classpath.
- `SHA-384 digest error` followed by widespread mixin target misses
  usually means IntelliJ compile-time jars leaked into the direct runtime classpath, not a DLI ABI
  break.
- Missing or renamed `fabric.dli.*` properties
  usually means the DLI contract changed and both the inspector and launch-service property writers
  need to be updated together.

Recommended update loop:

1. inspect the current Loom contract with `fabric inspect-dev`
2. update runtime version pins in `FabricRuntimeResolver`
3. adjust `FabricDevLaunchService` and the launch templates to match the new DLI contract
4. run `./gradlew run --args="dev setup-intellij"` and compare the generated run config against the
   inspected Loom contract
5. verify one real IntelliJ debug launch before changing unrelated toolchain code

## Updating IntelliJ Metadata Generation

The toolchain writes IntelliJ project files directly instead of relying on Gradle import. That makes
the workflow predictable, but it also means the toolchain depends on IntelliJ's current XML contract.

- Contract owners:
  `toolchain/src/main/java/dev/pswg/toolchain/intellij/IntelliJProjectSyncService.java`,
  `toolchain/src/main/java/dev/pswg/toolchain/fabric/FabricDevLaunchService.java`,
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
4. verify one rebuild and one debug launch before changing unrelated metadata generation

## Updating Mojang Metadata And Runtime Resolution

The vanilla runtime bundle depends on Mojang's version manifest, version metadata, asset index, and
library download schema remaining compatible with the toolchain models.

- Contract owners:
  `toolchain/src/main/java/dev/pswg/toolchain/mojang/MojangMetadataClient.java`,
  the `toolchain/src/main/java/dev/pswg/toolchain/mojang/model/...` records,
  and `toolchain/src/main/java/dev/pswg/toolchain/runtime/VanillaLaunchService.java`.
- Sensitive areas:
  version-manifest fields, `downloads.client/server`, library artifact paths, rule evaluation,
  asset index aliases, and the client logging config descriptor.
- What to compare:
  use `mojang manifest`, `mojang version`, and `mojang runtime` to inspect the current live payloads
  before changing model classes or launch assembly logic.

Common failure signals:

- download or JSON parse failures against otherwise reachable Mojang endpoints
  usually mean the launcher metadata schema changed.
- runtime launch builds but missing libraries or natives fail at startup
  usually mean library `downloads` or Mojang rule handling drifted.
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
  `toolchain/src/main/java/dev/pswg/toolchain/intellij/IntelliJMinecraftJarTransformer.java` and
  `toolchain/src/main/java/dev/pswg/toolchain/intellij/IntelliJDependencyResolver.java`.
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
