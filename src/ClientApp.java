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
    //Server Stuff

    private HashMap<String, Integer> setFights;

    public ClientApp(){
        try{
            this.socket = new Socket("127.0.0.1", 0104);
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
                roundRobin();
                doubleTourney(RRBinary);
            }
            else{
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
        try {
            if (introMatch == new DoubleElimMatch(new Fighter("bugaboo", -5, -5, -5), new Fighter("bugaboo", -5, -5, -5))) {
                currMatch = (DoubleElimMatch) objIn.readObject();
            } else {
                currMatch = introMatch;
            }
            //handles the cases where the code continues immediately after the Round Robin and after DE has started respectively

            while (true) {
                currMatch.setWinner(currMatch.getFight1());
                //THIS IS DUMMY CODE, SET THINGS TO ACTUALLY RUN THE MATCH WHEN THE FRONT END IS DONE
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
        String fullLine="";
        String ENDSTRING = "*--------------------**--------------------*";
        String[] splitString;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("Pool.txt"));

            for(int i = 0; i<3;i++){
                reader.readLine();
            }
            //skips the lines in the generated file that are not the actual matches

            fullLine = reader.readLine();
            while(!fullLine.equals(ENDSTRING)){
                splitString = fullLine.split("   \\|\\|   ");
                Red = inFighter(fighterList, splitString[0]);
                Blue = inFighter(fighterList, splitString[1]);
                System.out.println(Red+"\n"+Blue);
                fighterList = fightRR(Red,Blue,currPool, fighterList);
                fullLine = reader.readLine();
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

        for(int i = 0; i<currPool.getRounds();i++) {
            //REPLACE THIS LATER WITH CODE THAT ACTUALLY ASKS USER WHO GETS WHAT POINTS AND WHAT THEY GET
            redTotal++;
            System.out.println("RED WINS A POINT");
        }
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