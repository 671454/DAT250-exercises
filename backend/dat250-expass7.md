# DAT250: Software Technology Experiment Assignment 7

This exercise needed less work than the other ones, because much of the docker configurations were already implemented 
from previous exercises.
The main thing needed was to implement the app service in the compose file with a context of the whole backend-project 
("context: ."), and then connect this image and make it dependent on both the Redis and RabbitMQ images.
Environment variables for redis and rabbitmq, which specified host and ports and also username and password for AMQP, was
added in the compose file, which is described below.

To make sure my application is not running as root these lines were added in Dockerfile:
- RUN addgroup -S spring && adduser -S spring -G spring
- USER spring:spring

During containerization of the application (app) I encountered connection problems with the redis container. The reason
for this was that app used "localhost:6379" from previous experiments, but a docker environment like
this should communicate via known service-names. The solution was to use created env-variables in mye docker-compose 
file, like SPRING_DATA_REDIS_HOST("redis") and SPRING_DATA_REDIS_PORT("6379"), and now my app container could
connect to redis (and rabbitMQ) without problems. 

