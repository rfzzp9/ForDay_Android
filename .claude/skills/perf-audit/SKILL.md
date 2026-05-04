---
name: perf-audit
description: >
  Forday Android 성능 병목 감사 스킬. performance-auditor 에이전트 전용.
  Compose recomposition 과다, remember/derivedStateOf 누락, LazyList key 누락,
  collectAsState 사용(생명주기 누수), Coroutine 스코프 오용, Context 메모리 누수,
  Coil 이미지 사이즈 미지정을 감사한다.
---

# Performance Audit

performance-auditor 에이전트가 Forday Android 성능 병목을 감사할 때 따르는 체크리스트.

## 감사 항목

### 1. Compose Recomposition — remember 누락 (MAJOR)

```bash
# Composable 내부에서 객체 생성(remember 없이) 탐지
grep -rn "= listOf\|= mutableListOf\|= object\|= Pair\|= Triple" \
  app/src/main/java/com/forday/app/presentation/ --include="*.kt" \
  | grep -v "val\|var\|remember\|//\|test"
```

Composable 스코프에서 `remember` 없이 매번 새 객체 생성 → 매 recomposition마다 재계산. MAJOR.

```kotlin
// 위반
@Composable fun Foo() {
    val list = listOf(1, 2, 3)  // 매 recomposition마다 새 리스트 생성
}

// 수정
@Composable fun Foo() {
    val list = remember { listOf(1, 2, 3) }
}
```

### 2. derivedStateOf 누락 (MINOR)

```bash
# state에서 파생 계산을 remember 없이 하는 패턴 탐지
grep -rn "val.*=.*state\.\|val.*=.*uiState\." \
  app/src/main/java/com/forday/app/presentation/ --include="*.kt" \
  | grep -v "remember\|by\|collect\|//\|test"
```

상태에서 파생 값을 계산할 때 `remember { derivedStateOf { } }` 없으면 MINOR.

### 3. LazyList key 누락 (MAJOR)

```bash
# LazyColumn/LazyRow에서 key 파라미터 누락 탐지
grep -rn "items(\|item {" \
  app/src/main/java/com/forday/app/presentation/ --include="*.kt" \
  | grep -v "key\|//\|test"
```

`items(list)` 호출에 `key = { it.id }` 없으면 MAJOR — 리스트 변경 시 전체 재구성.

```kotlin
// 위반
LazyColumn { items(list) { item -> ItemCard(item) } }

// 수정
LazyColumn { items(list, key = { it.id }) { item -> ItemCard(item) } }
```

### 4. collectAsState 사용 (MAJOR)

```bash
# collectAsState (생명주기 무관) 사용 탐지
grep -rn "\.collectAsState()" \
  app/src/main/java/com/forday/app/presentation/ --include="*.kt" \
  | grep -v "//\|test"
```

발견 시: MAJOR — `collectAsStateWithLifecycle()`로 교체. 생명주기 무관 구독은 백그라운드에서도 리소스 소비.

### 5. GlobalScope / 스코프 불일치 (CRITICAL)

```bash
# GlobalScope 사용 탐지
grep -rn "GlobalScope\." \
  app/src/main/java/ --include="*.kt" | grep -v "//\|test"

# ViewModel 외부에서 viewModelScope 참조 탐지
grep -rn "viewModelScope" \
  app/src/main/java/com/forday/app/ --include="*.kt" \
  | grep -v "ViewModel\|//\|test"
```

`GlobalScope` 사용: CRITICAL — 취소 불가능한 코루틴 누수.

### 6. Main 스레드 Blocking (CRITICAL)

```bash
# suspend 함수 없이 blocking 호출 탐지
grep -rn "Thread.sleep\|runBlocking\|\.execute()\|\.get()" \
  app/src/main/java/ --include="*.kt" | grep -v "//\|test"

# IO 작업을 Dispatchers.Main에서 실행 탐지
grep -rn "withContext(Dispatchers.Main)" \
  app/src/main/java/ --include="*.kt" \
  | grep -v "//\|test"
```

`runBlocking` in Composable/ViewModel: CRITICAL.

### 7. Context 메모리 누수 (MAJOR)

```bash
# ViewModel에서 Context 직접 캡처 탐지
grep -rn "context\|Context\|applicationContext" \
  app/src/main/java/com/forday/app/presentation/ --include="*ViewModel*" \
  | grep -v "//\|test\|Application"
```

ViewModel에서 Activity Context 캡처: MAJOR — Activity 종료 후에도 GC 불가.  
Application Context(`@ApplicationContext`) 사용은 허용.

### 8. Coil 이미지 사이즈 미지정 (MINOR)

```bash
# AsyncImage/rememberAsyncImagePainter에서 size 파라미터 확인
grep -rn "AsyncImage\|rememberAsyncImagePainter" \
  app/src/main/java/ --include="*.kt" \
  | grep -v "size\|fillMax\|//\|test"
```

사이즈 미지정 시 원본 크기 디코딩 → 메모리 낭비. MINOR.

### 9. 불필요한 Recomposition — 불안정한 람다 (MINOR)

```bash
# Composable 파라미터로 인라인되지 않은 함수 참조 탐지
grep -rn "onClick = viewModel::\|onAction = viewModel::" \
  app/src/main/java/com/forday/app/presentation/ --include="*ScreenRoot*"
```

`viewModel::method` 참조는 매 recomposition마다 새 인스턴스 → MINOR (명시적 람다로 `remember` 래핑 가능).

## 리포트 형식

```markdown
## 성능 병목 감사 결과

### CRITICAL
- [파일경로:라인] 문제 설명
  영향: 예상 프레임 드롭 / 메모리 누수 / 배터리 소모
  수정 전: ...
  수정 후: ...

### MAJOR
- [파일경로:라인] 문제 설명
  영향: ...
  수정 방향: ...

### MINOR
- [파일경로:라인] 개선 권장

### 통과 항목
- collectAsStateWithLifecycle 사용 ✓
- ...
```
