package dk.dtu.compute.se.pisd.roborally.dal;



//Denne klasse GameInDB repræsenterer et spilobjekt, der gemmes i databasen.
// Den har to egenskaber, id og name, som henholdsvis repræsenterer spillets unikke identifikator og navn.
public class GameInDB {

    // I konstruktøren sættes id og name egenskaberne til de værdier, der gives som argumenter.
    public final int id;
    public final String name;

    //Overstyring af toString metoden giver en repræsentation af objektet som en streng, hvilket er nyttigt til debugging.
    // For et GameInDB objekt returnerer denne metode en streng bestående af id og navn adskilt af ": ".
    public GameInDB(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return id + ": " + name;
    }

}
