## Development Environment
- Framework/Language : `Spring Boot 3` `Java 17` `JPA` `gradle`
- DB : `h2 database`, `MariaDB 10.9`
- test : `JUnit` `AssertJ`
- IDE : `intellj`
- Architecture : `Layered Architecture + Clean Architecture`

## Introduce
- `e-커머스 상품 주문 서비스`에서 유저의 결제 시스템에 동시성 제어 중심으로 개발.
- 포인트 충전 시 동시성 제어를 위해 낙관적 락 먼저 테스트 후 @Version 제거 후 `분산 락`을 적용한 리팩토링. 
- 이전에 해봤던 주문 시스템에 비관적 락을 적용한 부분도 추후에 적용한 후, MSA 구조로 변환 시 이벤트 드리븐 아키텍처로 변형할 것.

## 동시성 제어
- 포인트 충전 및 사용
  ### Optimistic Lock(낙관적 락)
    - 분산 환경에서 데이터 무결성 유지 어려움.
    - 동시에 요청이 올 경우가 적은 경우 사용하기에 좋음. => 분산 환경이 아닌 경우 따닥 방지용으로 설정해두면 좋음.
    
  ### Distributed Lock(분산 락)
    - Redisson 사용
    - 락 획득 방식 : pub/sub 방식 
    - 락이 해제될 때마다 subscribe 클라이언트에게 "락 시도 가능" 알림을 보냄. <br>
      => 클라이언트에서 락 획득 실패 시 지속적으로 락 시도 요청이 필요 없어짐. <br>
      => spin Lock 방식의 Lettuce에 비해 Redis에 부하 감소
    - RLock: 락을 위한 인터페이스로 락 사용을 쉽게 할 수 있음.
    - 락 범위와 트랜잭션의 범위를 잘 고려해서 사용해야 함.