# Bootstrap 모듈

## 역할

* RESTAPI를 제공하는 모듈
* 프로그램의 기능을 사용하기 위한 시작점

## 패키지 구조

```markdown
.
└── {domain}/
    ├── api
    ├── controller
    └── dto
```

* `{domain}`: 도메인 이름을 의미합니다. 예를 들어, `auth`, `user` 등이 될 수 있습니다.
* `api`: API 스팩을 정의합니다.
* `controller`: api 스팩에 대한 구현체입니다.
* `dto`: 요청간 전달되는 데이터를 정의합니다.
