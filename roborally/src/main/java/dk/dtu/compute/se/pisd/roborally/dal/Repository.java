package dk.dtu.compute.se.pisd.roborally.dal;

import dk.dtu.compute.se.pisd.roborally.fileacces.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * @author Ekkart Kindler
 */

//Repository klassen implementerer IRepository interfacet og er ansvarlig for kommunikation med databasen.
// Den indeholder metoder til at oprette, opdatere, indlæse og hente spil fra databasen.

public class Repository implements IRepository {

    private static final String GAME_GAMEID = "gameID";

    private static final String GAME_NAME = "name";

    private static final String GAME_CURRENTPLAYER = "currentPlayer";

    private static final String GAME_PHASE = "phase";

    private static final String GAME_STEP = "step";

    private static final String PLAYER_PLAYERID = "playerID";

    private static final String PLAYER_NAME = "name";

    private static final String PLAYER_COLOUR = "colour";

    private static final String PLAYER_GAMEID = "gameID";

    private static final String PLAYER_POSITION_X = "positionX";

    private static final String PLAYER_POSITION_Y = "positionY";

    private static final String PLAYER_HEADING = "heading";
    private static final String FIELD_GAMEID = "gameID";
    private static final String FIELD_PLAYERID = "playerID";
    private static final String FIELD_TYPE = "type";
    private static final Integer FIELD_TYPE_REGISTER = 0;
    private static final Integer FIELD_TYPE_HAND = 1;
    private static final String FIELD_POSITION = "position";
    private static final String FIELD_VISIBLE = "visible";
    private static final String FIELD_COMMAND = "command";
    private static final String PLAYER_HP = "hp";


    private Connector connector;
    private Phase phase;
    private Player player;

    public Repository(Connector connector){

        this.connector = connector;
    }

    @Override
    // //createGameInDB: Denne metode tager et Board objekt som argument og opretter et nyt spil i databasen.
    //    // Metoden indsætter spillets navn, nuværende spiller, fase, trin, og board i den tilsvarende tabel.
    //    // Hvis oprettelsen lykkes, returneres true, ellers false.
    public boolean createGameInDB(Board game) {
        //tjekker hvis pillet har en id
        if (game.getGameId() == null) {
            //her opretter vi forbindelse til databasen ved at tildele med en connecter
            Connection connection = connector.getConnection();
            //vi laver try catch for at håndtere fejl og auto commit er dat til false, fordi vi vil have kontrol over det
            try {
                connection.setAutoCommit(false);
                // oprettelse af preparedstament, som referer til metoden getInsertGameStatementRGK()
                PreparedStatement ps = getInsertGameStatementRGK();
                ps.setString(1, "Date: " +  new Date()); // instead of name
                ps.setNull(2, Types.TINYINT); // game.getPlayerNumber(game.getCurrentPlayer())); is inserted after players!
                ps.setInt(3, game.getPhase().ordinal());
                ps.setInt(4, game.getStep());
                ps.setString(5, game.getBoardName()); // Tilføjelse af boardName til SQL statement

                //tilføj at boardname også gemmes
                int affectedRows = ps.executeUpdate();
                //koden ovenfor sikrer spillets detaljer bliver korrekt indsat i databasen
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (affectedRows == 1 && generatedKeys.next()) {
                    game.setGameId(generatedKeys.getInt(1));
                }
                generatedKeys.close();
                createPlayersInDB(game);
                createCardFieldsInDB(game);
                ps = getSelectGameStatementU();
                ps.setInt(1, game.getGameId());

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
                    rs.updateRow();
                } else {
                }
                rs.close();

                connection.commit();
                connection.setAutoCommit(true);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Some DB error");

                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            System.err.println("Game cannot be created in DB, since it has a game id already!");
        }
        return false;
    }
    //-----------------------------------------------------------------------------------------------------------------------
    @Override

    //updateGameInDB: Denne metode tager et Board objekt som argument og opdaterer et eksisterende spil i databasen.
    // Metoden opdaterer spillets nuværende spiller, fase og trin i den tilsvarende tabel.
    // Hvis opdateringen lykkes, returneres true, ellers false.
    public boolean updateGameInDB(Board game) {
        assert game.getGameId() != null;

        Connection connection = connector.getConnection();
        try {
            connection.setAutoCommit(false);

            PreparedStatement ps = getSelectGameStatementU();
            ps.setInt(1, game.getGameId());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
                rs.updateInt(GAME_PHASE, game.getPhase().ordinal());
                rs.updateInt(GAME_STEP, game.getStep());
                rs.updateRow();
            } else {
            }
            rs.close();

            updatePlayersInDB(game);
            //TOODO this method needs to be implemented first
            updateCardFieldsInDB(game);


            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Some DB error");

            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return false;
    }
    //__________________________________________________________________________________________________________________
    /**
     * @
     */
    @Override
    //loadGameFromDB: Denne metode tager et spil-id som argument og indlæser det tilsvarende spil fra databasen.
    // Metoden opretter et nyt Board objekt og indlæser spillets navn, nuværende spiller, fase og trin fra den tilsvarende tabel.
    // Derefter indlæses spillerne og deres positioner og retninger. Hvis indlæsningen lykkes, returneres det indlæste Board objekt,
    // ellers returneres null.
    public Board loadGameFromDB(int id) {
        Board game;
        try {
            PreparedStatement ps = getSelectGameStatementU();
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            int playerNo = -1;
            if (rs.next()) {
                game = LoadBoard.loadBoard(rs.getInt("boardname"));

                if (game == null) {
                    return null;
                }
                playerNo = rs.getInt(GAME_CURRENTPLAYER);
                game.setPhase(Phase.values()[rs.getInt(GAME_PHASE)]);
                game.setStep(rs.getInt(GAME_STEP));
            } else {
                return null;
            }
            rs.close();

            game.setGameId(id);
            loadPlayersFromDB(game);

            if (playerNo >= 0 && playerNo < game.getPlayersNumber()) {
                game.setCurrentPlayer(game.getPlayer(playerNo));
            } else {
                return null;
            }


            loadCardFieldsFromDB(game);


            return game;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Some DB error");
        }
        return null;
    }

    //__________________________________________________________________________________________________________________

    @Override
    //getGames: Denne metode returnerer en liste af GameInDB objekter, som repræsenterer alle spil i databasen.
    // Hvert GameInDB objekt indeholder spillets id og navn
    public List<GameInDB> getGames() {
        List<GameInDB> result = new ArrayList<>();
        try {
            PreparedStatement ps = getSelectGameIdsStatement();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(GAME_GAMEID);
                String name = rs.getString(GAME_NAME);
                result.add(new GameInDB(id,name));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    //__________________________________________________________________________________________________________________

    private void createPlayersInDB(Board game) throws SQLException {
        PreparedStatement ps = getSelectPlayersStatementU();
        ps.setInt(1, game.getGameId());

        ResultSet rs = ps.executeQuery();
        for (int i = 0; i < game.getPlayersNumber(); i++) {
            Player player = game.getPlayer(i);
            rs.moveToInsertRow();
            rs.updateInt(PLAYER_GAMEID, game.getGameId());
            rs.updateInt(PLAYER_PLAYERID, i);
            rs.updateString(PLAYER_NAME, player.getName());
            rs.updateString(PLAYER_COLOUR, player.getColor());
            rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
            rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
            rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
            rs.updateInt(PLAYER_HP, player.getHp());

            rs.insertRow();
        }

        rs.close();
    }

    private void loadPlayersFromDB(Board game) throws SQLException {
        PreparedStatement ps = getSelectPlayersASCStatement();
        ps.setInt(1, game.getGameId());

        ResultSet rs = ps.executeQuery();
        int i = 0;
        while (rs.next()) {
            int playerId = rs.getInt(PLAYER_PLAYERID);
            if (i++ == playerId) {
                String name = rs.getString(PLAYER_NAME);
                String colour = rs.getString(PLAYER_COLOUR);
                Player player = new Player(game, colour ,name);
                game.addPlayer(player);

                int x = rs.getInt(PLAYER_POSITION_X);
                int y = rs.getInt(PLAYER_POSITION_Y);
                player.setSpace(game.getSpace(x,y));
                int heading = rs.getInt(PLAYER_HEADING);
                player.setHeading(Heading.values()[heading]);

            } else {
                System.err.println("Game in DB does not have a player with id " + i +"!");
            }
        }
        rs.close();
    }

    private void updatePlayersInDB(Board game) throws SQLException {
        PreparedStatement ps = getSelectPlayersStatementU();
        ps.setInt(1, game.getGameId());

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int playerId = rs.getInt(PLAYER_PLAYERID);
            Player player = game.getPlayer(playerId);
            // rs.updateString(PLAYER_NAME, player.getName()); // not needed: player's names does not change
            rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
            rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
            rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());

            rs.updateRow();
        }
        rs.close();

    }
    /*
     */
    //Metoden her opretter en skema for alle kort og håndkort, som gemmes i databasen.
    private void createCardFieldsInDB(Board game) throws SQLException {
        //vi opretter preparedstatement som referer til metoden getSelectCardFieldStatementU
        PreparedStatement ps = getSelectCardFieldStatementU();
        //Vi tager udgangspunkt i gameid
        ps.setInt(1, game.getGameId());
        //Vi eksekverer query, som er en metode der henter data fra databsen
        ResultSet rs = ps.executeQuery();

        //overordnet så koden ovenover sørger for at gemme data i databasen

        for (int i = 0; i < game.getPlayersNumber(); i++) {
            player = game.getPlayer(i);
            for (int j = 0; j < 5; j++) {
                rs.moveToInsertRow();
                rs.updateInt(FIELD_GAMEID, game.getGameId());
                rs.updateInt(FIELD_PLAYERID, i);
                rs.updateInt(FIELD_POSITION, j);
                rs.updateInt(FIELD_TYPE, FIELD_TYPE_REGISTER);
                rs.updateBoolean(FIELD_VISIBLE, player.getProgramField(j).isVisible());
                CommandCard card = player.getProgramField(j).getCard();
                if (card != null){
                    rs.updateInt(FIELD_COMMAND, card.command.ordinal());
                }
                rs.insertRow();
            }
            for (int j = 0; j < 8; j++) {
                rs.moveToInsertRow();
                rs.updateInt(FIELD_GAMEID, game.getGameId());
                rs.updateInt(FIELD_PLAYERID, i);
                rs.updateInt(FIELD_POSITION, j);
                rs.updateInt(FIELD_TYPE, FIELD_TYPE_HAND);
                rs.updateBoolean(FIELD_VISIBLE, player.getCardField(j).isVisible());
                CommandCard card = player.getCardField(j).getCard();
                if (card != null){
                    rs.updateInt(FIELD_COMMAND, card.command.ordinal());
                }
                rs.insertRow();
            }
        }
        rs.close();
    }
/*
I metoden loadCardFieldsFromDB loades de værdier som vi i forrige metode gemte i databasen. Der bruges et while loop der hele tiden tjekker om der er mere som skal loades.
Vi henter dermed værdierne ved at bruge metoderne getInt, getObject eller getBoolean.
For at hente den Field som kortene befinder sig på tjekkes der om typen er FIELD_TYPE_REGISTER eller FIELD_TYPE_HAND og derefter bruges den rigtig metode til at hente de rigtige kort.
Hvis field ikke er tom så sættes Visibile til at være true og dermed visible.
 */

    private void loadCardFieldsFromDB(Board game) throws SQLException {
        PreparedStatement ps = getSelectCardFieldStatement();
        ps.setInt(1, game.getGameId());

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int playerID = rs.getInt(FIELD_PLAYERID);
            Player player = game.getPlayer(playerID);
            int type = rs.getInt(FIELD_TYPE);
            int pos = rs.getInt(FIELD_POSITION);
            CommandCardField field = null;
            if (type == FIELD_TYPE_REGISTER) {
                field = player.getProgramField(pos);
            } else if (type == FIELD_TYPE_HAND) {
                player.getCardField(pos);
            }
            else {
                field = null;
            }
            if (field != null) {
                field.setVisible(rs.getBoolean(FIELD_VISIBLE));
                Object c = rs.getObject(FIELD_COMMAND);
                if (c != null) {
                    Command card = Command.values() [rs.getInt(FIELD_COMMAND)];
                    field.setCard(new CommandCard(card));
                }
            }
        }
        rs.close();
    }
    //_________________________________________________________________________________________________________________
    /*
    I denne metode er det lidt på samme måde som createCardFieldInDB, hvor der findes mange af de samme måder tingene er brugt på.
    Og dermed er der ikke så meget at forklare yderligere.
     */
    private void updateCardFieldsInDB(Board game) throws SQLException {
        PreparedStatement ps = getSelectPlayersStatementU();
        ps.setInt(1, game.getGameId());

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int playerId = rs.getInt(FIELD_PLAYERID);
            Player player = game.getPlayer(playerId);
            int type = rs.getInt(FIELD_TYPE);
            int position = rs.getInt(FIELD_POSITION);
            CommandCardField field;

            if (type == FIELD_TYPE_REGISTER) {
                field = player.getProgramField(position);
            } else {
                field = player.getCardField(position);
            }
            CommandCard card = field.getCard();
            if (card != null) {
                Command command = card.command;
                rs.updateInt(FIELD_COMMAND, command.ordinal());
            } else {
                rs.updateNull(FIELD_COMMAND);
            }
            rs.updateBoolean(FIELD_VISIBLE,field.isVisible());
            rs.updateRow();
        }
        rs.close();
    }

    //-----------------------------------------------------------------------------------------------------------------


    private static final String SQL_INSERT_GAME =
            "INSERT INTO Game(name, currentPlayer, phase, step, boardName) VALUES (?, ?, ?, ?, ?)";

    private PreparedStatement insert_game_stmt = null;

    private PreparedStatement getInsertGameStatementRGK() {
        if (insert_game_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                insert_game_stmt = connection.prepareStatement(
                        SQL_INSERT_GAME,
                        Statement.RETURN_GENERATED_KEYS);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return insert_game_stmt;
    }

    private static final String SQL_SELECT_GAME =
            "SELECT * FROM Game WHERE gameID = ?";

    private PreparedStatement select_game_stmt = null;

    private PreparedStatement getSelectGameStatementU() {
        if (select_game_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_game_stmt = connection.prepareStatement(
                        SQL_SELECT_GAME,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return select_game_stmt;
    }

    private static final String SQL_SELECT_PLAYERS =
            "SELECT * FROM Player WHERE gameID = ?";

    private PreparedStatement select_players_stmt = null;

    private PreparedStatement getSelectPlayersStatementU() {
        if (select_players_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_players_stmt = connection.prepareStatement(
                        SQL_SELECT_PLAYERS,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return select_players_stmt;
    }
    private static final String SQL_SELECT_CARD_FIELDS =
            "SELECT * FROM CARDFIELD WHERE gameID = ?";
    private PreparedStatement select_card_field_stmt = null;
    private PreparedStatement getSelectCardFieldStatement() {
        if (select_card_field_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_card_field_stmt = connection.prepareStatement(SQL_SELECT_CARD_FIELDS);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return select_card_field_stmt;
    }
    private PreparedStatement select_card_field_stmt_u = null;
    private PreparedStatement getSelectCardFieldStatementU() {
        if (select_card_field_stmt_u == null) {
            Connection connection = connector.getConnection();
            try {
                select_card_field_stmt_u = connection.prepareStatement(
                        SQL_SELECT_CARD_FIELDS,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return  select_card_field_stmt_u;
    }
    private static final String SQL_SELECT_PLAYERS_ASC =
            "SELECT * FROM Player WHERE gameID = ? ORDER BY playerID ASC";

    private PreparedStatement select_players_asc_stmt = null;

    private PreparedStatement getSelectPlayersASCStatement() {
        if (select_players_asc_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                // This statement does not need to be updatable
                select_players_asc_stmt = connection.prepareStatement(
                        SQL_SELECT_PLAYERS_ASC);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return select_players_asc_stmt;
    }

    private static final String SQL_SELECT_GAMES =
            "SELECT gameID, name FROM Game";

    private PreparedStatement select_games_stmt = null;

    private PreparedStatement getSelectGameIdsStatement() {
        if (select_games_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_games_stmt = connection.prepareStatement(
                        SQL_SELECT_GAMES);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return select_games_stmt;
    }




}

//Ud over disse metoder indeholder Repository klassen også en række hjælpefunktioner til at udføre databaseforespørgsler
// og oprette PreparedStatement objekter til at udføre de specifikke SQL-forespørgsler.
// Disse hjælpefunktioner er primært ansvarlige for at håndtere spil, spillere og kortfelter i databasen.
//Det er vigtigt at bemærke, at Repository klassen er afhængig af en Connector objekt til at oprette og vedligeholde forbindelsen til databasen.
// Dette er implementeret ved hjælp af en konstruktør, der tager en Connector som argument og gemmer den som en instansvariabel.
