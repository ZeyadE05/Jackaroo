package model.card;


import model.player.Player;
import engine.GameManager;
import engine.board.BoardManager;
import java.util.ArrayList;
import exception.ActionException;
import exception.InvalidMarbleException;
import model.Colour;
import model.player.Marble;
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
    	String lowerName = name.toLowerCase();

        switch (lowerName) {
            case "jack":
            case "seven":
                return marbles.size() == 2;

            case "queen":
            case "ten":
                return marbles.size() == 0;

            case "ace":
            case "king":
                return marbles.size() == 0 || marbles.size() == 1;

            case "burner":
            case "saver":
            case "four":
            case "five":
                return marbles.size() == 1;

            default:
                // Default standard cards (e.g., 2, 3, 6, 8, 9)
                return marbles.size() == 1;
        }
    	
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
    
    
    public abstract void act(ArrayList<Marble> marbles) throws ActionException, InvalidMarbleException;


    
    
}
