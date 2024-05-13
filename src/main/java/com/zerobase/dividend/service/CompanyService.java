package com.zerobase.dividend.service;

import com.zerobase.dividend.exception.impl.ExistCompanyException;
import com.zerobase.dividend.exception.impl.NoCompanyException;
import com.zerobase.dividend.model.Company;
import com.zerobase.dividend.model.ScrapedResult;
import com.zerobase.dividend.persist.CompanyRepository;
import com.zerobase.dividend.persist.DividendRepository;
import com.zerobase.dividend.persist.entity.CompanyEntity;
import com.zerobase.dividend.persist.entity.DividendEntity;
import com.zerobase.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

// company service는 하나의 인스턴스만 사용되게 됨
// spring boot bean 은 singletone으로 관리됨
@Service
//Bean이 생성될때 사용하도록 설정
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        // 회사의 존재여부를 boolean 값으로 반환
        boolean exists = this.companyRepository.existsByticker(ticker);
        if (exists) {
            throw new ExistCompanyException();
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    // input 으로 ticker를 받고 배당금 정보를 Company로 반환
    // class 밖에서 호출 할 수 없도록 private 사용
    private Company storeCompanyAndDividend(String ticker) {
        // ticker를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }
        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        // company가 아닌 companyEntity가 저장 되어야함 company를 companyEntity로 변경해줘야함
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));

        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                // 여기서 사용하는 element를 다른 값으로 매핑해주기 위한 작업
                // map 함수는 collection의 element를 다른 값으로 mapping 해야될 때 사용 e는 collection의 item 하나 하나가 된다
                                                        // 여기선 getDividends의 하나가 e에 해당
                                                        .map(e -> new DividendEntity(companyEntity.getId(), e))
                                                        // 결과값을 리스트로 반환
                                                        .collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntities);

        return company;
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                                .map(e -> e.getName())
                                .collect(Collectors.toList());
    }

    public void addAutocompleteKeyword(String keyword) {
        // 회사명 저장
        this.trie.put(keyword, null);
    }

    public List<String> autocomplete(String keyword) {
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream()
                .collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {
        // 회사를 지우고 회사 이름을 반환
        var company = this.companyRepository.findByTicker(ticker)
                                .orElseThrow(() -> new NoCompanyException());

        // company에 있는 배당금 데이터 삭제
        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);

        // trie에서도 회사 이름을 지워야 함
        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }

}
