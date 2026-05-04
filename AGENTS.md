# Forday Android - Codex Entry

이 파일은 Codex용 진입 라우터입니다. 공통 문서의 원본은 `.agents/docs/` 아래에 있습니다.

## 문서 우선순위

1. 사용자의 최신 요청
2. 이 파일의 Codex 전용 지침
3. `.agents/docs/workflows/`
4. `.agents/docs/conventions/`
5. `.agents/docs/llm-wiki/`
6. `.agents/skills/`
7. `_workspace/`

## 공통 문서

- 코드 위치와 구조: `.agents/docs/llm-wiki/`
- 코드 작성 규칙: `.agents/docs/conventions/`
- 반복 작업 절차: `.agents/docs/workflows/`
- 임시 리뷰/감사 결과: `_workspace/`
- Codex 실행 지침: `.agents/skills/`

## 요청별 시작점

- 새 기능/화면/API 연동: `.agents/docs/workflows/feature-development.md`
- 버그 수정: `.agents/docs/workflows/bugfix.md`
- 전체 리뷰/PR 리뷰/감사: `.agents/docs/workflows/code-review.md`
- 커밋/PR 전 점검: `.agents/docs/workflows/pre-pr-check.md`
- 이슈/브랜치/PR 작성 규칙: `.agents/docs/conventions/github-workflow-conventions.md`

## Codex Skill 라우팅

- Android 개발, 기능 추가, 버그 수정, 리팩토링, API 연동: `android-dev`
- 크래시, 오동작, 상태 불일치, 네비게이션/DI/API 문제 원인 분석: `bug-analysis`
- 종합 리뷰, 전체 코드 리뷰, PR 리뷰, 아키텍처/보안/성능/스타일 감사: `code-review`

## 역할 경계

- 이 파일에는 프로젝트 구조나 코드 스타일 전문을 넣지 않는다.
- 공통 지식은 `.agents/docs/llm-wiki/`, `.agents/docs/conventions/`, `.agents/docs/workflows/`에만 유지한다.
- `_workspace/`의 리포트는 특정 시점의 참고 자료이며 영구 규칙으로 사용하지 않는다.
- 사용자가 명시적으로 요청한 경우에만 커밋한다. 커밋 기준은 `.agents/docs/conventions/commit-conventions.md`를 따른다.
- 사용자가 명시적으로 요청한 경우에만 이슈, 브랜치, PR 같은 GitHub 쓰기 작업을 수행한다. 브랜치명에는 AI 도구 이름을 넣지 않는다.
