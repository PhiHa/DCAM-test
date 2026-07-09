# Opt-in developer feature toggles

The normal rule is simple: a new feature works normally and does not know this system exists.
It needs no `FeatureGate`, developer setting, or toggle declaration.

`DeveloperFeatureToggles` contains only features that the product has explicitly decided may be
disabled in developer mode. It is an optional overlay on normal navigation and application
wiring, not a registry of every feature in the app.

The current fresh-install MVP profile enables image capture, video recording, and the read-only
Files browser. Standalone audio and unfinished/demo settings surfaces default off but remain
available through the hidden developer controls.

## Always-on infrastructure

Shared infrastructure must not be controlled by product-feature toggles:

- logging and diagnostics;
- database and migrations;
- configuration loading;
- storage and crypto utilities;
- app bootstrap and composition;
- shared network, device and hardware capabilities.

For example, disabling the Cloud settings screen hides and blocks that screen. It does not stop
Loggly, disable the network stack, or stop database-backed cloud state. Likewise, disabling the
Security settings screen does not turn off configured media encryption.

## Adding an ordinary feature

Implement its domain, use cases, ports, adapters, presentation and normal navigation entry. That
is all. A menu entry with no optional developer gate is visible and routable by default.

## Later allowing developer disablement

Only when that decision is made:

1. add a stable `FeatureGate` and developer `SettingId`;
2. add one declaration to `DeveloperFeatureToggles.createDefault`;
3. attach that optional gate to the feature's menu/route, if it has one;
4. guard each external entry boundary once, such as its UI action or hardware command;
5. define safe cleanup behavior for any already-running operation.

Feature implementation code should not scatter `isEnabled()` checks internally. Start commands
may be blocked, but cleanup commands must remain available. For example, disabling video may
block a new recording while `stopRecording()` remains callable for an existing recording.

Android workers or services need their own guard only when that worker/service itself belongs
exclusively to the disableable product feature. Shared platform workers must remain always on.

## Scope

This is opt-in runtime disablement in the Android `:app` module, backed by shared contracts in
the pure-Java `:core` module. It is not Android dynamic-feature delivery.
