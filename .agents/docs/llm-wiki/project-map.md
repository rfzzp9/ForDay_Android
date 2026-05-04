# Project Map

## 목적

이 문서는 코드 위치를 찾기 위한 최상위 지도다. 코드 작성 규칙은 `.agents/docs/conventions/`에서 확인한다.

## 루트 구조

```text
app/src/main/java/com/forday/app/
  FordayApplication.kt
  core/
  presentation/
  domain/
  data/
  remote/
```

## 주요 패키지

- `core/`: 공통 인프라, 디자인 시스템, 네비게이션, DataStore, logger, util
- `presentation/`: Compose 화면, ViewModel, UiState, feature별 UI 모델
- `domain/`: UseCase, Repository interface, Domain model
- `data/`: Repository 구현체, Entity, DataSource interface, Data mapper, DI
- `remote/`: Retrofit API, Request/Response DTO, DataSource 구현체, Remote mapper, network DI

## 공통 위치

- 디자인 시스템: `app/src/main/java/com/forday/app/core/designsystem/`
- 네비게이션 키: `app/src/main/java/com/forday/app/core/navigation/Route.kt`
- 메인 네비게이션 흐름: `app/src/main/java/com/forday/app/presentation/main/`
- Repository DI: `app/src/main/java/com/forday/app/data/di/RepositoryModule.kt`
- Remote DataSource DI: `app/src/main/java/com/forday/app/remote/di/RemoteDataSourceModule.kt`
- Network DI: `app/src/main/java/com/forday/app/remote/di/NetworkModule.kt`
