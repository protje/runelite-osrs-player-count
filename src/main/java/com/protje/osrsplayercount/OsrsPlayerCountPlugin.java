package com.protje.osrsplayercount;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.protje.osrsplayercount.overlays.OsrsPlayerCountOverlay;
import com.protje.osrsplayercount.scraper.OsrsPlayerCountWebScraper;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "OSRS Player Count"
)
public class OsrsPlayerCountPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OsrsPlayerCountConfig config;

	@Inject
	private OsrsPlayerCountWebScraper scraper;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private OsrsPlayerCountOverlay playerCountOverlay;

	@Override
	protected void startUp() throws Exception
	{
		log.info("OSRS player count plugin started!");
		overlayManager.add(playerCountOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("OSRS player count plugin stopped!");
		overlayManager.remove(playerCountOverlay);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) throws Exception {
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			// On login we want to get the amount of players
			scraper.forceGetPlayerCount();
		}
	}

	@Provides
	OsrsPlayerCountConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OsrsPlayerCountConfig.class);
	}
}