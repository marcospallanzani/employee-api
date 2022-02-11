# Installation

This project uses Docker and it can be easily set up by running the following command from the base folder of the project:

```
docker-compose up
```

Once the application is up and running, the following endpoint can be called to check if the API is reachable:

```
curl --location --request GET 'http://localhost:8080/ping'
```

# State Machine

To implement the state machine, I've decided to use `Java enumerations`. This solution is probably not 
the best one, in particular because of the way that enumerations are handled in the memory (I personally think that 
in a concurrent environment with many requests, this could be an issue).

I had also considered using an existing library but the learning curve was a bit too demanding for the scope of this 
technical test. That said, a library like `spring-statemachine` seems to be a better solution.

# Open API

The swagger file can be downloaded at the following link: 

```
http://localhost:8080/v3/api-docs
```

The application is lacking of relevant Open API annotations in:
- the request objects;
- the response objects;
- the controllers (possible error messages and status codes)

# Other possible improvements

The application is lacking of the following points:
- proper error handling;
- proper error logging;
- request validation;
- authentication;
- authorization;
- Open API annotations

