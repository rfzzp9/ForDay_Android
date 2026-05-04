# Commit Conventions

## 목적

커밋을 언제 만들고, 언제 나누고, 무엇을 포함할지 정한다.

## 가장 중요한 원칙

AI는 사용자가 명시적으로 요청한 경우에만 커밋한다.

예:

- "커밋해줘"
- "이 변경사항 커밋해"
- "스테이징하고 커밋해줘"
- "커밋 나눠서 만들어줘"

다음 요청은 커밋 요청이 아니다.

- "수정해줘"
- "작업해줘"
- "정리해줘"
- "검토해줘"
- "PR 준비해줘"

커밋 요청이 없으면 파일 수정과 검증까지만 하고, 커밋은 만들지 않는다.

## 커밋 전 필수 절차

커밋 요청을 받으면 아래 순서로 진행한다.

1. `git status --short`로 변경 파일을 확인한다.
2. 내가 만든 변경과 기존 변경을 구분한다.
3. `_workspace`, release artifact, local 설정 파일이 포함되어 있는지 확인한다.
4. 커밋을 하나로 할지 나눌지 판단한다.
5. 나눠야 할 변경이 섞여 있으면 사용자에게 분리 기준을 짧게 보고하거나, 명확한 경우 직접 나눠서 커밋한다.
6. staging 대상 파일을 명시적으로 지정한다.
7. 커밋 후 `git status --short --branch`와 `git log -1 --oneline`을 확인한다.

## 커밋을 반드시 나누는 경우

아래 중 하나라도 해당하면 기본적으로 나눈다.

- 앱 코드 변경과 문서 체계 변경이 함께 있다.
- 동작 변경과 리팩토링이 함께 있다.
- 버그 수정과 넓은 cleanup이 함께 있다.
- 포맷팅만 바뀐 파일이 실제 기능 변경과 섞여 있다.
- unrelated feature가 둘 이상 들어 있다.
- `_workspace` 리포트와 영구 문서 변경이 함께 있다.
- release artifact, keystore, `local.properties`, generated output이 실제 코드 변경과 섞여 있다.
- 의존성/빌드 설정 변경과 기능 구현이 함께 있다.
- 단순 rename/move와 내용 변경이 함께 커서 diff가 읽기 어렵다.
- 한 커밋 메시지에 `and`, `그리고`, `및`으로 서로 다른 목적을 붙여야 설명된다.

## 한 커밋으로 묶어도 되는 경우

아래 조건을 모두 만족하면 한 커밋으로 묶을 수 있다.

- 변경 목적이 하나다.
- 되돌릴 때 하나의 기능/수정만 되돌아간다.
- 리뷰어가 한 번에 흐름을 따라갈 수 있다.
- 관련 없는 포맷팅이나 cleanup이 없다.
- 임시 산출물이 포함되어 있지 않다.

Forday Android에서는 작은 vertical feature slice 하나를 한 커밋으로 묶을 수 있다.

예:

- `presentation/{feature}/`의 Route/Screen/ViewModel/UiState
- 해당 feature에 필요한 UseCase
- 기존 Repository interface/impl의 작은 메서드 추가
- 해당 API Response/Entity/Mapper
- 필요한 Navigation entry
- 필요한 Hilt binding

단, API 계약이 크거나 공통 컴포넌트 추출이 동반되면 분리한다.

## Forday Android 커밋 분리 기준

### 한 커밋 권장

- 작은 화면 하나 추가
- 작은 버그 하나 수정
- Mapper null 처리 하나 수정
- Navigation entry 누락 하나 수정
- DI binding 누락 하나 수정
- 해당 변경을 검증하는 테스트 추가

### 분리 권장

- 새 화면 추가 + 공통 디자인시스템 컴포넌트 추출
- API 연동 + 기존 Repository 구조 리팩토링
- 버그 수정 + 대량 파일 포맷팅
- Navigation 구조 변경 + unrelated 화면 수정
- 문서 허브 정리 + 앱 코드 수정
- `.agents/docs` 변경 + `.agents/skills` 구조 변경, 목적이 독립적일 때

## 커밋 타입

- `feat`: 사용자 기능 추가 또는 화면/API 흐름 추가
- `fix`: 버그 수정
- `refactor`: 동작 변화 없는 구조 개선
- `docs`: 문서만 변경
- `test`: 테스트만 추가/수정
- `chore`: 빌드, 설정, 의존성, 정리 작업
- `style`: 포맷팅만 변경, 동작 변화 없음

## 판단 질문

커밋 전 아래 질문에 답한다.

1. 이 커밋을 revert하면 하나의 의도만 되돌아가는가?
2. 커밋 메시지가 `type: short summary` 하나로 자연스럽게 설명되는가?
3. 리뷰어가 diff를 위에서 아래로 읽으며 하나의 흐름을 이해할 수 있는가?
4. 동작 변경 없는 리팩토링이 섞여 있지 않은가?
5. 포맷팅, generated file, 임시 파일이 섞여 있지 않은가?
6. 사용자 요청 범위를 벗어난 파일이 포함되어 있지 않은가?

하나라도 "아니오"라면 커밋을 나눈다.

## 예시

### 좋은 단일 커밋

```text
feat: add hobby detail screen
```

포함 가능:

- `presentation/hobbydetail/*`
- `domain/usecase/GetHobbyDetailUseCase.kt`
- `data/impl/HobbyRepositoryImpl.kt`의 작은 메서드 추가
- `remote/model/response/HobbyDetailResponse.kt`
- Navigation entry
- 필요한 DI binding

### 좋은 분리

```text
refactor: extract routine mapper helpers
feat: add routine detail screen
test: cover routine detail mapper defaults
```

### 나쁜 단일 커밋

```text
feat: add hobby detail and reformat onboarding and update docs
```

이 경우 feature, formatting, docs로 나눈다.

## staging 규칙

- `git add .`는 피한다.
- 커밋 목적에 맞는 파일만 명시적으로 staging한다.
- 이미 있던 사용자 변경은 커밋 범위에 섞지 않는다.
- 삭제 파일도 의도된 삭제인지 확인한다.
- `_workspace` 파일은 사용자가 요청했거나 리포트 보관이 목적일 때만 포함한다.

## 커밋하지 않는 경우

아래 경우에는 커밋 요청이 있어도 먼저 확인한다.

- 충돌 또는 merge 상태다.
- 빌드/테스트 실패 원인이 불분명하다.
- 사용자 변경과 내 변경이 같은 파일에 섞여 있다.
- secret, keystore, local 설정 파일이 staging 대상에 포함된다.
- 커밋을 어떻게 나눌지 판단하기 어렵다.
