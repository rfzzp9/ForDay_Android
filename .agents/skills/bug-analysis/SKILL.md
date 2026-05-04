---
name: bug-analysis
description: >
  Forday Android 버그 원인 분석 및 수정 방법론 스킬. bug-hunter 에이전트 전용.
  크래시, 잘못된 동작, UI 오류, 데이터 불일치 등 모든 버그 유형 분석.
  Clean Architecture 레이어 역추적, 스택 트레이스 분석, 최소 변경 수정 원칙 적용.
---

# Bug Analysis

bug-hunter 에이전트가 Forday Android 버그를 분석하고 수정할 때 따르는 방법론.

## 분석 절차

```
1. 증상 분류  →  2. 재현 경로  →  3. 원인 레이어 특정  →  4. 수정  →  5. 검증
```

### 1. 증상 분류

| 유형 | 특징 | 시작 레이어 |
|------|------|-----------|
| **앱 크래시** | 스택 트레이스 있음 | 트레이스 최상단 파일 |
| **UI 미갱신** | 데이터는 있지만 화면 반영 안 됨 | Presentation (ViewModel/Screen) |
| **데이터 오류** | 잘못된 값 표시 | Mapper 체인 또는 Repository |
| **네비게이션 오류** | 화면 전환 안 됨, 뒤로가기 불가 | Navigation (Route/Navigator) |
| **DI 크래시** | 앱 시작 즉시 종료 | Hilt 모듈 |
| **DataStore 오류** | 저장/불러오기 값 불일치 | DataStore 키 타입 |

### 2. 재현 경로 파악

어떤 사용자 액션 → 어떤 ViewModel 메서드 → 어떤 레이어를 거치는지 추적한다.

### 3. 원인 레이어 역추적

Presentation → Domain → Data → Remote 순으로 역방향 추적:

```kotlin
// 예: UI가 빈 리스트 표시
1. FooScreen → items 파라미터가 emptyList()인가?
2. FooViewModel → _uiState.update에서 items가 비어있나?
3. GetFooListUseCase → repository.getFooList() 반환이 비어있나?
4. FooRepositoryImpl → dataSource.getFooList()가 비어있나?
5. FooDataSourceImpl → api.getFooList() 응답이 비어있나?
6. FooResponse.toData() → items?.map {} ?: emptyList() — null 때문?
```

### 4. 수정 원칙

- **최소 변경**: 버그를 직접 유발하는 코드만 수정
- **사이드 이펙트 검토**: 수정이 다른 화면/기능에 영향을 주지 않는가
- **회귀 방지**: `_workspace_prev/`에 이전 수정 이력이 있으면 참조

### 5. 검증 방법 제시

수정 후 어떻게 확인할 것인지 구체적으로 제시:
- 어떤 화면에서 어떤 액션을 하면 버그가 재현되는가
- 수정 후 예상 동작은 무엇인가

## Forday 공통 버그 패턴 및 수정

### NPE / 크래시 — Nullable 미처리

```kotlin
// 원인: Response 필드가 실제로 null인데 non-null로 선언
data class FooResponse(@SerialName("memo") val memo: String)  // null 오면 크래시

// 수정
data class FooResponse(@SerialName("memo") val memo: String? = null)
// Entity에서
override fun toData() = FooEntity(memo = memo ?: "")
```

### Navigation 크래시 — @Serializable 누락

```kotlin
// 원인
data class FooScreen(val id: Long) : NavKey  // @Serializable 없음

// 수정
@Serializable
data class FooScreen(val id: Long = 0L) : NavKey
```

### Navigation 크래시 — serializersConfig 미등록

```kotlin
// 수정: MainNavigationState.kt
val serializersConfig = SerializersConfig {
    // 기존 항목들 유지
    add(FooScreen.serializer())  // 추가
}
```

### UI 미갱신 — StateFlow 업데이트 오류

```kotlin
// 원인: update 미사용
_uiState.value = FooUiState(items = newItems)  // 스레드 안전 미보장

// 수정
_uiState.update { it.copy(items = newItems) }
```

### 뒤로가기 불가 — Navigator handleBack 조건

```kotlin
// 분석 위치: MainNavigator.kt, Navigator.handleBack()
// 원인: topLevelRoute 조건 오류 또는 preloadStack 미설정
// 수정: 기존 navigate() 호출에 preloadStack 파라미터 추가
```

### DI 크래시 — Hilt 바인딩 누락

```kotlin
// 증상: "cannot be provided without an @Inject constructor"
// 수정: RepositoryModule.kt 또는 RemoteDataSourceModule.kt에 @Binds 추가
@Binds
@Singleton
abstract fun bindFooRepository(impl: FooRepositoryImpl): FooRepository
```

### DataStore 타입 불일치

```kotlin
// 원인: Long 값을 intPreferencesKey에 저장
val HOBBY_ID = intPreferencesKey("hobby_id")  // Long이 truncation됨

// 수정
val HOBBY_ID = longPreferencesKey("hobby_id")
```

## 수정 보고 형식

```
## 버그 분석 결과

**증상:** [사용자가 보고한 증상]
**근본 원인:** [원인 레이어 + 코드 위치 + 원인 설명]
**사이드 이펙트:** [수정이 영향을 주는 다른 기능 목록, 없으면 "없음"]

**수정 내용:**
- 파일: [경로]
  변경: [변경 내용 요약]

**검증 방법:**
1. [검증 단계 1]
2. [검증 단계 2]
```
