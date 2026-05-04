# Code Review Workflow

## 목적

전체 코드 리뷰, PR 리뷰, 변경사항 리뷰, 아키텍처/보안/성능/스타일 감사를 할 때 사용한다.

## 문서 읽기 순서

1. `.agents/docs/workflows/code-review.md`
2. `.agents/docs/llm-wiki/project-map.md`
3. `.agents/docs/llm-wiki/architecture-map.md`
4. `.agents/docs/conventions/architecture-conventions.md`
5. `.agents/docs/conventions/compose-style.md`
6. `.agents/docs/conventions/kotlin-style.md`
7. `.agents/docs/conventions/testing-conventions.md`

## 범위 결정

- PR 리뷰: base branch와 head branch diff를 본다.
- 변경사항 리뷰: `git diff`와 `git status` 기준으로 본다.
- 전체 리뷰: architecture, security, performance, style을 나눠 본다.
- 단일 파일 리뷰: 해당 파일과 직접 의존 파일만 본다.

## 확인 항목

- Architecture: 레이어 의존성, Mapper 체인, DI 등록, Navigation 등록
- Security: secret, token, logging, HTTP, 민감정보 노출
- Performance: recomposition, lifecycle collect, LazyList key, image loading
- Style: Route/Screen 분리, Preview, UiState 불변성, import, TODO, 파일 크기

## 결과 형식

finding은 심각도 높은 순서로 작성한다.

```text
Priority:
File:
Line:
Problem:
Risk:
Suggested Fix:
```

## 완료 기준

- 실제 버그, 유지보수 위험, 보안/성능 리스크 중심으로 작성했다.
- 같은 원인의 중복 finding을 합쳤다.
- 파일과 라인을 명확히 적었다.
- 확인하지 못한 테스트나 빌드가 있으면 명시했다.
