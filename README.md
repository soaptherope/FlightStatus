Upon launching create a Database and run scripts located in src/main/resources/db/changelog/0.0.1/changelog-1.sql
Obtain an APIKEY from Google Maps API which will be required for getting coordinates of a city to obtain a timezone 
Fill in the application.properties file with database info and credentials, as well as API key from google
If done correctly, launch the program and visit: http://localhost:8080/swagger-ui/index.html
From there you can create a user or login if you already made one under auth-controller.
Upon creating an user login with the credentials you specified and obtain your token, copy it excluding " on both ends.
On the top right corner in Swagger-UI you will see an unlocked lock - press on it and paste your token (remove ""), and then press authorize.
By default all users created have role "USER" and can only get the arrivals, if you wish to become an ADMIN (to edit and add flights) visit the /admin endpoint under auth-controller,
be cautios, you cannot go back.
