import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
    private ArrayList<Fighter> tempCompletedFighters;

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

    public void roundRobin(Pool[] pools, int numPools, int numDualElimFighters, Socket sock, int numRoundsDE, MyFrame frame) throws IOException{
        if(runStatus == 1) {
            frame.screen.next(frame.parentPanel);
            tempCompletedFighters = new ArrayList<Fighter>();
            completedFighters = new ArrayList<Fighter>();
            objOutput.writeObject(new DoubleElimMatch(new Fighter("bugaboo",-5,-5,-5), new Fighter("bugaboo",-5,-5,-5)));
            try {
                Fighter DUMMY = (Fighter) objInput.readObject(); //Just to confirm that the client got the data before progressing
            }
            catch(ClassNotFoundException e) {
                e.printStackTrace();
            }
            frame.setNumPools(numPools);
            while (completedRRs < numPools) {
                int tempRR;
                //objOutput = new ObjectOutputStream(sock.getOutputStream());
                //objInput = new ObjectInputStream(sock.getInputStream());
                tempRR = completedRRs;
                frame.setNumCompletedPools(completedRRs);
                pools[tempRR].setStatus(1);
                objOutput.writeObject(pools[tempRR]);
                frame.RRConsole.append("Client "+clientNum+":> Handling pool number: "+ (pools[tempRR].getNumber())+"\n\n");
                try {
                    Pool blablabla = (Pool) objInput.readObject();
                    completedFighters.addAll(blablabla.getFighterList());

                    pools[tempRR].setStatus(2);
                    frame.RRConsole.append("Client "+clientNum+":> Done with pool number: "+ (pools[tempRR].getNumber())+"\n\n");
                    completedRRs++;
                    frame.poolProgressLabel.setText(frame.numCompletedPools + "/" + frame.numPools);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < numPools; i++) {
                if (pools[i].getStatus() < 2) {
                    frame.numCompletedPools = i+1;
                    pools[i].setStatus(1);
                    objOutput.writeObject(pools[i]);
                    frame.RRConsole.append("Client "+clientNum+":> Handling pool number: "+ (i + 1)+"\n\n");
                    try {
                        Pool blablabla = (Pool) objInput.readObject();
                        completedFighters.addAll(blablabla.getFighterList());
                        pools[i].setStatus(2);
                        frame.RRConsole.append("Client "+clientNum+":> Done with pool number: "+ (i+1)+"\n\n");
                        frame.poolProgressLabel.setText(frame.numCompletedPools + "/" + frame.numPools);
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
                    if (i < numDualElimFighters) {
                        numOfLastPlaceBeforeCutoff++;
                    }
                }
            }
            //determines how many people need to have another match before DE can continue

            frame.setNumTiebreak(numOfLastPlacePeople - numOfLastPlaceBeforeCutoff);

            int cutoffSwitch = -1;
            if(numOfLastPlacePeople - numOfLastPlaceBeforeCutoff != 0){
                frame.remakeServerContinue();
                for(int i = 0; i < completedFighters.size(); i++){
                    if(completedFighters.get(i).getPoints() == lastPlacePoints){
                        cutoffSwitch = i;

                        JPanel addPanel = new JPanel();
                        addPanel.setBackground(Color.black);
                        addPanel.setPreferredSize(new Dimension(281,30));
                        addPanel.setMaximumSize(addPanel.getPreferredSize());
                        addPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                        JLabel nameFighterLabel = new JLabel();
                        nameFighterLabel.setForeground(Color.yellow);
                        JCheckBox check = new JCheckBox();
                        check.addItemListener(new ItemListener(){

                            public void itemStateChanged(ItemEvent i){
                                if(i.getStateChange()==ItemEvent.SELECTED){
                                    frame.increaseNumCurrSelectedTiebreak();
                                    frame.currSelectLabel.setText(frame.numCurrSelectedTiebreak+"/"+frame.numTiebreak);
                                }
                                else{
                                    frame.decreaseNumCurrSelectedTiebreak();
                                    frame.currSelectLabel.setText(frame.numCurrSelectedTiebreak+"/"+frame.numTiebreak);
                                }
                                //keeps track of the number of checkboxes... checked

                                if(frame.numTiebreak == frame.numCurrSelectedTiebreak){
                                    frame.startDualElimButton.setEnabled(true);
                                }
                                else{
                                    frame.startDualElimButton.setEnabled(false);
                                }
                                //if enough people selected, allow user to continue
                            }
                        });
                        nameFighterLabel.setText(completedFighters.get(i).getName());

                        addPanel.add(check);
                        addPanel.add(nameFighterLabel);

                        frame.tiebreakPanel.add(addPanel);
                    }
                    //puts Fighters in a format to be selected if they need to be placed in a tiebreaker and sends it to the front end
                    if(cutoffSwitch<0){
                        tempCompletedFighters.add(completedFighters.get(i));
                    }
                    //puts fighters, who aren't in tiebreaker at the very beginning of the list, in a temporary place
                }
            }
            frame.screen.next(frame.parentPanel);

            while(!frame.getStartDualElim()){
                System.out.print("");
            }
            //waits for the server to continue the program

            if(numOfLastPlacePeople - numOfLastPlaceBeforeCutoff != 0) {
                Component[] tiebreakPanels = frame.tiebreakPanel.getComponents();
                ArrayList<Fighter> didNotMakeIt = new ArrayList<Fighter>();
                for (Component c : tiebreakPanels) {
                    JPanel currPanel = (JPanel) c;
                    Component[] dataInPanel = currPanel.getComponents();
                    JCheckBox currCheck;
                    JLabel currLabel;
                    if (dataInPanel[0] instanceof JCheckBox) {
                        currCheck = (JCheckBox) dataInPanel[0];
                        currLabel = (JLabel) dataInPanel[1];
                    } else {
                        currCheck = (JCheckBox) dataInPanel[1];
                        currLabel = (JLabel) dataInPanel[0];
                    }
                    if (!currCheck.isSelected()) {
                        didNotMakeIt.add(completedFighters.get(getIndexOfName(currLabel.getText())));
                    } else {
                        tempCompletedFighters.add(completedFighters.get(getIndexOfName(currLabel.getText())));
                    }
                }
                tempCompletedFighters.addAll(didNotMakeIt);
                //lists the fighters in order of who won the tiebreaker first

                for (int i = 0; i < tempCompletedFighters.size(); i++) {
                    completedFighters.set(i, tempCompletedFighters.get(i));
                }
                //puts all the modified changes into the actual completedFighters list
            }

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
            StartDoubleElim(returner, frame);
            //sends all fighters over that made it to elims
        }
        //LATER FIX, KEEP DATA FROM BEFORE DISCONNECT
        //close();
    }
    //the server-side aspect of the round robin stuff

    public void StartDoubleElim(ArrayList<Fighter> fighterList, MyFrame frame){
        tempWinner = new ArrayList<>();
        tempLoser = new ArrayList<>();
        winnersBracket = InitializeBracket(fighterList);
        frame.screen.next(frame.parentPanel);

        while(winnersBracket.size()>1){
            runWinners(frame);
            winnersBracket = InitializeBracket(tempWinner);
        }
        //runs until the winner's bracket is over

        losersBracket = InitializeBracket(tempLoser);

        while(losersBracket.size()>1){
            runLosers(frame);
            losersBracket = InitializeBracket(tempLoser);
        }
        //runs until the loser's bracket is over

        finalMatch(winnersBracket.get(0).getFight1(), losersBracket.get(0).getFight1(),frame);
    }
    //Server-side Double Elim Process

    public ArrayList<DoubleElimMatch> InitializeBracket(ArrayList<Fighter> fighterList){
        ArrayList<DoubleElimMatch> matchList = new ArrayList<DoubleElimMatch>();
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

    public void runWinners(MyFrame frame){
        try {
            tempWinner = new ArrayList<>();
            for (DoubleElimMatch match : winnersBracket) {
                if (match.getFight2() == null) {
                    tempWinner.add(match.getFight1());
                } else {
                    objOutput.writeObject(match);
                    frame.DEConsole.append("Client "+clientNum+":> Handling WB: "+match.getFight1().getName()+" VS "+match.getFight2().getName()+"\n\n");
                    DoubleElimMatch finishedMatch = (DoubleElimMatch) objInput.readObject(); //WHEN DATA IS SENT FROM CLIENT
                    frame.DEConsole.append("Client "+clientNum+":> Done with WB: "+ match.getFight1().getName()+" VS "+match.getFight2().getName()+"\n\n");
                    frame.numCompletedBattles++;
                    frame.battleProgressLabel.setText(frame.numCompletedBattles + "/" + frame.numBattles);

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

    public void runLosers(MyFrame frame){
        tempLoser = new ArrayList<>();
        try {
            for (DoubleElimMatch match : losersBracket) {
                if (match.getFight2() == null) {
                    tempLoser.add(match.getFight1());
                } else {
                    objOutput.writeObject(match);
                    frame.DEConsole.append("Client "+clientNum+":> Handling LB: "+match.getFight1().getName()+" VS "+match.getFight2().getName()+"\n\n");
                    DoubleElimMatch finishedMatch = (DoubleElimMatch) objInput.readObject(); //WHEN DATA IS SENT FROM CLIENT
                    frame.DEConsole.append("Client "+clientNum+":> Done with LB: "+ match.getFight1().getName()+" VS "+match.getFight2().getName()+"\n\n");
                    frame.numCompletedBattles++;
                    frame.battleProgressLabel.setText(frame.numCompletedBattles + "/" + frame.numBattles);

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

    public void finalMatch(Fighter fight1, Fighter fight2, MyFrame frame){
        frame.battleLabel.setText("Final Match 1:");
        frame.battleProgressLabel.setText(fight1.getName()+" VS"+ fight2.getName());
        Fighter champion;
        try {
            objOutput.writeObject(new DoubleElimMatch(fight1, fight2));
            frame.DEConsole.append("Client "+clientNum+":> Handling FM: "+fight1.getName()+" VS "+fight2.getName()+"\n\n");
            champion = ((DoubleElimMatch) objInput.readObject()).getWinner();
            frame.DEConsole.append("Client "+clientNum+":> Done with FM: "+fight1.getName()+" VS "+fight2.getName()+"\n\n");
            if(champion == fight2){
                frame.battleLabel.setText("Final Match 2:");
                objOutput.writeObject(new DoubleElimMatch(fight1, fight2));
                frame.DEConsole.append("Client "+clientNum+":> Handling FM2: "+fight1.getName()+" VS "+fight2.getName()+"\n\n");
                champion = ((DoubleElimMatch) objInput.readObject()).getWinner();
                frame.DEConsole.append("Client "+clientNum+":> Done with FM2: "+ fight1.getName()+" VS "+fight2.getName()+"\n\n");
            }
            //If the person in the loser's bracket wins the first finals match

            frame.finalWinner.setText("The Champion is: "+champion.getName());
            frame.screen.next(frame.parentPanel);

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

    public int getIndexOfName(String name){
        for(int i = 0; i<completedFighters.size(); i++){
            if(completedFighters.get(i).getName().equals(name)){
                return i;
            }
        }
        return -1;
    }
}