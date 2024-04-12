package dk.dtu.compute.se.pisd.roborally.dal;

//RepositoryAccess klassen er en hjælpeklasse, der giver adgang til en enkelt instans af Repository klassen.
// Den bruger en statisk variabel repository til at gemme den enkeltstående instans af Repository.
public class RepositoryAccess {

    private static Repository repository;

    //Metoden getRepository er en statisk metode, der returnerer en reference til Repository objektet.
    // Hvis repository-variablen ikke er initialiseret,
    // oprettes en ny instans af Repository ved at kalde dens konstruktør med en Connector som argument.
    // Denne metode sikrer, at der kun oprettes én instans af Repository-klassen
    // og giver en global adgangspunkt til at få fat i denne instans.
    public static IRepository getRepository() {
        if(repository == null) {
            repository = new Repository(new Connector());
        }
        return repository;
    }

}
//Dette designmønster kaldes Singleton-mønsteret,
// hvor der kun er én instans af en klasse tilgængelig i hele applikationen.
// Det kan være nyttigt, når der kun er behov for én enkelt instans af en klasse,
// f.eks. når der skal oprettes forbindelse til en database.
//Ved at bruge getRepository-metoden kan andre klasser i applikationen få adgang til Repository-objektet og bruge dets metoder til at interagere med databasen uden at skulle oprette en ny instans af Repository hver gang.