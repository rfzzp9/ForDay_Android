# 코드 리뷰 체크리스트 상세

Forday Android 코드 리뷰 체크리스트 전체 목록. SKILL.md의 요약에서 불명확한 경우 이 문서를 참조한다.

## 목차

1. [아키텍처 체크](#아키텍처-체크)
2. [ViewModel 체크](#viewmodel-체크)
3. [Screen/UI 체크](#screenui-체크)
4. [Mapper 체크](#mapper-체크)
5. [DI 체크](#di-체크)
6. [Navigation 체크](#navigation-체크)
7. [API/Repository 체크](#apirepository-체크)
8. [자주 발생하는 실수](#자주-발생하는-실수)

---

## 아키텍처 체크

### 레이어 역전 (CRITICAL)

```kotlin
// 금지: Presentation에서 Data 레이어 직접 참조
class FooViewModel @Inject constructor(
    private val repository: FooRepositoryImpl  // 금지 — interface 사용
)

// 올바름
class FooViewModel @Inject constructor(
    private val getFooUseCase: GetFooUseCase  // UseCase만
)
```

### UseCase 우회 (MAJOR)

```kotlin
// 금지: ViewModel에서 Repository 직접 참조
class FooViewModel @Inject constructor(
    private val repository: FooRepository  // 단순 CRUD 외 비즈니스 로직이면 UseCase 사용
)

// 올바름 (비즈니스 로직이 있으면)
class FooViewModel @Inject constructor(
    private val getFooUseCase: GetFooUseCase
)
```

---

## ViewModel 체크

### 필수 구조

```kotlin
@HiltViewModel  // 필수
class FooViewModel @Inject constructor(
    private val useCase: FooUseCase
) : BaseViewModel<FooSideEffect>() {  // BaseViewModel 상속 필수

    private val _uiState = MutableStateFlow(FooUiState())
    val uiState: StateFlow<FooUiState> = _uiState.toStateIn()  // toStateIn() 필수
```

### 상태 업데이트 (MAJOR)

```kotlin
// 금지: 직접 재할당
_uiState.value = _uiState.value.copy(isLoading = true)

// 올바름
_uiState.update { it.copy(isLoading = true) }
```

### API 호출 패턴 (MAJOR)

```kotlin
// 권장 패턴
fun fetchData() = viewModelScope.launch {
    flow { emit(useCase()) }
        .catch { _sideEffectChannel.send(FooSideEffect.Exception(it)) }
        .collect { result -> _uiState.update { it.copy(data = result) } }
}

// 금지: try-catch 직접 사용 (BaseViewModel의 패턴과 불일치)
fun fetchData() = viewModelScope.launch {
    try { ... } catch (e: Exception) { ... }
}
```

### 로깅 (MINOR)

```kotlin
// 화면 진입 이벤트 로깅
viewModel.logEvent("foo_screen")  // ScreenRoot에서 호출
```

---

## Screen/UI 체크

### ScreenRoot / Screen 분리 (MAJOR)

```kotlin
// ScreenRoot: ViewModel 참조, SideEffect 수집, 상태 수집
@Composable
fun FooScreenRoot(viewModel: FooViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()  // collectAsStateWithLifecycle 필수
    LaunchedEffect(Unit) { viewModel.sideEffect.collect { ... } }
    FooScreen(data = state.data, onAction = viewModel::handleAction)
}

// Screen: ViewModel 참조 금지, Preview 가능
@Composable
fun FooScreen(
    data: List<Item>,
    onAction: (Action) -> Unit
) { /* ViewModel 없음 */ }
```

### collectAsState vs collectAsStateWithLifecycle (MAJOR)

```kotlin
// 금지: collectAsState (생명주기 무관)
val state by viewModel.uiState.collectAsState()

// 올바름: collectAsStateWithLifecycle
val state by viewModel.uiState.collectAsStateWithLifecycle()
```

### Preview (MINOR)

```kotlin
@Preview(showBackground = true)
@Composable
private fun FooScreenPreview() {
    FordayTheme {
        FooScreen(
            data = listOf(/* 샘플 데이터 */),
            onAction = {}
        )
    }
}
```

---

## Mapper 체크

### Response Nullable 처리 (CRITICAL)

```kotlin
// 금지: non-null 선언
data class FooResponse(val id: Int)  // 서버가 null 반환 시 크래시

// 올바름
@Serializable
data class FooResponse(
    @SerialName("id") val id: Int? = null  // nullable + 기본값
) : RemoteMapper<FooEntity> {
    override fun toData() = FooEntity(id = id ?: 0)  // Elvis 처리
}
```

### 직렬화 어노테이션 (CRITICAL)

```kotlin
// 금지: Gson 어노테이션 (Kotlinx Serialization 프로젝트)
@SerializedName("id") val id: Int?

// 올바름
@SerialName("id") val id: Int? = null
```

### 리스트 매핑 (MAJOR)

```kotlin
// 금지: NPE 가능성
items = items.map { it.toData() }

// 올바름
items = items?.map { it.toData() } ?: emptyList()
```

---

## DI 체크

### Repository 바인딩 (CRITICAL)

```kotlin
// data/di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindFooRepository(impl: FooRepositoryImpl): FooRepository
    // 새 Repository 추가 시 여기에 @Binds 추가
}
```

### DataSource 바인딩 (CRITICAL)

```kotlin
// remote/di/RemoteDataSourceModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindFooDataSource(impl: FooDataSourceImpl): FooDataSource
}
```

### API Service 제공 (CRITICAL)

```kotlin
// remote/di/NetworkModule.kt
@Provides
@Singleton
fun provideFooApi(retrofit: Retrofit): FooApi = retrofit.create(FooApi::class.java)
```

---

## Navigation 체크

### Serializable 누락 (CRITICAL)

```kotlin
// 금지: @Serializable 없음
data class FooScreen(val id: Long) : NavKey  // 크래시

// 올바름
@Serializable
data class FooScreen(val id: Long = 0L) : NavKey
```

### serializersConfig 등록 (CRITICAL)

non-primitive 파라미터가 포함된 NavKey는 등록 필요:

```kotlin
// presentation/main/MainNavigationState.kt
val serializersConfig = SerializersConfig {
    add(FooScreen.serializer())  // 누락 시 역직렬화 크래시
}
```

### EntryProvider 등록 (CRITICAL)

```kotlin
// presentation/main/MainFlow.kt
entry<FooScreen> { backStackEntry ->
    FooScreenRoot(
        onNext = { navigator.navigate(BarScreen) },
        onBack = { navigator.goBack() }
    )
}
```

---

## API/Repository 체크

### httpCatch 패턴 (MINOR)

서버 에러 코드 처리가 필요한 경우 `httpCatch` 패턴 사용 여부 확인:

```kotlin
// 프로젝트 기존 패턴 참조 (RoutineRepositoryImpl 등)
suspend fun fetchData(): ResultType = httpCatch {
    dataSource.fetchData()
}
```

### DataStore 키 타입 (MAJOR)

```kotlin
// 금지: 타입 불일치 (Long 값을 intPreferencesKey에 저장)
val HOBBY_ID = intPreferencesKey("hobby_id")  // Long이어야 할 경우

// 올바름
val HOBBY_ID = longPreferencesKey("hobby_id")
```

---

## 자주 발생하는 실수

| 실수 | 증상 | 수정 |
|------|------|------|
| `@SerializedName` 사용 | 런타임 역직렬화 오류 | `@SerialName`으로 교체 |
| NavKey `@Serializable` 누락 | 화면 전환 크래시 | `@Serializable` 추가 |
| `serializersConfig` 미등록 | 뒤로가기/복원 크래시 | `MainNavigationState`에 추가 |
| Hilt `@Binds` 누락 | 앱 시작 크래시 | Module에 바인딩 추가 |
| Response non-null 선언 | 서버 null 응답 시 NPE | nullable + Elvis 처리 |
| `collectAsState` 사용 | 생명주기 누수 | `collectAsStateWithLifecycle`로 교체 |
| ScreenRoot에 ViewModel 없음 | 기능 미동작 | `hiltViewModel()` 주입 추가 |
| Screen에서 ViewModel 참조 | Preview 불가 | Screen은 데이터/콜백만 받도록 분리 |
