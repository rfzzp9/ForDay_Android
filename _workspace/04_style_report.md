# Style Audit Report
**Scope:** `app/src/main/java/com/forday/app/`

## 1. @SerializedName vs kotlinx.serialization (MAJOR)
`@SerializedName` (Gson) 사용: **525회** — `remote/model/request` 및 `remote/model/response` 전반에서 Gson 어노테이션 사용 중

반면:
- `request/` 일부 파일은 `kotlinx.serialization.Serializable` import (BlockUserRequest, CreateHobbyRequest, CreateRoutinesRequest, DeleteS3ImageRequest, ExtendHobbyRequest 등)
- NetworkModule.kt 는 `GsonConverterFactory` 사용

**혼재(inconsistent)**: Gson + kotlinx.serialization 동시 사용 → 번들 사이즈 증가, 컨벤션 혼란. 프로젝트 가이드(CLAUDE.md에서 kotlinx serialization 명시)에 따르면 kotlinx로 통일해야 함.

오타: `remote/model/response/ExampleReponse.kt` → `ExampleResponse.kt`

## 2. UiState 기본값 (INFO)
- 10개 UiState 데이터 클래스 확인:
  - SettingsUiState, ModifyHobbyUiState, RoutinesUiState, MyPageUiState, NotificationUiState, OnboardingUiState, SplashUiState, RecordRoutineUiState, SosikUiState, SosikContentUiState
- 샘플 파일 기준 기본값 설정된 것으로 보이나, 개별 확인 필요

## 3. Preview 누락 (MINOR)
`*Screen.kt` 파일 중 `@Preview` 없는 파일:
- `presentation/discovery/DiscoveryScreen.kt`
- `presentation/inputhobbyroutines/screen/AIRecommendationRoutinesScreen.kt`
- `presentation/notification/screen/NotificationScreen.kt`

## 4. 네이밍 컨벤션
- `*ViewModel`, `*UiState`, `*ScreenRoot`, `*UseCase` 패턴 일관됨 (양호)
- `RegisterScreen.kt` 안의 `ReportScreenRoot` (Line 71) — 파일명과 함수명 불일치 (MINOR)

## 5. TODO / FIXME (MAJOR: 런타임 크래시 위험)
`TODO()` 함수 호출은 런타임 NotImplementedError 를 던짐. 실제 코드 경로에 존재하는 TODO():

**CRITICAL 위험 (Preview 가 아닌 실제 코드)**:
- `data/impl/AuthRepositoryImpl.kt:107` — `TODO("Not yet implemented")`
- `presentation/inputhobbyroutines/RoutinesState.kt:26` — 주석 TODO (OK)

**Preview / 샘플 코드 내 TODO() (실제 경로 아니나 실수로 호출 시 크래시)**:
- `core/designsystem/component/bottomsheet/AiRecommendationBottomSheet.kt:1197, 1198, 1225, 1226`
- `core/designsystem/dialog/HobbyInputDialog.kt:215, 216`
- `presentation/allsettings/settings/screen/SettingsScreen.kt:516-519`
- `presentation/home/HomeScreen.kt:1344-1350`
- `presentation/mypage/main/MyPageScreen.kt:2342-2346`

권장: Preview/샘플 에서는 `TODO()` 대신 하드코딩 dummy 값 제공.

일반 `//TODO` 주석:
- `core/designsystem/component/dropdown/PrivacySettingDropdown.kt:18`
- `presentation/home/component/header/HomeHeaderScreen.kt:160`
- `presentation/mypage/hobbyphotosetting/HobbyPhotoManagementScreen.kt:766`

## 6. logEvent / Analytics 커버리지 (MINOR)
- `HomeScreen.kt` 에서 `viewModel.logEvent(AnalyticsEvents.HOME_SCREEN)` 등 호출 (양호)
- 그러나 `HomeHeaderScreen.kt:139, 167, 171, 178` 및 `HomeHeaderViewModel.kt:313-314` 에서는 **주석 처리**된 상태
- 그 외 20+개 ScreenRoot 에서 logEvent 호출 여부 확인 필요

## 7. 파일 크기 (MAJOR, 유지보수 관점)
300 줄 초과 파일 30+개:

| 파일 | 줄수 |
|---|---|
| `presentation/mypage/main/MyPageScreen.kt` | 2352 |
| `presentation/mypage/routinedetail/screen/RoutineDetailScreen.kt` | 1850 |
| `presentation/record/screen/RecordRoutineScreen.kt` | 1360 |
| `presentation/home/HomeScreen.kt` | 1352 |
| `presentation/home/component/header/HomeHeaderScreen.kt` | 1252 |
| `core/designsystem/component/bottomsheet/AiRecommendationBottomSheet.kt` | 1227 |
| `presentation/mypage/hobbyphotosetting/HobbyPhotoManagementScreen.kt` | 1109 |
| `presentation/main/MainFlow.kt` | 1008 |
| `presentation/modifyhobby/screen/ModifyHobbyScreen.kt` | 1004 |
| `presentation/onboarding/OnboardingViewModel.kt` | 923 |
| `presentation/mypage/profilesetting/ProfileSettingScreen.kt` | 917 |
| `presentation/modifyroutine/screen/ModifyRoutineScreen.kt` | 851 |
| `presentation/sosik/screen/SosikScreen.kt` | 844 |
| `presentation/inputhobbyroutines/screen/InputRoutineScreen.kt` | 814 |

권장: 500+ 라인 파일 우선적으로 컴포넌트/유틸 파일 분리

## 8. 장식성 로그 문자열 (MINOR)
- `@@@@@@@@@@@@KeyHash`, `###########22######`, `@@@@@#########` 등 장식 문자열 남용 (MainActivity, TokenInterceptor, MyPageViewModel, OnboardingViewModel, SosikViewModel 등)
- 의도된 디버그 마커지만 릴리스에 포함되면 노이즈 과다

## 심각도 요약
- CRITICAL: 1건 (AuthRepositoryImpl TODO "Not yet implemented")
- MAJOR: 3건 (Gson/kotlinx 혼재, 파일 크기, Preview TODO() 지뢰)
- MINOR: 4건 (Preview 누락, 네이밍 불일치, logEvent 주석처리, 장식 로그)
