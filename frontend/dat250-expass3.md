# SPA - Exercise 3
#### This markdown explains how i implemented an SPA with basic CRUD operations that communicates with a Spring backend.

### Frontend Technologies
- React
- TypeScript


### Technical solution and challenges/ problems
- To ease testing of communication between backend and frontend some dummy data where produced in seeder class,
DataSeeder. This needed to be excluded from the JUnit testing ground by annotating the seeder with @Profile("!test").
- There was som minor callanges with serializtion/deserialization giving me only the objects ID instead of
complete representations. Fixed by setting @JsonIdentityReference(alwaysAsId = false) where needed
e.g in polls attribute VoteOptions, where it makes sense to se the complete option object, and not only its ID.
- Frontend consists of three components using an api.ts which delivers endpoints to the backend server.
  - CreatePoll --> using userID to create a poll
  - Identity --> creating a new user by only using username and email
  - ListPolls --> displays the current polls in servers memory
- To allow communication between frontend and backend, which are running on different ports, the @CrossOrigin annotation
enabled these two projects to communicate. By annotating the backend controllers, Spring automatically configures the necessary 
response headers such that frontend now is allowed to communicate with backend, which is of a different origin.  


