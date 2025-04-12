package engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import engine.board.Board;
import engine.board.SafeZone;
import exception.CannotDiscardException;
import exception.CannotFieldException;
import exception.GameException;
import exception.IllegalDestroyException;
import exception.InvalidCardException;
import exception.InvalidMarbleException;
import exception.SplitOutOfRangeException;
import model.Colour;
import model.card.Card;
import model.card.Deck;
import model.player.*;

@SuppressWarnings("unused")
public class Game implements GameManager {
    private final Board board;
    private final ArrayList<Player> players;
	private int currentPlayerIndex;
    private final ArrayList<Card> firePit;
    private int turn;

    public Game(String playerName) throws IOException {
        turn = 0;
        currentPlayerIndex = 0;
        firePit = new ArrayList<>();

        ArrayList<Colour> colourOrder = new ArrayList<>();
        
        colourOrder.addAll(Arrays.asList(Colour.values()));
        
        Collections.shuffle(colourOrder);
        
        this.board = new Board(colourOrder, this);
        
        Deck.loadCardPool(this.board, (GameManager)this);
        
        this.players = new ArrayList<>();
        this.players.add(new Player(playerName, colourOrder.get(0)));
        
        for (int i = 1; i < 4; i++) 
            this.players.add(new CPU("CPU " + i, colourOrder.get(i), this.board));
        
        for (int i = 0; i < 4; i++) 
            this.players.get(i).setHand(Deck.drawCards());
        
    }
    
    public Board getBoard() {
        return board;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Card> getFirePit() {
        return firePit;
    }
    
    // Q2.7.9 Regains a marble back to the owner’s Home Zone (collection of marbles).
	@Override
	public void sendHome(Marble marble){
	    Colour ownerColour = marble.getColour();
	    Player owner = null;
	    for (Player player : players) {
	        if (player.getColour() == ownerColour) {
	            owner = player;
	            break;
	        }
	    } 
	    owner.regainMarble(marble);
	}
	
	// Q2.7.10 
	@Override
	public void fieldMarble() throws CannotFieldException, IllegalDestroyException {
		Player currentPlayer = players.get(currentPlayerIndex);
		Marble marble = currentPlayer.getOneMarble();
		if(marble == null) throw new CannotFieldException("No Marbles Available");
		else {
			this.board.sendToBase(marble);
			for(int i = 0; i<currentPlayer.getMarbles().size();i++) {
				if(marble == currentPlayer.getMarbles().get(i)) {
					currentPlayer.getMarbles().remove(i);
				}
			}
		}
	}
	
	// Q2.7.11 
	@Override
	public void discardCard(Colour colour) throws CannotDiscardException {
		Player p = null;
		for(Player player: players) {
			if(player.getColour() == colour) {
				p = player;
			}
		}
		
		if(p.getHand().size() == 0) throw new CannotDiscardException("No cards to be thrown");
		else {
			int i = (int)(Math.random()*p.getHand().size());
			p.getHand().remove(i);
		}
	}
	
	// Q2.7.12 Discards a random card from the hand of a random player colour other than the current.
	@Override
	public void discardCard() throws CannotDiscardException {
		// TODO Auto-generated method stub
		int i = (int) (Math.random()*players.size());
		discardCard(players.get(i).getColour());
	}
	
	// Q2.7.12 Returns the colour of the current player.
	@Override
	public Colour getActivePlayerColour() {
		return players.get(currentPlayerIndex).getColour();
	}
	
	//2.7.14 Returns the colour of the next player.
	@Override
	public Colour getNextPlayerColour() {
		return players.get((currentPlayerIndex+1)%players.size()).getColour();
	}
	// Q2.7.1 This method allows the current player to select the given card.
	public void selectCard(Card card) throws InvalidCardException{
		players.get(currentPlayerIndex).selectCard(card);
	}
	// Q2.7.2 This method allows the current player to select the given marble.
	public void selectMarble(Marble marble) throws InvalidMarbleException{
		players.get(currentPlayerIndex).selectMarble(marble);
	}
	// Q2.7.3 This method allows the current player to deselect all previously selected card and marbles
	public void deselectAll() {
		players.get(currentPlayerIndex).deselectAll();
	}
	// Q2.7.4 
	public void editSplitDistance(int splitDistance) throws SplitOutOfRangeException{
		if(splitDistance <1 || splitDistance>6)
			throw new SplitOutOfRangeException("Split value must be between 1-6.");
		board.setSplitDistance(splitDistance);
	}
	// Q2.7.5 Checks whether the player’s turn should be skipped by comparing their hand card count against the turn.
	public boolean canPlayTurn() {
		int turnIndex = turn % 4; 
		return players.get(currentPlayerIndex).getHand().size() > turnIndex;
	}
	// Q2.7.6 This method allows the current player to play their turn.
	public void playPlayerTurn() throws GameException{
		players.get(currentPlayerIndex).play();
	}
	// Q2.7.7
	public void endPlayerTurn() throws SplitOutOfRangeException {
		// (a) Removing the current player’s selected card from their hand and adding it to the firePit.
		Card selectedCard = players.get(currentPlayerIndex).getSelectedCard();
		if(selectedCard != null) {
			firePit.add(selectedCard);
			players.get(currentPlayerIndex).getHand().remove(selectedCard);
		}
		// (b) Deselecting everything the current player has selected.
		players.get(currentPlayerIndex).deselectAll();
		// (c) Moving on the next player and setting them as the current player.
		currentPlayerIndex = (currentPlayerIndex + 1)%players.size();
		turn++;
		// (d) Starting a new turn once all players have played a card and the play order is back to the the first player
		if(turn % (4 * players.size()) == 0)
			startNewRound();
		// (e) Starting a new round once 4 turns have passed by resetting the turn counter.
		if(turn == 4) startNewRound();
		
		
	}
	//helper method for starting a new round
	public void startNewRound() throws SplitOutOfRangeException {
	    turn = 0;
	    // (f) Refilling all players’ hands from the deck when starting a new round.
	    for (Player player : players) {
	    	// (g) Refilling the Deck’s card pool with the cards in the firepit and clearing it if the cards pool has fewer than 4 cards to draw.
	        while (player.getHand().size() < 4) {
	            if (Deck.getPoolSize() < 4 && !firePit.isEmpty()) {
	                Deck.refillPool(firePit); 
	                while(!firePit.isEmpty()) {
	                	firePit.remove(0);
	                }
	            }
	            ArrayList<Card> newCards = Deck.drawCards();
	            player.getHand().addAll(newCards);
	        }
	    }
	    this.editSplitDistance(-1);
	    System.out.println("Starting a new round...");
	}
	// Q2.7.8 
	public Colour checkWin(){
		ArrayList<SafeZone> SafeZones = this.board.getSafeZones();
		for(SafeZone safeZone: SafeZones) {
			if(safeZone.isFull()) {
				return safeZone.getColour();
			}
		}
		return null;
	}
}
