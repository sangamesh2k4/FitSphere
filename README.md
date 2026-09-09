# FitSphere

**An all-in-one fitness platform** for tracking workouts, logging nutrition, monitoring body progress, and getting personalized health guidance — designed and built solo from frontend to backend to deployment.

🔗 **Live app:** [https://fitspherebysangamesh.vercel.app/](https://fitspherebysangamesh.vercel.app/)  
🔗 **API:** [https://fitsphere-s07g.onrender.com](https://fitsphere-s07g.onrender.com)  
🔗 **API docs:** [Swagger / OpenAPI](https://fitsphere-s07g.onrender.com/swagger-ui.html)

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)
![React](https://img.shields.io/badge/React-19-61DAFB)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-database-blue)
![Redis](https://img.shields.io/badge/Redis-caching-red)
![Tests](https://img.shields.io/badge/backend%20tests-65%20classes-success)

---

## What is FitSphere?

FitSphere is built around two user experiences:

###  Public

No account required.

- Health Assessment
- Macro Tracker
- Exercise Library
- Food search and details
- Exercise details and YouTube tutorials
- Public Contact Us form

The public features use real backend services and external APIs rather than mocked demo data. 

###  Authenticated

After registration, users get a personalized fitness tracking experience:

- Workout tracking
- Nutrition logging
- Body measurements
- Progress analytics
- Exercise and food favorites
- Personalized calorie and macro targets
- Profile and account settings

A separate `ROLE_ADMIN` area provides administration and support tools.

---

## Screenshots

### Public Experience

| Home                                    | Health Assessment |
|-----------------------------------------|---|
| ![FitSphere Home](screenshots/hero.png) | ![HealthAssessment](screenshots/healthassessment.png) |

| Macro Tracker | Exercise Library |
|---|---|
| ![Macro Tracker](screenshots/macrotracker.png) | ![Exercise Library](screenshots/explore-exercises.png) |

### Exercise Details

![Exercise Detail](screenshots/exercise-detail2.png)

### User Dashboard

![Dashboard](screenshots/dashboard.png)

### Workout Tracking

| Active Workout | Workout Complete |
|---|---|
| ![Active Workout](screenshots/activeworkout.png) | ![Workout Summary](screenshots/workoutsummary.png) |

### Progress & Measurements

| Progress Analytics | Body Measurements |
|---|---|
| ![Progress](screenshots/progress.png) | ![Body Measurements](screenshots/bodymeasurments.png) |

### Admin Dashboard

![Admin Usage Dashboard](screenshots/admin-usage.png)

## Features

###  Workout Tracking

- Start workouts and log exercises, sets, reps and weight
- Each set is persisted immediately
- Automatic estimated **1RM using the Epley formula**
- Automatic **personal-record detection**
- PRs are re-evaluated after editing or deleting sets
- Reorder exercises and edit completed workouts
- Weekly/monthly training-volume analytics
- Per-exercise strength progression charts

###  Nutrition

- Live food search using **USDA FoodData Central**
- 350,000+ real food items
- Breakfast, lunch, dinner and snack logging
- Daily calorie and macro tracking
- Nutrition targets based on the user's health profile
- Public Macro Tracker with serving-size adjustment
- Food detail pages
- Favorite foods for quicker logging

###  Health Assessment

Calculates:

- BMI
- BMR
- TDEE
- Body-fat range
- Recommended calories
- Protein, carbohydrate and fat targets
- Personalized recommendations

The resulting nutrition targets are carried into the authenticated nutrition experience.

###  Exercise Library

- 250+ exercises
- Muscle groups, equipment and difficulty
- Step-by-step instructions
- Pro tips
- Common mistakes
- Search and filtering
- Live YouTube tutorial recommendations
- Category → muscle mapping for dependent filtering
- Favorite exercises

YouTube content is retrieved through the **YouTube Data API**.

###  Body Measurements

Track measurement history and visualize trends such as:

- Weight
- Body-fat percentage
- Arm / bicep measurements
- Waist
- Thigh
- Other supported body metrics

###  Authentication & Security

- JWT access + refresh tokens
- BCrypt password hashing
- Email verification with OTP
- OTP-based password reset
- Redis-backed rate limiting
- Role-based authorization
- Query-level ownership enforcement
- Cross-user access tests

Sensitive endpoints such as login, registration, nutrition search and YouTube search are protected with Redis-backed rate limiting. 
### ️ Admin Panel

Role-gated administration for:

- User management
- Enable / disable user accounts
- Exercise management
- Enable / disable exercises
- Usage analytics
- Contact/support inbox
- Admin email replies

The usage dashboard includes application metrics such as users, food logs, exercises and daily request volume.

---

## Architecture

```text
React + Vite
      │
      ▼
Spring Boot REST API
      │
      ├── Spring Security + JWT
      ├── Services
      ├── Repositories / JPA
      ├── DTOs / Mappers
      ├── Redis Rate Limiting
      └── External API Integrations
              │
       ┌──────┴──────┐
       ▼             ▼
 PostgreSQL        Redis
       │
       ├── USDA FoodData Central
       └── YouTube Data API
```

### Key design decisions

- **Sets are the unit of truth:** workout sets are persisted independently rather than waiting for workout completion.
- **Single source of truth for health calculations:** calculated nutrition targets flow into the user's stored profile.
- **Continuous analytics:** empty periods still produce zero-value buckets so charts remain aligned.
- **Environment-based configuration:** secrets and environment-specific values are externalized rather than hardcoded. 

---

## Tech Stack

### Backend

![Java](https://img.shields.io/badge/Java%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot%203.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![OpenAPI](https://img.shields.io/badge/OpenAPI-6BA539?style=for-the-badge&logo=openapiinitiative&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit%205-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-78C257?style=for-the-badge&logo=mockito&logoColor=white)

### Frontend

![React](https://img.shields.io/badge/React%2019-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![React Router](https://img.shields.io/badge/React%20Router-CA4245?style=for-the-badge&logo=reactrouter&logoColor=white)
![Axios](https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=axios&logoColor=white)
![Recharts](https://img.shields.io/badge/Recharts-22B5BF?style=for-the-badge&logo=chartdotjs&logoColor=white)

### External APIs

![USDA](https://img.shields.io/badge/USDA%20FoodData%20Central-4B8B3B?style=for-the-badge&logoColor=white)
![YouTube API](https://img.shields.io/badge/YouTube%20Data%20API-FF0000?style=for-the-badge&logo=youtube&logoColor=white)

### Deployment & Infrastructure

![Vercel](https://img.shields.io/badge/Vercel-000000?style=for-the-badge&logo=vercel&logoColor=white)
![Render](https://img.shields.io/badge/Render-46E3B7?style=for-the-badge&logo=render&logoColor=black)
![Supabase](https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)
![Upstash](https://img.shields.io/badge/Upstash-00E9A3?style=for-the-badge&logo=upstash&logoColor=white)
---

##  Testing

FitSphere has **65 backend test classes** covering repository, service, controller and security layers.

The suite covers areas including:

- Workout and PR logic
- Health and macro calculations
- Analytics
- Repository queries
- JWT authentication
- Rate limiting
- Controller/API behavior
- Role-based access
- Cross-user ownership
- Email verification and password reset

```bash
./mvnw test
```

**Latest full test run:**

```text
Tests run: 603
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

---

##  Deployment

```text
Frontend
   │
   ▼
Vercel
   │
   ▼
Render
Spring Boot API
   ├── Supabase PostgreSQL
   ├── Upstash Redis
   ├── USDA FoodData Central
   └── YouTube Data API
```

The same application code runs locally and in production through environment-based configuration.

---

## 🔮 What's Next?

- Frontend testing with Vitest + React Testing Library
- Workout routine templates
- Broader consistency tracking across workouts, nutrition and measurements

---

## 👨‍💻 Built By

[**Sangamesh Janadi**](https://github.com/sangamesh2k4)

Built solo — from product design and React UI to backend architecture, database design, authentication, testing, API integrations, Docker and deployment.