import java.util.Scanner; //Just for use before UI is made
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ServerApp {
    public static void main(String[] args) {
        int numFighters = 0, numPools = 0, numRounds = 0, numFights = 0, currPool = 0;
        ArrayList<Fighter> fighterList = new ArrayList<>();

        Scanner scan = new Scanner(System.in);
        System.out.println("How many pools are there");
        numPools = scan.nextInt();
        System.out.println("How many rounds per bout?");
        numRounds = scan.nextInt();
        System.out.println("How many fights in the round robin section?");
        numFights = scan.nextInt();
        //This stuff is only for b4 the UI is made


        fighterList = FighterFile(numFights);
        Pool[] pools = LoadPools(fighterList, numRounds, numPools, fighterList.size());

    }

    public static ArrayList<Fighter> FighterFile(int numFights){
        ArrayList<Fighter> fighterList = new ArrayList<>();
        try {
            BufferedReader read = new BufferedReader(new FileReader("TestFighters.txt"));
            String line;
            while((line = read.readLine()) != null) {
                fighterList.add(new Fighter(line, 0, numFights));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return fighterList;
    }
    //gets all the fighters from the txt file and puts them in an arraylist with the correct format

    public static Pool[] LoadPools(ArrayList<Fighter> fighterList, int rounds, int numPools, int numFighters){
        int peopleInPools = numFighters/numPools; //guarenteed number of fighters in each pool
        int bonusPeople = numFighters % numPools; //remainder of fighters who make the pools uneven >:(
        int currPos = 0;
        int y = currPos;
        Pool tempPool = new Pool(new ArrayList<Fighter>(), rounds); //temporary pool used to store data before putting it in the array
        ArrayList<Fighter> tempFighters = new ArrayList<Fighter>();
        Pool[] pools = new Pool[numPools]; //return array

        for(int x = 0; x<numPools; x++){
            if(bonusPeople > 0){
                for(y = currPos; y<(peopleInPools+1+currPos); y++){
                    tempFighters.add(fighterList.get(y));
                }
                tempPool.setFighterList(tempFighters);
                pools[x] = tempPool;
                bonusPeople--;
                currPos = y;
            } //if there are still bonus people that make the pools uneven
            else{
                for(y =currPos; y<(peopleInPools+currPos); y++){
                    tempFighters.add(fighterList.get(y));
                }
                tempPool.setFighterList(tempFighters);
                pools[x] = tempPool;
                currPos = y;
            }
        }

        return pools;

    }
    //Takes the list of fighters and puts them in their respective pools
}