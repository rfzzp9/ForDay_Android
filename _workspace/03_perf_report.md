# Performance Audit Report
**Scope:** `app/src/main/java/com/forday/app/`

## 1. Composable 내 객체 생성 (remember 누락) (MAJOR)
Composable 함수 본문/subtree 에서 `listOf(...)` 가 `remember` 없이 반복 생성되는 위치:

- `presentation/allsettings/privacypolicy/screen/PrivacyPolicyScreen.kt` — Line 85, 103, 124, 171, 199, 212, 238, 251 (8회)
- `presentation/allsettings/termsofservice/screen/TermsOfServiceScreen.kt` — Line 91, 105, 118, 136, 151, 168, 181, 200, 216, 233, 249, 263, 277, 289 (14회)
- `presentation/inputhobbyroutines/screen/AIRecommendationRoutinesScreen.kt:194` — gradient `colors = listOf(...)`
- `presentation/inputhobbyroutines/screen/InputRoutineScreen.kt:693, 775` — gradient `colors = listOf(...)`
- `presentation/mypage/hobbyphotosetting/HobbyPhotoManagementScreen.kt:273, 277, 282, 287` — when 분기별 `listOf(...)` (recomposition 시마다 재생성)
  - Line 1085, 1089 — gradient colors 반복 생성

권장: `remember { listOf(...) }` 또는 top-level `private val` 상수로 추출.

## 2. LazyList key 누락 (MAJOR)
- `presentation/modifyhobby/screen/ModifyHobbyScreen.kt:224` — `items(state.hobbies) { hobby -> ... }` **key 없음** (동적 리스트)
- `presentation/mypage/hobbyphotosetting/HobbyPhotoManagementScreen.kt:812` — `items(photos) { photo -> ... }` **key 없음**
- `presentation/onboarding/hobbyselect/SelectHobbyScreen.kt:177` — `items(hobbies) { hobby -> ... }` **key 없음**

권장: `items(list, key = { it.id })` 로 설정하여 재구성 비용과 애니메이션 품질 개선.

고정 index 기반 `items(10) { ... }` (SelectHobbyScreen.kt:173) 및 `NotificationScreen.kt:376` 은 key 필요성 낮음 (데이터 정적).

## 3. collectAsState 사용 (MINOR)
- `presentation/mypage/routinedetail/screen/RoutineDetailScreen.kt:777` — `painter.state.collectAsState()` (Coil state 관찰용, lifecycle-aware 필요성은 낮음, 양호)
- 프로젝트 전반에 `collectAsStateWithLifecycle` 58회 사용 (양호)

## 4. GlobalScope 사용
- GlobalScope 사용 **없음** (양호)

## 5. runBlocking 사용 (CRITICAL)
- `data/AuthTokenProvider.kt:12, 16` — `getAccessToken()`, `getRefreshToken()` 에서 `runBlocking { userDataSource.accessTokenFlow.firstOrNull() }`
  - OkHttp Interceptor 콜 체인에서 호출 → **OkHttp dispatcher 스레드 blocking**
  - 네트워크 요청마다 메인 스레드 외 thread blocking 발생
- `remote/api/interceptor/TokenInterceptor.kt:64, 128` — `runBlocking` 으로 refresh 처리 (Interceptor 특성상 불가피하지만 최소화 필요)

권장: TokenProvider 가 suspend API 를 제공하도록 리팩토링하거나, 캐시된 토큰을 SharedPreferences/인메모리에 저장해 blocking call 제거.

## 6. ViewModel Context 캡처 (MAJOR)
- `presentation/mypage/MyPageViewModel.kt:677, 693, 694` — `loginWithKakao(context: Context, ...)` 메서드가 Context를 파라미터로 받지만 ViewModel 내부에서 콜백/launch 과정 중 Context 캡처 리스크 존재
- `presentation/onboarding/OnboardingViewModel.kt:707, 735, 737, 754, 763` — 동일 패턴
- `presentation/sosik/SosikViewModel.kt:145, 156, 157, 165, 171` — 동일 패턴

비록 메서드 파라미터로만 받지만, Kakao SDK 콜백이 지연되어 이미 destroyed된 Activity의 Context를 참조할 가능성 존재. 권장: `@ApplicationContext` 주입 + 콜백에서 WeakReference 처리.

## 7. 대형 파일로 인한 recomposition 비용 (MAJOR)
- `MyPageScreen.kt` 2352 줄 / `RoutineDetailScreen.kt` 1850 줄 / `HomeScreen.kt` 1352 줄 등
- 한 Composable 이 너무 많은 파생 상태를 들고 있으면 recomposition 범위 확대
- 권장: 컴포넌트 분리 + `derivedStateOf` 활용

## 8. 이미지 캐시 설정 (OK)
- `FordayApplication.kt:56-81` — Coil3 MemoryCache 25%, DiskCache 50MB, OkHttp timeout 30s — 합리적 설정

## 심각도 요약
- CRITICAL: 1건 (runBlocking in TokenProvider)
- MAJOR: 4건 (remember 누락, LazyList key 누락, Context 캡처, 대형 파일)
- MINOR: 1건 (collectAsState)
