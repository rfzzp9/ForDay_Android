# Decision Guide

## 목적

요청을 받았을 때 어떤 문서와 skill을 먼저 사용할지 빠르게 결정한다.

## 기본 판단

| 요청 유형 | 사용할 skill | 먼저 볼 workflow |
| --- | --- | --- |
| 기능 추가, 화면 추가, API 연동, 리팩토링 | `android-dev` | `.agents/docs/workflows/feature-development.md` |
| 크래시, 오동작, 데이터 불일치, Navigation/DI 오류 | `bug-analysis` | `.agents/docs/workflows/bugfix.md` |
| 전체 리뷰, PR 리뷰, 아키텍처/보안/성능/스타일 감사 | `code-review` | `.agents/docs/workflows/code-review.md` |
| 커밋 또는 PR 전 점검 | 요청 성격에 따라 선택 | `.agents/docs/workflows/pre-pr-check.md` |
| 이슈 생성, 이슈 브랜치 생성, PR 작성/수정 | 요청 성격에 따라 선택 | `.agents/docs/conventions/github-workflow-conventions.md` |

## 직접 답변해도 되는 경우

- 코드 변경이 필요 없는 개념 질문
- 파일 위치를 묻는 단순 질문
- 이미 문서에 있는 규칙을 설명하는 질문

## 사용자에게 먼저 물어봐야 하는 경우

- 요청이 기능 개발인지 버그 수정인지 불명확하다.
- 화면, API, 저장소 중 어느 범위를 바꿔야 하는지 판단할 수 없다.
- 기존 사용자 변경과 내 변경이 같은 파일에서 충돌한다.
- secret, keystore, `local.properties`, release artifact가 변경 범위에 포함된다.
- 빌드/테스트 실패 원인이 내 변경인지 기존 상태인지 불명확하다.
- 커밋을 어떻게 나눌지 애매하고 사용자가 커밋을 요청했다.

## Handoff 기준

- `android-dev` 중 원인 추적이 핵심이 되면 `bug-analysis` 기준으로 전환한다.
- `android-dev` 중 사용자가 "전체적으로 봐줘", "PR 리뷰"라고 하면 `code-review` 기준으로 전환한다.
- `bug-analysis` 중 수정 범위가 새 기능 구현으로 커지면 사용자에게 범위 변경을 알리고 `android-dev` 기준으로 진행한다.
- `code-review` 중 직접 수정 요청이 들어오면 새 요청으로 보고 `android-dev` 또는 `bug-analysis`를 선택한다.

## 완료 보고 기준

- 코드 변경 작업: 변경 요약, 수정 파일, 검증 결과, 남은 위험
- 버그 수정 작업: 증상, 원인, 수정 내용, 검증 결과
- 리뷰 작업: finding 우선, severity, file/line, test gap
- 문서 작업: 구조 변경 요약, 이동/추가한 문서, 남은 정리 후보
