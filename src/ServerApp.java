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
        int numFighters = 0, numRounds = 0, numFights = 0, currPool = 0, numDualElimFighters = 0, numRoundsDE = 0;
        String fileDirectory;
        ArrayList<Fighter> fighterList = new ArrayList<>();

        MyFrame frame = new MyFrame(1);

        while(!frame.getStartMenuDone()){
            System.out.print("");
        }
        //Waits for the user to input all the data

        try{
            final int numPools = Integer.parseInt(frame.poolNumText.getText());
            numFights = Integer.parseInt(frame.RRRoundNumText.getText());
            numRounds =  Integer.parseInt(frame.RRBoutNumText.getText());
            numRoundsDE = Integer.parseInt(frame.DEBoutNumText.getText());
            numDualElimFighters = Integer.parseInt(frame.DEFighterNumText.getText());
            fileDirectory = frame.selectedFilePathLabel.getText();

            fighterList = FighterFile(numFights, fileDirectory);
            Pool[] pools = LoadPools(fighterList, numRounds, numPools, fighterList.size());
            frame.numBattles = 2*numDualElimFighters-3;
            frame.battleProgressLabel.setText(frame.numCompletedBattles + "/" + frame.numBattles);
            //makes a list of fighters and puts them in a pool
            try {
                server = new ServerSocket(0104);
                while(true) Connect(pools, numPools, numDualElimFighters, numRoundsDE, frame);
            //sends one pool at a time to the client application til all the round robin matches are done
        }
        catch(IOException e){
            e.printStackTrace();
        }
        }
        catch(NumberFormatException e){
            e.printStackTrace();
        }

    }

    public ArrayList<Fighter> FighterFile(int numFights, String fileDirectory){
        ArrayList<Fighter> fighterList = new ArrayList<>();
        try {
            BufferedReader read = new BufferedReader(new FileReader(fileDirectory));
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

    public void Connect(Pool[] pools, int numPools, int numDualElimFighters, int numRoundsDE, MyFrame frame) throws IOException{
        try{
            Socket sock = server.accept();

            if(sock.isConnected()) {
                new Thread(() -> {
                    clientNum++;
                    ConnectedClient client = new ConnectedClient(sock, clientNum);
                    try {
                        System.out.println("STARTING RR");
                        client.roundRobin(pools, numPools, numDualElimFighters, sock, numRoundsDE, frame);
                        System.out.println("DONE WITH RR");
                        client.roundRobin(pools, numPools, numDualElimFighters, sock, numRoundsDE, frame);
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

            //close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    //creates a socket that the client can connect to before triggering the tourney phases

    public void close() throws IOException{
        server.close();
        objInput.close();
        objOutput.close();
    }
}