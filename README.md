# 항해+ 1주차 과제 - TDD로 개발하기 (서준혁)
## 동시성 제어 방식에 대한 분석 및 보고서
동시성 제어를 위해 synchronized 키워드 사용과 ReentrantLock 사용을 고민했다.
### synchronized
간단하게 특정 블록, 메소드 단위로 락을 거는 것이 가능하다.
다만, 자원을 공유하지 않는 경우에도 락을 걸기 때문에 성능이 저하된다
### ReentrantLock
락을 걸면서 Fairness 옵션을 사용하여 락을 획득하는 순서를 제어할 수 있다.
ConcurrentHashMap을 사용하여 userId 별로 Lock을 관리하여 자원을 공유하는 경우만 락이 걸리도록 구현했다.
