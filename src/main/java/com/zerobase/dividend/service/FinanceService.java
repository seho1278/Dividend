package com.zerobase.dividend.service;

import com.zerobase.dividend.exception.impl.NoCompanyException;
import com.zerobase.dividend.model.Company;
import com.zerobase.dividend.model.Dividend;
import com.zerobase.dividend.model.ScrapedResult;
import com.zerobase.dividend.model.constants.CacheKey;
import com.zerobase.dividend.persist.CompanyRepository;
import com.zerobase.dividend.persist.DividendRepository;
import com.zerobase.dividend.persist.entity.CompanyEntity;
import com.zerobase.dividend.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private static final Logger log = LoggerFactory.getLogger(FinanceService.class);
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    // 캐싱하기 전 요청이 자주 들어오는지 확인해보기
    // 자주 변경되는 데이터 인가?
    // 캐싱 대상이 되는 메서드에 어노테이블 작성
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
//        log.info("search company -> " + companyName);

        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                                            // 값이 없으면 인자로 넘겨주는 Exception을 넘겨줌
                                            // 없으면 Optional을 벗겨진 형태의 결과값을 반환
                                                .orElseThrow(() -> new NoCompanyException());

        // 2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환 - 1
//        List<Dividend> dividends = new ArrayList<>();
//        for (var entity : dividendEntities) {
//            dividends.add(Dividend.builder()
//                            .date(entity.getDate())
//                            .dividend(entity.getDividend())
//                            .build());
//        }

        // 3. 결과 조합 후 반환 - 2
        List<Dividend> dividends = dividendEntities.stream()
                                                    .map(e -> new Dividend(e.getDate(), e.getDividend()))
                                                    .collect(Collectors.toList());


        return new ScrapedResult(new Company(company.getTicker(), company.getName()), dividends);
    }
}
