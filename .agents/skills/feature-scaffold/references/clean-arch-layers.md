# Clean Architecture 레이어 상세 가이드

Forday Android 프로젝트의 레이어별 파일 구조, 패턴, 의존성 규칙.

## 목차

1. [레이어 의존성 규칙](#레이어-의존성-규칙)
2. [Remote 레이어](#remote-레이어)
3. [Data 레이어](#data-레이어)
4. [Domain 레이어](#domain-레이어)
5. [Presentation 레이어](#presentation-레이어)
6. [Navigation](#navigation)
7. [ScreenMode 패턴](#screenmode-패턴)
8. [Hilt DI 등록](#hilt-di-등록)

---

## 레이어 의존성 규칙

```
Presentation → Domain ← Data ← Remote
```

- Presentation은 Domain(UseCase, Repository interface)에만 의존
- Data는 Domain의 Repository interface를 구현
- Remote는 Data의 DataSource interface를 구현
- 레이어 역전 금지: Presentation이 Data/Remote 직접 참조 불가

---

## Remote 레이어

**위치:** `app/src/main/java/com/forday/app/remote/`

### API Service

```kotlin
// remote/api/service/FooApi.kt
interface FooApi {
    @GET("api/v1/foo/{id}")
    suspend fun getFoo(@Path("id") id: Long): FooResponse

    @POST("api/v1/foo")
    suspend fun createFoo(@Body request: CreateFooRequest): FooResponse

    @GET("api/v1/foo/list")
    suspend fun getFooList(@Query("page") page: Int): List<FooResponse>
}
```

### Response 모델

```kotlin
// remote/model/response/FooResponse.kt
@Serializable
data class FooResponse(
    @SerialName("id") val id: Long? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("items") val items: List<FooItemResponse>? = null
) : RemoteMapper<FooEntity> {
    override fun toData(): FooEntity = FooEntity(
        id = id ?: 0L,
        name = name ?: "",
        items = items?.map { it.toData() } ?: emptyList()
    )
}
```

### DataSource 구현체

```kotlin
// remote/impl/FooDataSourceImpl.kt
class FooDataSourceImpl @Inject constructor(
    private val api: FooApi
) : FooDataSource {
    override suspend fun getFoo(id: Long): FooEntity = api.getFoo(id).toData()
    override suspend fun getFooList(page: Int): List<FooEntity> =
        api.getFooList(page).map { it.toData() }
}
```

### DI 등록

```kotlin
// remote/di/RemoteDataSourceModule.kt에 추가
@Binds
abstract fun bindFooDataSource(impl: FooDataSourceImpl): FooDataSource
```

---

## Data 레이어

**위치:** `app/src/main/java/com/forday/app/data/`

### Entity 모델

```kotlin
// data/model/FooEntity.kt
data class FooEntity(
    val id: Long,          // non-null (Remote에서 null 제거됨)
    val name: String,
    val items: List<FooItemEntity>
) : DataMapper<FooDomain> {
    override fun toDomain(): FooDomain = FooDomain(
        id = id,
        name = name,
        items = items.map { it.toDomain() }
    )
}
```

### DataSource Interface

```kotlin
// data/remote/FooDataSource.kt
interface FooDataSource {
    suspend fun getFoo(id: Long): FooEntity
    suspend fun getFooList(page: Int): List<FooEntity>
    suspend fun createFoo(name: String): FooEntity
}
```

### Repository 구현체

```kotlin
// data/impl/FooRepositoryImpl.kt
class FooRepositoryImpl @Inject constructor(
    private val dataSource: FooDataSource
) : FooRepository {
    override suspend fun getFoo(id: Long): FooDomain =
        dataSource.getFoo(id).toDomain()

    override suspend fun getFooList(page: Int): List<FooDomain> =
        dataSource.getFooList(page).map { it.toDomain() }
}
```

### DI 등록

```kotlin
// data/di/RepositoryModule.kt에 추가
@Binds
abstract fun bindFooRepository(impl: FooRepositoryImpl): FooRepository
```

---

## Domain 레이어

**위치:** `app/src/main/java/com/forday/app/domain/`

### Domain 모델

```kotlin
// domain/model/FooDomain.kt
data class FooDomain(
    val id: Long,
    val name: String,
    val items: List<FooItemDomain>
)
```

### Repository Interface

```kotlin
// domain/repository/FooRepository.kt
interface FooRepository {
    suspend fun getFoo(id: Long): FooDomain
    suspend fun getFooList(page: Int): List<FooDomain>
    suspend fun createFoo(name: String): FooDomain
}
```

### UseCase

```kotlin
// domain/usecase/GetFooUseCase.kt
class GetFooUseCase @Inject constructor(
    private val repository: FooRepository
) {
    suspend operator fun invoke(id: Long): FooDomain = repository.getFoo(id)
}

// domain/usecase/GetFooListUseCase.kt
class GetFooListUseCase @Inject constructor(
    private val repository: FooRepository
) {
    suspend operator fun invoke(page: Int = 0): List<FooDomain> =
        repository.getFooList(page)
}
```

---

## Presentation 레이어

**위치:** `app/src/main/java/com/forday/app/presentation/{feature}/`

### UiState

```kotlin
// presentation/foo/FooUiState.kt
data class FooUiState(
    val items: List<FooUiModel> = emptyList(),
    val selectedItem: FooUiModel? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

// presentation/foo/model/FooUiModel.kt (필요 시)
data class FooUiModel(
    val id: Long,
    val displayName: String,
    val imageRes: Int
)

// Presentation Mapper
fun FooDomain.toPresentation(): FooUiModel = FooUiModel(
    id = id,
    displayName = name,
    imageRes = getImageRes(id)
)
```

### SideEffect

```kotlin
// presentation/foo/FooSideEffect.kt
sealed interface FooSideEffect {
    data class DomainError(val message: String) : FooSideEffect
    data class Exception(val throwable: Throwable) : FooSideEffect
    data object NavigateToDetail : FooSideEffect
}
```

### ViewModel

```kotlin
// presentation/foo/FooViewModel.kt
@HiltViewModel
class FooViewModel @Inject constructor(
    private val getFooListUseCase: GetFooListUseCase
) : BaseViewModel<FooSideEffect>() {

    private val _uiState = MutableStateFlow(FooUiState())
    val uiState: StateFlow<FooUiState> = _uiState.toStateIn()

    init { fetchFooList() }

    fun fetchFooList() = viewModelScope.launch {
        flow { emit(getFooListUseCase()) }
            .catch { _sideEffectChannel.send(FooSideEffect.Exception(it)) }
            .collect { list ->
                _uiState.update { it.copy(items = list.map { item -> item.toPresentation() }) }
            }
    }

    fun selectItem(item: FooUiModel) {
        _uiState.update { it.copy(selectedItem = item) }
    }
}
```

### ScreenRoot / Screen 분리

```kotlin
// presentation/foo/screen/FooScreenRoot.kt
@Composable
fun FooScreenRoot(
    onNext: () -> Unit,
    onBack: () -> Unit,
    viewModel: FooViewModel = hiltViewModel()
) {
    viewModel.logEvent("foo_screen")
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is FooSideEffect.DomainError -> { /* Toast */ }
                is FooSideEffect.NavigateToDetail -> onNext()
                is FooSideEffect.Exception -> { /* 에러 처리 */ }
            }
        }
    }

    FooScreen(
        items = state.items,
        isLoading = state.isLoading,
        onItemClick = viewModel::selectItem,
        onNext = onNext,
        onBack = onBack
    )
}

// presentation/foo/screen/FooScreen.kt
@Composable
fun FooScreen(
    items: List<FooUiModel>,
    isLoading: Boolean,
    onItemClick: (FooUiModel) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    // ViewModel 참조 없음 — Preview 가능
}

@Preview
@Composable
private fun FooScreenPreview() {
    FordayTheme {
        FooScreen(
            items = emptyList(),
            isLoading = false,
            onItemClick = {},
            onNext = {},
            onBack = {}
        )
    }
}
```

---

## Navigation

### NavKey 정의

```kotlin
// core/navigation/Route.kt에 추가
@Serializable
data class FooScreen(
    val params: FooParams? = null,
    val mode: ScreenMode = ScreenMode.DEFAULT
) : NavKey

@Serializable
data class FooParams(
    val id: Long,
    val name: String
)
```

### EntryProvider 등록

```kotlin
// presentation/main/MainFlow.kt의 entryProvider 블록에 추가
entry<FooScreen> { backStackEntry ->
    FooScreenRoot(
        params = backStackEntry.params,
        onNext = { navigator.navigate(NextScreen) },
        onBack = { navigator.goBack() }
    )
}
```

### Navigation3 serializersConfig 등록

복잡한 NavKey(non-primitive 파라미터 포함)는 `MainNavigationState`의 `serializersConfig`에 등록해야 한다:

```kotlin
// presentation/main/MainNavigationState.kt
val serializersConfig = SerializersConfig {
    // ... 기존 항목들 ...
    add(FooScreen.serializer())  // 추가
}
```

---

## ScreenMode 패턴

온보딩 플로우 화면에서 사용. ONBOARDING은 선택 즉시 ViewModel에 저장, DEFAULT는 로컬 상태 관리 후 onNext에서만 저장.

```kotlin
@Composable
fun FooScreenRoot(
    params: FooParams?,
    mode: ScreenMode,
    viewModel: FooViewModel
) {
    // DEFAULT 모드용 로컬 상태
    var localValue by remember(params, viewModel.uiState.value.savedValue) {
        mutableStateOf(
            if (mode == ScreenMode.DEFAULT) params?.value else null
        )
    }

    val currentValue = if (mode == ScreenMode.DEFAULT) localValue
                       else viewModel.uiState.value.savedValue

    FooScreen(
        value = currentValue,
        onValueChange = { newValue ->
            if (mode == ScreenMode.DEFAULT) localValue = newValue
            else viewModel.saveValue(newValue)  // ONBOARDING: 즉시 저장
        },
        onNext = {
            if (mode == ScreenMode.DEFAULT) viewModel.saveValue(localValue)  // onNext에서만 저장
            onNext()
        }
    )
}
```

---

## Hilt DI 등록

### RepositoryModule.kt 패턴

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindFooRepository(impl: FooRepositoryImpl): FooRepository
    // ... 기존 바인딩들
}
```

### RemoteDataSourceModule.kt 패턴

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindFooDataSource(impl: FooDataSourceImpl): FooDataSource
    // ... 기존 바인딩들
}
```

API 서비스는 `NetworkModule.kt`에서 `@Provides`로 제공되는지 확인한다. 새 Api 인터페이스 추가 시 `NetworkModule.kt`에 `@Provides fun provideFooApi(retrofit: Retrofit): FooApi` 추가 필요.
