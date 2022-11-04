package com.osrsplayercount.overlays;

import com.google.inject.Inject;
import com.osrsplayercount.OsrsPlayerCountPlugin;
import com.osrsplayercount.scraper.OsrsPlayerCountWebScraper;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

public class OsrsPlayerCountOverlay extends OverlayPanel {
	private final OsrsPlayerCountWebScraper scraper;

	@Inject
	private OsrsPlayerCountOverlay(OsrsPlayerCountWebScraper scraper)
	{
		setPosition(OverlayPosition.TOP_RIGHT);
		this.scraper = scraper;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		panelComponent.getChildren().clear();

		try {
			addPlayerCount(this.scraper.getPlayerCount());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return super.render(graphics);
	}

	/**
	 * Renders a title and the given amount of the player count.
	 * @param playerCount Amount of players
	 */
	private void addPlayerCount(final String playerCount) {
		panelComponent.getChildren().add(TitleComponent.builder().text("Players online:").build());
		panelComponent.getChildren().add(TitleComponent.builder().text(playerCount).build());
	}
}
