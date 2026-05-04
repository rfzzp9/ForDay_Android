---
name: code-review
description: >
  Forday Android 종합 코드 리뷰 오케스트레이터 스킬.
  아키텍처, 보안 취약점, 성능 병목, 코드 스타일을 병렬로 감사하고
  결과를 하나의 우선순위 정렬 리포트로 통합한다.
  "종합 리뷰", "전체 코드 리뷰", "PR 리뷰", "코드 감사", "아키텍처 검사",
  "보안 검사", "성능 분석", "스타일 검사" 등의 요청에 즉시 트리거.
  "다시 리뷰", "리뷰 재실행", "이 파일들 리뷰해줘", "변경사항 리뷰" 요청에도 트리거.
  단순 개념 질문, 개발 중 단일 변경 즉시 확인("방금 만든 코드 봐줘")은 android-dev 스킬로 위임.
---

# Code Review 오케스트레이터

아키텍처·보안·성능·스타일 4개 감사를 병렬 실행하고 종합 리포트를 생성한다.

**실행 모드:** 서브 에이전트 (팬아웃/팬인) — 4개 감사는 독립적이므로 팀 통신 불필요

## Phase 0: 컨텍스트 확인

```
_workspace/ 확인:
  없음              → 초기 실행 (Phase 1)
  있음 + 재실행     → mv _workspace _workspace_prev/ → Phase 1
  있음 + 범위 변경  → mv _workspace _workspace_prev/ → Phase 1
```

## Phase 1: 감사 범위 결정

사용자 요청에서 범위를 파악한다:

| 요청 유형 | 범위 결정 방법 |
|---------|-------------|
| "전체 코드 리뷰" | `app/src/main/java/` 전체 |
| "PR 리뷰", "변경사항 리뷰" | `git diff main...HEAD --name-only`로 변경 파일 목록 추출 |
| 파일/디렉토리 지정 | 지정된 경로 사용 |
| 범위 미지정 | `git diff HEAD~1 --name-only`로 최근 커밋 변경 파일 |

범위 확정 후 `_workspace/scope.txt`에 저장한다.

## Phase 2: 4개 에이전트 병렬 실행

**실행 모드: 서브 에이전트 (run_in_background: true)**

```
4개 에이전트를 동시에 실행한다:

Agent(architecture-auditor, run_in_background=true, model="opus")
  → 감사 범위 + arch-audit 스킬 사용 지시
  → 산출물: _workspace/01_arch_report.md

Agent(security-auditor, run_in_background=true, model="opus")
  → 감사 범위 + security-audit 스킬 사용 지시
  → 산출물: _workspace/02_security_report.md

Agent(performance-auditor, run_in_background=true, model="opus")
  → 감사 범위 + perf-audit 스킬 사용 지시
  → 산출물: _workspace/03_perf_report.md

Agent(style-auditor, run_in_background=true, model="opus")
  → 감사 범위 + style-audit 스킬 사용 지시
  → 산출물: _workspace/04_style_report.md
```

4개 모두 완료될 때까지 대기한다.

## Phase 3: 결과 수집 및 통합

4개 리포트를 읽고 통합한다:

1. 각 리포트에서 CRITICAL/MAJOR/MINOR 이슈 목록 추출
2. 중복 이슈 제거 (동일 파일·동일 라인에 여러 감사가 발견한 경우 → 가장 높은 심각도 유지)
3. 회귀 확인: `_workspace_prev/`가 있으면 이전 리포트와 비교하여 신규/해소/잔존 구분

## Phase 4: 종합 리포트 생성

```markdown
# Forday Android 종합 코드 리뷰

**감사 범위:** {파일 수} 파일
**감사 일시:** {날짜}
**이전 리뷰 대비:** 신규 N건 / 해소 N건 / 잔존 N건 (이전 리뷰 없으면 생략)

## 요약

| 등급 | 아키텍처 | 보안 | 성능 | 스타일 | 합계 |
|------|--------|------|------|------|------|
| CRITICAL | N | N | N | N | **N** |
| MAJOR | N | N | N | N | **N** |
| MINOR | N | N | N | N | **N** |

## 우선 액션 아이템
(CRITICAL → MAJOR 순, 파일경로:라인 포함)
1. [CRITICAL] {파일:라인} — {이슈} → {수정 방향}
...

## 아키텍처 감사
{01_arch_report.md 내용}

## 보안 취약점 감사
{02_security_report.md 내용}

## 성능 병목 감사
{03_perf_report.md 내용}

## 코드 스타일 감사
{04_style_report.md 내용}
```

최종 리포트를 `_workspace/final_review_report.md`에 저장하고 콘솔에도 출력한다.

## 에러 핸들링

- 에이전트 미완료: 120초 대기 후 재시도 1회, 재실패 시 해당 섹션 "감사 미완료" 표시
- 리포트 파일 미생성: 해당 에이전트 재실행
- git 명령 실패: 사용자에게 범위를 직접 요청

## 테스트 시나리오

### 정상 흐름
1. "전체 코드 리뷰해줘" 입력
2. 범위: `app/src/main/java/` 전체
3. 4개 에이전트 병렬 실행 → 각자 `_workspace/0N_*_report.md` 생성
4. 오케스트레이터가 4개 리포트 통합 → `_workspace/final_review_report.md`
5. 콘솔에 요약 테이블 + 우선 액션 아이템 출력

### 에러 흐름
1. security-auditor 실패 → 1회 재시도
2. 재실패 → 보안 섹션 "감사 미완료 — 수동 확인 필요" 표시
3. 나머지 3개 섹션으로 최종 리포트 생성
