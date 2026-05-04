# Forday Android 종합 코드 리뷰
**감사 범위:** `app/src/main/java/com/forday/app/` 전체 (+ AndroidManifest, build.gradle.kts, proguard-rules.pro)
**감사 일시:** 2026-04-19
**브랜치:** develop

## 요약

| 등급 | 아키텍처 | 보안 | 성능 | 스타일 | 합계 |
|---|---|---|---|---|---|
| CRITICAL | 1 | 2 | 1 | 1 | **5** |
| MAJOR    | 1 | 2 | 4 | 3 | **10** |
| MINOR    | 1 | 1 | 1 | 4 | **7** |

---

## 우선 액션 아이템 (CRITICAL → MAJOR 순)

### [CRITICAL-1] 릴리스 키스토어 패스워드 평문 커밋 (Security)
**파일:** `app/build.gradle.kts:36, 40`
```kotlin
storePassword = "wony2401"
keyPassword = "wony2401"
```
- `getProperty("KEYSTORE_PASSWORD")` 주석처리하고 하드코딩으로 대체됨
- **즉시 조치**: local.properties 기반 getProperty 복원 + git 히스토리에 남은 값은 키스토어 패스워드 교체 필요

### [CRITICAL-2] 릴리스 빌드에서 토큰/민감정보 Logcat 노출 (Security)
**원인:** `core/logger/di/LoggerModule.kt:67-72` 의 `provideTimberInitializer` 가 `DebugLogTree()` 를 `BuildConfig.DEBUG` 가드 없이 항상 주입
**토큰 노출 로그 지점:**
- `data/impl/AuthRepositoryImpl.kt:115, 192, 199`
- `presentation/mypage/MyPageViewModel.kt:689, 705, 734`
- `presentation/onboarding/OnboardingViewModel.kt:109, 730, 757, 842`
- `remote/api/interceptor/TokenInterceptor.kt:30-60` (Authorization 헤더 및 응답 body 전체 로깅)

**조치:**
```kotlin
if (BuildConfig.DEBUG) trees.add(DebugLogTree())
```
추가로 토큰을 로그에 찍는 모든 지점 제거 또는 `token.take(8) + "..."` 형태로 마스킹.

### [CRITICAL-3] `AuthRepositoryImpl.kt:107` TODO("Not yet implemented") (Arch/Style)
- 호출되면 `NotImplementedError` 런타임 크래시
- 해당 메서드가 정말 필요 없으면 제거, 필요하면 구현 완료 필수

### [CRITICAL-4] `runBlocking` 이 TokenProvider 매 요청마다 실행 (Performance)
**파일:** `data/AuthTokenProvider.kt:12, 16`
- `TokenInterceptor`가 매 네트워크 요청마다 `getAccessToken()/getRefreshToken()` 호출 → 각 호출이 `runBlocking`으로 DataStore Flow 대기
- OkHttp dispatcher 스레드 블로킹, 병목 발생 가능
- **조치:** 인메모리 캐시(AtomicReference)로 토큰 보유, DataStore는 백그라운드에서 단 방향 업데이트

### [CRITICAL-5] Preview/샘플 코드 내 `TODO()` 호출 지뢰 (Style)
Screen 레벨 Preview 에서 `TODO()` 가 함수 인자로 쓰이고 있음. Preview 렌더링 중 그 분기가 실행되면 크래시:
- `HomeScreen.kt:1344-1350`, `MyPageScreen.kt:2342-2346`, `SettingsScreen.kt:516-519`, `AiRecommendationBottomSheet.kt:1197-1226`, `HobbyInputDialog.kt:215-216`

---

### [MAJOR-1] UseCase 우회 — SettingsViewModel (Architecture)
**파일:** `presentation/allsettings/SettingsViewModel.kt:20`
```kotlin
private val repository: AuthRepository
```
다른 ViewModel은 UseCase 주입 사용. SettingsViewModel도 UseCase로 분리 필요.

### [MAJOR-2] cleartext traffic 전면 허용 (Security)
**파일:** `AndroidManifest.xml:28` `usesCleartextTraffic="true"`
- `networkSecurityConfig` 도 없음 → 모든 도메인에 HTTP 허용
- 권장: 개발 빌드용 manifest 분리 또는 `res/xml/network_security_config.xml` 로 도메인 화이트리스트

### [MAJOR-3] Composable 내 remember 없는 `listOf(...)` 반복 생성 (Performance)
- `PrivacyPolicyScreen.kt` (8회), `TermsOfServiceScreen.kt` (14회), `HobbyPhotoManagementScreen.kt` (when 분기 4회 + gradient 2회), `AIRecommendationRoutinesScreen.kt:194`, `InputRoutineScreen.kt:693, 775`
- 권장: top-level private val 또는 `remember { listOf(...) }`

### [MAJOR-4] LazyList key 누락 (Performance)
- `ModifyHobbyScreen.kt:224` `items(state.hobbies)`
- `HobbyPhotoManagementScreen.kt:812` `items(photos)`
- `SelectHobbyScreen.kt:177` `items(hobbies)`

### [MAJOR-5] Kakao login 콜백에서 Activity Context 캡처 위험 (Performance)
- `MyPageViewModel.kt:677+`, `OnboardingViewModel.kt:707+`, `SosikViewModel.kt:145+`
- Kakao SDK 콜백 지연 시 destroyed Activity Context 참조 가능

### [MAJOR-6] 대형 Screen 파일들 (2300+줄) (Performance/Style)
- `MyPageScreen.kt` 2352, `RoutineDetailScreen.kt` 1850, `RecordRoutineScreen.kt` 1360, `HomeScreen.kt` 1352, `HomeHeaderScreen.kt` 1252, `AiRecommendationBottomSheet.kt` 1227, `HobbyPhotoManagementScreen.kt` 1109, `MainFlow.kt` 1008, `ModifyHobbyScreen.kt` 1004
- 컴포넌트 분리 + `derivedStateOf` 권장

### [MAJOR-7] Gson + kotlinx.serialization 혼재 (Style)
- `@SerializedName` 525회 사용 + `@Serializable` 일부 request 에 존재
- `NetworkModule.kt` 는 `GsonConverterFactory` 사용
- **권장:** 프로젝트 가이드(kotlinx serialization)대로 통일

### [MAJOR-8] Preview 에서 TODO() 사용 (Style)
[CRITICAL-5] 와 중복 — 장식성 dummy 값으로 치환 필요

### [MAJOR-9] `Timber.plant` Debug/Release 공통 (Security)
[CRITICAL-2] 와 중복 — `BuildConfig.DEBUG` 가드 필요

### [MAJOR-10] Screen 함수 default param 으로 hiltViewModel() (Architecture)
- `HomeScreen.kt:140`, `ModifyHobbyScreen.kt:102`, `ModifyRoutineScreen.kt:56`, `NotificationScreen.kt:79`, `TermsAgreementScreen.kt:90`, `RegisterScreen.kt:78`, `SosikScreen.kt:200`
- Root 에서 ViewModel 획득 → Screen 에는 state/callback만 전달하는 패턴 권장 (테스트 용이성, Preview)

---

## 아키텍처 감사 (Architecture Audit)

**좋은 점:**
- Clean Architecture 4계층 (presentation/domain/data/remote) 구조 명확
- 9개 Repository 전부 `@Binds` 등록됨 (`RepositoryModule.kt` 1:1 매칭)
- 95+ Response 에 `RemoteMapper` 인터페이스 일관 적용
- `presentation` 에서 `data`/`remote` 직접 import 없음 (방향 준수)
- UiState 가 immutable data class로 선언됨
- `_uiState.value = ...` 재할당 안 함 (update 패턴 준수)

**개선 필요:**
- `SettingsViewModel` 이 Repository 직접 주입 → UseCase 추가 필요
- `AuthRepositoryImpl.kt:107` 에 미구현 TODO — 메서드 제거 또는 구현
- Screen 함수 default param 으로 hiltViewModel() — 22개 ScreenRoot 중 7~8개는 ScreenRoot 분리 불완전
- `ExampleReponse.kt` 오타

**상세:** `_workspace/01_arch_report.md`

---

## 보안 취약점 감사 (Security Audit)

**CRITICAL:**
1. 키스토어 패스워드 평문 커밋 (`build.gradle.kts`)
2. 토큰/FCM/계정 정보 Logcat 노출 (Release에서도)

**MAJOR:**
1. `usesCleartextTraffic="true"` + networkSecurityConfig 없음
2. `DebugLogTree` 가 Release 에서도 동작

**MINOR:**
1. `allowBackup="true"` — backup_rules.xml 내용 확인 필요

**좋은 점:**
- SSL/TLS 우회 코드 없음
- `BuildConfig.KAKAO_API_KEY`, `BASE_URL` 등은 local.properties 기반
- R8/ProGuard 민감 어트리뷰트 설정 합리적

**상세:** `_workspace/02_security_report.md`

---

## 성능 병목 감사 (Performance Audit)

**CRITICAL:**
- `AuthTokenProvider.runBlocking` 패턴 — 네트워크 요청마다 thread blocking

**MAJOR:**
- Composable 내 remember 없는 listOf 반복 생성 (30+ 지점)
- LazyList key 누락 3건 (동적 리스트)
- Kakao login callback Context 캡처 위험
- 1000줄+ Screen 파일 9개 — recomposition 비용 증가

**MINOR:**
- `RoutineDetailScreen.kt:777` 의 `collectAsState` (Coil state, 영향 적음)

**좋은 점:**
- `GlobalScope` 사용 없음
- `collectAsStateWithLifecycle` 58회 사용 (올바른 lifecycle 인식)
- Coil3 이미지 캐시 설정 합리적 (25% memory, 50MB disk)

**상세:** `_workspace/03_perf_report.md`

---

## 코드 스타일 감사 (Style Audit)

**CRITICAL:**
- `AuthRepositoryImpl.kt:107` 미구현 TODO()
- Preview/샘플에 `TODO()` 사용 (Preview 쉐도우 크래시 위험)

**MAJOR:**
- Gson `@SerializedName` (525회) + kotlinx `@Serializable` 혼재
- 대형 파일 (MyPageScreen 2352줄 등)
- logEvent가 주석처리된 채 방치 (`HomeHeaderScreen/ViewModel`)

**MINOR:**
- @Preview 없는 Screen 3개 (Discovery, AIRecommendationRoutines, Notification)
- `RegisterScreen.kt:71` 내 `ReportScreenRoot` 이름 불일치
- 장식성 로그 문자열 `@@@@@@`, `########` 남용

**상세:** `_workspace/04_style_report.md`

---

## 권장 조치 우선순위

### 즉시 (Sprint 0)
1. [CRITICAL-1] 키스토어 패스워드 local.properties 복원 + 히스토리 정리
2. [CRITICAL-2] `LoggerModule.provideTimberInitializer` 에 `BuildConfig.DEBUG` 가드 + 토큰 로그 제거
3. [CRITICAL-3] `AuthRepositoryImpl.kt:107` TODO 해결
4. [CRITICAL-5] Preview TODO() → dummy 값 치환

### 단기 (1~2 스프린트)
5. [CRITICAL-4] AuthTokenProvider 인메모리 캐시 추가
6. [MAJOR-2] networkSecurityConfig 도입, cleartext 제한
7. [MAJOR-4] LazyList `key` 추가
8. [MAJOR-3] Composable 내 remember/상수화
9. [MAJOR-1] SettingsViewModel 에 UseCase 추가
10. [MAJOR-7] Gson → kotlinx.serialization 마이그레이션 계획

### 중장기
11. [MAJOR-6] 대형 Screen 파일 리팩토링 (MyPageScreen 등)
12. [MAJOR-10] ScreenRoot/Screen 분리 일관화
13. [MAJOR-5] Context 캡처 패턴 제거
14. Analytics logEvent 복원
15. 300줄 초과 파일 점진적 분리
