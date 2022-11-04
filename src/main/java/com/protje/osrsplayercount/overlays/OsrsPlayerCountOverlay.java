package com.protje.osrsplayercount.overlays;

import com.google.inject.Inject;
import com.protje.osrsplayercount.scraper.OsrsPlayerCountWebScraper;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

public class OsrsPlayerCountOverlay extends OverlayPanel {
	private OsrsPlayerCountWebScraper scraper;

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

		String playerCount = this.scraper.getPlayerCount();

		try {
			addPlayerCount(playerCount);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return super.render(graphics);
	}

	/**
	 * Renders a title and the given amount of playerCount.
	 * @param playerCount Amount of players
	 */
	private void addPlayerCount(final String playerCount) {
		panelComponent.getChildren().add(TitleComponent.builder().text("Players online:").build());
		panelComponent.getChildren().add(TitleComponent.builder().text(playerCount).build());
	}
}
