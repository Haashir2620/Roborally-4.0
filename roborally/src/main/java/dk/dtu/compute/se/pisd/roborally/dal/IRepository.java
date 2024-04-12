package dk.dtu.compute.se.pisd.roborally.dal;

import dk.dtu.compute.se.pisd.roborally.model.Board;

import java.util.List;
//IRepository er en interface, der definerer en kontrakt for klasser, der implementerer databaserepositoriet.
// Den indeholder metoder til at oprette, opdatere, indlæse og hente spil fra en database.

public interface IRepository {

    //createGameInDB: Denne metode tager et Board objekt som argument og returnerer en boolean for at indikere,
    // om spillet er blevet succesfuldt oprettet i databasen.
    boolean createGameInDB(Board game);

    //updateGameInDB: Denne metode tager også et Board objekt som argument og returnerer en boolean for at indikere,
    // om spillet er blevet succesfuldt opdateret i databasen.
    boolean updateGameInDB(Board game);

    //loadGameFromDB: Denne metode tager et id for et spil som argument
    // og returnerer det tilsvarende Board objekt fra databasen.
    Board loadGameFromDB(int id);

    //getGames: Denne metode returnerer en liste af GameInDB objekter, som repræsenterer alle spil i databasen
    List<GameInDB> getGames();

}
