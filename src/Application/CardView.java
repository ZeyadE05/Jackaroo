package Application;

import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.card.Card;
import model.card.standard.Standard;
import model.card.standard.Suit;
import model.player.Player;

public class CardView extends StackPane {
    private static final double CARD_WIDTH = 60;
    private static final double CARD_HEIGHT = 90;
    private static final Color SELECTED_BORDER_COLOR = Color.GOLD;
    private static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final double BORDER_WIDTH = 2;
    private Card card;
    private boolean selected;
    private Rectangle border;
    Rectangle cardRectangle;
    
    
    public CardView(Card card) {
        this.card = card;
        this.selected = false;
        
        createCardView();
    }
    
    public void setCardFill(Color c) {
    	cardRectangle.setFill(c);
    }
    
    private void createCardView() {
        if (card == null){
        	cardRectangle = new Rectangle(20, 30);
            cardRectangle.setFill(Color.WHITE);
            cardRectangle.setArcWidth(10);
            cardRectangle.setArcHeight(10);
            border = new Rectangle(20 + 2, 30 + 2);
            border.setFill(Color.TRANSPARENT);
            border.setStroke(DEFAULT_BORDER_COLOR);
            border.setStrokeWidth(BORDER_WIDTH);
            border.setArcWidth(8);
            border.setArcHeight(8);
            getChildren().addAll(cardRectangle,border);
        }
        
        if(card != null){
        	 // Main card rectangle
            Rectangle cardRectangle = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
            cardRectangle.setFill(Color.WHITE);
            cardRectangle.setArcWidth(10);
            cardRectangle.setArcHeight(10);
        	// Content Group for Card Design
            Group cardContent = new Group();
            // Card border
            border = new Rectangle(CARD_WIDTH + 2 * BORDER_WIDTH, CARD_HEIGHT + 2 * BORDER_WIDTH);
            border.setFill(Color.TRANSPARENT);
            border.setStroke(DEFAULT_BORDER_COLOR);
            border.setStrokeWidth(BORDER_WIDTH);
            border.setArcWidth(12);
            border.setArcHeight(12);
            
            // Card name at top
            Text nameText = new Text(card.getName());
            nameText.setFont(Font.font("Arial", FontWeight.BOLD, 8));
            nameText.setX(5);
            nameText.setY(15);
            
            // Card design and value
            if (card instanceof Standard) {
                Standard standardCard = (Standard) card;
                Text rankText = new Text(getCardRankDisplay(standardCard.getRank()));
                rankText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                rankText.setX(CARD_WIDTH / 2 - 8);
                rankText.setY(CARD_HEIGHT / 2 + 9);
                
                Suit suit = standardCard.getSuit();
                Text suitText = new Text(getSuitSymbol(suit));
                suitText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                suitText.setX(CARD_WIDTH / 2 - 7);
                suitText.setY(CARD_HEIGHT / 2 - 10);
                suitText.setFill(getSuitColor(suit));
                
                cardContent.getChildren().addAll(rankText, suitText);
            }
            
            cardContent.getChildren().add(nameText);
            
            this.getChildren().addAll(border, cardRectangle, cardContent);
            
            // Add tooltip with card description
            Tooltip tooltip = new Tooltip(card.getDescription());
            Tooltip.install(this, tooltip);
            
            // Make it slightly transparent when not hovered
            this.setOpacity(0.9);
            
            // Mouse hover effect
            this.setOnMouseEntered(e -> {
                this.setOpacity(1.0);
                this.setScaleX(1.05);
                this.setScaleY(1.05);
            });
            
            this.setOnMouseExited(e -> {
                this.setOpacity(0.9);
                this.setScaleX(1.0);
                this.setScaleY(1.0);
            });
        }
        
    }
    
    private String getCardRankDisplay(int rank) {
        switch (rank) {
            case 1: return "A";
            case 11: return "J";
            case 12: return "Q";
            case 13: return "K";
            default: return String.valueOf(rank);
        }
    }
    
    private String getSuitSymbol(Suit suit) {
        switch (suit) {
            case HEART: return "♥";
            case DIAMOND: return "♦";
            case CLUB: return "♣";
            case SPADE: return "♠";
            default: return "";
        }
    }
    
    private Color getSuitColor(Suit suit) {
        return (suit == Suit.HEART || suit == Suit.DIAMOND) ? Color.RED : Color.BLACK;
    }
    
    public Card getCard() {
        return card;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            border.setStroke(SELECTED_BORDER_COLOR);
            border.setStrokeWidth(BORDER_WIDTH * 2);
        } else {
            border.setStroke(DEFAULT_BORDER_COLOR);
            border.setStrokeWidth(BORDER_WIDTH);
        }
    }
    
    public boolean isSelected() {
        return selected;
    }
}