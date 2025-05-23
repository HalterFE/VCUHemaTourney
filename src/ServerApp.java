import java.util.Scanner; //Just for use before UI is made
/*import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;*/
import java.io.*;
import java.util.ArrayList;
import java.net.*;

public class ServerApp extends Thread{
    private ServerSocket server;
    private ObjectInputStream objInput;
    private ObjectOutputStream objOutput;
    private int clientNum = 0;

    public static void main(String[] args) {
        new ServerApp();
    }
    public ServerApp(){
        int numFighters = 0, numRounds = 0, numFights = 0, currPool = 0, numDualElimFighters = 0;
        ArrayList<Fighter> fighterList = new ArrayList<>();

        Scanner scan = new Scanner(System.in);
        System.out.println("How many pools are there");
        final int numPools = scan.nextInt();
        System.out.println("How many rounds per bout?");
        numRounds = scan.nextInt();
        System.out.println("How many fights in the round robin section?");
        numFights = scan.nextInt();
        System.out.println("How many fighters in the dual elimination section?");
        numDualElimFighters = scan.nextInt();
        //This stuff is only for b4 the UI is made


        fighterList = FighterFile(numFights);
        Pool[] pools = LoadPools(fighterList, numRounds, numPools, fighterList.size());
        //makes a list of fighters and puts them in a pool

        try {
            server = new ServerSocket(0104);
            while(true) Connect(pools, numPools, numDualElimFighters
            );
            //sends one pool at a time to the client application til all the round robin matches are done
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    public ArrayList<Fighter> FighterFile(int numFights){
        ArrayList<Fighter> fighterList = new ArrayList<>();
        try {
            BufferedReader read = new BufferedReader(new FileReader("TestFighters.txt"));
            String line;
            while((line = read.readLine()) != null) {
                fighterList.add(new Fighter(line, 0, numFights, 0));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return fighterList;
    }
    //gets all the fighters from the txt file and puts them in an arraylist with the correct format

    public Pool[] LoadPools(ArrayList<Fighter> fighterList, int rounds, int numPools, int numFighters){
        int peopleInPools = numFighters/numPools; //guarenteed number of fighters in each pool
        int bonusPeople = numFighters % numPools; //remainder of fighters who make the pools uneven >:(
        int currPos = 0;
        int y = currPos;
        ArrayList<Fighter> tempFighters = new ArrayList<Fighter>();
        Pool[] pools = new Pool[numPools]; //return array

        for(int x = 0; x<numPools; x++){
            Pool tempPool = new Pool(new ArrayList<Fighter>(), rounds, -1); //temporary pool used to store data before putting it in the array
            tempFighters = new ArrayList<Fighter>();

            if(bonusPeople > 0){
                for(y = currPos; y<(peopleInPools+1+currPos); y++){
                    tempFighters.add(fighterList.get(y));
                }
                tempPool.setFighterList(tempFighters);
                tempPool.setNumber(x+1);
                tempPool.setFighterCount(tempFighters.size());
                tempPool.setRounds(rounds);
                pools[x] = tempPool;
                bonusPeople--;
                currPos = y;
            } //if there are still bonus people that make the pools uneven
            else{
                for(y =currPos; y<(peopleInPools+currPos); y++){
                    tempFighters.add(fighterList.get(y));
                }
                tempPool.setFighterList(tempFighters);
                tempPool.setNumber(x+1);
                tempPool.setFighterCount(tempFighters.size());
                tempPool.setRounds(rounds);
                pools[x] = tempPool;
                currPos = y;
            }
        }


        return pools;

    }
    //Takes the list of fighters and puts them in their respective pools

    public void Connect(Pool[] pools, int numPools, int numDualElimFighters) throws IOException{
        try{
            Socket sock = server.accept();

            if(sock.isConnected()) {
                new Thread(() -> {
                    clientNum++;
                    ConnectedClient client = new ConnectedClient(sock, clientNum);
                    try {
                        System.out.println("STARTING RR");
                        client.roundRobin(pools, numPools, numDualElimFighters, sock);
                        System.out.println("DONE WITH RR");
                        client.roundRobin(pools, numPools, numDualElimFighters, sock);
                        System.out.println("DONE");
                        //close();
                        //runs it once for RR and again for DE, the connected client uses the runStatus to determine RR or DE
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                    client.close();
                }).start();
            }

            //RoundRobin(pools, numPools, sock);
            //close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    //creates a socket that the client can connect to before triggering the tourney phases

    /*public void RoundRobin(Pool[] pools, int numPools, Socket sock) throws IOException{
        int completedRRs = 0;
        while (completedRRs < numPools) {

                int tempRR;
                objOutput = new ObjectOutputStream(sock.getOutputStream());
                objInput = new ObjectInputStream(sock.getInputStream());
                tempRR = completedRRs;
                objOutput.writeObject(pools[tempRR]);
                System.out.println("Client _ is handling pool number: " + (tempRR + 1));
                try {
                    Pool blablabla = (Pool) objInput.readObject();
                    completedRRs++;
                    System.out.println("Client _ is done with pool number:");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
        }
    }*/


    public void close() throws IOException{
        server.close();
        objInput.close();
        objOutput.close();
    }
}