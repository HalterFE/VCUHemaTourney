import java.util.ArrayList;
public class Pool{
    public ArrayList<Fighter> fighterList = new ArrayList<>();
    public int fighterCount, rounds;

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

    public Pool(ArrayList<Fighter> fighterList, int fighterCount) {
        this.fighterList = fighterList;
        this.fighterCount = fighterCount;
        this.rounds = rounds;
    }
}