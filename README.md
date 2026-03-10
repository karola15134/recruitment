# Coupon Service API

**Coupon Service API** is a simple REST service built with **Spring Boot** that allows creating and redeeming coupons with usage limits and country restrictions.

---

## Table of Contents

1. [Features](#features)
2. [Technologies](#technologies)
3. [Running the Application](#running-the-application)
4. [API Endpoints](#api-endpoints)
5. [Example Requests](#example-requests)
6. [Error Handling](#error-handling)
7. [Project Structure](#project-structure)
8. [Notes](#notes)

---

## Features

- Create coupons with `code`, `maxUses`, and `country` as parameters
- Redeem coupons (checks usage limit and client country)
- Validate required parameters
- Handle common errors:
    - Coupon already exists
    - Coupon does not exist
    - Redemption limit exceeded
    - Coupon for a different country

---

## Technologies

- Java 17
- Spring Boot 3.x
- Lombok
- Jackson
- Maven

---

## Running the Application

1. Clone the repository:

``` git clone https://github.com/karola15134/coupon-api.git ```

2. Run the project:

   - in IDE run class CouponApplication.java. Add environment variables in run configuration
DB_HOST, DB_USER, DB_PASSWORD
   - create .jar file using
   ```mvn clean package``` 
   
     target/ \
     ├─ coupon-service-0.0.1-SNAPSHOT.jar

Sample run: \
```java -jar target/coupon-api-0.0.1-SNAPSHOT.jar -DB_HOST=localhost -DB_USER=user -DB_PASSWORD=password ```

3. The API will be available at:
   http://localhost:8080/api

---

## API Endpoints
   | Endpoint        | HTTP Method           | Parameters                   | Description                        |
   | --------------- | --------------------- | ---------------------------- | ---------------------------------- |
   | `/createCoupon` | Any (GET, POST, etc.) | `code`, `maxUses`, `country` | Creates a new coupon               |
   | `/redeemCoupon` | Any (GET, POST, etc.) | `code`                       | Redeems a coupon for the client IP |

---

## Example Requests
Create a coupon \
```curl "http://localhost:8080/api/createCoupon" -d "code=TEST123&maxUses=5&country=PL"```

Sample responses: \
```Coupon has been properly created```

Redeem a Coupon
```curl "http://localhost:8080/api/redeemCoupon" -d "code=TEST123" ```

Sample reposes: \
```Coupon has been properly redeemed```

--- 

## Error Handling

The service returns clear text responses (text/plain) for errors:
- CouponAlreadyExistsException – Coupon with the given code already exists
- MissingCouponException – Coupon does not exist
- ReedemCountExceededException – Redemption limit exceeded
- DifferentCouponCountryException – Coupon is for a different country

Example response for missing coupon:
Coupon does not exist

---

## Project Structure
src/main/java/com/coupon/api/

├─ controller/      # REST controllers \
├─ service/         # Business logic \
├─ dto/             # Data transfer objects\
├─ exception/       # Custom exceptions\
├─ validator/       # Parameter validation\
└─ CouponApplication.java 

---

## Notes

- Endpoints currently accept any HTTP method. Consider using @PostMapping for stricter REST compliance.
- Parameters must be sent as application/x-www-form-urlencoded.