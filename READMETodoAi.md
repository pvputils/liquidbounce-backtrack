# Standalone Backtrack

Client-only Fabric mod for Minecraft **26.3**, **Java 25**, Fabric Loader **0.19.5+**, and Fabric API **0.160.5+26.3**. Install Fabric API separately beside the mod in your Minecraft instance's `mods` directory.

Only the Backtrack behavior remains. Incoming gameplay packets are delayed after attacking a target, retaining its older client position while tracking its actual position. Moving closer flushes the queue. Outgoing packets are never delayed. There is no LiquidBounce framework, browser, npm build, general Blink module, HUD, or ESP renderer. The original GPL license and behavior attribution are retained.

## Build and run

Set `JAVA_HOME` to a Java 25 JDK, then run from this repository:

- Windows: `gradlewTodoAi.bat build` or `gradlewTodoAi.bat runClient`
- Linux/macOS: `sh gradlewTodoAi build` or `sh gradlewTodoAi runClient`
- Client integration test: replace the task with `runClientGameTest`.

The installable jar is `build/libs/backtrack-1.0.0.jar` (not the sources jar). The development client uses `run/backtrackTodoAi`; it does not reuse the old LiquidBounce run directory.

New tracked files carry `TodoAi` before their extensions. The wrappers generate a minimal Gradle settings file under ignored `.gradle/bootstrapTodoAi`; resource processing generates Fabric's required `fabric.mod.json` and `en_us.json` in build output. Use these wrappers, rather than bare Gradle, to load `buildTodoAi.gradle`.

## Controls and settings

Backtrack starts disabled. **B** toggles it; rebind it in Minecraft's Controls menu. Client commands:

- `/backtrack` prints settings and queue size.
- `/backtrack on`, `/backtrack off`, `/backtrack toggle`
- `/backtrack set <setting> <value>` changes and saves a setting, e.g. `/backtrack set chance 100`.

Settings are saved in `config/backtrackTodoAi.json` in the running Minecraft instance. Changes to the JSON file take effect on restart. Invalid settings fall back to defaults with a log warning.

| Setting | Default | Allowed |
| --- | --- | --- |
| rangeMin / rangeMax | 1 / 3 blocks | 0–10; minimum ≤ maximum |
| delayMin / delayMax | 100 / 150 ms | 0–1000; minimum ≤ maximum |
| cooldownMin / cooldownMax | 0 / 10 ms | 0–2000; minimum ≤ maximum |
| trackingBuffer | 500 ms | 0–2000 |
| chance | 50 percent | 0–100; rolled per attack |
| attackWindow | 1000 ms | 0–5000 since last attack |
| pauseOnHurt | false | true / false |
| hurtThreshold | 3 ticks | 0–10 |
| targetMode | attack | attack / range |
| targetMobs | false | true / false; players remain eligible |

Distances use entity bounding boxes. Range mode selects the closest eligible entity but still requires a recent attack. Chat, player hurt sounds, and keepalive/ping packets pass immediately. Teleport, respawn, disconnect, protocol transitions, death, invalid targets, and disable flush or drop queued packets as appropriate; packets bound to an unloaded world or disconnected listener are dropped. The queue is capped at 4096 packets and manipulated only on the client thread.

## Validation

JUnit tests check FIFO ordering, exact delay boundaries, zero delay, flush, and drop behavior. A separate Fabric client test mod creates a world and exercises the actual packet-dispatch mixin, timed replay, disable flush, disabled pass-through, closer-target flush, and stale-packet dropping. Test classes and metadata are excluded from the production jar.
