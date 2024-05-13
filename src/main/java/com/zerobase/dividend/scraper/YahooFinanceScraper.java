package com.zerobase.dividend.scraper;

import com.zerobase.dividend.model.Company;
import com.zerobase.dividend.model.Dividend;
import com.zerobase.dividend.model.ScrapedResult;

import com.zerobase.dividend.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    // url을 멤버변수로 빼줄 경우 코드 유지보수 관점에서 찾기도 쉽고 수정도 어렵지 않다.
    // 상수값은 관례적으로 대문자로 변수명을 정함
    // static은 모든 인스턴스에서 접근할 수 있는 값이기 때문에 이해하지 않으면 많은 버그의 원인이 될 수 있음
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?frequency=1mo&period1=%d&period2=%d";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";

    // 하루의 초
    private static final long START_TIME = 86400;

    // 스크랩 동작 수행
    @Override
    public ScrapedResult scrap(Company company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);

        try {
            // 현재시간을 받아 초단위로 계산
            long now = System.currentTimeMillis() / 1000;
            
            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            // 연결 요청할 URL 입력
            Connection connection = Jsoup.connect(url);
            // document 인스턴스 반환
            Document document = connection.get();

//
//			// elements라는 속성을 반환
            Elements parsingDivs = document.getElementsByAttributeValue("class", "table svelte-ewueuo");
            Element tableEle = parsingDivs.get(0);

            Element tbody = tableEle.children().get(1);

            List<Dividend> dividends = new ArrayList<>();
            // children 모든 child 속성을 가져옴
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));


//                System.out.println(year + "/" + month + "/" + day + " -> " + dividend);
            }

            scrapResult.setDividends(dividends);

        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
        return scrapResult;

    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByAttributeValue("class", "svelte-3a2v0c").get(0);
//            String title = titleEle.text().split(" - ")[1].trim();
            String title = titleEle.text().split(" \\(")[0].trim();

            return new Company(ticker, title);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
