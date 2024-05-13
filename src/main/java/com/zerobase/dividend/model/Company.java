package com.zerobase.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// getter, setter, toString 등 여러 어노테이션을 포함한 어노테이션
// 모든 모델 클래스에 data 어노테이션을 붙이는건 지양한다.
@Data
// 인스턴스 변수를 초기화 하더라도 값이 무엇을 의미하는지 알 수 있음 - 헷갈릴 여지가 줄어듬
// 순서대로 입력하지 않아도 됨
// 일부 변수만 초기화 해서 세팅해줄 수 있음
//@Builder
// Entity는 db테이블과 직접적으로 매핑되기 위한 클래스
// Model 클래스를 생성해 entity의 역할과 분리 dto 같은 역할을 수행

// 기본 생성자 생성
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    private String ticker;
    private String name;
}
