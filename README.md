# OSRS player count

This is a simple runelite plugin that shows the amount of players that are currently logged in to [OSRS](https://oldschool.runescape.com/) in-game.

![image](https://user-images.githubusercontent.com/117227329/199975364-8eea4df3-f208-4efc-ad0e-50f671833966.png)

## Configuration

The plugin works out-of-the box and does not require any extra configuration. There are still some options to change if you wish to do so:

- Refresh interval
  - This is default set on 60 seconds
  - Minimum value is 60 seconds (1 minute)
  - Maximum value is 3600 seconds (1 hour)

## How does this work?

To retrieve the amount of players it does the following:
- Checks if the amount of players were already fetched within the pre-defined refresh interval.
- If enough time has passed, it fetches the [OSRS homepage](https://oldschool.runescape.com/) (async) to retrieve the player count.
  - Does some web scraping to retrieve the correct amount of players.
  - Stores this amount of players in memory
- If not, it simply retrieves what is currently stored in memory.
- Shows this in-game.
