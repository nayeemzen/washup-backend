Washup Application
==

Backend for washup.io

Build
===
Local build: mvn package

Run
===
java -jar target/...jar
or
mvn spring-boot:run

Protos
===
Run `mvn compile` after any changes to the proto to generate the java code.


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
===
curl -i -H "Content-Type: application/json" -H "Authorization: Bearer AUTH_TOKEN_HERE" -X POST -d '{
  "order_type": 1, 
  "delivery_date": 1515900795000, 
  "pickup_date": 1515801600000, 
  "idempotence_token": "abc"
}' http://localhost:8080/api/v1/orders/place-order


User
===
GetProfile
====
curl -i -H "Content-Type: application/json" -H "Authorization: Bearer AUTH_TOKEN_HERE" -X GET  http://localhost:8080/api/v1/users/get-profile

SetProfile
====
curl -i -H "Content-Type: application/json" -H "Authorization: Bearer AUTH_TOKEN_HERE" -X POST -d '{
  "user": {"first_name": "jack", "last_name": "nelson", "phone_number": "1233322"}
  }' http://localhost:8080/api/v1/users/set-profile


Address
===
GetAddress
====
curl -i -H "Content-Type: application/json" -H "Authorization: Bearer AUTH_TOKEN_HERE" -X GET  http://localhost:8080/api/v1/users/get-address

SetAddress
====
curl -i -H "Content-Type: application/json" -H "Authorization: Bearer AUTH_TOKEN_HERE" -X POST -d '{
  "street_address": "143 ali st", 
  "apt": "40", 
  "postal_code": "", 
  "notes": "abc"
}' http://localhost:8080/api/v1/users/set-address

Preferences
===
SetPreference
====
curl -i -H "Content-Type: application/json" -H "Authorization: Bearer AUTH_TOKEN" -X POST -d '
{
  "preference": {
    "scented": true, 
    "fabric_softener": true, 
    "one_day_delivery": true, 
    "laundry_reminder": true
  }
}' http://localhost:8080/api/v1/users/set-preferences

GetPreference
====
curl -i -H "Content-Type: application/json" -H "Authorization: Bearer AUTH_TOKEN" -X GET http://localhost:8080/api/v1/users/get-preferences



Employees
===

Register Employees
====
java -jar target/...jar -register_employee email=abc@washup.io password=something first_name=john last_name=doe

Login Employees
====
curl -i -H "Content-Type: application/json" -X POST -d '{"email": "ali@washup.io", "password":"123"}'  http://localhost:8080/_admin/login

Get Orders
===
curl -i -H "Content-Type: application/json" -H "Authorization: Bearer EMPLOYEE_AUTH_TOKEN" -X GET -d '{"start_date":1, "end_date":2, "order_type": "WASH_FOLD", "billed": true}'  http://localhost:8080/_admin/orders/get-orders

Cards
===

Set Card
====
curl -i -H "Content-Type: application/json" -H "Authorization: Bearer AUTH_TOKEN" -X POST -d '{
  "stripeCardToken": "tok_ca"
}' http://localhost:8080/api/v1/users/set-card

Get Card
====
curl -i -H "Content-Type: application/json" -H "Authorization: Bearer AUTH_TOKEN" -X GET http://localhost:8080/api/v1/users/get-card
