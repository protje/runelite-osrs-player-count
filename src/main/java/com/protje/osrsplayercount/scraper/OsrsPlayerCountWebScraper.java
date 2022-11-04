package com.protje.osrsplayercount.scraper;

import com.google.inject.Inject;
import com.protje.osrsplayercount.OsrsPlayerCountConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
/**
 * Web scraper specifically made for the OSRS home page to retrieve the amount of players
 */
public class OsrsPlayerCountWebScraper {
	static final String OSRS_HOMEPAGE_URL = "https://oldschool.runescape.com/";
	static final Pattern OSRS_PLAYER_COUNT_PATTERN = Pattern.compile("<p class='player-count'>There are currently ([\\d,]+) people playing!</p>", Pattern.DOTALL);

	@Inject
	private OsrsPlayerCountConfig config;
	@Inject
	private OkHttpClient httpClient;
	private String playerCount = "-";
	private long lastCheckedTime;

	/**
	 * This retreive the player count taking into account the set refreshInterval
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

	private long getTimestamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.getTime();
	}

	/**
	 * Retrieve the OSRS homepage and by using scraping and regex matching it retrieves the current amount of players.
	 * This function should be executed asynchronously so that it does not block the render function of the Runelite overlay.
	 */
	private Runnable extractPlayerCountFromHTML() {
		return () -> {
			final Request request = new Request.Builder().url(OSRS_HOMEPAGE_URL).build();
			final Response response;
			final String content;

			try {
				response = httpClient.newCall(request).execute();
				content = response.body().string();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			// By using regex matching we retrieve the correct amount of players
			final Matcher m = OSRS_PLAYER_COUNT_PATTERN.matcher(content);

			if(m.find()) {
				log.debug("Scraped OSRS homepage player count");
				playerCount = m.group(1);
			} else {
				log.error("Failed to scrape OSRS homepage player count");
				playerCount = "-";
			}
		};
	}

}
