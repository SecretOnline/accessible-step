# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).
The versioning scheme is listed in the README.

<!-- ### Known Issues -->
<!-- ### Added -->
<!-- ### Updated -->
<!-- ### Changed -->
<!-- ### Deprecated -->
<!-- ### Removed -->
<!-- ### Fixed -->
<!-- ### Security -->

## Unreleased - DATE

### Changed

- The step mode option in the Accessibility options screen has moved to the bottom.
  - This is mostly due to Mojang removing the Auto-jump option from this screen entirely. I still saw some value in keeping it here, and the easiest place to add it is at the end.

## v2.1.1 - 2025-07-22

Forge and Neoforge 1.20.1 and 1.20.4 only.

### Fixed

- Conflict with Apothic Attributes that caused the step height to be reduced by 0.6 blocks.
  - This mod uses the Forge `STEP_HEIGHT_ADDITION` attribute on versions prior to Minecraft 1.20.6.
  - Apothic Attributes interprets this attribute as the full step height, rather than a modifier.
  - If Apotheisis or Apothic Attributes are installed, this mod will try and set the step height higher to compensate.
- This does not affect Fabric or Minecraft 1.20.6 or later.

## v2.1.0 - 2025-04-18

### Added

- Official support for NeoForge!
  - It's been a bit of a journey to reach this point, but it's finally here.
  - I should be able to release new versions for both Fabric and NeoForge as quickly as I have been (within a couple of days of a new Minecraft release).
  - There have otherwise been no functionality changes to this mod, so this update isn't neessary for those using the Fabric releases.

Note: This will be the final release (barring any urgent bug fixes) for any Minecraft 1.20 versions. This mod is basically feature complete, so this shouldn't be an issue.

## v2.0.2 - 2025-03-26

### Updated

- Updated to support 1.21.5

## v2.0.1 - 2024-01-04

### Fixed

- Fixed version number included in mod.

## v2.0.0 - 2024-01-03

### Added

- Per-server (and word) configuration.
  - You can now have different settings on different servers and worlds. This should help prevent accidental use on competitive servers with strict anti-cheat.
  - In the mod options screen (requires Mod menu), there's a new button labeled "Custom Config for World".
- Keybinding option to toggle Step Assistance mode.
  - Find it in the main keybindinds menu. It is not bound to any key by default.
  - Cycles between "Off", "Step", and "Auto-Jump".

### Updated

- The config file format has changed.
  - This mod no longer stores settings in the main `options.txt`. Instead it will create a `config/accessible-step.json` file.
  - Configuration will be migrated to the new file.
  - While this won't have much impact on how the mod works for players, it may be important for modpack developers who ship default settings for this mod in their packs.
  - Pre-defined settings for individual worlds or servers can be included.

## v1.3.4 - 2024-10-23

Minecraft 1.21.2

### Updated

- Updated to Minecraft 1.21.2
- This release is otherwise identical to v1.3.2

## v1.3.3 - 2024-08-26

Minecraft 1.21.1

### Added

- Translations
  - Italian, by [VladAndreiMorariu](https://github.com/VladAndreiMorariu)

If you are willing to translate this mod's options into more languages, then please create a pull request on GitHub.

## v1.3.2 - 2024-08-09

Minecraft 1.21.1

### Updated

- Updated to Minecraft 1.21.1
- This release is otherwise identical to v1.3.1

## v1.3.1 - 2024-07-20

Minecraft 1.21

### Changed

- Misc changes to prepare for easier backports.
  - This release is pretty much the same as the last one, but CurseForge displays the last released Minecraft version so I need to release this so it says "1.21".

## v1.3.0 - 2024-07-07

Minecraft 1.21

### Added

- Option for changing the step height while sprinting.
  - This step height will be used while the player is sprinting, or when they're holding down the sprint key.
  - For players with Mod Menu installed, this option appears in the mod's settings screen.
  - For players without Mod Menu installed, you can change the height in your `options.txt`.
- Option to change the range of the step height sliders.
  - Previously the sliders were always from 0 to 10 blocks, which made it difficult to select specific values.
  - Since it's more likely that players want step heights closer to the vanilla value, these sliders are now limited to 2.5 blocks by default.
  - The option to allow the sliders to go up to 10 blocks remains for fun.

### Changed

- Tooltips for options in the settings screen now have periods/full stops (`.`) at the end, to be consistent in style with Minecraft's tooltips.

## v1.2.0 - 2024-06-29

Minecraft 1.21

### Added

- Options for changing the step height.
  - For users with Mod Menu installed, this appears in the mod's settings screen.
  - For users without Mod Menu installed, you can change the heights in your `options.txt`.

### Updated

- Add more information to the Mod Menu page.
- Update dependencies for development.
  - This should have no effect while using the mod.

## v1.1.0 - 2024-06-14

### Updated

- Updated to Minecraft 1.21

## v1.0.1 - 2024-06-13

### Updated

- Some minor changes to prepare for the 1.21 update

## v1.0.0 - 2024-06-02

Minecraft 1.20.6

### Added

- New option to cycle between different step assistance options
- Basic settings screen for Mod Menu users
