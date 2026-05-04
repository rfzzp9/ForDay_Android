---
name: feature-scaffold
description: >
  Forday Android Clean Architecture 기반 기능 스캐폴딩 스킬. feature-builder 에이전트 전용.
  새 화면, ViewModel, UseCase, Repository, API Service, Entity, Domain Model 구현 시 사용.
  Kotlin, Jetpack Compose, Hilt, Navigation3, MVI 패턴, ScreenRoot/Screen 분리를 엄격히 준수.
  레이어 구조 상세, 코드 패턴, 파일 위치는 references/clean-arch-layers.md 참조.
---

# Feature Scaffold

feature-builder 에이전트가 Forday Android 기능을 구현할 때 따르는 절차와 원칙.

## 탐색 우선 원칙

구현 전 반드시 기존 유사 파일을 탐색한다. 목적: 네이밍 컨벤션, 패키지 구조, 코드 패턴을 기존 코드와 일치시키기 위함이다.

탐색 대상:
- 유사 기능의 RepositoryImpl (e.g., `HobbyRepositoryImpl.kt`)
- `data/di/RepositoryModule.kt` — 기존 `@Binds` 패턴
- `remote/di/RemoteDataSourceModule.kt` — DataSource 바인딩 패턴
- `core/navigation/Route.kt` — NavKey 정의 형태
- `presentation/main/MainFlow.kt` — EntryProvider 등록 형태

레이어별 파일 구조 및 상세 패턴: `references/clean-arch-layers.md`

## 구현 체크리스트

### Remote 레이어
- [ ] `remote/api/service/{Feature}Api.kt` — Retrofit `@GET`/`@POST` 인터페이스
- [ ] `remote/model/response/{Feature}Response.kt` — 모든 필드 nullable, `RemoteMapper<Entity>` 구현
- [ ] `remote/model/request/{Feature}Request.kt` — 필요 시
- [ ] `remote/impl/{Feature}DataSourceImpl.kt` — `@Inject constructor(private val api: {Feature}Api)`
- [ ] `remote/di/RemoteDataSourceModule.kt` — `@Binds` 추가

### Data 레이어
- [ ] `data/model/{Feature}Entity.kt` — 모든 필드 non-null, `DataMapper<Domain>` 구현
- [ ] `data/remote/{Feature}DataSource.kt` — interface
- [ ] `data/impl/{Feature}RepositoryImpl.kt` — `@Inject constructor(private val dataSource: {Feature}DataSource)`
- [ ] `data/di/RepositoryModule.kt` — `@Binds` 추가

### Domain 레이어
- [ ] `domain/model/{Feature}Domain.kt` — non-null, 비즈니스 의미 반영
- [ ] `domain/repository/{Feature}Repository.kt` — interface
- [ ] `domain/usecase/{Verb}{Feature}UseCase.kt` — `operator fun invoke()`

### Presentation 레이어
- [ ] `presentation/{feature}/{Feature}UiState.kt` — data class, 모든 필드 기본값
- [ ] `presentation/{feature}/{Feature}SideEffect.kt` — sealed interface
- [ ] `presentation/{feature}/{Feature}ViewModel.kt` — `@HiltViewModel`, `BaseViewModel<SideEffect>` 상속
- [ ] `presentation/{feature}/screen/{Feature}ScreenRoot.kt` — ViewModel 연결, SideEffect 처리
- [ ] `presentation/{feature}/screen/{Feature}Screen.kt` — Pure Composable, `@Preview` 포함

### Navigation
- [ ] `core/navigation/Route.kt` — `@Serializable data class/object` 추가
- [ ] `presentation/main/MainFlow.kt` — `entry<{Feature}>` 추가

## 핵심 코드 패턴

### Mapper 규칙

```kotlin
// Response: 모든 필드 nullable, RemoteMapper<Entity> 구현
data class FooResponse(
    @SerialName("id") val id: Int? = null,        // @SerialName (Kotlinx Serialization)
    @SerialName("name") val name: String? = null
) : RemoteMapper<FooEntity> {
    override fun toData() = FooEntity(
        id = id ?: 0,           // null → 기본값
        name = name ?: ""
    )
}

// Entity: non-null, DataMapper<Domain> 구현
data class FooEntity(val id: Int, val name: String) : DataMapper<FooDomain> {
    override fun toDomain() = FooDomain(id = id, name = name)
}
```

### ViewModel API 호출 패턴

```kotlin
fun fetchData() = viewModelScope.launch {
    flow { emit(getFooUseCase()) }
        .catch { _sideEffectChannel.send(FooSideEffect.Exception(it)) }
        .collect { result -> _uiState.update { it.copy(data = result) } }
}
```

### ScreenRoot 패턴

```kotlin
@Composable
fun FooScreenRoot(
    onNext: () -> Unit,
    viewModel: FooViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is FooSideEffect.NavigateNext -> onNext()
            }
        }
    }
    FooScreen(
        data = state.data,
        onAction = viewModel::handleAction
    )
}
```

## 주의사항

- Kotlinx Serialization: `@SerialName` 사용 (`@SerializedName` 아님 — Gson 전용)
- Navigation3 NavKey: `NavKey` interface 구현 필수
- ScreenMode 분기가 필요한 화면: `references/clean-arch-layers.md`의 ScreenMode 섹션 참조
- 복잡한 파라미터 전달: `@Serializable data class`로 래핑 후 NavKey에 포함
