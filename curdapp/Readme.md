## Welcome to Spring Boot CURD Application

### App Structure
```bash
curdapp/
├── src/main/java/com/ecommerce/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/
│   ├── dto/
│   ├── exception/
│   └── config/
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

### Work Sequence

1. App created using [Spring Initializr](https://start.spring.io/)
2. Configure - `application.properties`.
3. Make Entities in model directory.
4. Make Repositories in repository directory.
5. Make 1. request, 2. response (Data Transfer Object (DTO)) in dto directory.
6. Make Exception Layer in exception directory
7. Make Service Layer in service directory

### App Structure - Details

```bash
curdapp/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── jakirbd/
│   │   │           └── curdapp/
│   │   │               ├── CurdappApplication.java
│   │   │               ├── controller/
│   │   │               │   ├── CategoryController.java
│   │   │               │   ├── ProductController.java
│   │   │               │   └── ImageController.java
│   │   │               ├── service/
│   │   │               │   ├── CategoryService.java
│   │   │               │   ├── ProductService.java
│   │   │               │   └── ImageService.java
│   │   │               ├── repository/
│   │   │               │   ├── CategoryRepository.java
│   │   │               │   ├── ProductRepository.java
│   │   │               │   └── ImageRepository.java
│   │   │               ├── model/
│   │   │               │   ├── Category.java
│   │   │               │   ├── Product.java
│   │   │               │   └── Image.java
│   │   │               ├── dto/
│   │   │               │   ├── request/
│   │   │               │   │   ├── CategoryRequest.java
│   │   │               │   │   ├── ProductRequest.java
│   │   │               │   │   └── ImageRequest.java
│   │   │               │   └── response/
│   │   │               │       ├── CategoryResponse.java
│   │   │               │       ├── ProductResponse.java
│   │   │               │       ├── ImageResponse.java
│   │   │               │       └── ErrorResponse.java
│   │   │               ├── exception/
│   │   │               │   ├── ResourceNotFoundException.java
│   │   │               │   ├── DuplicateResourceException.java
│   │   │               │   ├── BusinessException.java
│   │   │               │   └── GlobalExceptionHandler.java
│   │   │               └── config/
│   │   │                   └── WebConfig.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── jakirbd/
│                   └── curdapp/
│                       └── CurdappApplicationTests.java
```

```bash
mvn -v
mvn test -e
mvn clean install
mvn spring-boot:run
```

### Full CRUD Operations Summary

#### Category Endpoints

| Operation | Method | URL                  | Body Required |
| --------- | ------ | -------------------- | ------------- |
| Create    | POST   | /api/categories      | Yes           |
| Get All   | GET    | /api/categories      | No            |
| Get One   | GET    | /api/categories/{id} | No            |
| Update    | PUT    | /api/categories/{id} | Yes           |
| Delete    | DELETE | /api/categories/{id} | No            |

#### Product Endpoints

| Operation          | Method | URL                                 | Body Required           |
| ------------------ | ------ | ----------------------------------- | ----------------------- |
| Create             | POST   | /api/products                       | Yes                     |
| Create with Images | POST   | /api/products                       | Yes (with images array) |
| Get All            | GET    | /api/products?page=0&size=10        | No                      |
| Get One            | GET    | /api/products/{id}                  | No                      |
| Get by Category    | GET    | /api/products/category/{categoryId} | No                      |
| Update             | PUT    | /api/products/{id}                  | Yes                     |
| Delete             | DELETE | /api/products/{id}                  | No                      |

#### Image Endpoints

| Operation       | Method | URL                                     | Body      | Description                |
| --------------- | ------ | --------------------------------------- | --------- | -------------------------- |
| Create (URL)    | POST   | /api/images                             | JSON      | Add image from web URL     |
| Create (Upload) | POST   | /api/images/upload                      | form-data | Upload local image file    |
| Read            | GET    | /api/images/product/{productId}         | None      | Get all images for product |
| Read Primary    | GET    | /api/images/product/{productId}/primary | None      | Get primary image          |
| Update          | PUT    | /api/images/{imageId}/primary           | None      | Set image as primary       |
| Delete          | DELETE | /api/images/{imageId}                   | None      | Delete image               |
