<div align="center">
    <img src="client/Icon.jpg">
</div>
-----------------------------

Risotto is a booking management system which aims to simplify the job of the restaurant staff.
It is a multi-user client-server application that can be used to take bookings,
orders, take payments and analyze the revenues.

Both client and server can be executed everywhere thanks to the underlying cross-platform stack - Java on the server side and Web on the client side. Server is built on Jetty, which means that it will be able to sustain a high number of simultaneous users. Meanwhile, the client can be used by everyone easily thanks to its intuitive touch-friendly UI.

## Running from Development environment
### Server
Clone the repository from GitLab type:
```
git clone git@gitlab.com:comp2541/bison.git
```

Create a runnable .jar file for the server in `bison/server`:
```
ant distribute
```
Start the server in `bison/server/bin`:
```
java -jar RestaurantServer.jar
```
### Client
Clone the repository from GitLab type:
```
git clone git@gitlab.com:comp2541/bison.git
```
Install the essential packages in `bison/client`:
```
npm install
```
Build the client:
```
npm run build
```
Run the client from a local web server of your choice. One option is to use Python built-in web server:
```
python -m SimpleHTTPServer
```
Open the client in your browser. In case of Python built-in web server, you will need to open `http://localhost:8000` to start the client.
## Running from CI environment
As we are using Agile software development approach, we are constantly testing our changes and making sure that no new bugs in the sprints. This is why a Continious Integration builds were setup to ensure that project structure is not violated and to have the latest client/server build available at all times.  
In order to access a CI build, go to [Builds](https://gitlab.com/comp2541/bison/builds) and pick a build that you want to run. Copy-paste the `Build Id` and go to `http://bison.isat.xyz/Bison-%Build Id%/`. You will see `client` and `server` folder that contain the corresponding executables.

To run the server, download `http://bison.isat.xyz/Bison-%Build Id%/server/RestaurantServer.jar` and execute `java -jar RestaurantServer.jar`.

To run the client, simply open `http://bison.isat.xyz/Bison-%Build Id%/client` once the server is started.

## Distribution
We have identified that Windows OS is a primary platform for POS/Booking systems in the restaurants. In order to provide the best UX possible on this platform, we are distributing click-to-run executables for both client and server.
