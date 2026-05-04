# Architecture Map

## 목적

이 문서는 Forday Android의 레이어 위치와 의존성 방향을 설명한다.

## 레이어 방향

```text
Presentation -> Domain -> Data -> Remote
```

## 데이터 흐름

```text
API Response
  -> RemoteMapper.toData()
Entity
  -> DataMapper.toDomain()
Domain Model
  -> toPresentation()
UI Model
  -> Screen
```

## Presentation

위치: `app/src/main/java/com/forday/app/presentation/{feature}/`

역할:
- 화면 상태 표시
- 사용자 이벤트 수신
- ViewModel을 통한 UseCase 호출
- Domain model을 UI model로 변환

대표 파일:
- `{Feature}ViewModel.kt`
- `{Feature}UiState.kt`
- `screen/{Feature}Route.kt` 또는 `screen/{Feature}ScreenRoot.kt`
- `screen/{Feature}Screen.kt`
- `model/{Feature}UiModel.kt`

## Domain

위치: `app/src/main/java/com/forday/app/domain/`

역할:
- 비즈니스 UseCase
- Repository interface
- Domain model

대표 파일:
- `usecase/*UseCase.kt`
- `repository/*Repository.kt`
- `model/*Domain.kt`

## Data

위치: `app/src/main/java/com/forday/app/data/`

역할:
- Repository 구현
- DataSource interface
- Entity
- Entity -> Domain mapper

대표 파일:
- `impl/*RepositoryImpl.kt`
- `remote/*DataSource.kt`
- `model/*Entity.kt`
- `di/RepositoryModule.kt`

## Remote

위치: `app/src/main/java/com/forday/app/remote/`

역할:
- Retrofit API
- Request/Response DTO
- DataSource 구현
- Response -> Entity mapper

대표 파일:
- `api/service/*Api.kt`
- `model/request/*Request.kt`
- `model/response/*Response.kt`
- `impl/*DataSourceImpl.kt`
- `di/RemoteDataSourceModule.kt`
- `di/NetworkModule.kt`
