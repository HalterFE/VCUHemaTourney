import java.util.ArrayList;
import java.io.Serializable;
public class Pool implements Serializable{
    public ArrayList<Fighter> fighterList = new ArrayList<>();
    public int fighterCount;
    public int rounds;
    public int number;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int status;

    public ArrayList<Fighter> getFighterList() {
        return fighterList;
    }

    public void setFighterList(ArrayList<Fighter> fighterList) {
        this.fighterList = fighterList;
    }

    public int getFighterCount() {
        return fighterCount;
    }

    public void setFighterCount(int fighterCount) {
        this.fighterCount = fighterCount;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getNumber() { return number; }

    public void setNumber(int number) {this.number = number;}

    public Pool(ArrayList<Fighter> fighterList, int fighterCount, int number) {
        this.fighterList = fighterList;
        this.fighterCount = fighterCount;
        this.rounds = rounds;
        this.number = number;
        this.status = 0;
    }
}