package com.zerobase.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
// data 어노테이션에 포함되지 않음
// 모든 필드를 초기화하는 생성자 코드를 사용할 수 있음
@AllArgsConstructor
public class ScrapedResult {

    private Company company;

    private List<Dividend> dividends;

    public ScrapedResult() {
        this.dividends = new ArrayList<>();
    }
}
