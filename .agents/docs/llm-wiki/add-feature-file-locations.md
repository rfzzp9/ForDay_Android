# Add Feature File Locations

## Compose/component 패키지 위치

새 Compose 화면은 다음 presentation 구조를 우선 사용한다.

```text
app/src/main/java/com/forday/app/presentation/{feature}/
  {Feature}ViewModel.kt
  {Feature}UiState.kt
  compose/{Feature}Route.kt 또는 compose/{Feature}ScreenRoot.kt
  compose/{Feature}Screen.kt
  component/{Feature}*.kt, UI 조각이 크거나 반복되거나 독립적으로 읽혀야 하는 경우
  model/{Feature}UiModel.kt, 필요한 경우
```

`compose`는 화면 단위 조립에 사용하고, `component`는 stateless UI 조각에 사용한다.

## 목적

새 기능을 추가할 때 어느 파일을 만들거나 수정할지 빠르게 정한다.

## 화면만 추가하는 경우

```text
app/src/main/java/com/forday/app/presentation/{feature}/
  {Feature}ViewModel.kt
  {Feature}UiState.kt
  screen/{Feature}Route.kt 또는 screen/{Feature}ScreenRoot.kt
  screen/{Feature}Screen.kt
  model/{Feature}UiModel.kt, 필요한 경우
```

추가 확인:
- `core/navigation/Route.kt`
- `presentation/main/MainFlow.kt`

## API 연동이 있는 경우

```text
remote/api/service/{Domain}Api.kt
remote/model/request/*Request.kt
remote/model/response/*Response.kt
remote/impl/{Domain}DataSourceImpl.kt
remote/di/RemoteDataSourceModule.kt

data/remote/{Domain}DataSource.kt
data/model/*Entity.kt
data/impl/{Domain}RepositoryImpl.kt
data/di/RepositoryModule.kt

domain/model/*Domain.kt
domain/repository/{Domain}Repository.kt
domain/usecase/*UseCase.kt
```

## 완료 전 확인

- Response -> Entity -> Domain -> UiModel 변환 흐름이 있는가
- Repository interface와 구현체 시그니처가 일치하는가
- Hilt binding이 등록되어 있는가
- Navigation entry가 등록되어 있는가
- Screen Preview가 필요한 경우 추가되어 있는가
