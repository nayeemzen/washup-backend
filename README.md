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


Order
==
curl -i -H "Content-Type: application/json" -H "Authorization: Bearer AUTH_TOKEN_HERE" -X POST -d '{
  "order_type": 0, 
  "delivery_date": 1515900795000, 
  "pickup_date": 1515801600000, 
  "idempotence_token": "abc"
}' http://localhost:8080/api/v1/orders/place-order
