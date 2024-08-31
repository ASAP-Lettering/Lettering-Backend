# Infrastructure 모듈

## 역할

* Infrastructure 모듈은 Application 모듈과 Domain 모듈을 지원하는 역할을 한다.
* 주로 Application 모듈에서의 port의 구현체를 제공한다.
* Application 모듈에서 필요한 외부 라이브러리와의 연동을 담당한다.
  * ex) db, security, http client, ...


## 패키지 구조

```markdown
.
└── Infrastructure-Module/
    ├── Client
    └── Security
```