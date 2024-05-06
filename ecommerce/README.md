## Development Environment
- Framework/Language : `Spring Boot 3` `Java 17` `JPA` `gradle`
- DB : `h2 database`, `MariaDB 10.9`
- test : `JUnit` `AssertJ`
- IDE : `intellj`
- Architecture : `Layered Architecture + Clean Architecture`

## Introduce
- `e-커머스 상품 주문 서비스`

## Requirements
- 기능 
  - 포인트 충전 / 조회 API
  - 상품 조회 API
  - 주문 API
  - 인기 판매 상품 조회 API
- 상품 주문에 필요한 메뉴 정보들을 미리 구성한다.
- 사용자는 상품을 여러개 선택해 주문할 수 있고, 미리 충전한 포인트로 결제한다.
- 포인트 충전과 사용은 동시에 일어날 수 없다.
- 상품의 재고에 문제가 없도록 한다.
- 다수의 인스턴스로 어플리케이션이 동작하더라도 기능에 문제가 없도록 한다.
- 인기 판매 상품 조회는 상품 주문 내역을 통해 최근 3일동안 판매량이 높은 순서대로 5개 상품을 조회한다. 
