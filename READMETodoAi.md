# Original LiquidBounce Backtrack on Fabric

Minecraft **26.3**, **Java 25**. This uses LiquidBounce's original Kotlin Backtrack module, not a rewritten approximation. Its module, shared Blink packet queue, position tracker, Chronometer, Box/Model/Wireframe/None ESP implementations, wireframe pose geometry, targeting, and supporting event/config/render systems are retained from upstream.

Backtrack is the only registered module. Other source files needed by the retained framework remain available as dependencies; this is deliberately not a source-only extraction. Browser startup, browser theme building, online marketplace/account initialization, and deep-learning startup are omitted. No npm or Node installation is required to build or run.

## Build and launch

Set `JAVA_HOME` to a Java 25 JDK. On Windows run `gradlew.bat build` and `gradlew.bat runClient`. On Linux/macOS use `./gradlew build` and `./gradlew runClient`.

Install `build/libs/liquidbounce-0.40.1.jar` in a Fabric Loader 0.19.5+ Minecraft 26.3 instance alongside Fabric API 0.160.5+26.3 and Fabric Language Kotlin matching the version in `gradle/libs.versions.toml`. The mod retains its upstream `liquidbounce` ID to preserve internal resource/config references, and displays as Backtrack Fabric. Its development client uses `run/backtrack-originalTodoAi`, separate from previous run directories.

## Configuration menu

Press **O** (rebindable in Minecraft Controls), run `/backtrack config`, or use Backtrack Fabric's configure button in Mod Menu. The native menu exposes every original Backtrack setting, nested hurt-time options, all ESP modes and their colors/lighting, combat/visual target filters, shared Blink visuals, friend names/aliases and attack cancellation, and the original module key/action/modifier binding. Minecraft Controls also exposes the Fabric toggle/menu bindings.

Changes save immediately through the existing configuration system. Numeric/range editors show allowed bounds and reject invalid or reversed ranges. Colors accept `#RRGGBB` or `#AARRGGBB` (alpha first). Value editors offer reset-to-default and cancel; menus paginate to fit the window. Inactive visual modes remain configurable.

## Controls and original settings

**B** toggles Backtrack and can be rebound in Minecraft Controls. `/backtrack on`, `/backtrack off`, `/backtrack toggle` and `/backtrack` control/report the original module. `/backtrackvisual` toggles visuals, and `/backtrackvisual box|model|wireframe|none` selects an original ESP mode. The original command system remains available:

- `.toggle Backtrack`
- `.bind Backtrack b`
- `.value set <setting-path> <value>`: use Tab completion for exact paths and allowed values.
- `.targets` configures the original global target filters; `.friend` manages friend exclusions.

The original Backtrack settings are Range, Delay, NextBacktrackDelay, TrackingBuffer, Chance, PauseOnHurtTime/HurtTime, TargetMode (Attack or Range), LastAttackTimeToWork, and Esp. All defaults, validation, target selection, queue timing and packet exceptions come from the upstream module unchanged.

ESP choices are **Box**, **Model**, **Wireframe** (upstream default), and **None**. Box and Wireframe retain their original Color/OutlineColor settings; Model retains OutlineColor and LightPercent. Select None to hide the visuals independently of Backtrack. These render at the tracked actual position using the original render pipeline; no replacement cyan/orange overlay is used. Changes persist in the upstream `LiquidBounce` configuration folder inside the Minecraft instance.

## Validation

`gradlew build` runs the restored unit tests, Detekt, ABI and access-widener checks. `gradlew runClientGameTest` launches a fresh Fabric world and checks that only Backtrack is registered, no browser starts, original incoming packets queue/replay, disable flushes packets, and all four original ESP choices can be selected and rendered. The same world test exercises native menu widgets, invalid ranges, configuration reload, color editing, target toggles and friend add/alias/remove. Screenshots cover the menu, value editor, target filters and three visible ESP modes. Test code is a separate mod and is excluded from the production jar.

The original source can be checked against upstream commit `0a80fecbdd3dedff68235454bd34ef71cd7596d3`. Startup/build adaptations are marked with Codex comments; new bridge/test/document files use TodoAi names. Original restored files retain their upstream names and GPL attribution.
