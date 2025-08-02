import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientApp{
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    private String IPAddress;
    //Server Stuff

    private MyFrame frameC;
    private HashMap<String, Integer> setFights;

    public ClientApp(){
        frameC = new MyFrame(0);
        try{
            while(!frameC.getStartMenuDoneC()){
                System.out.print("");
            }
            IPAddress = frameC.IPText.getText();

            this.socket = new Socket(IPAddress, 0104);
            this.objOut = new ObjectOutputStream(socket.getOutputStream());
            this.objIn = new ObjectInputStream(socket.getInputStream());
            this.dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        }
        catch(IOException e){
            e.printStackTrace();
        }
        try {
            DoubleElimMatch RRBinary = (DoubleElimMatch) objIn.readObject();
            objOut.writeObject(new Fighter("DUMMY", 0, 0, 0));
            if (RRBinary.getFight1().getPoints() == -5) {
                frameC.screen.show(frameC.parentPanel,"ContinueC");
                roundRobin();
                doubleTourney(RRBinary);
            }
            else{
                frameC.screen.show(frameC.parentPanel,"ContinueC");
                doubleTourney(RRBinary);
            }
            //The first option is for if a client joins during the RR Portion
            //The second option is for if a client joins later
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        close();
    }

    public void doubleTourney(DoubleElimMatch introMatch){
        DoubleElimMatch currMatch;

        frameC.redDeck.setText(" ");
        frameC.blueDeck.setText(" ");
        frameC.matchOrPoolHeader.setText("Dual Elim Match");
        frameC.onDeck.setText(" ");
        //Removes the info about next fighters since it is impossible to know for dual elim stuff

        try {
            if (introMatch.getFight1().getPoints() == -5) {
                currMatch = (DoubleElimMatch) objIn.readObject();
            } else {
                currMatch = introMatch;
            }
            //handles the cases where the code continues immediately after the Round Robin and after DE has started respectively

            while (true) {
                frameC.RED.setText(currMatch.getFight1().getName());
                frameC.BLUE.setText(currMatch.getFight2().getName());
                frameC.REDCONFIRM.setText(currMatch.getFight1().getName());
                frameC.BLUECONFIRM.setText(currMatch.getFight2().getName());
                //sets the current match details on top of the screen

                frameC.screen.show(frameC.parentPanel,"FightC");

                int currRound = -1;
                while(currRound <= currMatch.getRounds()){
                    currRound = frameC.getCurrRound();
                    System.out.print("");
                }

                frameC.screen.show(frameC.parentPanel,"FightConfirmC");
                while(!frameC.currPoolDoneC){
                    System.out.print("");
                }
                //waits for user to decide if final scores are correct

                if(frameC.getNewRedPointTotal() > frameC.getNewBluePointTotal()){
                    currMatch.setWinner(1);
                    //objOut.writeObject(currMatch.getFight1());
                }
                else{
                    currMatch.setWinner(2);
                    //objOut.writeObject(currMatch.getFight2());
                }

                frameC.currPoolDoneC = false;
                //allows this code to be used for future matches

                frameC.screen.show(frameC.parentPanel,"ContinueC");
                objOut.writeObject(currMatch);
                currMatch = (DoubleElimMatch) objIn.readObject();
            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void roundRobin(){
        try {
            Object obj = objIn.readObject();
            Pool currPool = (Pool) obj;
            //gets pool data from ConnectedClient

            while (currPool.getFighterCount() != -7) {
                try {
                    try {
                        makeMatchesRR(currPool);
                        currPool = runRR(currPool);

                        objOut.writeObject(currPool);
                        //sends data after the round robin match is done for this pool

                        frameC.screen.show(frameC.parentPanel,"ContinueC");
                        //shows the user that the client is waiting for a new pool or dual elim spar

                        obj = objIn.readObject();
                        currPool = (Pool) obj;
                        //gets pool data from ConnectedClient

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void makeMatchesRR(Pool currPool){
        setFights = new HashMap<>();
        int ptri = 0;
        int ptrj = 1;
        ArrayList<Fighter> fightList = currPool.getFighterList();
        int matchToMake = (currPool.getFighterCount()*fightList.get(0).getBattlesLeft())/2;
        boolean a,b;
        boolean SUBSTITUTE = false;
        if((currPool.getFighterCount()*fightList.get(0).getBattlesLeft()) % 2 == 1){
            SUBSTITUTE = true;
            matchToMake++;
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("Pool.txt"));
            writer.write("*--------------------**--------------------*");
            writer.write("\n|      Red Team      ||     Blue Team      |");
            writer.write("\n*--------------------**--------------------*");
            //header of the match list

            while (matchToMake > 0) {
                if(matchToMake == 1 && SUBSTITUTE){
                    for(int i = 0; i<currPool.getFighterCount(); i++) {
                        if (fightList.get(i).getBattlesLeft() > 0) {
                            writer.write("\n" + fightList.get(i).getName() + "   ||   SUBSTITUTE IN");
                            fightList.get(i).setBattlesLeft(fightList.get(i).getBattlesLeft() - 1);
                            matchToMake--;
                            SUBSTITUTE = false;
                        }
                    }
                }

                if(fightList.get(ptri).getBattlesLeft() > 0 & fightList.get(ptrj).getBattlesLeft() > 0){
                    a=true;
                }
                else{
                    a=false;
                }
                b = setFights.containsKey(fightList.get(ptri).getName() + fightList.get(ptrj).getName());
                //conditions to add match to register

                if (a & !b) {
                    writer.write("\n"+fightList.get(ptri).getName()+"   ||   "+fightList.get(ptrj).getName());

                    setFights.put(fightList.get(ptri).getName() + fightList.get(ptrj).getName(), 1);
                    setFights.put(fightList.get(ptrj).getName() + fightList.get(ptri).getName(), 1);
                    fightList.get(ptri).setBattlesLeft(fightList.get(ptri).getBattlesLeft()-1);
                    fightList.get(ptrj).setBattlesLeft(fightList.get(ptrj).getBattlesLeft()-1);
                    matchToMake--;

                    ptri = movePointer(ptri, 2, currPool.getFighterCount());
                    ptrj = movePointer(ptrj, 2, currPool.getFighterCount());

                } else {
                    ptrj = movePointer(ptrj, 1, currPool.getFighterCount());
                    if(ptri == ptrj){
                        ptri = movePointer(ptri, 1, currPool.getFighterCount());
                        ptrj = movePointer(ptrj, 2, currPool.getFighterCount());
                    }
                }
            }
            writer.write("\n*--------------------**--------------------*");
            writer.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    //loads the match data into a text file

    public Pool runRR(Pool currPool){
        ArrayList<Fighter> fighterList = currPool.getFighterList();
        Fighter Red, Blue;
        String fullLine="", nextLine = "";
        String ENDSTRING = "*--------------------**--------------------*";
        String[] splitString, splitNextString;

        frameC.matchOrPoolHeader.setText("Pool Number: "+currPool.getNumber());

        try {
            BufferedReader reader = new BufferedReader(new FileReader("Pool.txt"));

            for(int i = 0; i<3;i++){
                reader.readLine();
            }
            //skips the lines in the generated file that are not the actual matches

            fullLine = reader.readLine();
            nextLine = reader.readLine();
            while(!fullLine.equals(ENDSTRING)){
                splitString = fullLine.split("   \\|\\|   ");

                if(nextLine.equals(ENDSTRING)){
                    frameC.redDeck.setText("End of Pool");
                    frameC.blueDeck.setText("End of Pool");
                }
                else{
                    splitNextString = nextLine.split("   \\|\\|   ");
                    frameC.redDeck.setText(splitNextString[0]);
                    frameC.blueDeck.setText(splitNextString[1]);
                }
                //Allows the front end to show the next match in the pool

                Red = inFighter(fighterList, splitString[0]);
                Blue = inFighter(fighterList, splitString[1]);

                fighterList = fightRR(Red,Blue,currPool, fighterList);
                fullLine = nextLine;
                nextLine = reader.readLine();
            }

            currPool.setFighterList(fighterList);
            return currPool;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return currPool;
    }
    //runs the matches that are set up in the makeMatches method

    public ArrayList<Fighter> fightRR(Fighter RED, Fighter BLUE, Pool currPool, ArrayList<Fighter> fighterList){
        int redTotal = 0;
        int blueTotal = 0;

        frameC.RED.setText(RED.getName());
        frameC.BLUE.setText(BLUE.getName());
        frameC.REDCONFIRM.setText(RED.getName());
        frameC.BLUECONFIRM.setText(BLUE.getName());
        //sets the current pool details on top of the screen

        frameC.screen.show(frameC.parentPanel,"FightC");

        int currRound = -1;
        while(currRound <= currPool.getRounds()){
            currRound = frameC.getCurrRound();
            System.out.print("");
        }
        //the user goes through all the rounds on the front end until all the matches have been completed

        /*for(int i = 0; i<currPool.getRounds();i++) {
            //REPLACE THIS LATER WITH CODE THAT ACTUALLY ASKS USER WHO GETS WHAT POINTS AND WHAT THEY GET
            redTotal++;
            System.out.println("RED WINS A POINT");
        }*/

        frameC.screen.show(frameC.parentPanel,"FightConfirmC");
        while(!frameC.currPoolDoneC){
            System.out.print("");
        }
        //waits for user to decide if final scores are correct

        redTotal = frameC.getNewRedPointTotal();
        blueTotal = frameC.getNewBluePointTotal();

        frameC.currPoolDoneC = false;
        //allows this code to be used for future pools/dual elims

        RED.setPoints((RED.getPoints()+redTotal));
        BLUE.setPoints(BLUE.getPoints()+blueTotal);
        replaceFighter(fighterList, RED);
        replaceFighter(fighterList, BLUE);
        return fighterList;
    }

    public Fighter inFighter(ArrayList<Fighter> fightList, String match){
        if(match.equals("SUBSTITUTE IN")){
            return new Fighter("Substitute",-300,-300,-300);
        }
        for(Fighter fighter : fightList){
            if(fighter.getName().equals(match)){
                return fighter;
            }
        }
        return null;
    }
    //returns the fighter with the name in the match

    public ArrayList<Fighter> replaceFighter(ArrayList<Fighter> fightList, Fighter Red){
        if(Red.getName().equals("Substitute")){
            return fightList;
        }
        for(Fighter fighter: fightList){
            if(fighter.getName().equals(Red)){
                fighter = Red;
            }
        }
        return fightList;
    }

    public int movePointer(int ptr, int move, int limit){
        if((ptr+move) >=limit){
            return (ptr+move)-limit;
        }
        else{
            return ptr+move;
        }
    }
    //allows the pointer to move around the list if it would normally go OOB

    public void close(){
        try{
            socket.close();
            objIn.close();
            objOut.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    //closes the client application's server functionality

    public static void main(String[] args){
        new ClientApp();
    }
}