# Domain 모듈

## 역할

* Domain 모듈은 서비스에서 사용되는 도메인 모델들을 관리한다.
* 외부 라이브러리나 프레임워크에 의존하지 않는다.

## 패키지 구조

```markdown
.
└── Domain-Module/
    └── {domain}/
        ├── entity
        ├── enums
        ├── vo
        └── {service}
```