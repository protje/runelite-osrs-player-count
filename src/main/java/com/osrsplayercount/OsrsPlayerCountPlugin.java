package com.osrsplayercount;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.osrsplayercount.overlays.OsrsPlayerCountOverlay;
import com.osrsplayercount.scraper.OsrsPlayerCountWebScraper;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
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
		log.info("Example started!");
		String playerCount = scraper.getPlayerCount();
		log.info(playerCount);

		overlayManager.add(playerCountOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");

		overlayManager.remove(playerCountOverlay);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Provides
	OsrsPlayerCountConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OsrsPlayerCountConfig.class);
	}
}
