package com.osrsplayercount;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("osrs-player-count")
public interface OsrsPlayerCountConfig extends Config
{
	@ConfigItem(
			keyName = "refreshInterval",
			name = "Refresh interval",
			description = "Controls the time in seconds before the player count gets retrieved from the OSRS home page.",
			position = 7
	)
	@Range(
			min = 60,
			max = 999
	)
	default int refreshInterval()
	{
		return 60;
	}
}
