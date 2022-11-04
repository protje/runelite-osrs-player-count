package com.osrsplayercount;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class OsrsPlayerCountTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(OsrsPlayerCountPlugin.class);
		RuneLite.main(args);
	}
}
