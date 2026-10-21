# Automated gameplay tests

PSWG tests gameplay with Minecraft's own GameTest framework and Fabric's GameTest APIs. The game owns the
whole test lifecycle: server and client startup, test discovery, ticking, scheduling, timeouts, retries and
reporting. PSWG adds a small harness on top that expresses scenarios in PSWG terms and produces useful
evidence when one fails.

| Layer | Runs on | Use it for |
|---|---|---|
| Server GameTest | The headless GameTest server | Components, ammo, heat, cadence, damage, reloads, authority, multi-player ownership through connected test players |
| Client GameTest | A real client with an integrated server | Key bindings, input routing, client/server round trips, HUD and screen behaviour, rendering |

Both layers run the real game code paths: server scenarios send the same input packets a client would, and
client scenarios press the registered key bindings.

## Running the tests

Tests are compiled and launched by the toolchain. From the repository root:

```powershell
.\toolchain\toolchain.ps1 test server          # headless GameTest server, about 30 seconds
.\toolchain\toolchain.ps1 test client          # opens a Minecraft window briefly
.\toolchain\toolchain.ps1 test all             # server, then client
```

The command exits with `0` only when every requested environment passed. Useful options:

| Option | Meaning |
|---|---|
| `--module <id>` | Launch this module and its dependencies instead of `pswg_entrypoint`. |
| `--filter <selector>` | Server only. A vanilla test selector, for example `pswg_blasters_gametest:*`. |
| `--client-mod <ids>` | Client only. Comma-separated test-mod ids, for example `pswg_blasters_gametest`. |
| `--timeout <seconds>` | Wall-clock budget per environment. The process is killed when it expires. |
| `--ide-output` | Reuse IntelliJ's compiled outputs instead of compiling a fresh tree first. |
| `--quiet` | Do not echo the game's console output; it is still written to the results directory. |
| `--accept-eula` | Writes `eula=true` into the GameTest game directory. Only needed for scenarios that start a dedicated server from a client test. By passing it you agree to the [Minecraft EULA](https://aka.ms/MinecraftEULA). It is never written implicitly. |
| `--record-templates <module>` | Writes screenshot templates that do not exist yet into that module's GameTest resources. Without it a missing template fails the test. |

Use the default `pswg_entrypoint` target for the complete PSWG suite. Some core resources reference registrations
owned by another content module, so a reduced `--module` launch is only valid when that module's resource graph is
self-contained.

`dev setup-intellij` also generates the **Fabric GameTest Server** and **Fabric GameTest Client** run
configurations, so the same tests can be run and debugged from IntelliJ.

The toolchain jar must include GameTest support. When working on a local toolchain build, point the wrapper at
it with the `TOOLCHAIN_JAR` environment variable.

### Results

Every run writes to `toolchain/work/instances/fabric-gametest-<server|client>/<platform>/<version>/results/`:

| File | Content |
|---|---|
| `server-report.xml` | JUnit-style report written by Minecraft's GameTest server. |
| `client-report.xml` | JUnit-style report with one case per client scenario. |
| `<environment>-output.log` | Everything the game printed. |
| `diagnostics/<Class>.<test>.json` | One snapshot per failed test: the failure, the arena and players, every held stack with its components, and the steps the test took. |
| `screenshots/` | A screenshot of the client at the moment a client scenario failed. |

The game directory next to it is reset before every run, so a test never sees the previous run's world.

## How a module hosts tests

A module declares GameTest source sets in `toolchain.toml`:

```toml
gametest = true
gametest_dependencies = ["pswg_core"]   # GameTest source sets and libraries this module's tests may use
```

This adds two source sets that are compiled **separately**, like `main` and `client`:

| Source set | Compiled against | May use |
|---|---|---|
| `src/gametest/{java,resources}` | the common/server game | the module's `main`, its `gametest_dependencies` |
| `src/gametest_client/java` | the client game | additionally the module's `client` and the client GameTest sets |

A server test therefore cannot reference a client class by accident; the compiler refuses it.

Each module with tests ships a test-only mod descriptor at `src/gametest/resources/fabric.mod.json`. It has
its own id (`pswg_blasters_gametest`), declares the test classes under the `fabric-gametest` and
`fabric-client-gametest` entrypoints, and depends on the mod it tests. Native structures live beside it under
`data/<test mod id>/gametest/structure/`.

Test source sets are never part of a release. They are separate modules that artifact assembly never reads,
and assembly fails if a packaged descriptor declares a GameTest entrypoint or a packaged file collides with a
GameTest output.

Fabric's GameTest APIs are separate artifacts from the Fabric API bundle. `pswg_core` declares them once with
`gametest_compile_dependencies` and `gametest_client_dependencies`; every module whose tests depend on
`pswg_core` inherits them.

### Where harness code lives

`pswg_core` owns the harness, which knows nothing about any feature (`dev.pswg.gametest`). A module adds a thin
domain layer on top, for example `dev.pswg.gametest.blasters` with blaster stacks, a test player that speaks
the blaster input protocol, and blaster assertions. Keep feature knowledge out of the core harness.

## Writing a server test

A suite extends `PswgGameTestSuite` and declares its context type. Test methods are ordinary `@GameTest`
methods that receive that context:

```java
public final class BlasterGameTests extends PswgGameTestSuite<BlasterTestContext>
{
	@Override
	protected BlasterTestContext createContext(GameTestHelper helper, String testName)
	{
		return new BlasterTestContext(helper, testName);
	}

	@GameTest(structure = "pswg_gametest:firing_lane", maxTicks = 160)
	public void singleShotConsumesAmmoAndDealsDamage(BlasterTestContext test)
	{
		var shooter = test.joinShooter();
		var blaster = shooter.hold(InteractionHand.MAIN_HAND, BlasterTestStacks.loadedBlaster(test.level(), TEST_BLASTER));
		var target = test.spawnTarget(EntityTypes.COW, 8);

		shooter.lookAt(target);

		test.sequence()
		    .afterTicks(test.drawTicks(blaster) + 2)
		    .then("press the trigger", () -> shooter.press(InteractionHand.MAIN_HAND))
		    .until("the shot is committed", () -> test.assertLoadedRounds("after one shot", blaster, 23))
		    .succeed();
	}
}
```

The test id the runner reports is `<test mod id>:<snake_case class_method>`, for example
`pswg_blasters_gametest:blaster_game_tests_single_shot_consumes_ammo_and_deals_damage`.

### Time and sequences

The test method runs once, in the tick the test starts, on the server thread. **Arrange there**: create players,
stacks and targets directly. Describe the timed part with `test.sequence()`:

| Step | Meaning |
|---|---|
| `then(description, action)` | Runs once. Consecutive steps without a pause run in the **same tick**. Assertions inside fail the test immediately. |
| `afterTicks(n)` | The next step runs `n` ticks after the previous one. |
| `until(description, assertion)` | Re-checks every tick until the assertion stops throwing. If it never does, the test times out and reports the assertion's last message. |
| `succeed()` | Releases the test's resources and passes the test. |

A sequence compiles to a native `GameTestSequence`, so everything runs on the server thread. `maxTicks` bounds the
whole test. Never block, sleep or start threads in a test.

Every step is guarded: an unexpected exception fails that one test instead of escaping through the server tick,
and after the first failure the remaining steps of the same test are skipped.

### Players, packets and structures

* `test.joinPlayer(...)` joins a real `ServerPlayer` over an in-memory connection. It has a packet listener, a
  game mode and an inventory menu, and it is removed when the test ends. Deliver serverbound payloads to
  `player.connection` to take the real input path, as `BlasterTestPlayer` does.
* Tests that need room declare a native structure. `pswg_gametest:firing_lane` is an empty 24x6x7 lane. The
  structure's barrier shell supplies the floor, walls and ceiling.
* Tests of one batch run **at the same time**, each in its own structure. Do not rely on global state, and let
  the context own everything a test creates.

### Writing good expectations

* Derive numbers from the authored data instead of repeating them, so rebalancing a weapon does not break a test
  that verifies mechanics.
* Assert on state production code keeps in components and the world, and describe items with their codecs.
  Never build item NBT or JSON by hand.
* Do not seed or replace production randomness. Make the geometry guarantee the outcome instead, for example a
  target that is wider than the weapon's spread cone at the chosen distance.
* A failing test that exposes a production defect is a **finding**, not something to hide. File it in `bd`, and
  reference the issue where a test has to work around it, as `BlasterGameTests` does for `pswg-dw1`.

## Writing a client test

A client test class extends `PswgClientGameTest` and declares named scenarios. Each scenario gets a fresh
singleplayer world that is closed afterwards, input held by a scenario is released, and one failing scenario
does not stop the others:

```java
public final class BlasterClientGameTests extends PswgClientGameTest
{
	@Override
	protected void scenarios(ScenarioRegistry scenarios)
	{
		scenarios.add("cycleModeKeySelectsTheNextModeOnBothSides", test ->
		{
			giveLoadedBlaster(test);
			test.press(BlasterControls.mode);
			test.waitForServer("the server selected the next mode", server -> /* ... */ true);
			test.waitForClient("the client sees the next mode", client -> /* ... */ true);
		});
	}
}
```

* Press the **registered key bindings** (`BlasterControls.mode`, `client.options.keyAttack`), never hard-coded
  keys or direct calls into feature code.
* Read and change state through `readClient`, `readServer` and `runOnServer`, which run on the thread that owns
  the state. Waits (`waitForClient`, `waitForServer`, `waitTicks`) advance real client ticks.
* A scenario body runs on Fabric's client test thread, so blocking waits are legal there, and only there.
* Prefer asserting on state. Use screenshots for stable visual contracts: take them through
  `test.client().assertScreenshotContains(...)`, keep templates small, and record new ones with
  `--record-templates`.

## Limits

* Client tests need a GPU and a display.
* There is one real client per test.
