# concurrency-control-practice
Application and DB level concurrency control tests for Java Spring Boot application.

## 상품 재고 처리 동시성 이슈
1개의 상품에 동시에 재고 차감 요청을 하고, 요청한 재고만큼 차감되는지 테스트 한다.

### Application 레벨 동시성 제어
#### 1. Synchronized
- 하나의 프로세스 내에서만 동시성을 보장한다. 멀티 프로세스에서 보장이 안된다.
- Application 단에서 인스턴스나 클래스 단위로 동작한다.
- 자동으로 lock을 걸고 푼다.

#### 2. ReentrantLock
- java.util.concurrent.locks 패키지의 Lock 인터페이스를 구현한다.
- 좀 더 세밀한 lock 기능을 사용하고 싶을 때 사용한다.
- 수동으로 lock을 사용한다.
  - tryLock() : 락 획득을 시도해서 성공하면 true, 실패하면 false를 반환한다.
  - lock() : 락을 획득한다.
  - unlock() : 데드락을 방지하기 위해 꼭 실행해야 한다.