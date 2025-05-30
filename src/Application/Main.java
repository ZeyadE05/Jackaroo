package Application;

import java.io.IOException;
import java.util.ArrayList;

import model.Colour;
import model.card.Card;
import model.card.Deck;
import model.card.standard.Ace;
import model.card.standard.King;
import model.card.standard.Seven;
import model.player.Marble;
import model.player.Player;
import engine.Game;
import engine.board.Cell;
import engine.board.SafeZone;
import exception.GameException;
import exception.InvalidCardException;
import exception.InvalidMarbleException;
import exception.SplitOutOfRangeException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Main extends Application{
	String playerName;
	ArrayList<CircleWithText> playerLabels;
	Game model;
	Pane boardLayout;
	GridPane trackView;
	TextField inputName;
	ArrayList <MarbleView> trackCells;
	ArrayList<ArrayList<MarbleView>> safeZonesView;
	Pane DeckView;
	Colour mainPlayerColour;
	Color playerColor;
	Color CPU1Color;
	Color CPU2Color;
	Color CPU3Color;
	CardView firePit;
	HBox playerHandBox;
	HBox CPU1HandBox;
	HBox CPU2HandBox;
	HBox CPU3HandBox;
	ArrayList<CardView> cards;
	CardView selectedCardView;
	ArrayList<HomeZoneView> homeZonesView;
	ArrayList <MarbleView> selectedMarblesView;
	int splitDistance = 1;
	Scene winnerScene;
	ArrayList <Integer> TrapIndex;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setResizable(true);
		primaryStage.setMinWidth(1000);
		primaryStage.setMinHeight(900);
		//login scene
		VBox loginLayout = new VBox(20);
		Label welcome = new Label("Welcome to Jackaroo!");
		loginLayout.setAlignment(Pos.CENTER);
		loginLayout.setStyle("-fx-background-color: #8B5C2D;");
		inputName = new TextField();
		inputName.setPromptText("enter your name");
		inputName.setMaxWidth(150);
		Button startGame = new Button("StartGame");
		startGame.setDefaultButton(true);
		loginLayout.getChildren().addAll(welcome,inputName,startGame);
		Scene login = new Scene(loginLayout,1000,1000);
		// main board Scene
		boardLayout = new Pane();
		boardLayout.setStyle("-fx-background-color: #708090;");
		//play button
		Button play = new Button("PLAY");
		play.setOnAction(e->{
			handlePlay();
			if(model.checkWin() != null) {
				primaryStage.setScene(winnerScene);
			}
		});
		play.setScaleX(3);
		play.setScaleY(3);
		play.setLayoutX(800);
		play.setLayoutY(300);
		boardLayout.getChildren().add(play);
		Scene Board = new Scene(boardLayout,1000,1000);
		//adding shortcut for fielding
		Board.setOnKeyPressed(event -> {
		    if (event.getCode() == KeyCode.F) {
		        fieldMarble();
		        // Trigger your action
		        announceWin(model.getActivePlayerColour());
		        primaryStage.setScene(winnerScene);
		    }
		});
		startGame.setOnAction(e1 -> {
			handleStartGame();
			primaryStage.setScene(Board);
		});
		primaryStage.setScene(login);
		primaryStage.show();
	}
	
	public void findTrapPos() {
		TrapIndex = new ArrayList<>();
		ArrayList <Cell> gameTrack = model.getBoard().getTrack();
		for(int i = 0; i<gameTrack.size();i++) {
			if(gameTrack.get(i).isTrap()) {
				TrapIndex.add(i);
			}
		}
	}
	
	public void fieldMarble() {
		if(cards!= null) {
			for(CardView c: cards) {
				if(c.getCard() instanceof Ace || c.getCard() instanceof King) {
					selectCardHandle(c);
					handlePlay();
					return;
				}
			}
			Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Info Dialog");
	        alert.setHeaderText("Unable to Field!!");
	        alert.setContentText("Neither a King nor Ace are available to allow fielding");
	        alert.showAndWait();
		}
	}
	
	public void handlePlay() {
		if(selectedCardView != null) {
			findTrapPos();
        	ArrayList<Integer> oldTrapPos = TrapIndex;
        	boolean firepit = false;
			if (model.canPlayTurn()) {
				firepit = true;
	            try {
	                this.model.playPlayerTurn();
	            } catch (GameException e) {
	            	if( e instanceof InvalidCardException ) {
	            		Alert alert = new Alert(AlertType.WARNING);
	        	        alert.setTitle("Info Dialog");
	        	        alert.setHeaderText("Invalid Card Selection!!");
	        	        alert.setContentText(e.getMessage());
	        	        alert.showAndWait();
	            	}
	            	if( e instanceof InvalidMarbleException) {
	            		Alert alert = new Alert(AlertType.WARNING);
	        	        alert.setTitle("Info Dialog");
	        	        alert.setHeaderText("Invalid Marble Selection!!");
	        	        alert.setContentText(e.getMessage());
	        	        alert.showAndWait();
	            	}
	            	
	            }
	            
	        }
	        else {// to stop user from throwing card away when not his turn;
	        	this.model.deselectAll();
	            selectedCardView = null;
	            selectedMarblesView = new ArrayList<>();
	        }
	        this.model.endPlayerTurn();
	        if (!model.getFirePit().isEmpty()) {
	            Card latest = model.getFirePit().get(model.getFirePit().size() - 1);
	            if(firepit) {
	            	updateFirePit(latest);
	            }
	        }
	        findTrapPos();
	        ArrayList<Integer> newTrapPos = TrapIndex;
	        for(int i = 0; i<newTrapPos.size();i++) {
	        	if(newTrapPos.get(i)!=oldTrapPos.get(i)) {
	            		Alert alert = new Alert(AlertType.INFORMATION);
	        	        alert.setTitle("Info Dialog");
	        	        alert.setHeaderText("TRAP!!");
	        	        alert.setContentText("Marble sent to homezone");
	        	        alert.showAndWait();
	            	}
	        	}
	        
	        if(model.checkWin()!=null) {
	        	announceWin(model.checkWin());
	        	return;
	        }
	        if (model.getActivePlayerColour() == mainPlayerColour) {
	            this.model.deselectAll();
	            selectedCardView = null;
	            selectedMarblesView = new ArrayList<>();
	        }

	        updateBoardView();
	        updatePlayerNames();
	        playTurnsRecursively(0);
	}
		else {
			if(model.canPlayTurn()) {
				Alert alert = new Alert(AlertType.WARNING);
		        alert.setTitle("Info Dialog");
		        alert.setHeaderText("Invalid Card Selection!!");
		        alert.setContentText("Please select a card");
		        alert.showAndWait();
			}
			else {
				this.model.endPlayerTurn();
				updateBoardView();
		        updatePlayerNames();
		        playTurnsRecursively(0);
			}
		}
		
	}
	
	public void announceWin(Colour color) {
		String x = color + "";
		StackPane s = new StackPane();
		x.toUpperCase();
		Label winner = new Label(x + " HAS WON!!!");
		winner.setStyle("-fx-font-size: 50px;");
		if(color == Colour.BLUE) {
			s.setStyle("-fx-background-color: #0000FF;");
			winner.setTextFill(Color.WHITE);
		}
		if(color == Colour.GREEN) {
			s.setStyle("-fx-background-color: #008000;");
			winner.setTextFill(Color.WHITE);
		}
		if(color == Colour.RED) {
			s.setStyle("-fx-background-color: #FF0000;");
			winner.setTextFill(Color.WHITE);
		}
		if(color == Colour.YELLOW) {
			s.setStyle("-fx-background-color: #FFFF00;");
			winner.setTextFill(Color.BLACK);
		}
		s.getChildren().add(winner);
		winnerScene = new Scene(s,400,400);
	}
	
	private void playTurnsRecursively(int turnCount) {
	    if (turnCount >= 3) {
	        return; // Base case: stop after 4 turns
	    }
	    int time = (int) ((Math.random()*2) + 1);
	    PauseTransition pause = new PauseTransition(Duration.seconds(time));
	    pause.setOnFinished(event -> {
	    	boolean firepit = false;
	        if (model.canPlayTurn()) {
	        	firepit = true;
	            try {
	                this.model.playPlayerTurn();
	            } catch (GameException e) {
	            }
	            
	        }
	        this.model.endPlayerTurn();
	        if(model.checkWin()!=null) {
	        	announceWin(model.checkWin());
	        	return;
	        }
	        if (!model.getFirePit().isEmpty()) {
	            Card latest = model.getFirePit().get(model.getFirePit().size() - 1);
	            if(firepit) {
	            	updateFirePit(latest);
	            }
	        }

	        updateBoardView();
	        updatePlayerNames();

	        // Recursive call to trigger next turn
	        playTurnsRecursively(turnCount + 1);
	    });

	    pause.play();
	}
	
	public void handleStartGame(){
		try {
			String name = inputName.getText();
			if(!name.trim().isEmpty()){
				playerName = inputName.getText();
			}
			else{
				playerName = "Player 1";
			}
			this.model = new Game(playerName);
			mainPlayerColour = model.getPlayers().get(0).getColour();
			//initializing player names
			renderPlayerNames();
			//initializing track
			renderTrack();
			//initializing SafeZones
			renderSafeZones();
			//initializing Home Zones
			renderHomeZones();
			//initializing fire pit
			updateFirePit(null);
			//initializing player hand
			updatePlayerHand();
			//initializing CPU Hand
			updateCPUHand();
			//initializing Deck View
			renderDeck();
		} 
		catch (IOException e) {
			Alert alert = new Alert(AlertType.WARNING);
	        alert.setTitle("Info Dialog");
	        alert.setHeaderText("error!!");
	        alert.setContentText("Please enter valid Input");
	        alert.showAndWait();
		}
	}
	
	public void updateTrackView(){
		for(int i = 0; i<100;i++){
			Cell cell = this.model.getBoard().getTrack().get(i);
			if(cell.getMarble()!= null){
				int row = trackView.getRowIndex(trackCells.get(i));
				int col = trackView.getColumnIndex(trackCells.get(i));
				trackView.getChildren().remove(trackCells.get(i));
				MarbleView c = new MarbleView(8,cell.getMarble(),getColor(cell.getMarble().getColour()));
				c.setOnMouseClicked(e -> selectMarbleHandle(c));
				trackCells.set(i, c);
				trackView.add(c, col, row);
			}
			else{
				int row = trackView.getRowIndex(trackCells.get(i));
				int col = trackView.getColumnIndex(trackCells.get(i));
				trackView.getChildren().remove(trackCells.get(i));
				MarbleView c = new MarbleView(8,cell.getMarble(),Color.WHITE);
				trackCells.set(i, c);
				trackView.add(c, col, row);
			}
		}
	}
	
	public void updateSafeZonesView(){
		ArrayList<SafeZone> safeZones = this.model.getBoard().getSafeZones();
		for(int i = 0; i<safeZones.size();i++){
			SafeZone s = safeZones.get(i);
			for(int j = 0; j<s.getCells().size();j++){
				Marble marble = s.getCells().get(j).getMarble();
				if(marble!= null){
//					safeZonesView.get(i).get(j).setOpacity(1.5);
					int row = trackView.getRowIndex(safeZonesView.get(i).get(j));
					int col = trackView.getColumnIndex(safeZonesView.get(i).get(j));
					trackView.getChildren().remove(safeZonesView.get(i).get(j));
					MarbleView c = new MarbleView(8,marble,getColor(marble.getColour()));
					c.setOnMouseClicked(e -> selectMarbleHandle(c));
					safeZonesView.get(i).set(j, c);
					trackView.add(c, col, row);
				}
				else{
					int row = trackView.getRowIndex(safeZonesView.get(i).get(j));
					int col = trackView.getColumnIndex(safeZonesView.get(i).get(j));
					trackView.getChildren().remove(safeZonesView.get(i).get(j));
					MarbleView c = new MarbleView(8,marble,Color.WHITE);
					safeZonesView.get(i).set(j, c);
					trackView.add(c, col, row);
				}
			}
		}
	}
	
	public void updateHomeZoneView(){
		ArrayList<Player> players = model.getPlayers();
		for(HomeZoneView h: homeZonesView) {
			boardLayout.getChildren().remove(h);
		}
		homeZonesView = new ArrayList<>();
		for(int i = 0; i<players.size();i++){
			ArrayList<Marble> homeZone = players.get(i).getMarbles();
			homeZonesView.add(new HomeZoneView(getColor(players.get(i).getColour()),homeZone));
		}
		renderHomeZones();
		
	}
	
	public void updateBoardView(){
		updateTrackView();
		updatePlayerHand();
		updateCPUHand();
		updateSafeZonesView();
		updateHomeZoneView();
		updateCPUHand();
		renderDeck();
	}
	
	public void updatePlayerHand(){
		boardLayout.getChildren().remove(playerHandBox);
		System.out.println(this.model.getPlayers().get(0).getHand().size());
		playerHandBox = new HBox(10);
		selectedCardView = null;
		ArrayList<Card> hand = this.model.getPlayers().get(0).getHand();
		cards = new ArrayList<>();
		for(Card card: hand){
			CardView c = new CardView(card);
			cards.add(c);
			c.setOnMouseClicked(event -> selectCardHandle(c));
			playerHandBox.getChildren().add(c);
		}
		playerHandBox.setLayoutX(280);
		playerHandBox.setLayoutY(700);
		boardLayout.getChildren().add(playerHandBox);
	}
	
	public void selectCardHandle(CardView card) {
	    try {
	        if (selectedCardView == null) {
	            // Try to select the card in the model first
	            this.model.getPlayers().get(0).selectCard(card.getCard());
	            // If successful, update the UI
	            selectedCardView = card;
	            card.setSelected(true);
	            System.out.println("Card selected: " + card.getCard().getName());
	            if(card.getCard() instanceof Seven) {
	            	selectSplitDistance();
	            }
	        } else {
	            if (selectedCardView != card) {
	                System.out.println("Please deselect card to pick another");
	            } else {
	                // Try to deselect in the model first
	                this.model.getPlayers().get(0).selectCard(null);
	                // If successful, update the UI
	                selectedCardView = null;
	                card.setSelected(false);
	                System.out.println("Card deselected");
	            }
	        }
	        
	        // Print selected card info only if a card is actually selected
	        Card selectedCard = this.model.getPlayers().get(0).getSelectedCard();
	        if (selectedCard != null) {
	            System.out.println("Currently selected card: " + selectedCard.getName());
	        } else {
	            System.out.println("No card currently selected");
	        }
	    } catch (InvalidCardException e) {
	        // Handle the exception properly
	    	Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Info Dialog");
	        alert.setHeaderText("Invalid Card Selection!");
	        alert.setContentText(e.getMessage());
	        alert.showAndWait();
	        // Reset UI state to match model state
	        if (selectedCardView != null) {
	            selectedCardView.setSelected(false);
	            selectedCardView = null;
	        }
	    }
	}
	
	public void selectSplitDistance() {
		Stage window = new Stage();
		window.setResizable(false);
		VBox layout = new VBox(0);
		GridPane buttons = new GridPane();
		buttons.setMinWidth(400);
		Button increase = new Button("Increase Split Distance");
		increase.setMinWidth(200);
		increase.setMinHeight(100);
		Button decrease = new Button("Decrease Split Distance");
		decrease.setMinWidth(200);
		decrease.setMinHeight(100);
		Button confirm = new Button("Confirm Split Distance");
		confirm.setMinWidth(400);
		confirm.setMinHeight(100);
		TextArea splitDistanceText = new TextArea();
		splitDistanceText.setMaxWidth(400);
		splitDistanceText.setMaxHeight(50);
		splitDistanceText.setPromptText("Please Choose a Split Distance");
		increase.setOnAction(event ->{
			if(splitDistance < 6) {
				splitDistance++;
				splitDistanceText.setText("Current Split Distance Is: " + splitDistance);
			}
		});
		decrease.setOnAction(event ->{
			if(splitDistance > 1) {
				splitDistance--;
				splitDistanceText.setText("Current Split Distance Is: " + splitDistance);
			}
		});
		confirm.setOnAction(event ->{
			window.close();
			try {
				this.model.editSplitDistance(splitDistance);
			} catch (SplitOutOfRangeException e) {
				Alert alert = new Alert(AlertType.INFORMATION);
    	        alert.setTitle("Info Dialog");
    	        alert.setHeaderText("Split out of Range!!");
    	        alert.setContentText(e.getMessage());
    	        alert.showAndWait();
			}
		});
		buttons.add(increase, 0, 0);
		buttons.add(decrease, 1, 0);
		layout.getChildren().addAll(splitDistanceText,buttons,confirm);
		Scene s = new Scene(layout,400,250);
		window.setScene(s);
		window.show();
		
	}
	
	public void selectMarbleHandle(MarbleView marble) {
	    // Initialize the list if it's null
	    if(selectedMarblesView == null) {
	        selectedMarblesView = new ArrayList<>();
	    }
	    
	    // Case 1: The marble is already selected - DESELECT IT
	    if(selectedMarblesView.contains(marble)) {
	        // Remove it from our view selection
	        selectedMarblesView.remove(marble);
	        marble.setSelected(false);
	        
	        // Reset all selections in the model
	        this.model.getPlayers().get(0).deselectAll();
	        
	        // Re-select the card if one was selected
	        if(selectedCardView != null) {
	            try {
	                this.model.getPlayers().get(0).selectCard(selectedCardView.getCard());
	            } catch (InvalidCardException e) {
	                // Handle exception if needed
	                System.err.println("Failed to reselect card: " + e.getMessage());
	            }
	        }
	        
	        // Re-select any remaining marbles
	        for(MarbleView m : selectedMarblesView) {
	            try {
	                this.model.getPlayers().get(0).selectMarble(m.getMarble());
	            } catch (InvalidMarbleException e) {
	                // Handle exception if needed
	                System.err.println("Failed to reselect marble: " + e.getMessage());
	            }
	        }
	        return; // Exit after handling deselection
	    }
	    
	    // Case 2: Trying to select a new marble
	    
	    // Case 2a: We can add another marble (less than 2 currently selected)
	    if(selectedMarblesView.size() < 2) {
	        selectedMarblesView.add(marble);
	        marble.setSelected(true);
	        
	        try {
	            this.model.getPlayers().get(0).selectMarble(marble.getMarble());
	        } catch (InvalidMarbleException e) {
	            // Alert the user about the invalid selection
	            Alert alert = new Alert(AlertType.INFORMATION);
	            alert.setTitle("Info Dialog");
	            alert.setHeaderText("Invalid Marble Selection!");
	            alert.setContentText(e.getMessage());
	            alert.showAndWait();
	            
	            // Remove the invalid selection from our view
	            selectedMarblesView.remove(marble);
	            marble.setSelected(false);
	        }
	    }
	    // Case 2b: Already have 2 marbles selected
	    else {
	        Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Info Dialog");
	        alert.setHeaderText("Too Many Selections");
	        alert.setContentText("Please select only two marbles");
	        alert.showAndWait();
	    }
	}
	
	
	public void updateCPUHand(){
		boardLayout.getChildren().remove(CPU1HandBox);
		boardLayout.getChildren().remove(CPU2HandBox);
		boardLayout.getChildren().remove(CPU3HandBox);
		CPU1HandBox = new HBox(10);
		CPU2HandBox = new HBox(10);
		CPU3HandBox = new HBox(10);
		for(int i = 1; i<model.getPlayers().size();i++){
			ArrayList<Card> hand = this.model.getPlayers().get(i).getHand();
			for(Card card: hand){
				if(i == 1){
					CPU1HandBox.getChildren().add(new CardView(null));
				}
				if(i == 2){
					CPU2HandBox.getChildren().add(new CardView(null));
				}
				if(i == 3){
					CPU3HandBox.getChildren().add(new CardView(null));
				}
			}
		}
		CPU1HandBox.setLayoutX(50);
		CPU1HandBox.setLayoutY(120);
		CPU2HandBox.setLayoutX(720);
		CPU2HandBox.setLayoutY(120);
		CPU3HandBox.setLayoutX(730);
		CPU3HandBox.setLayoutY(550);
		boardLayout.getChildren().addAll(CPU1HandBox,CPU2HandBox,CPU3HandBox);
	}
	
	public void updateFirePit(Card card){
		boardLayout.getChildren().remove(firePit);
		firePit = new CardView(card);
		firePit.setLayoutX(400);
		firePit.setLayoutY(300);
		boardLayout.getChildren().add(firePit);
	}
	
	public void renderTrack(){
		trackView = new GridPane();
		trackCells = new ArrayList<>();
		initializeGrid(trackView);
		trackView.setLayoutX(220);
		trackView.setLayoutY(100);
		boardLayout.getChildren().addAll(trackView);
	}
	
	public void renderSafeZones(){
		safeZonesView = new ArrayList<>();
		for(int i = 0; i<4;i++){
			safeZonesView.add(new ArrayList<>());
		}
		addLineOfCircles(trackView,24,2,4,true,true,safeZonesView.get(0));
		addLineOfCircles(trackView,2,1,4,false,false,safeZonesView.get(3));
		addLineOfCircles(trackView,1,23,4,true,false,safeZonesView.get(2));
		addLineOfCircles(trackView,23,24,4,false,true,safeZonesView.get(1));
	}
	
	public void renderPlayerNames(){
		playerLabels = new ArrayList<>();
		playerColor = getColor(model.getPlayers().get(0).getColour());
		CPU1Color = getColor(model.getPlayers().get(1).getColour());
		CPU2Color = getColor(model.getPlayers().get(2).getColour());
		CPU3Color = getColor(model.getPlayers().get(3).getColour());
		CircleWithText player1 = new CircleWithText(playerName,50,playerColor);
		player1.setLayoutX(100);
		player1.setLayoutY(600);
		player1.highlightCircle(Color.WHITE);
		playerLabels.add(player1);
		CircleWithText cpu1 = new CircleWithText("CPU 1",50,CPU1Color);
		cpu1.setLayoutX(100);
		cpu1.setLayoutY(0);
		cpu1.highlightCircle(Color.DARKGOLDENROD);
		playerLabels.add(cpu1);
		CircleWithText cpu2 = new CircleWithText("CPU 2",50,CPU2Color);
		cpu2.setLayoutX(700);
		cpu2.setLayoutY(0);
		cpu2.highlightCircle(Color.TRANSPARENT);
		playerLabels.add(cpu2);
		CircleWithText cpu3 = new CircleWithText("CPU 3",50,CPU3Color);
		cpu3.setLayoutX(700);
		cpu3.setLayoutY(600);
		cpu2.highlightCircle(Color.TRANSPARENT);
		playerLabels.add(cpu3);
		boardLayout.getChildren().addAll(player1,cpu1,cpu2,cpu3);
	}
	
	public void updatePlayerNames() {
		Color activePlayerColor = getColor(model.getActivePlayerColour());
		if(activePlayerColor == playerColor) {
			playerLabels.get(0).highlightCircle(Color.WHITE);
			playerLabels.get(1).highlightCircle(Color.DARKGOLDENROD);
			playerLabels.get(2).highlightCircle(Color.TRANSPARENT);
			playerLabels.get(3).highlightCircle(Color.TRANSPARENT);
		}
		if(activePlayerColor == CPU1Color) {
			playerLabels.get(0).highlightCircle(Color.TRANSPARENT);
			playerLabels.get(1).highlightCircle(Color.WHITE);
			playerLabels.get(2).highlightCircle(Color.DARKGOLDENROD);
			playerLabels.get(3).highlightCircle(Color.TRANSPARENT);
		}
		if(activePlayerColor == CPU2Color) {
			playerLabels.get(0).highlightCircle(Color.TRANSPARENT);
			playerLabels.get(1).highlightCircle(Color.TRANSPARENT);
			playerLabels.get(2).highlightCircle(Color.WHITE);
			playerLabels.get(3).highlightCircle(Color.DARKGOLDENROD);
		}
		if(activePlayerColor == CPU3Color) {
			playerLabels.get(0).highlightCircle(Color.DARKGOLDENROD);
			playerLabels.get(1).highlightCircle(Color.TRANSPARENT);
			playerLabels.get(2).highlightCircle(Color.TRANSPARENT);
			playerLabels.get(3).highlightCircle(Color.WHITE);
		}	
	}
	
	public void renderHomeZones(){
		homeZonesView = new ArrayList<>();
		ArrayList<Marble> m = model.getPlayers().get(0).getMarbles();
		ArrayList<Marble> m1 = model.getPlayers().get(1).getMarbles();
		ArrayList<Marble> m2= model.getPlayers().get(2).getMarbles();
		ArrayList<Marble> m3 = model.getPlayers().get(3).getMarbles();
		
		homeZonesView.add(new HomeZoneView(playerColor,m));
		homeZonesView.add(new HomeZoneView(CPU1Color,m1));
		homeZonesView.add(new HomeZoneView(CPU2Color,m2));
		homeZonesView.add(new HomeZoneView(CPU3Color,m3));
		for(HomeZoneView h: homeZonesView){
			boardLayout.getChildren().add(h);
			for(MarbleView marbleView: h.marbles) {
				marbleView.setOnMouseClicked(e -> selectMarbleHandle(marbleView));
			}
		}
		homeZonesView.get(0).setLayoutX(250);
		homeZonesView.get(0).setLayoutY(630);
		homeZonesView.get(1).setLayoutX(250);
		homeZonesView.get(1).setLayoutY(30);
		homeZonesView.get(2).setLayoutX(600);
		homeZonesView.get(2).setLayoutY(30);
		homeZonesView.get(3).setLayoutX(600);
		homeZonesView.get(3).setLayoutY(630);
	}
	
	public void renderDeck() {
		boardLayout.getChildren().remove(DeckView);
		DeckView = new Pane();
		int size = Deck.getPoolSize();
		for(int i = 0; i<size; i++) {
			CardView temp = new CardView(null);
			temp.setLayoutX(3*i);
			temp.setLayoutY(0);
			DeckView.getChildren().add(temp);
			if(i == size-1) {
				temp.setCardFill(Color.BLUE);
				Label deck = new Label("Deck");
				deck.setTextFill(Color.WHITE);
				deck.setStyle("-fx-font-size: 8px;");
				deck.setLayoutX(3*i+3);
				deck.setLayoutY(8);
				DeckView.getChildren().add(deck);
			}
		}
		DeckView.setLayoutX(670);
		DeckView.setLayoutY(400);
		boardLayout.getChildren().add(DeckView);
	}
	
	public void initializeGrid(GridPane g) {
        g.setVgap(2);
        addClockwiseTrack(g, 0, 0, 26, 26, 0);
    }

    public void addClockwiseTrack(GridPane grid, int startRow, int startCol,
                                  int horizontalLength, int verticalLength, int Circles) {
        int radius = 8;
        int i = 0;
        // Left edge (top to bottom)
        for (int row = verticalLength - 1; row > 0; row--) {
        	MarbleView c = new MarbleView(radius,null,Color.WHITE);
            grid.add(c, startCol, startRow + row);
            trackCells.add(c);
        }
        // Top edge (right to left)
        for (int col = 0; col < horizontalLength - 1; col++) {
        	MarbleView c = new MarbleView(radius,null,Color.WHITE);
            grid.add(c, startCol + col, startRow);
            trackCells.add(c);
        }
        // Right edge (bottom to top)
        for (int row = 0; row < verticalLength - 1; row++) {
        	MarbleView c = new MarbleView(radius,null,Color.WHITE);
            grid.add(c, startCol + horizontalLength - 1, startRow + row);
            trackCells.add(c);
        }
        // Bottom edge (left to right)
        for (int col = horizontalLength - 1; col > 0; col--) {
        	MarbleView c = new MarbleView(radius,null,Color.WHITE);
            grid.add(c, startCol + col, startRow + verticalLength - 1);
            trackCells.add(c);
        }
    }
    
    public void addLineOfCircles(GridPane grid, int startRow, int startCol,
            int length, boolean isVertical, boolean reverse, ArrayList<MarbleView> safeZone) {
    	int radius = 8;

    	for (int i = 0; i < length; i++) {
    		MarbleView circle = new MarbleView(8,null,Color.WHITE);
    		int row = startRow;
    		int col = startCol;

    		if (isVertical) {
    			row = reverse ? startRow - i : startRow + i;
    		} 
    		else {
    			col = reverse ? startCol - i : startCol + i;
    		}

    		grid.add(circle, col, row);
    		safeZone.add(circle); // Optional: if you're tracking the circles
    	}
    }
    
    private Color getColor(Colour colour){
    	switch(colour){
    	case RED:
    		return Color.RED;
    	case BLUE:
    		return Color.BLUE;
    	case YELLOW:
    		return Color.YELLOW;
    	case GREEN:
    		return Color.GREEN;
    	}
		return Color.WHITE;
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
