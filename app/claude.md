# ForDay Android Project

## 프로젝트 개요

ForDay는 취미 활동 기록 및 관리 앱입니다.

- **프로젝트명**: ForDay
- **패키지명**: `com.forday.app`
- **아키텍처**: Clean Architecture (Presentation - Domain - Data - Remote)
- **언어**: Kotlin
- **UI**: Jetpack Compose + Material3
- **DI**: Hilt
- **비동기**: Coroutines + Flow
- **네트워크**: Retrofit + OkHttp + Kotlinx Serialization
- **이미지**: Coil
- **내비게이션**: Navigation3 (Type-safe)
- **로컬 저장소**: DataStore
- **최소 SDK**: 28 (Android 9.0)
- **타겟 SDK**: 36
- **Java 버전**: 17

---

## 프로젝트 구조

```
app/src/main/java/com/forday/app/
├── FordayApplication.kt
│
├── core/                          # 공통 모듈
│   ├── datastore/                 # DataStore (로컬 저장소)
│   │   ├── UserLocalDataSource.kt
│   │   ├── di/DataStoreModule.kt
│   │   └── model/
│   ├── designsystem/              # 디자인 시스템
│   │   ├── component/             # 재사용 가능한 UI 컴포넌트
│   │   │   ├── button/
│   │   │   ├── textfield/
│   │   │   ├── dropdown/
│   │   │   ├── layout/OnboardingLayout.kt
│   │   │   ├── topbar/TopAppBar.kt
│   │   │   └── ...
│   │   ├── dialog/
│   │   ├── theme/                 # 색상, 타이포그래피
│   │   └── toast/
│   ├── logger/                    # Firebase Analytics, Crashlytics
│   ├── navigation/Route.kt        # NavKey 인터페이스
│   └── util/
│
├── presentation/                  # UI Layer
│   ├── onboarding/
│   │   ├── OnboardingViewModel.kt
│   │   ├── OnboardingUiState.kt   # sealed interface
│   │   ├── splash/
│   │   ├── login/
│   │   ├── hobbyselect/
│   │   ├── timeselect/
│   │   ├── purposeselect/
│   │   ├── frequencyselect/
│   │   ├── periodselect/
│   │   ├── nicknameinput/
│   │   └── showhobbies/
│   │
│   ├── home/
│   │   ├── HomeViewModel.kt
│   │   ├── HomeUiState.kt         # sealed interface
│   │   ├── HomeRoute.kt           # ViewModel 연결 + 에러 처리
│   │   ├── HomeScreen.kt          # 순수 상태 기반 렌더링
│   │   ├── model/HomeMapper.kt
│   │   └── navigation/Home.kt
│   │
│   ├── record/
│   │   ├── RecordRoutineViewModel.kt
│   │   ├── RecordRoutineUiState.kt
│   │   ├── RecordMapper.kt
│   │   └── screen/
│   │       ├── RecordRoutineRoute.kt
│   │       └── RecordRoutineScreen.kt
│   │
│   ├── modifyhobby/
│   │   ├── ModifyHobbyViewModel.kt
│   │   ├── ModifyHobbyUiState.kt
│   │   └── screen/
│   │       ├── ModifyHobbyRoute.kt
│   │       └── ModifyHobbyScreen.kt
│   │
│   ├── mypage/
│   │   ├── MyPageViewModel.kt
│   │   ├── routinedetail/
│   │   └── ...
│   │
│   └── main/
│       ├── MainActivity.kt
│       ├── AppEntryPoint.kt
│       ├── MainFlow.kt
│       ├── MainNavigator.kt
│       └── MainNavigationState.kt
│
├── domain/                        # Domain Layer
│   ├── model/                     # Domain Models
│   │   ├── *Domain.kt
│   │   └── DomainEntity.kt
│   ├── repository/                # Repository Interfaces
│   │   ├── AuthRepository.kt
│   │   ├── HobbyRepository.kt
│   │   └── ...
│   └── usecase/                   # Use Cases (비즈니스 로직)
│       ├── CreateHobbyUseCase.kt
│       ├── GetHobbyDataUseCase.kt
│       └── ...
│
├── data/                          # Data Layer
│   ├── DataMapper.kt              # Entity → Domain Mapper
│   ├── di/
│   │   ├── RepositoryModule.kt
│   │   └── TokenModule.kt
│   ├── impl/                      # Repository 구현체
│   │   ├── AuthRepositoryImpl.kt
│   │   ├── HobbyRepositoryImpl.kt
│   │   └── ...
│   ├── model/                     # Data Entities
│   │   ├── *Entity.kt
│   │   └── DataLayerEntity.kt
│   └── remote/                    # DataSource Interfaces
│       ├── AuthDataSource.kt
│       └── ...
│
└── remote/                        # Remote Layer (Network)
    ├── RemoteMapper.kt            # Response → Entity Mapper
    ├── api/
    │   ├── service/               # Retrofit API Interfaces
    │   │   ├── AuthApi.kt
    │   │   ├── HobbyApi.kt
    │   │   └── ...
    │   └── interceptor/
    │       ├── TokenInterceptor.kt
    │       └── TokenProvider.kt
    ├── di/
    │   ├── NetworkModule.kt
    │   └── RemoteDataSourceModule.kt
    ├── impl/                      # DataSource 구현체
    │   ├── AuthDataSourceImpl.kt
    │   └── ...
    └── model/
        ├── request/               # API Request Models
        └── response/              # API Response Models
```

---

## Clean Architecture 레이어

### 데이터 흐름

```
User Action (UI)
    ↓
[Presentation] Route → ViewModel → UiState
    ↓
[Domain] UseCase → Repository Interface → Domain Model
    ↓
[Data] Repository Impl → DataSource → Entity
    ↓
[Remote] API Service → Request/Response
    ↓
Server
```

### Mapper 체인

```
API Response (JSON)
    ↓ RemoteMapper.toData()
Entity (data/model/)
    ↓ DataMapper.toDomain()
Domain Model (domain/model/)
    ↓ .toPresentation()
UI Model (presentation/{feature}/model/)
```

---

## 코딩 규칙

### 1. 네이밍 규칙

- **클래스**: PascalCase (`HomeViewModel`, `AuthRepositoryImpl`)
- **함수/메서드**: camelCase (`navigateHome()`, `toggleEditMode()`)
- **상수**: UPPER_SNAKE_CASE — `companion object` 내 정의
- **Private MutableState**: 언더스코어 접두사 camelCase (`_errorFlow`, `_uiState`)
- **Composable 함수**: PascalCase (`HomeScreen()`, `HomeRoute()`)
- **내부 컴포넌트**: `private fun` + PascalCase (`HomeContent()`, `HomeLoading()`)

```kotlin
// Private SharedFlow 패턴
private val _errorFlow = MutableSharedFlow<Throwable>()
val errorFlow get() = _errorFlow.asSharedFlow()

// 상수 - companion object 내 UPPER_SNAKE_CASE
companion object {
    private const val KAKAO_LOGIN_TYPE = "kakao"
}
```

**파일별 네이밍 컨벤션:**

| 종류 | 패턴 | 예시 |
|------|------|------|
| ViewModel | `{Feature}ViewModel` | `HomeViewModel` |
| UiState | `{Feature}UiState` | `HomeUiState` |
| UiEffect | `{Feature}UiEffect` | `HomeUiEffect` |
| Route (Composable) | `{Feature}Route` | `HomeRoute` |
| Screen (Composable) | `{Feature}Screen` | `HomeScreen` |
| NavKey | `{Feature}` (data class/object) | `Home`, `RecordRoutine` |
| UseCase | `{Verb}{Feature}UseCase` | `GetHobbyDataUseCase` |

---

### 2. ViewModel 패턴

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHobbyDataUseCase: GetHobbyDataUseCase,
    private val navigator: Navigator,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    // UiState: sealed interface → StateFlow
    val uiState: StateFlow<HomeUiState> =
        flow { emit(getHobbyDataUseCase()) }
            .map { data -> HomeUiState.Success(data = data) }
            .catch { throwable -> _errorFlow.emit(throwable) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading,
            )

    // suspend UseCase - launchIn 패턴
    fun saveRecord(params: RecordParams) {
        flow { emit(saveRecordUseCase(params)) }
            .onEach { _uiState.update { state -> state.copy(isSaved = true) } }
            .catch { _errorFlow.emit(it) }
            .launchIn(viewModelScope)
    }

    // Navigation
    fun navigateToRecord() = viewModelScope.launch {
        navigator.navigate(RecordRoutine())
    }
}
```

**Analytics 로깅:**

```kotlin
// Route에서 화면 진입 시
LaunchedEffect(Unit) {
    viewModel.logEvent("home_screen")
}
```

---

### 3. UiState 패턴

`sealed interface` + `@Stable` / `@Immutable` 사용. `data class` 단일 UiState 금지.

```kotlin
@Stable
sealed interface HomeUiState {
    @Immutable
    data object Loading : HomeUiState

    @Immutable
    data object Empty : HomeUiState

    @Immutable
    data class Success(
        val hobbies: ImmutableList<HobbyUiModel> = persistentListOf(),
        val isEditMode: Boolean = false,
    ) : HomeUiState
}
```

**상태 변경 시** `.copy()` 또는 새 인스턴스 생성:

```kotlin
// Success 상태 내부 업데이트
_uiState.update { current ->
    if (current is HomeUiState.Success) {
        current.copy(isEditMode = !current.isEditMode)
    } else current
}
```

---

### 4. UiEffect 패턴

에러가 아닌 일회성 UI 효과(Toast, 스낵바 메시지 등)에만 사용. 에러는 `errorFlow`로 처리.

```kotlin
@Stable
sealed interface HomeUiEffect {
    @Immutable
    data object Idle : HomeUiEffect

    @Immutable
    data class ShowToast(val message: String) : HomeUiEffect
}
```

---

### 5. Screen 구조 (Route / Screen 분리)

- **`{Feature}Route`**: ViewModel 주입, 에러 처리, SideEffect 수집 담당
- **`{Feature}Screen`**: 순수 상태 기반 렌더링 — ViewModel 직접 접근 금지
- **`private fun`**: 세부 UI 컴포넌트
- **파라미터 순서**: 필수 상태/데이터 → 콜백 함수 → `Modifier = Modifier` (마지막)
- **Preview**: 파일 끝에 `@Preview`, `private fun` 선언

```kotlin
// Route - ViewModel 주입 및 에러 처리
@Composable
fun HomeRoute(
    onNavigateToRecord: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.errorFlow.collectLatest { throwable ->
            // 에러 처리 (스낵바 등)
        }
    }

    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        HomeScreen(
            uiState = uiState,
            onNavigateToRecord = onNavigateToRecord,
            onToggleEdit = viewModel::toggleEditMode,
        )
    }
}

// Screen - 순수 상태 기반
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onNavigateToRecord: () -> Unit,
    onToggleEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // UI only - No ViewModel reference
    when (uiState) {
        is HomeUiState.Loading -> HomeLoading()
        is HomeUiState.Empty -> HomeEmpty()
        is HomeUiState.Success -> HomeContent(uiState = uiState, ...)
    }
}

// 내부 컴포넌트 - private fun
@Composable
private fun HomeContent(
    uiState: HomeUiState.Success,
    modifier: Modifier = Modifier,
) { ... }

// Preview
@Preview
@Composable
private fun PreviewHomeScreen() {
    HomeScreen(
        uiState = HomeUiState.Success(),
        onNavigateToRecord = {},
        onToggleEdit = {},
    )
}
```

---

### 6. UseCase 패턴

`operator fun invoke()` 사용.

```kotlin
// Flow 반환 UseCase
interface GetHobbyDataUseCase {
    operator fun invoke(): Flow<List<HobbyDomain>>
}

// suspend UseCase
interface SaveRecordUseCase {
    suspend operator fun invoke(params: RecordParams): RecordDomain
}

// 구현체
class GetHobbyDataUseCaseImpl @Inject constructor(
    private val repository: HobbyRepository
) : GetHobbyDataUseCase {
    override fun invoke(): Flow<List<HobbyDomain>> = repository.getHobbies()
}
```

---

### 7. Repository 패턴

```kotlin
// Interface (domain/repository/)
interface HobbyRepository {
    fun getHobbies(): Flow<List<HobbyDomain>>
    suspend fun saveHobby(params: HobbyParams): HobbyDomain
}

// Implementation (data/impl/)
class HobbyRepositoryImpl @Inject constructor(
    private val dataSource: HobbyDataSource
) : HobbyRepository {
    override fun getHobbies(): Flow<List<HobbyDomain>> =
        dataSource.fetchHobbies()
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveHobby(params: HobbyParams): HobbyDomain =
        dataSource.saveHobby(params).toDomain()
}
```

---

### 8. Mapper 구현

#### RemoteMapper (Response → Entity)

```kotlin
data class HobbyResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("items") val items: List<ItemResponse>? = emptyList()
) : RemoteMapper<HobbyEntity> {
    override fun toData(): HobbyEntity = HobbyEntity(
        id = id ?: 0,
        name = name ?: "",
        items = items?.map { it.toData() } ?: emptyList()
    )
}
```

#### DataMapper (Entity → Domain)

```kotlin
data class HobbyEntity(
    val id: Int,
    val name: String,
    val items: List<ItemEntity>
) : DataMapper<HobbyDomain> {
    override fun toDomain(): HobbyDomain = HobbyDomain(
        id = id,
        name = name,
        items = items.map { it.toDomain() }
    )
}
```

#### Presentation Mapper (Domain → UI)

```kotlin
fun HobbyDomain.toPresentation(): HobbyUiModel = HobbyUiModel(
    id = id,
    displayName = name,
    imageResId = getDrawableResId(imageCode)
)
```

---

### 9. Navigation (Navigation3 Type-safe)

```kotlin
// NavKey 정의
@Serializable
data class RecordRoutine(
    val params: RecordParams? = null,
    val mode: ScreenMode = ScreenMode.DEFAULT
) : NavKey

// 파라미터 전달용 data class
@Serializable
data class RecordParams(
    val hobbyId: Long,
    val hobbyName: String
)

// EntryProvider에서 사용
entry<RecordRoutine> { backStackEntry ->
    RecordRoutineRoute(
        params = backStackEntry.key.params,
        onBack = { navigator.goBack() }
    )
}
```

**Navigator:**

```kotlin
class Navigator(val state: MainNavigationState) {
    fun navigate(route: NavKey)
    fun goBack()
    fun replaceWith(route: NavKey)
    fun resetTo(route: NavKey, preloadStack: List<NavKey> = emptyList())
}
```

**Top-level Destinations (Bottom Bar 탭):**

```kotlin
val TOP_LEVEL_DESTINATIONS = mapOf(
    Home to BottomNavItem(...),
    Discovery to BottomNavItem(...),
    Story to BottomNavItem(...),
    MyPage to BottomNavItem(...)
)
```

---

### 10. ScreenMode 패턴 (Forday 고유)

```kotlin
enum class ScreenMode {
    ONBOARDING,  // 온보딩: 선택 즉시 ViewModel 저장
    DEFAULT      // 수정 모드: 로컬 상태 관리 → onNext에서만 저장
}

@Composable
fun SelectTimeRoute(
    params: TimeParams?,
    mode: ScreenMode,
    viewModel: SelectTimeViewModel = hiltViewModel()
) {
    val localValue = remember(params) {
        mutableStateOf(
            if (mode == ScreenMode.DEFAULT) params?.minutes else null
        )
    }

    val currentValue = if (mode == ScreenMode.DEFAULT) {
        localValue.value
    } else {
        viewModel.uiState.collectAsStateWithLifecycle().value.minutes
    }

    SelectTimeScreen(
        value = currentValue,
        onValueChange = { newValue ->
            if (mode == ScreenMode.DEFAULT) localValue.value = newValue
            else viewModel.updateMinutes(newValue)
        },
        onNext = { finalValue ->
            if (mode == ScreenMode.DEFAULT) viewModel.updateMinutes(finalValue)
            viewModel.navigateToNext()
        }
    )
}
```

---

### 11. DI (Hilt) 모듈 구성

- **`@Binds`**: 인터페이스 → 구현 바인딩 (선호)
- **`@Provides`**: 복잡한 인스턴스 생성에만 사용
- **Scope**: `@Singleton` (Repository, UseCase), `@ViewModelScoped` (ViewModel 종속 의존성)
- **`@InstallIn`**: `SingletonComponent::class` (앱 수준), `ViewModelComponent::class` (ViewModel 수준)

```kotlin
// @Binds 사용 (선호)
@InstallIn(SingletonComponent::class)
@Module
internal abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideHobbyRepository(
        impl: HobbyRepositoryImpl,
    ): HobbyRepository
}

// @Provides 사용 (복잡한 생성)
@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = ...
}

// @ViewModelScoped
@Module
@InstallIn(ViewModelComponent::class)
internal abstract class NavigatorModule {
    @Binds
    @ViewModelScoped
    abstract fun bindNavigator(impl: NavigatorImpl): Navigator
}
```

---

### 12. 에러 처리 / Exception 패턴

- **`errorFlow`**: `MutableSharedFlow<Throwable>` — ViewModel에서 에러 발생 시 emit
- **`try-catch` 위치**: Flow 연산자 `.catch { }` 사용
- **사용자 노출**: Route에서 `collectLatest`로 수집 후 스낵바/토스트 표시

```kotlin
// ViewModel - errorFlow 패턴
private val _errorFlow = MutableSharedFlow<Throwable>()
val errorFlow get() = _errorFlow.asSharedFlow()

val uiState: StateFlow<HomeUiState> =
    flow { emit(getHobbyDataUseCase()) }
        .map { HomeUiState.Success(it) }
        .catch { throwable -> _errorFlow.emit(throwable) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Loading)

// Route - 에러 수집
LaunchedEffect(Unit) {
    viewModel.errorFlow.collectLatest { throwable ->
        // Toast 또는 Snackbar 표시
    }
}

// Repository - 도메인 에러
override suspend fun getHobby(id: Long): HobbyDomain =
    dataSource.fetchHobby(id).toDomain() ?: error("Hobby not found: $id")
```

---

### 13. Coroutine / Flow 사용 방식

- **`StateFlow`**: UI 상태 (초기값 필수, 최신값 유지)
- **`SharedFlow`**: 에러, 일회성 이벤트
- **`viewModelScope`**: ViewModel 메서드 내 코루틴
- **`collectAsStateWithLifecycle`**: Composable에서 반드시 사용 (`collectAsState()` 금지)
- **주요 연산자**: `.map`, `.catch`, `.combine`, `.stateIn`, `.launchIn`, `.onEach`

```kotlin
// Composable - collectAsStateWithLifecycle 필수
val uiState by viewModel.uiState.collectAsStateWithLifecycle()

// Flow → StateFlow (stateIn)
val uiState: StateFlow<HomeUiState> =
    getHobbyDataUseCase()
        .map { it.toHomeUiState() }
        .catch { _errorFlow.emit(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading,
        )

// combine으로 여러 Flow 결합
init {
    combine(
        getHobbiesUseCase(),
        getRecordsUseCase(),
    ) { hobbies, records ->
        HomeUiState.Success(hobbies = hobbies.toPersistentList(), records = records.toPersistentList())
    }
        .catch { _errorFlow.emit(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Loading)
}
```

---

### 14. sealed class / enum 사용 기준

- **`sealed interface`**: UiState, UiEffect — 상태/이벤트 모델링
- **`enum class`**: 고정된 상수 집합 (`ScreenMode`, `TopAppBarType` 등)
- **`sealed class`**: 사용 지양 — `sealed interface` 선호
- UiState 내 각 subtype에 `@Immutable` 필수

```kotlin
// UiState - sealed interface
@Stable
sealed interface RecordRoutineUiState {
    @Immutable data object Loading : RecordRoutineUiState
    @Immutable data object Empty : RecordRoutineUiState
    @Immutable data class Success(
        val routines: ImmutableList<RoutineUiModel> = persistentListOf(),
        val selectedRoutine: RoutineUiModel? = null,
    ) : RecordRoutineUiState
}

// UiEffect - sealed interface
@Stable
sealed interface RecordRoutineUiEffect {
    @Immutable data object Idle : RecordRoutineUiEffect
    @Immutable data class ShowToast(val message: String) : RecordRoutineUiEffect
}

// Enum - 고정 옵션
enum class ScreenMode { ONBOARDING, DEFAULT }
```

---

### 15. Import 순서

1. 표준 라이브러리 (`java.*`, `kotlin.*`)
2. Android (`android.*`, `androidx.*`)
3. Third-party (`com.google.*`, `dagger.*`, `kotlinx.*`, `com.kakao.*`, `timber.*`)
4. 프로젝트 내부 (`com.forday.app.*`)

각 그룹 내 알파벳순 정렬. Wildcard import 미사용.

```kotlin
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.domain.model.HobbyDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
```

---

### 16. 주석 스타일

- **KDoc**: 파라미터 설명이 필요한 public/internal 함수에 사용 (`@param`)
- **TODO**: `// TODO 설명` 형식
- **섹션 구분 주석**: 미사용
- **코드 설명 주석**: 최소화 (self-documenting code 선호)

```kotlin
/**
 * @param title 화면 상단에 표시할 제목
 * @param currentStep 현재 온보딩 단계
 * @param totalSteps 전체 온보딩 단계 수
 * @param content 화면 고유 컨텐츠
 */
@Composable
fun OnboardingLayout(
    title: String,
    currentStep: Int,
    totalSteps: Int = 5,
    content: @Composable () -> Unit,
) { ... }

// TODO 형식
// TODO 서버에서 실시간으로 불러오기
```

---

### 17. Edge-to-Edge 적용 방식

- **WindowInsets 처리**: `systemBarsPadding()` Modifier 사용 (Route Composable 레벨)
- **시스템 바 색상**: `Theme.kt`의 `SideEffect` 내 `WindowCompat.getInsetsController()`로 관리

```kotlin
// Route에서 systemBarsPadding 적용
Box(
    modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .systemBarsPadding()
) {
    HomeScreen(uiState = uiState, ...)
}
```

---

### 18. 리소스 파일 네이밍

- **drawable**: snake_case (`ic_arrow_back`, `ic_hobby_book`)
- **string**: 계층적 snake_case (`app_name`, `home_screen_title`)
- **color**: `ForDayTheme.color` Kotlin object 사용, `colors.xml` 미사용

```kotlin
// 색상 사용
ForDayTheme.color.Primary001     // #FF9447 (주황)
ForDayTheme.color.Neutral900     // #1E1E1E (검정)
ForDayTheme.color.Gray03         // Border

// @StringRes 파라미터 패턴
@Composable
fun FordayTopAppBar(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
) {
    Text(text = stringResource(id = titleRes))
}
```

---

### 19. 테스트 코드 작성 방식

- **테스트 위치**: `src/test/java` (단위), `src/androidTest/java` (UI)
- **테스트 네이밍**: `{Subject}Test`
- **테스트 프레임워크**: JUnit5 + Kotest (BDD 스타일), Roborazzi (스크린샷 UI 테스트)
- **Mock 라이브러리**: Turbine (Flow 테스트), Fake 객체 직접 구현 선호

```kotlin
// Kotest BDD 스타일 - Repository 테스트
internal class HobbyRepositoryImplTest : BehaviorSpec() {
    init {
        Given("취미 데이터가 존재한다") {
            When("취미 목록을 조회한다") {
                val result = repository.getHobbies().first()
                Then("취미 목록을 반환한다") {
                    result.size shouldBe 2
                }
            }
        }
    }
}

// Turbine - Flow 테스트
@Test
fun `취미 선택 시 UiState 업데이트 확인`() = runTest {
    viewModel.uiState.test {
        assertEquals(HomeUiState.Loading, awaitItem())
        viewModel.selectHobby(hobbyId = 1L)
        val success = awaitItem() as HomeUiState.Success
        assertEquals(1L, success.selectedHobbyId)
        cancelAndIgnoreRemainingEvents()
    }
}

// Roborazzi 스크린샷 테스트
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class HomeScreenTest {
    @Test
    fun `홈 화면 스크린샷 테스트`() {
        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState.Success(hobbies = sampleHobbies),
                onNavigateToRecord = {},
                onToggleEdit = {},
            )
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
```

---

## 주요 컴포넌트

### OnboardingLayout (공통 레이아웃)

```kotlin
/**
 * @param title 화면 상단 제목
 * @param currentStep 현재 단계
 * @param totalSteps 전체 단계 수
 * @param mode ONBOARDING / DEFAULT
 * @param onBack 뒤로가기 콜백
 * @param content 화면 고유 컨텐츠 슬롯
 */
@Composable
fun OnboardingLayout(
    title: String,
    currentStep: Int,
    totalSteps: Int = 5,
    mode: ScreenMode = ScreenMode.ONBOARDING,
    onBack: () -> Unit = {},
    content: @Composable () -> Unit,
)
```

### BottomNextButton

```kotlin
@Composable
fun BottomNextButton(
    text: String = "다음",
    state: BottomButtonState,  // ENABLED, DISABLED
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
)
```

---

## API 구조

### API Service (Retrofit)

```kotlin
interface HobbyApi {
    @GET("api/hobbies")
    suspend fun getHobbies(): List<HobbyResponse>

    @POST("api/hobbies")
    suspend fun createHobby(
        @Body request: CreateHobbyRequest
    ): HobbyResponse
}
```

---

## 데이터 저장 (DataStore)

### 저장 항목

```kotlin
// UserLocalDataSource.kt
- accessToken: String?
- refreshToken: String?
- isOnboardingCompleted: Boolean
- isNicknameSet: Boolean
- onboardingData: OnboardingDataEntity
```

### UseCase 패턴으로 접근

```kotlin
class SaveOnboardingDataUseCase @Inject constructor(
    private val dataSource: UserLocalDataSource
) {
    suspend operator fun invoke(data: OnboardingData) {
        dataSource.saveOnboardingData(data)
    }
}
```

---

## Firebase 통합

### Analytics 로깅

```kotlin
// Route에서 화면 진입 시
LaunchedEffect(Unit) {
    viewModel.logEvent("home_screen")
}

// 사용자 액션 로깅
viewModel.logEvent("selected_hobby_card_독서")
```

### Crashlytics

```kotlin
// errorFlow에서 자동 수집 (BaseViewModel 또는 Route에서 처리)
viewModel.errorFlow.collectLatest { throwable ->
    FirebaseCrashlytics.getInstance().recordException(throwable)
}
```

---

## 체크리스트

### UI 작성 시
- [ ] `{Feature}Route` / `{Feature}Screen` 분리 적용
- [ ] Route에서 `errorFlow` 수집 및 처리
- [ ] ScreenMode 분기 처리 (ONBOARDING/DEFAULT)
- [ ] `@Preview` 함수 작성 (파일 끝, `private fun`)
- [ ] `systemBarsPadding()` 적용 (Route 레벨)
- [ ] `collectAsStateWithLifecycle()` 사용 (`collectAsState()` 금지)

### ViewModel 작성 시
- [ ] `@HiltViewModel` + `@Inject` 추가
- [ ] `_errorFlow: MutableSharedFlow<Throwable>` 선언
- [ ] UiState: `sealed interface + @Stable/@Immutable`
- [ ] `StateFlow` 노출 (`stateIn()` 사용)
- [ ] Flow 체인: `.map { }.catch { _errorFlow.emit(it) }.stateIn(...)`
- [ ] Navigation: `navigator.navigate()` 직접 호출

### API 연동 시
- [ ] Response → Entity → Domain 매핑 체인
- [ ] Nullable 필드 처리 (Elvis 연산자)
- [ ] 리스트 매핑 (`?.map { it.toData() } ?: emptyList()`)
- [ ] `.catch { _errorFlow.emit(it) }` 에러 처리

### DI 등록 시
- [ ] `@Binds` 우선 사용 (구현 바인딩)
- [ ] Repository, UseCase: `@Singleton`
- [ ] `@Provides`는 복잡한 생성에만 사용

### Navigation 시
- [ ] NavKey에 `@Serializable` 추가
- [ ] 복잡한 파라미터는 별도 `data class`로 정의
- [ ] EntryProvider에 `entry<>` 추가
- [ ] `Navigator`로 화면 전환

### 테스트 작성 시
- [ ] 새 UiState/Domain 모델은 단위 테스트 필수
- [ ] Repository/UseCase: Fake 객체 + Kotest BehaviorSpec
- [ ] Flow 테스트: Turbine 사용
- [ ] UI 변경 시 Roborazzi 스크린샷 갱신

---

## 주의사항

### 1. Null Safety
- Response: nullable + 기본값 (Elvis 연산자 처리)
- Entity/Domain: non-null
- UI: nullable은 의미있을 때만

### 2. UiState 불변성
- `ImmutableList`, `ImmutableSet` 사용 (kotlinx-immutable-collections)
- `@Immutable`, `@Stable` 데코레이션 필수
- 상태 변경 시 `.copy()` 또는 새 인스턴스 생성

### 3. Flow 구독 방식
- Composable에서 반드시 `collectAsStateWithLifecycle()` 사용
- 에러는 `_errorFlow`로 emit 후 Route에서 처리

### 4. Composable 구조 원칙
- `{Feature}Route`에서만 ViewModel 주입 및 에러/Effect 처리
- `{Feature}Screen`은 순수 상태 기반 (ViewModel 직접 접근 금지)
- Preview는 항상 파일 끝, `private fun`

### 5. DI 규칙
- UseCase, Repository는 `@Singleton`
- Composable 내 ViewModel은 반드시 `hiltViewModel()` 사용
- 인터페이스 바인딩은 `@Binds` 우선

### 6. ScreenMode 분기
- ONBOARDING: ViewModel 즉시 저장
- DEFAULT: 로컬 상태 → onNext에서만 저장
- onBack 시 로컬 상태는 초기값으로 복원

### 7. 테스트
- 새 도메인 모델/UiState는 단위 테스트 필수
- Repository/UseCase는 Fake 객체 + Kotest BehaviorSpec
- UI 변경 시 Roborazzi 스크린샷 갱신

---

## 빌드 설정

### 환경 변수 (local.properties)

```properties
KAKAO_API_KEY=your_kakao_api_key
KAKAO_REDIRECT_URI=your_redirect_uri
BASE_URL=https://api.example.com/
```

### 주요 의존성

```kotlin
// Compose
androidx.compose.bom
androidx.compose.material3

// Navigation3
androidx.navigation3.runtime
androidx.navigation3.ui

// Hilt
com.google.dagger:hilt-android
androidx.hilt:hilt-navigation-compose

// Network
com.squareup.retrofit2:retrofit
org.jetbrains.kotlinx:kotlinx-serialization-json
com.squareup.okhttp3:logging-interceptor

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-core
org.jetbrains.kotlinx:kotlinx-coroutines-android

// Immutable Collections
org.jetbrains.kotlinx:kotlinx-collections-immutable

// Coil
io.coil-kt:coil-compose

// Firebase
com.google.firebase:firebase-bom
com.google.firebase:firebase-crashlytics
com.google.firebase:firebase-analytics
com.google.firebase:firebase-messaging

// DataStore
androidx.datastore:datastore-preferences

// Kakao
com.kakao.sdk:v2-user

// Timber
com.jakewharton.timber:timber

// Lottie
com.airbnb.android:lottie-compose

// Testing
junit:junit
io.kotest:kotest-runner-junit5
app.cash.turbine:turbine
io.mockk:mockk
io.github.takahirom.roborazzi:roborazzi
```

---

**최종 업데이트**: 2026-04-27
