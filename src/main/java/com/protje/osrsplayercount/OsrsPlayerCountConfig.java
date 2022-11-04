package com.protje.osrsplayercount;

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
			description = "Controls the time in seconds before the player count gets retrieved from the OSRS home page."
	)
	@Range(
			min = 60,
			max = 3600
	)
	default int refreshInterval()
	{
		return 60;
	}
}
