import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ConnectedClient{
    private Socket clientSocket;
    private DataOutputStream dataOut;
    private DataInputStream dataIn;
    private ObjectOutputStream objOutput;
    private ObjectInputStream objInput;
    private int clientNum;
    private int runStatus = 1; //for telling clients what part to run

    public static int completedRRs = 0;
    private ArrayList<Fighter> completedFighters;

    private ArrayList<DoubleElimMatch> winnersBracket;
    private ArrayList<DoubleElimMatch> losersBracket;
    private ArrayList<Fighter> tempWinner;
    private ArrayList<Fighter> tempLoser;

    public ConnectedClient(Socket clientSocket, int clientNum){
        this.clientSocket = clientSocket;
        this.clientNum = clientNum;
        try {
            this.dataOut = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            this.dataIn = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            this.objInput = new ObjectInputStream(clientSocket.getInputStream());
            this.objOutput = new ObjectOutputStream(clientSocket.getOutputStream());
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void roundRobin(Pool[] pools, int numPools, int numDualElimFighters, Socket sock) throws IOException{
        if(runStatus == 1) {
            completedFighters = new ArrayList<Fighter>();
            objOutput.writeObject(new DoubleElimMatch(new Fighter("bugaboo",-5,-5,-5), new Fighter("bugaboo",-5,-5,-5)));
            try {
                Fighter DUMMY = (Fighter) objInput.readObject(); //Just to confirm that the client got the data before progressing
            }
            catch(ClassNotFoundException e) {
                e.printStackTrace();
            }
            while (completedRRs < numPools) {
                int tempRR;
                //objOutput = new ObjectOutputStream(sock.getOutputStream());
                //objInput = new ObjectInputStream(sock.getInputStream());
                tempRR = completedRRs;
                pools[tempRR].setStatus(1);
                objOutput.writeObject(pools[tempRR]);
                System.out.println("Client " + clientNum + " is handling pool number: " + (pools[tempRR].getNumber()));
                try {
                    Pool blablabla = (Pool) objInput.readObject();
                    completedFighters.addAll(blablabla.getFighterList());

                    pools[tempRR].setStatus(2);
                    System.out.println("Client " + clientNum + " is done with pool number: " + (pools[tempRR].getNumber()));
                    completedRRs++;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < numPools; i++) {
                if (pools[i].getStatus() < 2) {
                    pools[i].setStatus(1);
                    objOutput.writeObject(pools[i]);
                    System.out.println("Client " + clientNum + " is handling pool number: " + (i + 1));
                    try {
                        Pool blablabla = (Pool) objInput.readObject();
                        completedFighters.addAll(blablabla.getFighterList());
                        pools[i].setStatus(2);
                        System.out.println("Client " + clientNum + " is done with pool number: " + (i + 1));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            //objOutput.writeObject(new Pool(new ArrayList<Fighter>(), -7, -7));
            //double checks if there were any disconnects earlier and decides to run the pools again

            completedFighters.sort(Comparator.comparingInt(Fighter::getPoints));
            Collections.reverse(completedFighters);

            int lastPlacePoints = (completedFighters.get(numDualElimFighters - 1)).getPoints();
            int numOfLastPlacePeople = 0;
            int numOfLastPlaceBeforeCutoff = 0;

            for (int i = 0; i < completedFighters.size(); i++) {
                if (completedFighters.get(i).getPoints() == lastPlacePoints) {
                    numOfLastPlacePeople++;
                    if (i <= numDualElimFighters) {
                        numOfLastPlaceBeforeCutoff++;
                    }
                }
            }
            //determines how many people need to have another match before DE can continue

        /*
            When working on front end, code here should display the fighters on screen that have 'lastPlacePoints'
            and ask the user which ones won tiebreaker outside the program, the number of people allowed in equals
            numOfLastPlaceBeforeCutoff. If there is only 1 person with the value of lastPlacePoints, ignore this.
        */

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("Points After Round Robin.txt"));
                writer.write("*--------------------**--------------------*");
                writer.write("\n|      Fighter       ||       Points       |");
                writer.write("\n*--------------------**--------------------*");
                //header of the point list

                int count = 0;
                for (Fighter fighter: completedFighters) {
                    writer.write("\n" + fighter.getName() + "   ||   " + fighter.getPoints());
                    count++;
                    if (count == numDualElimFighters) {
                        writer.write("\n*--------------------**--------------------*");
                        writer.write("\n*  ALL ABOVE PROCEED TO DOUBLE ELIMINATION *");
                        writer.write("\n*--------------------**--------------------*");
                    }
                }

                writer.write("\n*--------------------**--------------------*");
                writer.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            //takes data from the list of completed fighters and orders them in descending order

            runStatus = 2; //time for DE
            objOutput.writeObject(new Pool(new ArrayList<Fighter>(), -7, -7));
            //tells the client to stop running round robin stuff

        }
        else{
            ArrayList<Fighter> returner = new ArrayList<Fighter>(completedFighters.subList(0, numDualElimFighters));
            StartDoubleElim(returner);
            //sends all fighters over that made it to elims
        }
        //LATER FIX, KEEP DATA FROM BEFORE DISCONNECT
        //close();
    }
    //the server-side aspect of the round robin stuff

    public void StartDoubleElim(ArrayList<Fighter> fighterList){
        tempWinner = new ArrayList<>();
        tempLoser = new ArrayList<>();
        winnersBracket = InitializeBracket(fighterList);


        while(winnersBracket.size()>1){
            runWinners();
            winnersBracket = InitializeBracket(tempWinner);
        }
        //runs until the winner's bracket is over

        losersBracket = InitializeBracket(tempLoser);

        while(losersBracket.size()>1){
            runLosers();
            losersBracket = InitializeBracket(tempLoser);
        }
        //runs until the loser's bracket is over

        finalMatch(winnersBracket.get(0).getFight1(), losersBracket.get(0).getFight1());
    }
    //Server-side Double Elim Process

    public ArrayList<DoubleElimMatch> InitializeBracket(ArrayList<Fighter> fighterList){
        ArrayList<DoubleElimMatch> matchList = new ArrayList<DoubleElimMatch>();
        System.out.println(fighterList.size());
        for(int i = 0; i<fighterList.size();i+=2){
            if(i+1 == fighterList.size()){
                matchList.add(new DoubleElimMatch(fighterList.get(i),null));
            }
            //handles cases where the current part of the tournament doesn't have even participants
            else {
                matchList.add(new DoubleElimMatch(fighterList.get(i), fighterList.get(i + 1)));
            }
        }
        return matchList;

    }
    //Returns an arraylist of matches for every fighter given in the fighterList. This is just one round of matches

    public void runWinners(){
        try {
            tempWinner = new ArrayList<>();
            for (DoubleElimMatch match : winnersBracket) {
                if (match.getFight2() == null) {
                    tempWinner.add(match.getFight1());
                } else {
                    objOutput.writeObject(match);
                    DoubleElimMatch finishedMatch = (DoubleElimMatch) objInput.readObject(); //WHEN DATA IS SENT FROM CLIENT

                    if (match.getWinner() == match.getFight1()) {
                        tempWinner.add(match.getFight1());
                        tempLoser.add(match.getFight2());
                    } else {
                        tempWinner.add(match.getFight2());
                        tempLoser.add(match.getFight1());
                    }
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }//sends match data to the client to run individual matches, match data sent back is then used

    public void runLosers(){
        tempLoser = new ArrayList<>();
        try {
            for (DoubleElimMatch match : losersBracket) {
                if (match.getFight2() == null) {
                    tempLoser.add(match.getFight1());
                } else {
                    objOutput.writeObject(match);
                    DoubleElimMatch finishedMatch = (DoubleElimMatch) objInput.readObject(); //WHEN DATA IS SENT FROM CLIENT

                    if (match.getWinner() == match.getFight1()) {
                        tempLoser.add(match.getFight1());
                    } else {
                        tempLoser.add(match.getFight2());
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    } //sends match data to the client to run individual matches, match data sent back is then used, this time losers are OBLITERATED

    public void finalMatch(Fighter fight1, Fighter fight2){
        try {
            objOutput.writeObject(new DoubleElimMatch(fight1, fight2));
            Fighter champion = ((DoubleElimMatch) objInput.readObject()).getWinner();
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    //the final match for the tournament... to be sent to the client lol

    public void close(){
        try {
            clientSocket.close();
            dataOut.close();
            objInput.close();
            objOutput.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    //for disconnecting the client from the server
}