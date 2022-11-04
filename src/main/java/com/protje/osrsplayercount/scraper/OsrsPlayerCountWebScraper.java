package com.protje.osrsplayercount.scraper;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.google.inject.Inject;
import com.protje.osrsplayercount.OsrsPlayerCountConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.Callback;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
/**
 * Web scraper specifically made for the OSRS home page to retrieve the amount of players
 */
public class OsrsPlayerCountWebScraper {
	static final String OSRS_HOMEPAGE_URL = "https://oldschool.runescape.com/";
	static final Pattern OSRS_PLAYER_COUNT_PATTERN = Pattern.compile("^There are currently ([\\d,]+) people playing!$");

	@Inject
	private OsrsPlayerCountConfig config;
	private WebClient webClient;
	private String playerCount = "-";
	private long lastCheckedTime;

	@Inject
	public OsrsPlayerCountWebScraper() {
		this.webClient = new WebClient();
		this.webClient.getOptions().setCssEnabled(false);
		this.webClient.getOptions().setJavaScriptEnabled(false);
	}

	private long getTimestamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.getTime();
	}

	/**
	 * This retreive the player count taking into account the set refetchInterval
	 * @return The amount of OSRS players
	 */
	public String getPlayerCount() {
		// We only want to re-scrape after the set amount of time
		// Time from the config is in seconds, so it gets multiplied it by 1000 to get it in milliseconds
		if(this.getTimestamp() - this.lastCheckedTime >=  config.refreshInterval() * 1000) {
			CompletableFuture.runAsync(extractPlayerCountFromHTML());
			this.lastCheckedTime = getTimestamp();
		}
		return this.playerCount;
	}

	/**
	 * This retrieves the player count without caring about the set refetchInterval value.
	 * @return The amount of OSRS players
	 */
	public String forceGetPlayerCount() {
		extractPlayerCountFromHTML();
		return this.playerCount;
	}

	/**
	 * Retrieve the OSRS homepage and by using scraping and regex matching it retrieves the current amount of players.
	 * This function should be executed asynchronously so that it does not block the render function of the Runelite overlay.
	 */
	private Runnable extractPlayerCountFromHTML() {
		return () -> {
			log.debug("Scraped OSRS homepage player count");
			HtmlPage page = null;
			try {
				page = webClient.getPage(OSRS_HOMEPAGE_URL);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			// Scraping based on the class name of the `p` tag on the OSRS homepage
			final HtmlParagraph playerCountP = (HtmlParagraph) page.getByXPath("//p[@class='player-count']").get(0);
			final String innerHTML = playerCountP.asNormalizedText();

			// By using regex matching we retrieve the correct amount of players
			final Matcher m = OSRS_PLAYER_COUNT_PATTERN.matcher(innerHTML);

			if(m.find()) {
				playerCount = m.group(1);
			} else {
				log.error("Failed to scrape OSRS homepage player count");
				playerCount = "-";
			}
		};
	}

}
