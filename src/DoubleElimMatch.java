import java.io.Serializable;

public class DoubleElimMatch implements Serializable{
    public Fighter fight1, fight2, winner;

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int rounds;

    public Fighter getFight1() {
        return fight1;
    }

    public void setFight1(Fighter fight1) {
        this.fight1 = fight1;
    }

    public Fighter getFight2() {
        return fight2;
    }

    public void setFight2(Fighter fight2) {
        this.fight2 = fight2;
    }

    public Fighter getWinner() {
        return winner;
    }

    public void setWinner(int i) {
        if(i==1) {
            this.winner = this.fight1;
        }
        else{
            this.winner = this.fight2;
        }
    }

    public DoubleElimMatch(Fighter fight1, Fighter fight2, int rounds){
        this.fight1 = fight1;
        this.fight2 = fight2;
        this.winner = null; //before a match's winner is determined
        this.rounds = rounds;
    }
}