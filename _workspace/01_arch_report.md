# Architecture Audit Report
**Scope:** `app/src/main/java/com/forday/app/`
**Layers:** presentation, domain, data, remote, core

## 1. 레이어 의존성 방향 위반
- presentation → data/remote 직접 import: **없음 (양호)**
- 유일한 예외: `presentation/allsettings/SettingsViewModel.kt:4` 에서 `AuthRepository` import (domain 레이어이므로 OK; 그러나 UseCase를 통하지 않고 Repository를 직접 주입)

## 2. UseCase 우회 (MAJOR)
ViewModel에서 Repository를 직접 주입하는 사례:
- `presentation/allsettings/SettingsViewModel.kt:20` — `private val repository: AuthRepository` 
  - UseCase 계층 건너뛰고 Repository 직접 호출 → 아키텍처 가이드 위반
  - 나머지 ViewModel은 UseCase를 사용하는 것으로 보임

## 3. RemoteMapper 커버리지 (INFO)
- Response 데이터 클래스: 95+ 개 확인
- 모든 Response 는 `RemoteMapper<XxxEntity>` 인터페이스를 구현 (샘플 확인: KakaoLoginResponse.kt OK)
- `ExampleReponse.kt`: 오타 있음 (Reponse → Response)

## 4. DataMapper 커버리지 (INFO)
- Entity 데이터 클래스 ~80개
- `data/DataMapper.kt` 최상위 mapper 존재
- 일부 Entity 파일은 Domain 모델로의 mapper가 누락된 것으로 추정 (개별 검증 필요)

## 5. DI 등록 (OK)
- `data/impl/` 하위 9개 Impl 전부 `RepositoryModule.kt`에 `@Binds` 등록됨
  - AppVersionPolicy, Auth, User, Hobby, File, S3Upload, Routine, Sosik, Notification
- **일치함 (양호)**

## 6. ScreenRoot 분리 (MIXED)
- 총 22개 `*ScreenRoot` 확인됨
- Root 외부에서 `hiltViewModel()` 호출하는 문제 케이스:
  - `presentation/home/HomeScreen.kt:140` — Screen 함수의 default param (`viewModel: HomeViewModel = hiltViewModel()`)
  - `presentation/modifyhobby/screen/ModifyHobbyScreen.kt:102`
  - `presentation/modifyroutine/screen/ModifyRoutineScreen.kt:56`
  - `presentation/notification/screen/NotificationScreen.kt:79`
  - `presentation/onboarding/termsagreement/screen/TermsAgreementScreen.kt:90`
  - `presentation/sosik/screen/RegisterScreen.kt:78`
  - `presentation/sosik/screen/SosikScreen.kt:200`
  - `presentation/inputhobbyroutines/screen/LoadingRoutinesScreen.kt:201`
  - (심각도 MINOR — default param로 쓰이긴 하지만, Root 패턴 일관성 저해)
- `MainFlow.kt:189~194, 834` — MainFlow 에서 여러 ViewModel을 `hiltViewModel()` 직접 획득 (설계상 의도된 공유 ViewModel 패턴일 수 있음)

## 7. UiState var 필드 (OK)
- UiState 내 `var` 선언된 필드 없음 (immutable data class 준수)

## 8. StateFlow 직접 재할당 (OK)
- `_uiState.value = ...` 패턴 없음 (update { copy(...) } 패턴 준수 추정)

## 9. 기타 발견
- `data/impl/AuthRepositoryImpl.kt:107` — `TODO("Not yet implemented")` 미구현 메서드 (CRITICAL: 런타임 크래시 위험)
- `domain/usecase/` 에 30+ UseCase 파일 존재 — 대부분 분리 양호
