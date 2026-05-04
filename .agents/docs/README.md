# Agent Docs

## 목적

`.agents/docs`는 Codex, Gemini, Claude가 공통으로 참조하는 문서 허브다.

## 문서 종류

- `llm-wiki/`: 코드 위치와 구조 지도
- `conventions/`: 코드 작성 규칙
- `workflows/`: 반복 작업 순서와 완료 기준
- `conventions/github-workflow-conventions.md`: 이슈, 브랜치, PR 작성 규칙

## 사용 순서

1. 작업 종류를 정한다.
2. `workflows/`에서 진행 순서를 확인한다.
3. `llm-wiki/`에서 코드 위치를 찾는다.
4. `conventions/`에서 작성 규칙을 확인한다.

요청 유형이 애매하면 먼저 `.agents/docs/decision-guide.md`를 확인한다.

## 제외

일회성 리뷰 결과와 조사 메모는 `_workspace/`에 둔다.
