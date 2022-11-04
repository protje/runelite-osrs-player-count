package com.osrsplayercount.scraper;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.google.inject.Inject;
import com.osrsplayercount.OsrsPlayerCountConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
/**
 * Web scraper specifically made for the OSRS home page to retrieve the amount of players
 */
public class OsrsPlayerCountWebScraper {
	static final String OSRS_HOMEPAGE_URL = "https://oldschool.runescape.com/";
	static final Pattern OSRS_PLAYER_COUNT_PATTERN = Pattern.compile("^There are currently ([\\d,]+) people playing!$");

	private WebClient webClient;
	private String playerCount;
	private int refetchInterval;

	private long lastCheckedTime;

	@Inject
	public OsrsPlayerCountWebScraper(OsrsPlayerCountConfig config) {
		this.webClient = new WebClient();
		this.webClient.getOptions().setCssEnabled(false);
		this.webClient.getOptions().setJavaScriptEnabled(false);
//		Time from the config is in seconds, so it gets multiplied it by 1000 to get it in milliseconds
		this.refetchInterval = config.refreshInterval() * 1000;
	}

	private long getTimestamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.getTime();
	}

	/**
	 * This retreive the player count taking into account the set refetchInterval
	 * @return The amount of OSRS players
	 * @throws Exception If scraping failed
	 */
	public String getPlayerCount() throws Exception {
		// We only want to re-scrape after the set amount of time
		if(this.getTimestamp() - this.lastCheckedTime >= refetchInterval) {
			extractPlayerCountFromHTML();
		}
		return this.playerCount;
	}

	/**
	 * This retrieves the player count without caring about the set refetchInterval value.
	 * @return The amount of OSRS players
	 * @throws Exception If scraping failed
	 */
	public String forceGetPlayerCount() throws Exception {
		extractPlayerCountFromHTML();
		return this.playerCount;
	}

	/**
	 * Retrieve the OSRS homepage and by using scraping and regex matching it retrieves the current amount of players.
	 * @throws Exception If scraping failed
	 */
	private void extractPlayerCountFromHTML() throws Exception {
		log.info("Scraped");
		HtmlPage page = webClient.getPage(OSRS_HOMEPAGE_URL);
		// Scraping based on the class name of the `p` tag on the OSRS homepage
		final HtmlParagraph playerCountP = (HtmlParagraph) page.getByXPath("//p[@class='player-count']").get(0);
		final String innerHTML = playerCountP.asNormalizedText();

		// By using regex matching we retrieve the correct amount of players
		Matcher m = OSRS_PLAYER_COUNT_PATTERN.matcher(innerHTML);

		if(m.find()) {
			this.playerCount = m.group(1);
			this.lastCheckedTime = getTimestamp();
		} else {
			throw new Exception("Player count was unable to be found");
		}
	}

}
