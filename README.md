# concurrency-control-practice
Application and DB level concurrency control tests for Java Spring Boot application.
## Introuduce
### For What?
- Java 애플리케이션에서 메모리를 효율적으로 사용하기 위해 멀티 스레드를 활용하는데, 여러 스레드가 동시에 데이터에 읽기/쓰기 시 동시성 이슈가 발생한다.
- Race Condition 등의 동시성 이슈를 제어하기 위해 여러 서버 환경에서의 해결방안을 고민하고, 다양한 동기화 매커니즘을 테스트해본다.
- 이커머스 서비스에 도입하여 동시성 제어를 구현하며, 대용량 트래픽을 고려해 설계하고 성능 개선을 한다.

### Package
- demo : 동시성 공부 및 재고 차감 비즈니스 로직으로 Application level 동시성 제어 테스트
- ecommerce : 한 이커머스 서비스 Application
  - 레이어드 + 클린 아키텍처로 구현
    - 간단히 3 Layer로 구성한 demo에서 락 획득에 실패에 대한 예외, Retry 처리를 하는데 제한이 있는 부분을 보완.
    - DIP를 적용해 도메인 중심의 설계로 책임과 분리를 명확히 하여, 
  - 동시성 제어
  - 대용량 트래픽을 고려한 설계 및 성능 개선을 위한 추가 구현(DB Index, Caching Strategy)
  - 대기열 구현 추가 (e.g. 쿠폰 선착순 10명, 할인가 상품 주문 선착순 10명 ..)

## Done (4/29 ~ 5/10)
1. 3-tier로 controller(presentation), service(domain), infra(repository) 레이어로 패키지 구성 및 H2 DB 연동.
2. Application Level Lock, 비관적 락 사용한 재고 차감 동시성 제어 및 테스트
    - 이슈 : ReentrantLock을 사용할 때, 락 실패에 대한 Retry 처리를 하지 않아서 의도한대로 동시성 제어가 잘 안되었다.
    - 해결해야 할 점 : 트랜잭션 앞단에 Layer(Facade Layer)를 두어 락 실패 시 Retry하는 로직을 추가해 제어해야겠다고 생각함.
3. 기존의 레이어드 아키텍처 + 클린 아키텍처로 ecommerce 프로젝트 구성.
4. 낙관적 락, 분산 락으로 포인트 충전, 사용(결제) 동시성 제어 추가해서 기능 구현

##  To Do 
1. 비관적 락으로 주문의 재고 감소/복원 동시성 제어 추가해서 기능 구현
2. 대용량 트래픽을 고려 및 성능 개선을 위한 인덱스 설계, 조회 시 Redis Cache 도입
3. 추후 MSA 분리가 되면 Transactional Outbox Pattern 도입하여 트랜잭션 처리 고려

## 동시성 이슈
1. Race Condition
   - 공통 자원에 대해 여러 작업들(프로세스, 스레드, 트랜잭션)이 동시에 접근할 때 발생한다.
   - Application 레벨(JVM)에서의 동시성 이슈 : 멀티 프로세스 환경에서 데이터 정합성이 깨진다. 예상치 못한 결과가 발생할 수 있다.
   - DB 레벨에서의 동시성 이슈 : 데이터의 무결성이 깨지게 된다.
2. Mutual Exclusion
   - 한번에 하나의 작업만 특정 데이터에 접근할 수 있도록 막는 방식
   - Database Lock을 사용한다.
3. DeadLock
   - 서로 다른 작업이 각각의 블로킹 상태로 진입하여 다른 자원의 블로킹에 진입할 수 없어 기다리고 있는 상태

## 이커머스에서 동시성 이슈
주문 요청 시 재고 차감 및 복원 기능과 포인트 충전 및 사용 기능에 대한 트랜잭션 처리와 동시성 제어할 방법 고민
DB Transaction 격리 수준에 대한 고민

### [단일 서버 환경]
#### 1. 상품, 결제(포인트), 주문 도메인으로 분리된 `Monolithic 구조` 또는 `MSA 구조` + 하나의 트랜잭션
  - 상품 재고 차감 및 복원 : `Pessimistic Lock` (`x-lock`)
    - 선택한 이유 : 규모 확장 및 트래픽이 늘어남에 따라 Scale-out을 해야하는 경우를 대비하여 멀티 인스턴스 환경에서도 여러 유저가 동시에 주문 요청 시 상품 재고에 대한 데이터의 무결성을 보장하는 것이 중요하다고 생각했다. 
    - 제한점 1 : 먼저 요청한 유저에게 락 획득을 주고, 나머지 요청에 대해서는 대기를 하도록 하여 Latency에 의한 성능이 저하될 수 있는 문제가 발생할 수 있다.
    - 제한점 2 : 분산 데이터베이스 환경에서 데이터 무결성을 보장할 수 없다.
  - 포인트 충전 및 사용 : `Optimistic Lock`
    - 선택한 이유 : 하나의 요청을 처리하고, 그 외의 요청은 실패 프로세스를 만들지 않고 예외 처리로 하는 프로세스를 생각했다.
    - 제한점 : 요청 실패에 대해 무시하거나 필요 시 실패에 대한 로직을 구현해야 한다.

### [Scale-out으로 서버의 수평 확장된 분산 환경]
#### 1. 상품, 결제(포인트), 주문 도메인으로 분리된 `MSA 구조` + 하나의 트랜잭션
  - 상품 재고 차감 및 복원 : `Distributed Lock`
    - Redis의 pub/sub 방식 사용
    - 분산락 선택한 이유 : 멀티 인스턴스 환경에서 여러 유저가 동시에 주문 요청 시 상품 재고에 대한 데이터의 무결성을 보장하기 위해서.
    - Redis의 pub/sub 방식 선택한 이유 : spin lock 방식은 락을 획득하지 못한 경우 락을 획득하기 위해 계속 redis에 요청을 보내면서 redis에 부하가 발생할 수 있다. 하지만 pub/sub은 락이 해제될 때마다 구독 중인 클라이언트에게 락 획득에 대한 알림을 보내기 때문에 락 획득 요청 부하 발생이 발생하지 않는다.
  - 포인트 충전 및 사용 : `Optimistic Lock` / `Distributed Lock`
    - 선택한 이유 : 하나의 요청을 처리하고, 그 외의 요청은 실패 프로세스를 만들지 않고 예외 처리로 하는 프로세스를 생각했다.

#### 2. 상품, 결제(포인트), 주문 도메인으로 분리된 `MSA 구조` + 각 트랜잭션으로 분리
  - 상품 재고 차감 및 복원, 포인트 충전 및 사용 : EDA를 적용하여 `Kakfa` 사용한 동시성 제어

## 동시성 제어
### Application 레벨 동시성 제어
- JVM 내부의 동시성 제어 : 여러 스레드가 동일한 Java 메모리에 접근하는 것을 제어
- 동시성 제어에 의해 영향을 주는 범위 : Java 메모리에만 영향
- 단일 프로세스(JVM)에서 동시성 보장
- 멀티 프로세스 환경(JVM 여러 대)에서 동시성 보장이 되지 않는다.

  #### 1. Synchronized
    - Application 단에서 인스턴스나 클래스 단위로 동작한다.
    - 자동으로 lock을 걸고 푼다.
    - 간단한 동기화를 위해 사용.

  #### 2. ConcurrentHashMap + ReentrantLock
  - 여러 번 락을 획득하고 해제해야 하는 경우, 좀 더 세밀한 lock 기능을 사용하고 싶을 때 사용
  - ConcurrentHashMap : ReentrantLock을 효율적으로 관리하며, 멀티 스레드 환경에서 동시성을 보장한다.
  - ReentrantLock : java.util.concurrent.locks 패키지의 Lock 인터페이스를 구현한다.
    - 수동으로 lock을 사용한다.
      - tryLock() : 락 획득을 즉시 시도해서 성공하면 true, 실패하면 false를 반환한다.
      - tryLock(time, timeUnit) : 락 획득을 시간 내에 시도해서 성공하면 true, 실패하면 false를 반환한다.
      - lock() : 락을 획득한다.
      - unlock() : 데드락을 방지하기 위해 꼭 실행해야 한다.

### DB 레벨 동시성 제어
- DB 레벨 동시성 제어 : 여러 트랜잭션이 동일한 데이터베이스에 접근하는 것을 제어
- 동시성 제어에 의해 영향을 주는 범위 : 데이터베이스 전체에 영향
- 멀티 프로세스 환경에서도 동시성 보장
- 트랜잭션 간 충돌 빈도가 많은지에 따라 락 방식 선택.

  #### 1. 낙관적 락(Optimistic Lock)
  - 데이터를 읽는 시점에 Lock을 설정하는 것 대신 수정 시점에 값이 변경되었는지를 체크한다.
  - 충돌 가능성이 적을 때 사용하면 좋다. 선착순과 같이 나머지 실패 처리에 대해 리스크가 없는 경우
  - Lock 설정 없이 데이터 정합성을 보장할 수 있어 성능적으로 우위에 있다.
  - 트랜잭션 커밋 시점에 버전이 같지 않으면 `ObjectOptimisticLockingException` 예외를 발생시킨다.
  - 만약 충돌이 자주 일어나면, Race Condition으로 인한 retry가 많아질 수 있고, DB Connection이 많아지고 스레드 점유하는 메모리가 늘어나는 문제가 발생 가능하다. 
  #### 2. 비관적 락(Pessimistic Lock)
  - 데이터를 읽는 시점에 Lock을 설정하고, 트랜잭션이 완료될 때까지 유지한다.
  - 순서가 보장되어야 하는 경우
  - 실패에 대한 프로세스가 필요한 경우
  - Lock의 범위가 커질 수 있으므로, 다른 테이블에서의 조회에 영향을 미치지 않도록 주의해야 한다.
  - 조회에 사용되는 락의 범위가 Table 단위인지, Row 단위인지에 따라 성능에 미치는 영향이 다르다.
    - PK와 조회에 사용하는 컬럼이 같다면 Row 단위로 Lock이 걸리고, 다르다면 Table 단위로 Lock이 걸린다.
  #### 3. 네임드 락(Named Lock)
    - MySQL에서 제공하는 user-level lock
    - 비관적 락과의 차이점 : 비관적 락은 row 또는 테이블 단위로 lock을 걸지만, 네임드 락은 metadata 단위(문자열)로 lock을 건다.
    - 별도의 공간에 lock 정보가 저장된다. 
    - GET_LOCK(str,timeout) 으로 Lock을 획득하고, RELEASE_LOCK(str) 으로 Lock을 해제한다.
    - 만약 비관적 락으로 Table 자체에 락을 걸면, 다른 비즈니스 로직 또는 프로세스에서 해당 테이블을 조회할 때 Lock이 걸려있는 상태라면 대기를 해야한다.
        => 문자열을 key로 네임드 락을 걸면, 테이블에 락을 걸지 않기 때문에 다른 서비스에서 해당 Row 조회 시 대기없이 조회 가능하다.
  #### 4. Transaction Isolation Level
  - MariaDB 10.9 버전을 기준으로 기본 트랜잭션 격리 수준은 `REPEATABLE READ`
  - 장점 : `Non-repeatable Read` 문제(한 트랜잭션내에서 같은 쿼리를 두 번 수행했을 때, 결과가 다르게 나타나는 현상)를 방지한다.
  - 기본 REPEATABLE READ의 문제점 : `Phantom Read` -> 데이터 조회 후 다른 트랜잭션이 수행한 Update/Insert 이후 조회 시 다른 결과가 나올 수 있다.
  - MariaDB 10.9 버전에서의 REPEATABLE READ의 `Phantom Read` : 
    - SELECT 시 문제가 안되는 이유 : 다른 트랜잭션이 변경을 수행해도 SELECT로 조회한 경우 MVCC에 의해 조회한 트랜잭션보다 나중에 실행된 트랜잭션이 있다면 언두 로그에서 데이터를 조회하기 때문이다.
    - SELECT FOR UPDATE 시 Phantom Read : 배타 락을 건 경우. 다른 트랜젹션에서 수행한 작업에 의해 레코드가 안보였다 보였다 하는 현상. 이유는 배타락이 있는 읽기는 데이터 조회를 언두 로그가 아닌 실제 테이블에서 조회하기 때문이다.
  - Transaction Isolation Level이 `READ COMMITTED`인 경우
    - `Non-repeatable Read` 문제 => 트랜잭션이 실행 중 여러 번 조회할 때, 다른 트랜잭션이 중간에 데이터를 변경해서 조회할 때마다 다른 값이 조회되는 경우

### 분산 락(Distributed Lock)
- 프로세스 단위에 대한 처리를 여러 인스턴스에 대해 동일한 Lock으로 처리할 수 있다.
- 불필요한 DB 커넥션이나 시간이 오래걸리는 I/O에 대한 접근을 차단할 수 있고, DB에 가해지는 직접적인 부하를 원천 차단할 수 있어 효과적이다.
- 관리 주체가 DB, Redis 로 늘어나며, Lock의 관리 주체가 다운되면 서비스 전체에 문제가 발생할 수 있다.