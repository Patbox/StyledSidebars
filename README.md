![Logo](https://imgur.com/Ha5mcMk.png)

# Styled Sidebars
This mod allows you to create dynamic and server side sidebars (using scoreboard) with 
all functionality you need! Supports static, scrolling (with looping) and paged sidebars 
with full support for Text Placeholder API and compatible mods.

If you have any questions, you can ask them on my [Discord](https://pb4.eu/discord)

[Also check out my other mods and project, as you might find them useful!](https://pb4.eu)

![example](https://imgur.com/jbNI9xN.gif)

## Commands (and permissions):
- `/styledsidebars` - Main command (`styledsidebars.main`, available by default)
- `/styledsidebars reload` - Reloads configuration and styles (requires `styledsidebars.reload`)
- `/styledsidebars switch <style>` or `/sidebar <style>` - Changes selected style (`styledsidebars.switch`, available by default)
- `/styledsidebars switchothers <players> <style> ` - Changes selected style of players (`styledsidebars.switch.others`)

## Configuration:
You can find config file in `./config/styled-sidebars/`.
```json5
{
  // config version, don't modify as otherwise it might get corrupted!
  "config_version_dont_modify": 1,
  "_comment": "Before changing anything, see https://github.com/Patbox/StyledSidebars#configuration",
  // Id of a style used by default. Id is determined by filename of style
  "default_style": "default",
  "messages": {
    // Message sent after player changes the sidebar
    "changed": "Your sidebar has been changed to: <gold>${style}</gold>",
    // Message sent after player trying to change sidebar to invalid one
    "unknown": "<red>This sidebar doesn't exist!</red>"
  }
}
```
### Styles:
This mod allows having multiple styles, that can be selected by players (just put them in `./config/styled-sidebars/styles/` and use `/styledsidebars reload` command)
[Formatting uses Simplified Text Format for which docs you can find here](https://placeholders.pb4.eu/user/text-format/).

```json5
{
  // Optional requirement, limiting access to this style
  // See https://github.com/Patbox/PredicateAPI/blob/1.19.3/BUILTIN.md
  "require": {/* ... */},
  // Name of config used in commands (for display)
  "config_name": "...",
  // Amount of ticks (1/20 of a second) between sending updates.
  "update_tick_time": 20,
  // Amount of updates required for page change (only if using pages)
  "page_change": 5,
  // Amount of updates required for title change (only if using more one title "frames")
  "title_change": 10,
  // Amount of updates required to scroll sidebar by one line
  "scroll_speed": 1,
  // Enables smooth looping of scrolling sidebar
  "scroll_loop": true,
  // Title of sidebar. If single it doesn't change (excluding placeholders).
  // If multiple, it will change it every X updates. Supports formatting
  "title": [
    "...",
    "...2"
  ],
  
  // Lines used for single page sidebars. Only present when there are no pages
  "lines": [
    // Simple line, always displayed (supports formatting like rest)
    "...",
    // Logical line, allows to hide them behind permissions/etc instead of always displaying
    {
      "value": [
        // Simple lines, doesn't support logical ones!
        "..."
      ],
      // Requirement for display, same system as style one
      "require": {/* ... */}
    }
  ],
  // Used for paged sidebars. Will change page every X updates (specified above)
  "pages": [
    // Same values as inside "lines" definition
    [
      "...",
      {
        /* ... */
      }
    ],
    // Another page
    [
      "..."
    ]
  ]
}
```

## Built in placeholders:
For supported placeholders list, see [Placeholder API's wiki](https://placeholders.pb4.eu/user/general/)

## Download:
- [Modrinth](https://modrinth.com/mod/styled-sidebars)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/styled-sidebars)
- [Github Releases](https://github.com/Patbox/StyledSidebars/releases)