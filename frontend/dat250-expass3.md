# SPA - Exercise 3
#### This markdown explains how i implemented the SPA with basic CRUD operations that communicates with a Spring backend.

### Frontend Technologies
- React
- TypeScript


### Technical solution and challenges/ problems
- To ease testing of communication between backend and frontend some dummy data where produced in a seeder class,
DataSeeder. This needed to be excluded from the JUnit testing ground by annotating the seeder with @Profile("!test").
- Frontend consists of three components using an api.ts which delivers endpoints for the backend server.
  - CreatePoll --> using userID to create a poll
  - Identity --> creating a new user by only using username and email
  - ListPolls --> displays the current polls in servers memory, and allows user to vote on displayed polls
- To allow communication between frontend and backend, which are running on different ports, the @CrossOrigin annotation
was needed to enable these two projects to communicate with each other. By annotating the backend controllers, Spring automatically configured the necessary 
response-headers such that frontend where allowed to communicate with backend, which is of a different origin.
- There was som minor challenges with serialization/deserialization giving me only the objects ID instead of
    complete representations. Fixed by setting @JsonIdentityReference(alwaysAsId = false) where needed
    e.g in polls attribute VoteOptions, where it makes sense to see the complete option object when user is going to vote, and not only the options ID.
- After completing the components and verifying correct and working communication between the two modules, i ran the "npm run build" command. 
The static files produced where copied over to backend module, and now making the backend run the frontend code on the server. 

The complete project, including backend and frontend, can be found at my repo: https://github.com/671454/DAT250-exercise2.git
NB: The repo is named exercise2, but includes exercise 3 as well. 


