package com.zerobase.dividend.persist;

import com.zerobase.dividend.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    // 회사 저장정보 확인
    // springboot 에서 정해준 규칙에 맞는 네이밍으로 시그니쳐 함수를 정의하면 스프링에서 자동적으로 메서드 내부에 코드를 생성하고 실행 시켜줌
    boolean existsByticker(String ticker);

    //JpaRepository를 상속받아 findAll 메서드를 별도로 작성하지 않아도 사용할 수 있다.

    // NullPointException 방지
    Optional<CompanyEntity> findByName(String name);

    Optional<CompanyEntity> findByTicker(String ticker);

    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);
}
