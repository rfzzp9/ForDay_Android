---
name: android-review
description: >
  Forday Android 코딩 컨벤션, Clean Architecture 레이어 분리, MVI 패턴 준수를 검증하는 스킬.
  code-reviewer 에이전트 전용. ScreenRoot/Screen 분리, ViewModel 패턴, Mapper 체인,
  Hilt DI 등록, Navigation3 설정, Null Safety, ScreenMode 분기 등을 체크.
  상세 체크리스트: references/review-checklist.md 참조.
---

# Android Review

code-reviewer 에이전트가 Forday Android 코드를 검증할 때 따르는 체크리스트와 판단 기준.

## 심각도 분류

| 등급 | 정의 | 처리 |
|------|------|------|
| **CRITICAL** | 빌드 실패, 런타임 크래시, 데이터 손실 가능성 | 반드시 수정 후 진행 |
| **MAJOR** | 아키텍처 위반, 레이어 역전, DI 누락, MVI 미준수 | 수정 강권, 블로킹 가능 |
| **MINOR** | 네이밍, 불필요한 코드, 스타일 | 권고, 블로킹 안 함 |

## 핵심 체크리스트

상세 체크리스트: `references/review-checklist.md`

### 1. 레이어 분리 (MAJOR)
- [ ] Presentation이 Data/Remote 레이어를 직접 참조하지 않음
- [ ] Repository impl이 Domain model을 반환함 (Entity 노출 금지)
- [ ] ScreenRoot에만 ViewModel, Screen은 Pure Composable

### 2. ViewModel 패턴 (MAJOR)
- [ ] `@HiltViewModel` + `@Inject constructor` 존재
- [ ] `BaseViewModel<SideEffect>` 상속
- [ ] StateFlow로 상태 노출 (`toStateIn()` 사용)
- [ ] API 호출: `flow { emit() }.catch { }.collect { }` 패턴
- [ ] `_uiState.update { }` 사용 (직접 재할당 금지)

### 3. Mapper 체인 완결성 (CRITICAL)
- [ ] Response: 모든 필드 nullable, `RemoteMapper<Entity>` 구현
- [ ] Entity: 모든 필드 non-null, `DataMapper<Domain>` 구현
- [ ] Null 처리: Elvis 연산자 (`?: 기본값`), 리스트: `?.map { } ?: emptyList()`
- [ ] `@SerialName` 사용 (`@SerializedName` 아님)

### 4. Hilt DI (CRITICAL)
- [ ] 새 Repository: `RepositoryModule.kt`에 `@Binds` 등록
- [ ] 새 DataSource: `RemoteDataSourceModule.kt`에 `@Binds` 등록
- [ ] 새 Api 서비스: `NetworkModule.kt`에 `@Provides` 추가

### 5. Navigation (CRITICAL)
- [ ] NavKey에 `@Serializable` 추가
- [ ] 파라미터 포함 NavKey: `MainNavigationState`의 `serializersConfig` 등록
- [ ] `MainFlow.kt`의 `entryProvider`에 `entry<>` 추가

### 6. Null Safety (MAJOR)
- [ ] Response 필드: nullable 선언
- [ ] Entity/Domain 필드: non-null 선언
- [ ] UI nullable: 의미있는 경우만 사용 (로딩 중 미결정 등)

### 7. SideEffect 패턴 (MAJOR)
- [ ] sealed interface로 정의
- [ ] `LaunchedEffect`에서 수집 (ScreenRoot)
- [ ] 네비게이션, Toast 등 단발성 이벤트에 사용 (State가 아님)

### 8. ScreenMode (MINOR)
- [ ] ONBOARDING: ViewModel 즉시 저장
- [ ] DEFAULT: 로컬 상태 관리 → onNext에서만 저장

## 즉시 수정 코드 제시 조건

CRITICAL 이슈 발견 시 반드시 수정 코드를 함께 제시한다:

```kotlin
// BEFORE (CRITICAL: @SerialName 미사용)
data class FooResponse(@SerializedName("id") val id: Int?)

// AFTER
@Serializable
data class FooResponse(@SerialName("id") val id: Int? = null)
```

## 리포트 형식

```
## 코드 리뷰 결과

### CRITICAL
- [파일경로:라인] 문제 설명
  수정 코드: ...

### MAJOR
- [파일경로:라인] 문제 설명
  수정 제안: ...

### MINOR
- [파일경로:라인] 개선 제안

### 통과
- 레이어 분리 ✓
- Mapper 체인 ✓
```
