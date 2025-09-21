Fikk mye problemer mellom datatyper som følge av at Jackson serialiserer til Integer, mens objektets datatype er av typen Long.
Dette kunne kanskje vært fikset med DTOer, men jeg har ingen kjennskap til dette så forholdt meg til Map<String, Object> 
