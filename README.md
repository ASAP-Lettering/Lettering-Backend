# Lettering Backend



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