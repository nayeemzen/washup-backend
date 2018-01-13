Washup Application
==

Backend for washup.io


Signup
===
curl -H "Content-Type: application/json" -X POST -d '{
  "first_name": "john",
  "last_name": "doe",
  "email": "admin",
  "password": "password",
  "phone_number": "phone_number"
}' http://localhost:8080/api/v1/users/sign-up


Login
===
curl -i -H "Content-Type: application/json" -X POST -d '{
  "email": "johndoe@gmail.com",
  "password": "password"
}' http://localhost:8080/api/v1/users/login
