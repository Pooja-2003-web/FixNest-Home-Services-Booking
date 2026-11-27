# FixNest Backend (Spring Boot + MySQL)

## Run
1. Install JDK 17+, Maven, MySQL. Set your MySQL password in `src/main/resources/application.properties`.
2. `mvn spring-boot:run` (tables and sample data are created automatically; `database/schema.sql` is the reference DDL).
3. Open http://localhost:8080 (the website is served by Spring Boot from `static/index.html` and talks to the API automatically).
   Opening the HTML file directly also works while the backend runs. With no backend it falls back to browser-storage demo mode.

## Relationships
customer 1-* bookings | service 1-* bookings | booking 1-1 payment (simulated) | chatbot_faq standalone

## API
| Method | Endpoint | Purpose |
|---|---|---|
| GET | /api/services | List active services |
| POST | /api/bookings | Create booking (+ pending payment) |
| GET | /api/bookings?phone= | Customer booking history |
| POST | /api/bookings/{id}/pay | Simulated payment `{method, simulateFailure}` |
| POST | /api/chat | `{message}` -> `{answer}` from chatbot_faq |
| GET | /api/chat/faq | All FAQs |
| GET/POST/PUT/DELETE | /api/admin/services | Admin: manage services |
| GET | /api/admin/bookings | Admin: all bookings |
| PATCH | /api/admin/bookings/{id}/status | `{status: CONFIRMED}` |
| POST/DELETE | /api/admin/faq | Admin: manage FAQs |

Admin endpoints use HTTP Basic auth (admin / admin123, set in application.properties).

## Sample booking
curl -X POST localhost:8080/api/bookings -H "Content-Type: application/json" -d '{"name":"Asha","phone":"9876543210","serviceId":1,"date":"2026-12-01","time":"09:00 AM","address":"12 MG Road"}'
