---
name: android-dev
description: >
  Forday Android 앱 개발의 모든 작업을 처리하는 오케스트레이터 스킬.
  새 기능 추가, 화면 구현, ViewModel/UseCase/Repository 작성, API 연동,
  Clean Architecture 레이어 구성, Navigation 연결, 버그 수정, 리팩토링,
  Compose UI 작업, 개발 중 단일 변경 즉시 검토를 담당한다.
  "기능 만들어줘", "화면 추가해줘", "ViewModel 작성해줘", "버그 수정해줘",
  "리팩토링해줘", "API 연동해줘", "다시 실행해줘", "업데이트해줘", "수정해줘",
  "보완해줘", "방금 만든 코드 확인해줘", "이 변경사항 바로 봐줘" 등의 요청에 즉시 트리거.
  단, "전체 리뷰", "종합 감사", "PR 리뷰", "코드베이스 검사" 등 대규모 독립 감사 요청은
  code-review 스킬로 위임한다. 코드 작성·수정·디버깅 요청이면 이 스킬을 사용할 것.
---

# Android Dev 오케스트레이터

Forday Android 개발 요청을 분류하고 전문 에이전트 팀을 조율하여 완성도 높은 코드를 산출한다.

## 실행 모드

| 요청 유형 | 실행 모드 | 에이전트 |
|---------|---------|--------|
| 기능 개발 / 리팩토링 | 에이전트 팀 | feature-builder + code-reviewer |
| 버그 수정 | 에이전트 팀 | bug-hunter + code-reviewer |
| 코드 리뷰만 | 서브 에이전트 | code-reviewer |

## Phase 0: 컨텍스트 확인

```
_workspace/ 확인:
  없음              → 초기 실행 (Phase 1)
  있음 + 부분 수정  → 관련 에이전트만 재호출
  있음 + 새 요청   → mv _workspace _workspace_prev/ → Phase 1
```

## Phase 1: 요청 분류

요청 내용을 읽고 유형을 판단한다:

- **기능 개발**: 새 화면, 새 기능, API 연동, 새 UseCase → feature-builder 주도 + code-reviewer 검증
- **버그 수정**: 크래시, 오류 동작, 데이터 불일치 → bug-hunter 주도 + code-reviewer 검증
- **단일 변경 검토**: 방금 작성한 코드 즉시 확인, 컨벤션 확인 → code-reviewer 단독
  (전체 코드베이스/PR 단위 종합 감사 요청은 code-review 스킬로 위임)
- **리팩토링**: 기존 코드 개선, 중복 제거 → code-reviewer 분석 후 feature-builder 적용

## Phase 2: 코드베이스 컨텍스트 파악

에이전트 실행 전 관련 기존 파일을 탐색한다:

```bash
# 유사 기능 파일 탐색
find app/src/main/java/com/forday/app -name "*{Feature}*"

# DI 모듈 현황
cat data/di/RepositoryModule.kt
cat remote/di/RemoteDataSourceModule.kt

# Navigation 현황
cat core/navigation/Route.kt
```

## Phase 3: 에이전트 실행

### 에이전트 팀 구성 (기능 개발/버그 수정)

```
1. TeamCreate(team_name="android-dev-team", members=[에이전트 목록])
2. TaskCreate(tasks with dependencies)
3. 팀원들이 SendMessage로 자체 조율
4. 결과 수집
```

에이전트 정의: `.Codex/agents/{name}.md`
모든 Agent 호출: `model: "opus"` 필수

### 서브 에이전트 (코드 리뷰)

```
Agent(
    subagent_type = "general-purpose",
    prompt = [code-reviewer 정의 내용] + [검토 대상 파일],
    model = "opus"
)
```

## Phase 4: 결과 검증

- [ ] 생성/수정 파일이 올바른 레이어 경로에 위치
- [ ] Hilt 모듈에 `@Binds` 등록 완료
- [ ] Navigation `entry<>` 등록 완료
- [ ] Mapper 체인 완결 (Response → Entity → Domain)
- [ ] code-reviewer의 CRITICAL/MAJOR 이슈 해소

## Phase 5: 사용자 보고

변경된 파일 목록과 각 파일의 역할을 간결하게 요약한다.

## 데이터 전달

- 에이전트 간: SendMessage (실시간) + 파일 (`_workspace/`)
- 파일 컨벤션: `{phase}_{agent}_{artifact}.{ext}`
- 최종 산출물: 실제 소스 경로에 직접 작성

## 에러 핸들링

- 에이전트 실패: 1회 재시도 후 오케스트레이터 직접 처리
- 컴파일 오류: bug-hunter에게 위임
- 아키텍처 이슈: code-reviewer → feature-builder 재작업

## 테스트 시나리오

### 정상 흐름 (기능 개발)
1. "홈 화면에 배너 표시 기능 추가해줘" 입력
2. feature-builder가 Remote → Presentation 레이어 구현
3. code-reviewer가 MVI 패턴, 매퍼 체인, DI 등록 검증
4. 이슈 없으면 파일 목록 + 역할 요약 보고

### 에러 흐름 (DI 누락)
1. feature-builder 구현 완료
2. code-reviewer가 Hilt 바인딩 누락 발견 (CRITICAL)
3. code-reviewer → feature-builder SendMessage로 수정 요청
4. feature-builder 수정 → code-reviewer 재검증 → 완료
