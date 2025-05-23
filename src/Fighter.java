import java.io.Serializable;
public class Fighter implements Serializable{
    public String name;
    public int points;
    public int battlesLeft;
    public int cards;
    public int losses, wins; //for the double elim part, not the whole tourney

    public Fighter(String name, int points, int battlesLeft, int cards) {
        this.name = name;
        this.points = points;
        this.battlesLeft = battlesLeft;
        this.cards = cards;
        this.losses = 0;
        this.wins = 0;
    }

    public int getWins() {
        return wins;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getBattlesLeft() {
        return battlesLeft;
    }

    public void setBattlesLeft(int battlesLeft) {
        this.battlesLeft = battlesLeft;
    }

    public int getCards() {
        return cards;
    }

    public void setCards(int cards) {
        this.cards = cards;
    }

    public int getLosses() {
        return losses;
    }

    public void addLoss(){
        this.losses = this.losses+1;
    }

    public void addWin(){
        this.wins = this.wins+1;
    }

}
