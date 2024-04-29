# concurrency-control-practice
Application and DB level concurrency control tests for Java Spring Boot application.

## 동시성 이슈
1. Race Condition
- 공통 자원에 대해 여러 작업들(프로세스, 스레드, 트랜잭션)이 동시에 접근할 때 발생한다.
- Application의 동시성 이슈 : 비즈니스 로직에서의 데이터 정합성이 깨진다. 예상치 못한 결과가 발생할 수 있다.
- DB의 동시성 이슈 : 데이터의 무결성이 깨지게 된다.
2. Mutual Exclusion
3. DeadLock

## 이커머스 서비스에서의 동시성 이슈
### Case
- 상품 재고 차감 및 복원
  - 1개의 상품에 동시에 재고 차감 요청을 하고, 요청한 재고만큼 차감되는지 테스트 한다.
  - 동시성 제어를 위해 선택한 Lock : `Pessimistic Lock`
  - 선택한 이유: 
- 포인트 충전 및 사용
  - 유저의 포인트를 동시에 충전, 사용 시 데이터의 정합성을 유지하는지 테스트

## 동시성 제어
### Application 레벨 동시성 제어
- 단일 프로세스인지 멀티 프로세스인지에 따라 선택
  #### 1. Synchronized
  - 하나의 프로세스 내에서만 동시성을 보장한다. 멀티 프로세스에서 보장이 안된다.
    - Application 단에서 인스턴스나 클래스 단위로 동작한다.
    - 자동으로 lock을 걸고 푼다.

  #### 2. ConcurrentHashMap + ReentrantLock
  - ConcurrentHashMap : ReentrantLock을 효율적으로 관리하며, 멀티 프로세스에서 동시성을 보장한다.
    - ReentrantLock : java.util.concurrent.locks 패키지의 Lock 인터페이스를 구현한다.
    - 좀 더 세밀한 lock 기능을 사용하고 싶을 때 사용한다.
    - 수동으로 lock을 사용한다.
      - tryLock() : 락 획득을 즉시 시도해서 성공하면 true, 실패하면 false를 반환한다.
      - tryLock(time, timeUnit) : 락 획득을 시간 내에 시도해서 성공하면 true, 실패하면 false를 반환한다.
      - lock() : 락을 획득한다.
      - unlock() : 데드락을 방지하기 위해 꼭 실행해야 한다.

### DB 레벨 동시성 제어
- 트랜잭션 간 충돌 빈도가 많은지에 따라 락 방식 선택.
  #### 1. 낙관적 락(Optimistic Lock)
  - 선착순과 같이 나머지 실패 처리에 대해 리스크가 없는 경우
  - Lock 설정 없이 데이터 정합성을 보장할 수 있어 성능적으로 우위에 있다.
  #### 2. 비관적 락(Pessimistic Lock)
  - 순서가 보장되어야 하는 경우
  - 실패에 대한 프로세스가 필요한 경우
  
### 