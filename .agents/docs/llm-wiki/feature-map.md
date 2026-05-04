# Feature Map

## 목적

기존 기능의 위치를 빠르게 찾기 위한 지도다. 정확한 최신 파일 목록은 항상 실제 코드에서 확인한다.

## 주요 Presentation Feature

```text
app/src/main/java/com/forday/app/presentation/
  home/
  record/
  modifyhobby/
  mypage/
  onboarding/
  main/
```

## 기능을 찾는 순서

1. `presentation/{feature}/`에서 화면과 ViewModel을 찾는다.
2. ViewModel 생성자에서 사용하는 UseCase를 확인한다.
3. UseCase가 참조하는 Repository interface를 확인한다.
4. `data/impl/`에서 Repository 구현체를 확인한다.
5. Repository 구현체가 쓰는 DataSource interface와 Remote 구현체를 확인한다.
6. API가 필요하면 `remote/api/service/`와 `remote/model/`을 확인한다.

## 유사 기능 참고 후보

- 홈/목록/상태 흐름: `presentation/home/`
- 기록/루틴 입력 흐름: `presentation/record/`
- 수정 모드/기존 값 편집 흐름: `presentation/modifyhobby/`
- 마이페이지/상세 진입 흐름: `presentation/mypage/`
- 온보딩 단계 흐름: `presentation/onboarding/`
