/* Need to switch off FK check for MySQL since there are crosswise FK references */
SET FOREIGN_KEY_CHECKS = 0;;

CREATE TABLE IF NOT EXISTS Game (
  gameID int NOT NULL UNIQUE AUTO_INCREMENT,
  name varchar(255),
    phase tinyint,
    step tinyint,
    currentPlayer tinyint NULL,

    PRIMARY KEY (gameID),
    FOREIGN KEY (gameID, currentPlayer) REFERENCES Player(gameID, playerID)
    );;

CREATE TABLE IF NOT EXISTS Player (
  gameID int NOT NULL,
   playerID tinyint NOT NULL,

 name varchar(255),
    colour varchar(31),

    positionX int,
    positionY int,
    heading tinyint,

    PRIMARY KEY (gameID, playerID),
    FOREIGN KEY (gameID) REFERENCES Game(gameID)
    );;

SET FOREIGN_KEY_CHECKS = 1;;

CREATE TABLE IF NOT EXISTS CardField (
 gameID int NOT NULL,
 playerID tinyint NOT NULL,
 type tinyint NOT NULL,
 position tinyint NOT NULL,
 visible BIT NOT NULL,
 command tinyint,

   PRIMARY KEY (gameID, playerID, type, position),
    FOREIGN KEY (gameID) REFERENCES Game(gameID)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    FOREIGN KEY (gameID, playerID) REFERENCES Player(gameID, playerID)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    )
/*Dette er et SQL-skript, der definerer tabellerne i en database og deres relationer. Skriptet opretter to tabeller: Game og Player.

    Tabellen Game indeholder følgende kolonner:

    gameID: Unik identifikator for spillet (auto increment)
    name: Navnet på spillet
    phase: Fasen af spillet
    step: Trin inden for fasen
    currentPlayer: ID for den aktuelle spiller (kan være null)
    Tabellen Player indeholder følgende kolonner:

    gameID: ID for spillet, som spilleren tilhører
    playerID: Unik identifikator for spilleren inden for spillet
    name: Navnet på spilleren
    colour: Farven for spilleren
    positionX: X-koordinat for spillerens position
    positionY: Y-koordinat for spillerens position
    heading: Retningen, som spilleren vender (enum eller numerisk værdi)
    Derudover er der oprettet en tabel CardField, som ikke er inkluderet i det viste skærmbillede.

    Der er også defineret fremmednøglebegrænsninger (foreign key constraints), der forbinder tabellerne og sikrer dataintegritet. De sikrer, at gameID i Player refererer til gameID i Game-tabellen, og at gameID og playerID i CardField refererer til gameID og playerID i Player-tabellen.
    Til sidst er der to linjer, der midlertidigt deaktiverer og aktiverer kontrol af fremmednøglebegrænsninger.*/
