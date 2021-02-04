* [TARKIN](#tarkin)
	* [Introduction](#introduction)
	* [Builder pattern](#builder-pattern)
	* [Blocks](#blocks)
		* [State](#state)
			* [Blockstate Suppliers](#blockstate-suppliers)
		* [Model](#model)
		* [Item model](#item-model)
		* [Loot table](#loot-table)
		* [Premade block builders](#premade-block-builders)
	* [Items](#items)
		* [Model](#model-1)
		* [Premade item builders](#premade-item-builders)
	* [Recipes](#recipes)
		* [Shapeless recipes](#shapeless-recipes)
		* [Shaped recipes](#shaped-recipes)
			* [Shaped recipe builder methods](#shaped-recipe-builder-methods)
				* [Grid methods](#grid-methods)
				* [Fill methods](#fill-methods)
		* [Cooking recipes](#cooking-recipes)
			* [Experience](#experience)
			* [Cooking time](#cooking-time)
		* [Pre-made recipe generators](#pre-made-recipe-generators)
	* [Models](#models)
		* [Textures](#textures)
	* [Loot Tables](#loot-tables)

# TARKIN

## Introduction

Text Asset Record-Keeping, Integration, and Normalization (TARKIN) was built to streamline adding new text-based assets to the game, including: 

* Block and item models
* Blockstates
* Recipes
* Loot tables

All assets are defined in-code using builder patterns that take registered blocks as input and generate assets automatically based on that data.

## Builder pattern

Generator classes expose a number of methods to manipulate the assets generated for a specific target. You can obtain an instance of generator classes using one of the premade static builders, specific to each class.

All generator instances are inert unless built into an asset. Generator instances are built into an asset via the

```java
<GeneratorInstance>.build(List<BuiltAsset>)
```

method, which appends the asset to a list of other assets that are ready to write to a file. TARKIN handles writing them to files automatically, so as long as you call `build(...)` on your generator instance, it will result in a file.

## Blocks

The main class relevant to creating anything related to blocks is `BlockGenerator`. This class contains a number of methods that directly create assets for specific kinds of blocks, but also provide generic alternatives if a block requires special properties.

### State
```java
BlockGenerator::state(BlockStateSupplierFunc);
BlockGenerator::state(BlockStateSupplierFunc, Identifier);
```

Provides the block with a blockstate. A number of pre-made blockstate suppliers can be used here for convenience. A second method is available with the added ability to specify the name of the model file used by the blockstate. Not every blockstate supplier may directly use this name.

#### Blockstate Suppliers

Method Name | Description
--- | ---
`BlockStateGenerator::basic` | Resulting blockstate has no special properties, i.e. directly renders the block model with no transformations or substitutions based on state.
`BlockStateGenerator::randomRotation` | Will randomly rotate the block's model, like sand or grass.
`BlockStateSupplier::column` | Will rotate based on the vanilla "log" rotation system, provided the underlying `Block` exposes the correct blockstates.
`BlockStateSupplier::slab` | Will select the apropriate model based on vanilla's slab placement system, provided the underlying `Block` exposes the correct blockstates.
`BlockStateSupplier::stairs` | Will select and rotate the apropriate model based on vanilla's stair placement system, provided the underlying `Block` exposes the correct blockstates.

### Model
```java
BlockGenerator::model(Function<Block, ModelFile>);
BlockGenerator::models(Function<Block, Collection<ModelFile>>);
```

Provides the block with one or more models. A number of pre-made model suppliers can be used here for convenience. A second method is available for model suppliers which supply multiple models for a single block, like stairs and slabs. See [Models](#models) for information on creating or using pre-made model suppliers.

### Item model
```java
BlockGenerator::itemModel(Function<Block, ModelFile>);
```

Provides the block with an item model. Not all blocks use the block model as the item model, so each block should have their item model specified through this function. See [Models](#models) for information on creating or using pre-made model suppliers.

### Loot table
```java
BlockGenerator::lootTable(Function<Block, LootTableFile>);
```

Provides the block with a loot table. See [Loot Tables](#loot-tables) for information on creating or using pre-made model suppliers.

### Premade block builders

Method Name | Inherits | Description
--- | --- | ---
`blockNoModel` | N/A | The most basic generator possible for a block. It alone generates no assets.
`blockNoModelDefaultDrops` | `blockNoModel` | Generates a block with no assets other than a loot table. Useful for when both blockstates and models are handled by an external system, like Pm3D.
`block` | `blockNoModel` | Generates a block with `BlockStateGenerator::basic`, block model `ModelFile::cube`, and item model `ModelFile::ofBlock`. Any block without drops would use this.
`basic` | `block` | Generates a block that additionally has `LootTableFile::basic`. Almost every block uses this, whether it's directly or through a builder pattern.
`basicRandomRotation` | `basic` | Generates a block with `BlockStateGenerator::randomRotation`
`leaves` | `basic` | Generates a block with `ModelFile::leaves`
`column` | `basic` | Generates a block with `BlockStateGenerator::column` and `ModelFile.column(...)` using the supplied IDs for the top and side textures
`staticColumn` | `basic` | Generates a block with `ModelFile.column(...)` using the supplied IDs for the top and side textures. Note that block will not rotate, it just has different top and side textures.
`stairs` | `basic` | Generates a block with `BlockStateGenerator.stairs(...)` and `ModelFile.stairs(...)`, using the supplied top and side textures (or the same texture for both). Ends up generating 3 different JSON models (normal, inner, and outer stairs).
`slab` | `basic` | Generates a block with `BlockStateGenerator.slab(...)` and `ModelFile.slab(...)`, using the supplied top and side textures (or the same texture for both). Ends up generating 2 different JSON models (top and bottom slabs).
`cross` | `basic` | Generates a block with `ModelFile::cross` and item model `ModelFile::item` which uses the block texture.
`tintedCross` | `basic` | Same as `cross` but assumes the texture is grayscale and will be tinted by the biome color.


## Items

Similar to `BlockGenerator`, the main class relevant to creating anything related to items is `ItemGenerator`

### Model
```java
ItemGenerator::model(Function<Item, ModelFile>);
```

Provides the item with one or more models. The function call can be repeated for additional models, although the additional model feature isn't used by any items yet (a different item class, like clocks or compasses, is required to make use of it). A number of pre-made model suppliers can be used here for convenience. See [Models](#models) for information on creating or using pre-made model suppliers.

### Premade item builders
Method Name | Inherits | Description
--- | --- | ---
`itemNoModel` | N/A | The most basic generator possible for an item. It alone generates no assets.
`basic` | `itemNoModel` | Generates an item with `ModelFile::item`
`empty` | `itemNoModel` | Generates an item with `ModelFile::empty`. Used when an item uses an external system, like Pm3D. Prevents log warnings about missing model files.

## Recipes

The `RecipeGenerator` class has multiple subclasses, each fulfilling a recipe type.

### Shapeless recipes

Shapeless recipes are generated through `RecipeGenerator.Shapeless`. Obtain an instance through the static builder method:

```java
RecipeGenerator.Shapeless.of(ItemStack, String);
```

which specifies the recipe output and the source name. The source name is the string appended to the recipe file for organization. For example, "ingot" would be preferred for creating nuggets from an ingot, and the output filename would be `xxxxx_nugget_from_ingot.json`.

Ingredients are added with the builder method

```java
RecipeGenerator.Shapeless::ingredient(ItemConvertible);
```

which accepts blocks or items.

### Shaped recipes

Shaped recipes are generated through `RecipeGenerator.Shaped`. Obtain an instance through the static builder method:

```java
RecipeGenerator.Shaped.of(ItemStack);
```

which, unlike the shaped recipe static builder, does not require a source name. This is because shaped recipe generator instances allow multiple shaped recipes to be specified, each with their own source name. This allows multiple unique shaped recipes to be specified for the same output item.

All recipes which do not fill the 3x3 grid can be placed anywhere in the grid, so long as the recipe is valid. The examples will show the recipe in the top-left corner for consistency.

#### Shaped recipe builder methods

For grid parameters, each will follow the grid pattern below, from `a` to `i`:

x     |  1  |  2  |  3  |
---   | --- | --- | --- |
**1** |  a  |  b  |  c  |
**2** |  d  |  e  |  f  |
**3** |  g  |  h  |  i  |

##### Grid methods

Directly specify each grid slot. All `a-i` parameters are `ItemConvertible`s and are shown as their grid slots for convenience.

```java
RecipeGenerator.Shaped.grid1x2(String, a, b);
RecipeGenerator.Shaped.grid1x3(String, a, b, c);
RecipeGenerator.Shaped.grid2x2(String, a, b, d, e);
RecipeGenerator.Shaped.grid2x3(String, a, b, d, e, g, h);
RecipeGenerator.Shaped.grid3x2(String, a, b, c, d, e, f);
RecipeGenerator.Shaped.grid3x3(String, a, b, c, d, e, f, g, h, i);
```

##### Fill methods

Fill methods fill the entire square of each size with the specified item.

```java
RecipeGenerator.Shaped.fill2x2(String, ItemConvertible);
RecipeGenerator.Shaped.fill3x3(String, ItemConvertible);
```

### Cooking recipes

Cooking recipes are generated through `RecipeGenerator.Cooking`. Obtain an instance through one of the static builder methods:

```java
RecipeGenerator.Cooking.smelting(ItemConvertible, ItemConvertible, String);
RecipeGenerator.Cooking.blasting(ItemConvertible, ItemConvertible, String);
RecipeGenerator.Cooking.campfire(ItemConvertible, ItemConvertible, String);
RecipeGenerator.Cooking.smoking(ItemConvertible, ItemConvertible, String);
```

The default experience is 0.35, and the default cook times are as follows:

Recipe | Cook Time (ticks) | (seconds)
--- | --- | ---
Smelting | 200 | 10
Blasting | 100 | 5
Campfire | 600 | 30
Smoking | 100 | 5

#### Experience

The experience can be changed with the following recipe generator instance builder method:

```java
RecipeGenerator.Cooking::experience(float);
```

#### Cooking time

The cooking time can be changed with the following recipe generator instance builder method:

```java
RecipeGenerator.Cooking::cookTime(int);
```

### Pre-made recipe generators

A number of pre-made recipe generators exist for convenience in the `RecipeGenerator` class.

Method | Description
--- | ---
`buildFood` | Creates smelting, smoking, and campfire recipes for the specified input and output.
`buildOreToIngot` | Creates smelting and blasting recipes from ore to ingot
`buildBidirectional` | Creates one shapeless recipe that breaks `largeUnit` into 9 `smallUnit`s, and one shaped recipe that combines 9 `smallUnit`s into one `largeUnit`. Used for nuggets, ingots, etc.
`buildMetal` | Creates smelting and blasting recipes from ore to ingot, and bidirectional recipes between ingots <-> blocks, and if a nugget item is specified, between nuggets <-> ingots

## Models

Model file suppliers are provided through `ModelFile`. Obtain an instance through one of the pre-made model suppliers.

Method | Description
--- | ---
`ModelFile::ofBlock` | Model that inherits the model of the specified block
`ModelFile::ofModel` | Model that inherits the specified model
`ModelFile::cube` | A basic cube. Used by most blocks.
`ModelFile::item(Block)` | Normal "item" model using the specified block's texture. Used for plants, etc.
`ModelFile::item(Item)` | Normal "item" model using the specified item's texture. Used by most items.
`ModelFile::empty` | No visual appearance. Used when an item uses an external model method, like Pm3D
`ModelFile::leaves` | Leaf block model using the specified block's texture. Used for leaves.
`ModelFile::column` | Column block model using the specified top and side texture.
`ModelFile::slab` | Slab block models using the specified top and side texture.
`ModelFile::stairs` | Stairs block models using the specified top and side texture.
`ModelFile::cross` | Cross model using the specified block's texture.
`ModelFile::tintedCross` | Same as `ModelFile::cross` but assumes the texture is grayscale and will be tinted by the biome color.

### Textures

Each model file can have specific textures updated on an individual basis. To specify a specific texture in a model supplier, use the following builder method:

```java
ModelFile::texture(String, Identifier);
```

## Loot Tables

Loot table suppliers are provided through `LootTableFile`. Loot table suppliers are mostly just a functional interface around the loot table's JSON structure. Multiple pools, conditions, etc. can be specified with builder methods. Only one pre-made supplier exists at the moment.

Method | Description
--- | ---
`LootTableFile::basic` | Standard loot table that allows the specified block to be dropped under the condition that it survives an explosion.