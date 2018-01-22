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
===
curl -i -H "Content-Type: application/json" -H "Authorization: Bearer AUTH_TOKEN_HERE" -X POST -d '{
  "order_type": 0, 
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
