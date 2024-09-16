# Lettering Backend

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ASAP-Lettering_Lettering-Backend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=ASAP-Lettering_Lettering-Backend)

## System Architecture


### Overview


```markdown
.
├── Application-Module/
│   └── {domain}/
│       ├── port/
│       │   ├── in
│       │   └── out
│       ├── service
│       ├── vo
│       └── exception
├── Bootstrap-Module/
│   ├── {domain}/
│   │   ├── api
│   │   ├── controller
│   │   └── dto
│   └── common
├── Common-Module
├── Domain-Module/
│   └── {domain}/
│       ├── entity
│       ├── enums
│       ├── vo
│       └── {service}
└── Infrastructure-Module/
    ├── Client
    └── Security
```