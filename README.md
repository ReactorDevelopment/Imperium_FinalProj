# Imperium_FinalProj
Comp Sci II Final

Two areas of interest: File Handling and Exception Handling
  File Handling:
    Most notably in 
    GameActivity->saveGame(String) Line 461
    OpenSaveActivity->loadGame(String) Line 298

  Exception Handling:
    Most notably in 
    OpenSaveActivity->loadGame(String) Line 301
    Many more examples found in various places ex (GameActivity, Game, Ai, Province etc

Game Play and usage:
  Initially, pressing the New Game button loads the create menu, where the slider determines the number of players
    and the helmets below the flags togggle a player's status as a computer or not
  Pressing Create builds a new game, whose mechanics are very similar to those of Risk
  In a game a player begins in a setup stage, where taping on any unowned province claims it
  Once all have been claimed, a player places 50-30 additional troops on their owned provinces
  Once these troops are exhausted, the turns begin, each with three phases
    The Placement phase allows a player to place reinforcements on owned provinces
    The Attack phase allows the player to attack bordering provinces by taping on one then the other and pressing the attack button
    The Transport phase ends a turn after a player has transported troops from one owned province to another, or by pressing End Transport
    The button to switch stages is located in the bottom right corner
  Pressing and holding a province shows information about the province and the continent it is on
  The arrow on the top right opens the navigation bar where a player can save a game to a file or quit the current game
  
  Pressing Load Game from the main menu loads the open save menu, where, upon taping one of the listed saves, it will highlight
    this allows a user to either rename the save, load it into a game, or delete it
