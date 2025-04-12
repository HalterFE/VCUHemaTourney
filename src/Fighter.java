public class Fighter {
    public String name;
    public int points;
    public int battlesLeft;

    public Fighter(String name, int points, int battlesLeft) {
        this.name = name;
        this.points = points;
        this.battlesLeft = battlesLeft;
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
}
