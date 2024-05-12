## Development Environment
- Framework/Language : `Spring Boot 3` `Java 17` `JPA` `gradle`
- DB : `h2 database`, `MariaDB 10.9`
- test : `JUnit` `AssertJ`
- IDE : `intellj`
- Architecture : `Layered Architecture + Clean Architecture`

## Introduce
- `e-커머스 상품 주문 서비스`에서 유저의 결제 시스템에 동시성 제어 중심으로 개발.
- 이전에 해봤던 주문 시스템에 비관적 락을 적용한 부분도 추후에 적용한 후, MSA 구조로 변환 시 이벤트 드리븐 아키텍처로 변형할 것.

## 동시성 제어
- 포인트 충전 및 사용(결제)
  - Optimistic Lock(낙관적 락) 사용
  - Distributed Lock(분산 락) 사용 
    ### Redisson
    - 락 획득 방식 : pub/sub 방식 
    - 락이 해제될 때마다 subscribe 클라이언트에게 "락 시도 가능" 알림을 보낸다. <br>
      => 클라이언트에서 락 획득 실패 시 지속적으로 락 시도 요청이 필요 없어짐 <br>
      => spin Lock 방식의 Lettuce에 비해 Redis에 부하 감소
    - RLock: 락을 위한 인터페이스로 락 사용을 쉽게 할 수 있다.
    - AOP로 구현하는 이유
      - 분산 락 처리 로직을 비즈니스 로직과 분리해서 가독성 증가
      - 재사용성 고려
      - 확장에 유연성 증가