package model.card;
import model.player.Player;
import engine.GameManager;
import engine.board.BoardManager;
import java.util.ArrayList; 
import exception.ActionException; 
import exception.InvalidMarbleException; 
import exception.IllegalMovementException;
import exception.IllegalDestroyException; 
import exception.CannotFieldException; 
import exception.CannotDiscardException;
import model.Colour;
import model.player.Marble; 


public abstract class Card {
	private final String name;
    private final String description;
    protected BoardManager boardManager;
    protected GameManager gameManager;

    public Card(String name, String description, BoardManager boardManager, GameManager gameManager) {
        this.name = name;
        this.description = description;
        this.boardManager = boardManager;
        this.gameManager = gameManager;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    public boolean validateMarbleSize(ArrayList<Marble> marbles) {
    	ArrayList<Marble> expected = boardManager.getActionableMarbles();
        return marbles.size() == expected.size();
    	
    }
    public boolean validateMarbleColours(ArrayList<Marble> marbles) {
    	
        Colour playerColour = gameManager.getActivePlayerColour();  

        boolean isJackCard = "Jack".equalsIgnoreCase(getName());  

        if (isJackCard) {
            if (marbles.size() != 2) {
                return false; 
            }
            for (Marble marble : marbles) {
                if (!marble.getColour().equals(playerColour)) {
                    return false;  
                }
            }
            return true;
        }
            return true;
    }
    
    
    public void act(ArrayList<Marble> marbles) throws ActionException, InvalidMarbleException {
        // Example logic for performing actions on the selected marbles

        if (marbles.isEmpty()) {
            throw new InvalidMarbleException("No marbles to act upon.");
        }

        // Get actionable marbles from the board
        ArrayList<Marble> actionableMarbles = boardManager.getActionableMarbles();

        // For each marble in the passed list, check if it's actionable
        for (Marble marble : marbles) {
            if (!actionableMarbles.contains(marble)) {
                throw new InvalidMarbleException("Marble is not actionable.");
            }

            // Example action: move the marble
            try {
                boardManager.moveBy(marble, 1, false);  // Move by 1 step without destroying
            } catch (IllegalMovementException | IllegalDestroyException e) {
                // Throw ActionException with an anonymous subclass if the action fails
                throw new ActionException() {};
            }

            // Example action: send marble to base
            try {
                boardManager.sendToBase(marble);
            } catch (CannotFieldException e) {
                // Throw ActionException with an anonymous subclass if unable to send marble to base
                throw new ActionException() {};
            }
        }

        // Attempt to discard a card after completing actions on marbles
        try {
            gameManager.discardCard();
        } catch (CannotDiscardException e) {
            // Throw ActionException with an anonymous subclass if unable to discard card
            throw new ActionException("Unable to discard card after action.") {};
        }
    }


    
    
}
