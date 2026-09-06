# Stack Calc

**A calculator that thinks in stacks.**

You know the moment. You need enough concrete for a build, or you're pricing a trade, and you end up with a random number like 4712 items. How many shulker boxes is that? Stack Calc answers instantly, right in game.

Press `=` and type anything. The result is broken down into shulker boxes, stacks and leftover items as you type.

```
64 * 8 + 30   =   542   =   3 stack + 30 item
5h + 12       =   8652  =   5 shulker + 4 item
```

## Features

- **Full expression parser.** `+ - * / % ^`, parentheses, and implicit multiplication like `2(3+4)`.
- **Unit suffixes.** Type `5s` instead of `320`. See the table below.
- **Live breakdown.** Shulker boxes, stacks and leftovers, colored so you can read them at a glance.
- **Syntax highlighting.** Numbers, operators, parentheses and suffixes each get their own color. Invalid characters turn red before you even finish typing.
- **Stack size toggle.** Switch between 64, 16 and 1 for items that do not stack fully.
- **Copy to clipboard.** Press Enter and the result is ready to paste.
- **`/stack` command.** Runs entirely on your client, so it works on any server, modded or vanilla.

## Suffixes

| Suffix | Meaning | Value |
| --- | --- | --- |
| `s`, `st`, `stack` | one stack | 64 |
| `h`, `sh`, `shulker` | shulker box | 1728 |
| `c`, `chest` | single chest | 1728 |
| `dc` | double chest | 3456 |
| `k` | thousand | 1000 |
| `mi`, `mil` | million | 1000000 |

## Usage

- Press `=` to open the calculator. The key is rebindable under Options, Controls, Stack Calc.
- `/stack <amount> [perstack]` breaks a plain number down in chat.
- `/stack calc <expression>` evaluates an expression in chat.

## Notes

**Client side only.** Nothing to install on the server. You can use it in single player, on a vanilla server, or on a modded one where you are not the host.

Requires **Fabric API**. Available in English and Italian, and translation pull requests are welcome.

Source code and issue tracker: https://github.com/davidesidoti/stackcalc

Licensed under MIT.
