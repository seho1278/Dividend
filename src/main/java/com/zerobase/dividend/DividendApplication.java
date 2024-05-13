package com.zerobase.dividend;

import com.zerobase.dividend.model.Company;
import com.zerobase.dividend.scraper.Scraper;
import com.zerobase.dividend.scraper.YahooFinanceScraper;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class DividendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DividendApplication.class, args);

		// 각각 메모리를 가지게 됨
		// trie는 서버에 하나만 유지 되어야함, 코드의 일관성 유지를 위해 bean으로 관리
//		Trie trie = new PatriciaTrie();
//		AutoComplete autoComplete = new AutoComplete(trie);
//		AutoComplete autoComplete1 = new AutoComplete(trie);
//
//		autoComplete.add("hello");
//		autoComplete1.add("hello");
//
//		// 다른 트라이의 값을 조회하면 에러가 발생함
//
//		System.out.println(autoComplete.get("hello"));
//		System.out.println(autoComplete1.get("hello"));
	}

}
