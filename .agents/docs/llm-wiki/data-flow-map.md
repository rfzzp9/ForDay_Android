# Data Flow Map

## 목적

화면에서 API까지 코드를 추적할 때 확인 순서를 제공한다.

## 기본 추적 순서

```text
Screen or Route
  -> ViewModel
  -> UseCase
  -> Repository interface
  -> RepositoryImpl
  -> DataSource interface
  -> DataSourceImpl
  -> Api service
  -> Request/Response
  -> Mapper
```

## 화면 상태 문제

1. `presentation/{feature}/screen/*`
2. `presentation/{feature}/{Feature}ViewModel.kt`
3. `presentation/{feature}/{Feature}UiState.kt`
4. `presentation/{feature}/model/*UiModel.kt`
5. Domain -> UI mapper

확인할 것:
- Screen이 ViewModel을 직접 참조하지 않는가
- UiState 기본값이 안전한가
- `collectAsStateWithLifecycle()`를 쓰는가
- Success/Loading/Error 분기가 빠지지 않았는가

## API 데이터 문제

1. `remote/api/service/*Api.kt`
2. `remote/model/request/*Request.kt`
3. `remote/model/response/*Response.kt`
4. `remote/impl/*DataSourceImpl.kt`
5. `data/remote/*DataSource.kt`
6. `data/impl/*RepositoryImpl.kt`
7. `domain/repository/*Repository.kt`
8. `domain/usecase/*UseCase.kt`
9. `presentation/{feature}/{Feature}ViewModel.kt`

확인할 것:
- Response nullable 처리
- Response -> Entity -> Domain -> UiModel 변환
- Repository interface와 구현체 시그니처 일치
- Flow/suspend 계약 일치

## Navigation 문제

1. `core/navigation/Route.kt`
2. `presentation/main/MainFlow.kt`
3. `presentation/main/MainNavigator.kt`
4. `presentation/main/MainNavigationState.kt`
5. feature Route 또는 ScreenRoot

확인할 것:
- NavKey 직렬화 가능 여부
- entry 등록 여부
- parameter 기본값
- back behavior

## DI 문제

1. 생성자 `@Inject`
2. `data/di/RepositoryModule.kt`
3. `remote/di/RemoteDataSourceModule.kt`
4. `remote/di/NetworkModule.kt`
5. ViewModel constructor

확인할 것:
- interface binding 누락
- Retrofit API provider 누락
- scope 부적절
- 구현체 생성자 의존성 누락
