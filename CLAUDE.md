# Forday Android - Claude Entry

이 파일은 Claude용 진입 라우터입니다. Codex, Gemini와 같은 `.agents/docs/` 공통 문서 체계를 사용합니다.

## 먼저 볼 문서

- 작업 순서: `.agents/docs/workflows/`
- 코드 위치와 구조: `.agents/docs/llm-wiki/`
- 코드 작성 규칙: `.agents/docs/conventions/`
- 임시 리뷰/감사 결과: `_workspace/`

## 요청별 시작점

- 새 기능/화면/API 연동: `.agents/docs/workflows/feature-development.md`
- 버그 수정: `.agents/docs/workflows/bugfix.md`
- 전체 리뷰/PR 리뷰/감사: `.agents/docs/workflows/code-review.md`
- 커밋/PR 전 점검: `.agents/docs/workflows/pre-pr-check.md`

## 문서 역할

- `.agents/docs/llm-wiki/`: 코드베이스 지도. 어디를 봐야 하는지 설명한다.
- `.agents/docs/conventions/`: Kotlin, Compose, Architecture, Testing 작성 규칙을 설명한다.
- `.agents/docs/workflows/`: 작업을 어떤 순서로 진행하고 언제 완료로 볼지 설명한다.
- `_workspace/`: 특정 시점의 분석 결과와 임시 산출물을 보관한다.

## 유지 원칙

- 이 파일에는 긴 프로젝트 설명을 넣지 않는다.
- 공통 규칙은 `.agents/docs/` 아래 한 곳에만 둔다.
- 문서가 충돌하면 사용자의 최신 요청을 우선하고, 그다음 workflows, conventions, llm-wiki 순으로 판단한다.
