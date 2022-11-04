package com.osrsplayercount.scraper;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.google.inject.Inject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OsrsPlayerCountWebScraper {
	static final String OSRS_HOMEPAGE_URL = "https://oldschool.runescape.com/";
	static final Pattern OSRS_PLAYER_COUNT_PATTERN = Pattern.compile("^There are currently ([\\d,]+) people playing!$");

	private WebClient webClient;
	private String playerCount;

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

	public String getPlayerCount() throws Exception {
		if(this.getTimestamp() - this.lastCheckedTime > 60000) {
			extractPlayerCountFromHTML();
		}
		return this.playerCount;
	}

	private void extractPlayerCountFromHTML() throws Exception {
		HtmlPage page = webClient.getPage(OSRS_HOMEPAGE_URL);
		final HtmlParagraph playerCountP = (HtmlParagraph) page.getByXPath("//p[@class='player-count']").get(0);
		final String innerHTML = playerCountP.asNormalizedText();

		Matcher m = OSRS_PLAYER_COUNT_PATTERN.matcher(innerHTML);

		if(m.find()) {
			this.playerCount = m.group(1);
			this.lastCheckedTime = getTimestamp();
		} else {
			throw new Exception("Player count unable to be found");
		}
	}

}
