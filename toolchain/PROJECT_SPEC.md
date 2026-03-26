# Project Spec

## Purpose

`toolchain.toml` is the host project's manifest for the bespoke toolchain.

The toolchain itself is the reusable engine under `toolchain/`. The tracked repo root supplies the
project-specific facts through `toolchain.toml`.

If you are bringing this toolchain into another Fabric mod later, this file is the first thing you
should expect to rewrite.

## File Location

The file lives at the tracked repo root, above `toolchain/`:

```text
MyMod/
├── toolchain.toml
├── projects/
└── toolchain/
```

The toolchain assumes:

- `toolchain/` is the toolchain project
- The parent directory of `toolchain/` is the host project root
- Module roots in `toolchain.toml` are relative to that host project root

## Top-Level Structure

The file has two main sections:

```toml
[project]
id = "examplemod"
name = "ExampleMod"
minecraft_version = "26.1-rc-1"
default_development_module = "example_entrypoint"

[modules.example_core]
kind = "fabric_split_sources"
```

`[project]` defines repo-wide facts.

`[modules.<id>]` defines one module.

## Project Fields

These fields are expected under `[project]`.

`id`

- Logical project identifier used by the build graph.
- This does not need to be the same as the Mod ID.

`name`

- IntelliJ-facing project name.
- Usually the repository name.

`minecraft_version`

- Minecraft version used by the authoritative graph.
- Example: `26.1-rc-1`

`default_development_module`

- The module injected by default when you run `dev setup-intellij` without `--module`.
- For single-module projects, this is the only module.
- For multi-module projects, this is usually the bundle or entrypoint module.

## Module Kinds

The built-in module conventions are:

`java`

- A plain Java module with:
    - `src/main/java`
    - `src/main/resources`

`fabric_split_sources`

- A normal Fabric module with:
    - `src/main/java`
    - `src/main/resources`
    - `src/client/java`
    - `src/client/resources`
- This is the preferred kind for most gameplay modules.

`fabric_resource_only`

- A Fabric module that only contributes resources and metadata.
- Useful for aggregate entrypoint or bundle modules.

## Common Module Fields

These are the fields you will use most often.

`root`

- Optional.
- Overrides the default module root.
- Default: `projects/<module-id>`

`dependency`

- Optional singular shorthand for one logical module dependency.

`dependencies`

- Optional list form for multiple logical module dependencies.

`annotation_processor`

- Optional singular shorthand for one annotation-processor module dependency.

`annotation_processors`

- Optional list form for multiple annotation-processor module dependencies.

`fabric_mod_id`

- Optional override for the module's Fabric mod id.
- If omitted for Fabric modules, the module id is used.

`artifact_id`

- Optional override for the packaged artifact name.
- Useful when the logical module id and shipped jar name differ.

`aggregate_member`

- Optional singular shorthand for one aggregate member.

`aggregate_members`

- Optional list form for multiple aggregate members.
- Used by bundle/entrypoint modules.

`main_mixins`

- Optional list of mixin config file names relative to `src/main/resources`.

`client_mixins`

- Optional list of mixin config file names relative to `src/client/resources`.

`datagen`

- Optional.
- `true` means use the default checked-in datagen root: `src/main/generated`
- A string means use a custom path relative to the module root.

`generated_sources`

- Optional.
- `true` means use the standard main annotation-processor output:
  `build/generated/sources/annotationProcessor/java/main`
- A string or list overrides that path.

`generated_client_sources`

- Optional.
- `true` means use the standard client annotation-processor output:
  `build/generated/sources/annotationProcessor/java/client`
- A string or list overrides that path.

## External Dependency Syntax

External Maven dependencies can be written in two ways.

Verbose table form:

```toml
compile_dependencies = [
	{ notation = "group:artifact:version", repository = "maven_central" },
]
```

Compact string form:

```toml
compile_dependencies = [
	"group:artifact:version @ maven_central",
]
```

The compact string form is preferred unless the table form is clearer.

Built-in repository tokens:

- `maven_central`
- `fabric`

You can also use a full repository URL.

## Defaults And Shortcuts

The spec is designed with some common shorthands in mind.

Examples:

- `kind = "fabric_split_sources"` already implies the standard `src/main` and `src/client` roots
- `annotation_processor = "framework-generator"` is usually enough for generated-source modules
- `generated_sources = true` means "use the standard AP output"
- `datagen = true` means "use `src/main/generated`"
- `main_mixins = ["example.mixins.json"]` is shorter than a full `src/main/resources/...` path

When in doubt, prefer the shorter convention-based form first. Only add path overrides when the
module really differs from the normal layout.

## Minimal Example

This is a compact but complete example for a small project:

```toml
[project]
id = "examplemod"
name = "ExampleMod"
minecraft_version = "26.1-rc-1"
default_development_module = "example_entrypoint"

[modules.framework]
kind = "java"

[modules."framework-generator"]
kind = "java"
dependency = "framework"
provided_annotation_processor_classes = [
	"dev.example.codegen.ExampleProcessor",
]
generated_sources = true
compile_dependencies = [
	"com.palantir.javapoet:javapoet:0.5.0 @ maven_central",
]

[modules.example_core]
kind = "fabric_split_sources"
dependency = "framework"
annotation_processor = "framework-generator"
fabric_mod_id = "examplemod"
main_mixins = ["example.mixins.json"]
client_mixins = ["example.client.mixins.json"]
datagen = true

[modules.example_entrypoint]
kind = "fabric_resource_only"
artifact_id = "examplemod"
fabric_mod_id = "examplemod_bundle"
aggregate_member = "example_core"
```

## Example: Bringing Up A New Mod

Suppose you are starting a new Fabric mod with this structure:

- `framework`
- `framework-generator`
- `example_core`
- `example_weapons`
- `example_entrypoint`

A practical first pass would be:

```toml
[project]
id = "examplemod"
name = "ExampleMod"
minecraft_version = "26.1-rc-1"
default_development_module = "example_entrypoint"

[modules.framework]
kind = "java"

[modules."framework-generator"]
kind = "java"
dependency = "framework"
provided_annotation_processor_classes = [
	"dev.example.codegen.ExampleProcessor",
]
generated_sources = true

[modules.example_core]
kind = "fabric_split_sources"
dependency = "framework"
annotation_processor = "framework-generator"
fabric_mod_id = "examplemod"
main_mixins = ["example.mixins.json"]
client_mixins = ["example.client.mixins.json"]
datagen = true

[modules.example_weapons]
kind = "fabric_split_sources"
dependency = "example_core"
annotation_processor = "framework-generator"
main_mixins = ["example_weapons.mixins.json"]
client_mixins = ["example_weapons.client.mixins.json"]
datagen = true

[modules.example_entrypoint]
kind = "fabric_resource_only"
artifact_id = "examplemod"
fabric_mod_id = "examplemod_bundle"
aggregate_members = ["example_weapons"]
```

Then run:

```bash
cd toolchain
./gradlew run --args="dev setup-intellij"
```

At that point the toolchain can generate:

- IntelliJ module metadata
- Fabric client/server run configs
- module-scoped datagen run configs

## Example: Adding A New Feature Module

Suppose your mod already has `example_core` and you want to add `example_droids`.

1. Create the module on disk, usually under `projects/example_droids/`
2. Add its Fabric metadata and source/resource folders
3. Add a module entry like this:

```toml
[modules.example_droids]
kind = "fabric_split_sources"
dependency = "example_core"
annotation_processor = "framework-generator"
main_mixins = ["example_droids.mixins.json"]
client_mixins = ["example_droids.client.mixins.json"]
datagen = true
```

4. Add it to the bundle module if you want it loaded by the default development run:

```toml
[modules.example_entrypoint]
kind = "fabric_resource_only"
artifact_id = "examplemod"
fabric_mod_id = "examplemod_bundle"
aggregate_members = ["example_weapons", "example_droids"]
```

5. Regenerate the workflow:

```bash
cd toolchain
./gradlew run --args="dev setup-intellij"
```

That is usually all you need for the toolchain side.

## Example: Adding Datagen To An Existing Module

If a module already exists and you want it to own checked-in generated data:

```toml
[modules.example_core]
kind = "fabric_split_sources"
dependency = "framework"
annotation_processor = "framework-generator"
main_mixins = ["example.mixins.json"]
client_mixins = ["example.client.mixins.json"]
datagen = true
```

The generated `Fabric Datagen <module>` config will then target that module's own
`src/main/generated` output.

If the module needs a custom output path:

```toml
datagen = "src/generated/datagen"
```

## Example: Resource-Only Bundle Module

Bundle modules are often simple:

```toml
[modules.example_entrypoint]
kind = "fabric_resource_only"
artifact_id = "examplemod"
fabric_mod_id = "examplemod_bundle"
aggregate_members = ["example_world", "example_items"]
```

This kind of module is useful when:

- one dev run should load several content modules together
- one packaged bundle jar should include several nested member jars
- the bundle module contributes metadata/resources but no main Java sources

## When To Override Paths

Most modules should not need custom path overrides.

Only override roots like `main_sources`, `client_sources`, `main_resources`, or `client_resources`
when:

- the module genuinely has a non-standard layout
- you are fitting the toolchain to a repo that already uses a different layout
- a generated or vendored source root must be modeled explicitly

If the module uses the standard Fabric layout, keep the TOML short and let the defaults do the
work.

## Recommended Style

For a readable file:

- keep one module block together
- prefer singular keys when only one value is present
- prefer relative mixin names over full paths
- prefer `true` shorthands for default generated/datagen roots
- use comments sparingly, only when a module is unusual
- keep bundle modules near the bottom, after the concrete content modules they aggregate

## Troubleshooting

If the toolchain rejects `toolchain.toml`:

1. check that every referenced module id is declared under `[modules]`
2. check that `default_development_module` matches a real module id
3. check that dependency shorthand uses `notation @ repository`
4. check that `main_mixins` and `client_mixins` only contain resource-relative file names
5. rerun:

```bash
cd toolchain
./gradlew run --args="dev setup-intellij"
```

If the config is structurally valid, the toolchain should move on to normal IntelliJ sync and
launch generation.
