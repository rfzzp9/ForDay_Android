# Workflows

## 목적

`workflows`는 Codex, Gemini, Claude가 공통으로 따르는 작업 순서표다.

## 포함

- 기능 개발 순서
- 버그 수정 순서
- 코드 리뷰 순서
- PR 전 점검 순서
- 각 단계에서 참고할 `.agents/docs/llm-wiki/`와 `.agents/docs/conventions/` 문서

## 제외

- 코드베이스 전체 구조 설명
- 세부 코드 스타일 전문
- 특정 AI 전용 실행 지침
- 일회성 리뷰 결과

## 문서 목록

- `feature-development.md`: 새 기능, 화면, API 연동
- `bugfix.md`: 크래시, 상태 오류, API 오류, 네비게이션 오류
- `code-review.md`: 전체 리뷰, PR 리뷰, 감사
- `pre-pr-check.md`: 커밋 또는 PR 전 최종 점검
- `change-management.md`: 기존 변경, 삭제 파일, generated/local 파일 처리
