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
		this.refetchInterval = config.refreshInterval();
	}

	private long getTimestamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.getTime();
	}

	public String getPlayerCount() throws Exception {
		// We only want to re-scrape after the set amount of time
		if(this.getTimestamp() - this.lastCheckedTime >= refetchInterval) {
			extractPlayerCountFromHTML();
		}
		return this.playerCount;
	}

	public String forceGetPlayerCount() throws Exception {
		extractPlayerCountFromHTML();
		return this.playerCount;
	}

	private void extractPlayerCountFromHTML() throws Exception {
		log.info("Scraped");
		HtmlPage page = webClient.getPage(OSRS_HOMEPAGE_URL);
		final HtmlParagraph playerCountP = (HtmlParagraph) page.getByXPath("//p[@class='player-count']").get(0);
		final String innerHTML = playerCountP.asNormalizedText();

		// By using regex matching we retreive the correct amount of players
		Matcher m = OSRS_PLAYER_COUNT_PATTERN.matcher(innerHTML);

		if(m.find()) {
			this.playerCount = m.group(1);
			this.lastCheckedTime = getTimestamp();
		} else {
			throw new Exception("Player count unable to be found");
		}
	}

}
