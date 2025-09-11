DAT250 - Assignment 2

## What i implementet
- Spring boot backend project.
- GitHub Actions: Gradle build, docker and tests with JaCoCo.
- Domain entities from PollApp.
- PollManagerV2.
- HTTP Client.
- REST Handlers.
- Automated JUnit tests.

The domain classes are implemented with relational attributes which makes it easy to backtrack and find all relations
a domain object holds. This makes it also more prone to inconsistencies since there is more steps involved in creating
and updating objects. PollManagerV2 functions as the service and is responsible for CRUD operations, and throws
exceptions where needed. There is implemented two controllers, PollController and UserController, which handles POST-
and GET- mappings. The controllers handles serialization and deserialization between client and server via the Jackson
dependency. 
HTTP Client was used to quickly test if the controllers worked.
Automated JUnit tests where implemented and integrated as a part of the GitHub workflow file.
The test consist of an end-to-end test which tests: create user -> create poll -> vote on poll.



## Technical problems and challenges
1. Since ive never used GitHub Actions before, I encountered some problems implementing the two workflows 
   ci.yml and gradle.yml. Solved by doing research.
2. As a result of me misunderstanding how we were supposed to implement the domain classes from the model, and 
   what responsibility the PollManager class was supposed to have, this is the second attempt im doing on this exercise.
   For some reason I forgot the purpose of a domain model, and just implemented the classes just as presented in
   the visual model, and this without adding attributes that represents relations between the entities. By doing it
   this way, the PollManager class needed to hold all relations between entities through a lot of HashMaps.
   This caused the exercise to be a lot more complex than needed.
