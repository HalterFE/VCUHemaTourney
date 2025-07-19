import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.text.Document;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MyFrame extends JFrame implements ActionListener, DocumentListener{
    private JButton fileSelect, startButton, startButtonC, nextFightButtonC, submitFightButtonC;
    public JButton startDualElimButton;
    public CardLayout screen;
    public JLabel selectedFilePathLabel;
    public JTextField poolNumText, RRRoundNumText, RRBoutNumText, DEBoutNumText, DEFighterNumText, IPText;
    private boolean StartMenuDone, StartDualElim, StartMenuDoneC;
    private ImageIcon logo;
    private Border border;
    public JPanel parentPanel, tiebreakPanel, ContinueS;
    public int numPools, numCompletedPools, numTiebreak, numCurrSelectedTiebreak, numBattles, numCompletedBattles;
    public JTextArea RRConsole, DEConsole;
    public JLabel poolProgressLabel, currSelectLabel, battleLabel, battleProgressLabel, finalWinner;
    public JScrollPane continueScrollPane;

    private int redPointTotal, bluePointTotal, currRound, newRedPointTotal, newBluePointTotal; //clientside running of pools and matches
    public JLabel matchOrPoolHeader, roundLabel, redDeck, blueDeck, redPointTotalLabel, bluePointTotalLabel, redPointTotalLabelConfirm, bluePointTotalLabelConfirm, RED, BLUE, REDCONFIRM, BLUECONFIRM, onDeck;
    private JTextField redPoints, bluePoints, redChange, blueChange;
    public boolean currPoolDoneC;
    public JPanel FightC, FightConfirmC;

    public MyFrame(int state) {
        border = BorderFactory.createLineBorder(Color.yellow, 4);
        logo = new ImageIcon("TESTIMAGE.jpg");
        this.StartMenuDone = false;
        this.StartDualElim = false;
        this.StartMenuDoneC = false;
        this.currPoolDoneC = false;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        screen = new CardLayout();
        parentPanel = new JPanel(screen);
        this.setLayout(screen);
        this.setSize(600, 450);
        this.setResizable(false);

        if(state == 1) {
            this.setTitle("HEMA Tourney SERVER");
            numCurrSelectedTiebreak = 0;
            numCompletedBattles = 0;
            this.numCurrSelectedTiebreak = 0;
            this.setIconImage(logo.getImage());
            JPanel StartS = makeStartScreenServer();
            JPanel RoundRobinS = makeRoundRobinServer();
            ContinueS = makeContinueServer();
            JPanel DualElimS = makeDualElimServer();
            JPanel FinalS = makeFinalServer();

            parentPanel.add(StartS);
            parentPanel.add(RoundRobinS);
            parentPanel.add(ContinueS);
            parentPanel.add(DualElimS);
            parentPanel.add(FinalS);
            this.add(parentPanel);
            this.setVisible(true);
            //makes everything visible
        }
        else{
            this.setTitle("HEMA Tourney CLIENT");
            this.setIconImage(logo.getImage());
            JPanel StartC = makeStartScreenClient();
            FightC = makeFightScreenClient();
            FightConfirmC = makeFightScreenConfirmClient();
            JPanel ContinueC = makeContinueClient();
            JPanel FinalC = makeFinalClient();

            parentPanel.add(StartC, "StartC");
            parentPanel.add(FightC, "FightC");
            parentPanel.add(FightConfirmC, "FightConfirmC");
            parentPanel.add(ContinueC, "ContinueC");
            parentPanel.add(FinalC, "FinalC");

            this.add(parentPanel);
            this.setVisible(true);
        }
    }

    public static void main(String[] args) {
        new MyFrame(0);
    }

    public void changedUpdate(DocumentEvent d) {
        Object source = d.getDocument().getProperty("owner");
        if(source==poolNumText||source==RRRoundNumText||source==RRBoutNumText||source==DEBoutNumText||source==DEFighterNumText) {
            try {
                Integer.parseInt(poolNumText.getText());
                Integer.parseInt(RRRoundNumText.getText());
                Integer.parseInt(RRBoutNumText.getText());
                Integer.parseInt(DEBoutNumText.getText());
                Integer.parseInt(DEFighterNumText.getText());
                if (selectedFilePathLabel.getText().endsWith(".txt")) {
                    startButton.setEnabled(true);
                } else {
                    startButton.setEnabled(false);
                }
            } catch (NumberFormatException f) {
                startButton.setEnabled(false);
            }
        }
        //if the values in the start screen are changed
        if(source==IPText){
            try {
                InetAddress.getByName(IPText.getText());
                startButtonC.setEnabled(true);
            }
            catch(UnknownHostException u){
                startButtonC.setEnabled(false);
            }
        }
        //if the ip address is a number
        if(source==redPoints||source==bluePoints){
            try{
                Integer.parseInt(redPoints.getText());
                Integer.parseInt(bluePoints.getText());
                nextFightButtonC.setEnabled(true);
            }
            catch (NumberFormatException f) {
                nextFightButtonC.setEnabled(false);
            }
        }
        //if both redPoints and bluePoints have a point value
        if(source==redChange||source==blueChange){
            try{
                newRedPointTotal = Integer.parseInt(redChange.getText());
                newBluePointTotal = Integer.parseInt(blueChange.getText());
                submitFightButtonC.setEnabled(true);
            }
            catch (NumberFormatException f) {
                submitFightButtonC.setEnabled(false);
            }
        }
        //if both redChange and blueChange have an int value
    }
    public void insertUpdate(DocumentEvent d) {
        Object source = d.getDocument().getProperty("owner");
        if(source==poolNumText||source==RRRoundNumText||source==RRBoutNumText||source==DEBoutNumText||source==DEFighterNumText) {
            try {
                Integer.parseInt(poolNumText.getText());
                Integer.parseInt(RRRoundNumText.getText());
                Integer.parseInt(RRBoutNumText.getText());
                Integer.parseInt(DEBoutNumText.getText());
                Integer.parseInt(DEFighterNumText.getText());
                if (selectedFilePathLabel.getText().endsWith(".txt")) {
                    startButton.setEnabled(true);
                } else {
                    startButton.setEnabled(false);
                }
            } catch (NumberFormatException f) {
                startButton.setEnabled(false);
            }
        }
        //if the values in the start screen are edited
        if(source==IPText){
            try {
                InetAddress.getByName(IPText.getText());
                startButtonC.setEnabled(true);
            }
            catch(UnknownHostException u){
                startButtonC.setEnabled(false);
            }
        }
        //if the ip address is a number
        if(source==redPoints||source==bluePoints){
            try{
                newRedPointTotal = Integer.parseInt(redChange.getText());
                newBluePointTotal = Integer.parseInt(blueChange.getText());
                nextFightButtonC.setEnabled(true);
            }
            catch (NumberFormatException f) {
                nextFightButtonC.setEnabled(false);
            }
        }
        //if both redPoints and bluePoints have a point value
        if(source==redChange||source==blueChange){
            try{
                Integer.parseInt(redChange.getText());
                Integer.parseInt(blueChange.getText());
                submitFightButtonC.setEnabled(true);
            }
            catch (NumberFormatException f) {
                submitFightButtonC.setEnabled(false);
            }
        }
        //if both redChange and blueChange have an int value
    }
    public void removeUpdate(DocumentEvent d) {
        Object source = d.getDocument().getProperty("owner");
        if(source==poolNumText||source==RRRoundNumText||source==RRBoutNumText||source==DEBoutNumText||source==DEFighterNumText){
            startButton.setEnabled(false);
        }
        //if the values in the start screen are removed, button won't work
        if(source==IPText){
            try {
                InetAddress.getByName(IPText.getText());
                startButtonC.setEnabled(true);
            }
            catch(UnknownHostException u){
                startButtonC.setEnabled(false);
            }
        }
        //if the ip address is a number
        if(source==redPoints||source==bluePoints){
            try{
                newRedPointTotal = Integer.parseInt(redChange.getText());
                newBluePointTotal = Integer.parseInt(blueChange.getText());
                nextFightButtonC.setEnabled(true);
            }
            catch (NumberFormatException f) {
                nextFightButtonC.setEnabled(false);
            }
        }
        //if both redPoints and bluePoints have a point value
        if(source==redChange||source==blueChange){
            try{
                Integer.parseInt(redChange.getText());
                Integer.parseInt(blueChange.getText());
                submitFightButtonC.setEnabled(true);
            }
            catch (NumberFormatException f) {
                submitFightButtonC.setEnabled(false);
            }
        }
        //if both redChange and blueChange have an int value
    }
    //checks whenever a document is changed to see if certain buttons should be active

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == nextFightButtonC){
            try {
                bluePointTotal = bluePointTotal + Integer.parseInt(bluePoints.getText());
                redPointTotal = redPointTotal + Integer.parseInt(redPoints.getText());
                newRedPointTotal = redPointTotal;
                newBluePointTotal = bluePointTotal;
                currRound++;
                redPointTotalLabel.setText("Total: "+redPointTotal);
                bluePointTotalLabel.setText("Total: "+bluePointTotal);
                redPointTotalLabelConfirm.setText("Total: "+redPointTotal);
                bluePointTotalLabelConfirm.setText("Total: "+bluePointTotal);
                roundLabel.setText("Round: "+currRound);
                /*
                    If the current round exceeds the number of rounds per spar, go to the confirmation screen
                */
            }
            catch (NumberFormatException f) {
                f.printStackTrace();
            }
        }
        if(e.getSource()== submitFightButtonC){
            redPointTotal = 0;
            bluePointTotal = 0;
            redPointTotalLabel.setText("Total: "+redPointTotal);
            bluePointTotalLabel.setText("Total: "+bluePointTotal);
            redPointTotalLabelConfirm.setText("Total: "+redPointTotal);
            bluePointTotalLabelConfirm.setText("Total: "+bluePointTotal);
            this.currRound = 1;
            roundLabel.setText("Round: "+currRound);
            currPoolDoneC = true;
        }
        if (e.getSource() == startDualElimButton) {
            this.StartDualElim = true;
        }
        if (e.getSource() == fileSelect) {
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showOpenDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
                File participantFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
                selectedFilePathLabel.setText(participantFile.getPath());
                try {
                    Integer.parseInt(poolNumText.getText());
                    Integer.parseInt(RRRoundNumText.getText());
                    Integer.parseInt(RRBoutNumText.getText());
                    Integer.parseInt(DEBoutNumText.getText());
                    Integer.parseInt(DEFighterNumText.getText());
                    if (selectedFilePathLabel.getText().endsWith(".txt")) {
                        startButton.setEnabled(true);
                    } else {
                        startButton.setEnabled(false);
                    }
                } catch (NumberFormatException f) {
                    startButton.setEnabled(false);
                }
            }
        }
        //selects the participant text file
        if (e.getSource() == startButton) {
            this.StartMenuDone = true;
        }
        if (e.getSource() == startButtonC){
            this.StartMenuDoneC = true;
        }
    }

    public boolean getStartMenuDone() {
        return this.StartMenuDone;
    }
    public boolean getStartMenuDoneC() {
        return this.StartMenuDoneC;
    }
    public boolean getStartDualElim() {
        return this.StartDualElim;
    }
    public void setNumTiebreak(int i) {
        this.numTiebreak = i;
    }
    public void setNumPools(int i) {
        this.numPools = i;
    }
    public void setNumCompletedPools(int i) {
        this.numCompletedPools = i;
    }
    public int getNewRedPointTotal(){ return newRedPointTotal;}
    public int getNewBluePointTotal(){ return newBluePointTotal;}
    public int getCurrRound(){ return currRound;}
    //Miscellaneous getters and setters

    public void increaseNumCurrSelectedTiebreak(){
        this.numCurrSelectedTiebreak++;
    }
    public void decreaseNumCurrSelectedTiebreak(){
        this.numCurrSelectedTiebreak--;
    }
    //I wonder what these do...

    public JPanel makeFinalServer(){
        JPanel FinalS = new JPanel();
        FinalS.setBounds(0, 0, 586, 412);
        FinalS.setBorder(border);
        FinalS.setBackground(Color.black);
        FinalS.setLayout(null);
        //stuff for the Final Screen of the Server

        finalWinner = new JLabel("The Champion Is: Lorem Ipsum Dolor Sit Amet");
        finalWinner.setIcon(logo);
        finalWinner.setHorizontalTextPosition(JLabel.CENTER);
        finalWinner.setVerticalTextPosition(JLabel.BOTTOM);
        finalWinner.setHorizontalAlignment(SwingConstants.CENTER);
        finalWinner.setFont(new Font("JetBrains Mono", Font.BOLD, 25));
        finalWinner.setForeground(Color.yellow);
        finalWinner.setBounds(10, 30, 550, 200);
        //The label that displays who won in the end

        FinalS.add(finalWinner);

        return FinalS;
    }
    //Returns a JPanel that serves as a screen for displaying who won the tournament for the server

    public JPanel makeFinalClient(){
        JPanel FinalC = new JPanel();
        FinalC.setBounds(0, 0, 586, 412);
        FinalC.setBorder(border);
        FinalC.setBackground(Color.black);
        FinalC.setLayout(null);
        //stuff for the Final Screen of the Server

        finalWinner = new JLabel("<html><body>&nbsp;&nbsp;&nbsp;The Tournament is Over<br/>Check the Server for Results</body></html>");
        finalWinner.setIcon(logo);
        finalWinner.setHorizontalTextPosition(JLabel.CENTER);
        finalWinner.setVerticalTextPosition(JLabel.BOTTOM);
        finalWinner.setHorizontalAlignment(SwingConstants.CENTER);
        finalWinner.setFont(new Font("JetBrains Mono", Font.BOLD, 25));
        finalWinner.setForeground(Color.yellow);
        finalWinner.setBounds(10, 30, 550, 300);
        //The label that displays who won in the end

        FinalC.add(finalWinner);

        return FinalC;
    }
    //returns a JPanel telling the client that the tourney is done and that the server has the results

    public JPanel makeDualElimServer(){
        JPanel DualElimS = new JPanel();
        DualElimS.setBounds(0, 0, 586, 412);
        DualElimS.setBorder(border);
        DualElimS.setBackground(Color.black);
        DualElimS.setLayout(null);
        //This is stuff for the DE screen for the server

        battleLabel = new JLabel("Completed Battles:");
        battleLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 20));
        battleLabel.setForeground(Color.yellow);
        battleLabel.setBounds(50, 165, 250, 20);
        //Header for displaying battle data

        battleProgressLabel = new JLabel(numCompletedBattles + "/" + numBattles);
        battleProgressLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 20));
        battleProgressLabel.setForeground(Color.yellow);
        battleProgressLabel.setBounds(115, 205, 50, 20);
        //displays data about what battles are done or not

        DEConsole = new JTextArea("Server :> Running Dual Elim Protocol\n\n");
        DEConsole.setFont(new Font("JetBrains", Font.BOLD, 15));
        DEConsole.setForeground(Color.yellow);
        DEConsole.setBackground(Color.black);
        DEConsole.setBounds(255, 10, 321, 392);
        DEConsole.setBorder(border);
        DEConsole.setEditable(false);
        //console that displays client activity in regards to battles

        DualElimS.add(battleLabel);
        DualElimS.add(battleProgressLabel);
        DualElimS.add(DEConsole);

        return DualElimS;
    }
    //Returns a JPanel that serves as the screen for running the dual elimination matches for the server

    public JPanel makeContinueServer() {
        JPanel ContinueS = new JPanel();
        ContinueS.setBounds(0, 0, 586, 412);
        ContinueS.setBorder(border);
        ContinueS.setBackground(Color.black);
        ContinueS.setLayout(null);
        //This is stuff for the Continue screen for the server

        JLabel continueLabel = new JLabel("Continue?");
        continueLabel.setIcon(logo);
        continueLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 40));
        continueLabel.setForeground(Color.yellow);
        continueLabel.setBounds(190, 30, 250, 200);
        continueLabel.setHorizontalTextPosition(JLabel.CENTER);
        continueLabel.setVerticalTextPosition(JLabel.BOTTOM);

        startDualElimButton = new JButton("Continue");
        startDualElimButton.addActionListener(this);
        startDualElimButton.setBounds(240, 280, 100, 30);

        ContinueS.add(startDualElimButton);
        ContinueS.add(continueLabel);
        //continue screen if no tiebreaker matches need to be run

        return ContinueS;
    }
    //returns a JPanel that serves as the break screen between running the Round Robin and Dual Elim sections

    public JPanel makeContinueClient(){
        JPanel FinalC = new JPanel();
        FinalC.setBounds(0, 0, 586, 412);
        FinalC.setBorder(border);
        FinalC.setBackground(Color.black);
        FinalC.setLayout(null);
        //stuff for the Final Screen of the Server

        JLabel waitForServer = new JLabel("Waiting for the server to send data...");
        waitForServer.setFont(new Font("JetBrains Mono", Font.BOLD, 25));
        waitForServer.setForeground(Color.yellow);
        waitForServer.setBounds(79, 176, 428, 30);
        //The label that displays who won in the end

        FinalC.add(waitForServer);

        return FinalC;
    }
    //returns a JPanel that fills the space between the client getting new data from the server

    public JPanel makeRoundRobinServer() {
        JPanel RoundRobinS = new JPanel();
        RoundRobinS.setBounds(0, 0, 586, 412);
        RoundRobinS.setBorder(border);
        RoundRobinS.setBackground(Color.black);
        RoundRobinS.setLayout(null);
        //This is stuff for the RR screen for the server

        JLabel poolLabel = new JLabel("Completed Pools:");
        poolLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 20));
        poolLabel.setForeground(Color.yellow);
        poolLabel.setBounds(50, 165, 250, 20);
        //Header for displaying pool data

        poolProgressLabel = new JLabel(numCompletedPools + "/" + numPools);
        poolProgressLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 20));
        poolProgressLabel.setForeground(Color.yellow);
        poolProgressLabel.setBounds(115, 205, 50, 20);
        //displays data about what pools are done or not

        RRConsole = new JTextArea("Server :> Running Round Robin Protocol\n\n");
        RRConsole.setFont(new Font("JetBrains", Font.BOLD, 15));
        RRConsole.setForeground(Color.yellow);
        RRConsole.setBackground(Color.black);
        RRConsole.setBounds(255, 10, 321, 392);
        RRConsole.setBorder(border);
        RRConsole.setEditable(false);
        //console that displays client activity in regards to pool

        RoundRobinS.add(poolLabel);
        RoundRobinS.add(poolProgressLabel);
        RoundRobinS.add(RRConsole);
        return RoundRobinS;
    }
    //returns a JPanel that serves as the screen for running round robins for the server

    public JPanel makeFightScreenClient(){
        currRound = 1;
        JPanel FightScreenC = new JPanel();
        FightScreenC.setBounds(0, 0, 586, 412);
        FightScreenC.setBorder(border);
        FightScreenC.setBackground(Color.black);
        FightScreenC.setLayout(null);
        //This is stuff for the fight screen of the client

        matchOrPoolHeader = new JLabel("Pool Number: X");
        matchOrPoolHeader.setFont(new Font("JetBrains Mono", Font.BOLD, 30));
        matchOrPoolHeader.setForeground(Color.yellow);
        matchOrPoolHeader.setBounds(168, 15, 250, 40);
        FightScreenC.add(matchOrPoolHeader);
        //label at the top of the screen
        //the text can change later once the pool stuff is done

        RED = new JLabel("REDrrrrrrrrrrrrr");
        BLUE = new JLabel("BLUEbbbbbbbbbbbb");
        JLabel VS = new JLabel("VS.");
        RED.setFont(new Font("JetBrains Mono", Font.BOLD, 30));
        BLUE.setFont(new Font("JetBrains Mono", Font.BOLD, 30));
        VS.setFont(new Font("JetBrains Mono", Font.BOLD, 40));
        RED.setForeground(Color.red);
        BLUE.setForeground(Color.blue);
        VS.setForeground(Color.white);
        RED.setBounds(30, 75, 200, 80);
        BLUE.setBounds(354, 75, 200, 80);
        VS.setBounds(260, 75, 64, 80);
        FightScreenC.add(RED);
        FightScreenC.add(BLUE);
        FightScreenC.add(VS);
        //RED vs BLUE labels

        JLabel redPointLabel = new JLabel("Points:"), bluePointLabel = new JLabel("Points:");
        redPointLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 25));
        bluePointLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 25));
        redPointLabel.setForeground(Color.white);
        bluePointLabel.setForeground(Color.white);
        redPointLabel.setBounds(40, 175, 82, 25);
        bluePointLabel.setBounds(437, 175, 82, 25);
        FightScreenC.add(redPointLabel);
        FightScreenC.add(bluePointLabel);
        //Labels before the inputs of points in every round

        redPoints = new JTextField("0");
        redPoints.getDocument().addDocumentListener(this);
        redPoints.getDocument().putProperty("owner", redPoints);
        bluePoints = new JTextField("0");
        bluePoints.getDocument().addDocumentListener(this);
        bluePoints.getDocument().putProperty("owner", bluePoints);
        redPoints.setPreferredSize(new Dimension(20, 25));
        redPoints.setBounds(126, 175, 20, 25);
        bluePoints.setPreferredSize(new Dimension(20, 25));
        bluePoints.setBounds(522, 175, 20, 25);
        FightScreenC.add(redPoints);
        FightScreenC.add(bluePoints);
        //text fields where user submits how many points each person earned that round

        redPointTotalLabel = new JLabel("Total: "+redPointTotal);
        bluePointTotalLabel = new JLabel("Total: "+bluePointTotal);
        redPointTotalLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 20));
        bluePointTotalLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 20));
        redPointTotalLabel.setForeground(Color.red);
        bluePointTotalLabel.setForeground(Color.blue);
        redPointTotalLabel.setBounds(56, 245, 82, 20);
        bluePointTotalLabel.setBounds(452, 245, 82, 20);
        FightScreenC.add(redPointTotalLabel);
        FightScreenC.add(bluePointTotalLabel);
        //lists the total number of points earned by each fighter

        roundLabel = new JLabel("Round: "+currRound);
        roundLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 20));
        roundLabel.setForeground(Color.yellow);
        roundLabel.setBounds(247, 215, 100, 20);
        FightScreenC.add(roundLabel);
        //lists what round the user is on

        nextFightButtonC = new JButton("Next Round");
        nextFightButtonC.addActionListener(this);
        nextFightButtonC.setBounds(232, 275, 112, 30);
        FightScreenC.add(nextFightButtonC);
        //button to go to the next round

        onDeck = new JLabel("<html><body>On Deck<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;VS:</body></html>");
        onDeck.setFont(new Font("JetBrains Mono", Font.BOLD, 15));
        onDeck.setForeground(Color.white);
        onDeck.setBounds(262, 335, 70, 40);
        redDeck = new JLabel("Lorem Ipsum Dolor Sit Amet");
        blueDeck = new JLabel("Lorem Ipsum Dolor Sit Amet");
        redDeck.setFont(new Font("JetBrains Mono", Font.BOLD, 15));
        blueDeck.setFont(new Font("JetBrains Mono", Font.BOLD, 15));
        redDeck.setForeground(Color.red);
        redDeck.setBounds(30, 355, 232, 20);
        blueDeck.setForeground(Color.blue);
        blueDeck.setBounds(352, 355, 232, 20);
        FightScreenC.add(onDeck);
        FightScreenC.add(redDeck);
        FightScreenC.add(blueDeck);

        return FightScreenC;
    }
    //returns a JPanel that displays the current round being fought in a pool or during dual elims

    public JPanel makeFightScreenConfirmClient(){
        JPanel FightScreenConfirmC = new JPanel();
        FightScreenConfirmC.setBounds(0, 0, 586, 412);
        FightScreenConfirmC.setBorder(border);
        FightScreenConfirmC.setBackground(Color.black);
        FightScreenConfirmC.setLayout(null);
        //This is stuff for the fight screen of the client

        FightScreenConfirmC.add(matchOrPoolHeader);
        //label at the top of the screen
        //the text can change later once the pool stuff is done

        JLabel CORRECT = new JLabel("<html><body>&nbsp;&nbsp;&nbsp;IS THIS<br/>CORRECT?</body></html>");
        REDCONFIRM = new JLabel("REDrrrrrrrrrrrrr");
        BLUECONFIRM = new JLabel("BLUEbbbbbbbbbbbb");
        REDCONFIRM.setFont(new Font("JetBrains Mono", Font.BOLD, 30));
        BLUECONFIRM.setFont(new Font("JetBrains Mono", Font.BOLD, 30));
        CORRECT.setFont(new Font("JetBrains Mono", Font.BOLD, 20));
        REDCONFIRM.setForeground(Color.red);
        BLUECONFIRM.setForeground(Color.blue);
        CORRECT.setForeground(Color.white);
        REDCONFIRM.setBounds(20, 75, 200, 80);
        BLUECONFIRM.setBounds(359, 75, 200, 80);
        CORRECT.setBounds(235, 75, 128, 80);
        FightScreenConfirmC.add(REDCONFIRM);
        FightScreenConfirmC.add(BLUECONFIRM);
        FightScreenConfirmC.add(CORRECT);
        //RED vs BLUE labels

        redPointTotalLabelConfirm = new JLabel("Total: "+redPointTotal);
        bluePointTotalLabelConfirm = new JLabel("Total: "+bluePointTotal);
        redPointTotalLabelConfirm.setBounds(56, 205, 122, 20);
        bluePointTotalLabelConfirm.setBounds(452, 205, 122, 20);
        redPointTotalLabelConfirm.setFont(new Font("JetBrains Mono", Font.BOLD, 25));
        bluePointTotalLabelConfirm.setFont(new Font("JetBrains Mono", Font.BOLD, 25));
        redPointTotalLabelConfirm.setForeground(Color.white);
        bluePointTotalLabelConfirm.setForeground(Color.white);
        FightScreenConfirmC.add(redPointTotalLabelConfirm);
        FightScreenConfirmC.add(bluePointTotalLabelConfirm);
        //lists the total number of points earned by each fighter

        submitFightButtonC= new JButton("Confirm");
        submitFightButtonC.addActionListener(this);
        submitFightButtonC.setBounds(182, 320, 212, 60);
        FightScreenConfirmC.add(submitFightButtonC);
        //button to go to the next round

        JLabel redPointLabel = new JLabel("New Points:"), bluePointLabel = new JLabel("New Points:");
        redPointLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 25));
        bluePointLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 25));
        redPointLabel.setForeground(Color.white);
        bluePointLabel.setForeground(Color.white);
        redPointLabel.setBounds(20, 255, 150, 25);
        bluePointLabel.setBounds(397, 255, 150, 25);
        FightScreenConfirmC.add(redPointLabel);
        FightScreenConfirmC.add(bluePointLabel);
        //Labels before the inputs of points in every round

        redChange = new JTextField(String.valueOf(redPointTotal));
        redChange.getDocument().addDocumentListener(this);
        redChange.getDocument().putProperty("owner", redChange);
        blueChange = new JTextField(String.valueOf(bluePointTotal));
        blueChange.getDocument().addDocumentListener(this);
        blueChange.getDocument().putProperty("owner", blueChange);
        redChange.setPreferredSize(new Dimension(20, 25));
        redChange.setBounds(170, 255, 20, 25);
        blueChange.setPreferredSize(new Dimension(20, 25));
        blueChange.setBounds(547, 255, 20, 25);
        FightScreenConfirmC.add(redChange);
        FightScreenConfirmC.add(blueChange);

        return FightScreenConfirmC;
    }
    //returns a JPanel that shows the point totals after all the fights between two fighters

    public JPanel makeStartScreenServer() {
        JPanel StartS = new JPanel();
        StartS.setBounds(0, 0, 586, 412);
        StartS.setBorder(border);
        StartS.setBackground(Color.black);
        StartS.setLayout(null);
        //This is stuff for the start screen for the server

        JLabel logoLabel = new JLabel("HEMA Tourney Runner SERVER");
        logoLabel.setIcon(logo);
        logoLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 20));
        logoLabel.setForeground(Color.yellow);
        logoLabel.setHorizontalTextPosition(JLabel.CENTER);
        logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
        logoLabel.setBounds(145, 30, 350, 175);
        StartS.add(logoLabel);
        //Logo that will be in the top middle of the start screen

        JLabel poolLabel = new JLabel("Number of Pools:"),
                RRRoundLabel = new JLabel("Rounds in RR Section:"),
                RRBoutLabel = new JLabel("Bouts in Each Round of RR:"),
                DEBoutLabel = new JLabel("Bouts in Each Round of DE:"),
                numDualElimLabel = new JLabel("Number of Fighters in DE:");
        poolNumText = new JTextField();
        poolNumText.getDocument().addDocumentListener(this);
        poolNumText.getDocument().putProperty("owner", poolNumText);
        RRRoundNumText = new JTextField();
        RRRoundNumText.getDocument().addDocumentListener(this);
        RRRoundNumText.getDocument().putProperty("owner", RRRoundNumText);
        RRBoutNumText = new JTextField();
        RRBoutNumText.getDocument().addDocumentListener(this);
        RRBoutNumText.getDocument().putProperty("owner", RRBoutNumText);
        DEBoutNumText = new JTextField();
        DEBoutNumText.getDocument().addDocumentListener(this);
        DEBoutNumText.getDocument().putProperty("owner", DEBoutNumText);
        DEFighterNumText = new JTextField();
        DEFighterNumText.getDocument().addDocumentListener(this);
        DEFighterNumText.getDocument().putProperty("owner", DEFighterNumText);
        poolLabel.setBounds(10, 220, 160, 30);
        poolLabel.setForeground(Color.yellow);
        RRRoundLabel.setBounds(10, 255, 160, 30);
        RRRoundLabel.setForeground(Color.yellow);
        RRBoutLabel.setBounds(10, 290, 160, 30);
        RRBoutLabel.setForeground(Color.yellow);
        DEBoutLabel.setBounds(10, 325, 160, 30);
        DEBoutLabel.setForeground(Color.yellow);
        numDualElimLabel.setBounds(10, 360, 160, 30);
        numDualElimLabel.setForeground(Color.yellow);
        //setting label locations properly

        poolNumText.setPreferredSize(new Dimension(20, 30));
        poolNumText.setBounds(180, 220, 20, 30);
        RRRoundNumText.setPreferredSize(new Dimension(20, 30));
        RRRoundNumText.setBounds(180, 255, 20, 30);
        RRBoutNumText.setPreferredSize(new Dimension(20, 30));
        RRBoutNumText.setBounds(180, 290, 20, 30);
        DEBoutNumText.setPreferredSize(new Dimension(20, 30));
        DEBoutNumText.setBounds(180, 325, 20, 30);
        DEFighterNumText.setPreferredSize(new Dimension(20, 30));
        DEFighterNumText.setBounds(180, 360, 20, 30);
        //setting locations of textfields

        StartS.add(poolLabel);
        StartS.add(RRRoundLabel);
        StartS.add(RRBoutLabel);
        StartS.add(DEBoutLabel);
        StartS.add(numDualElimLabel);
        StartS.add(poolNumText);
        StartS.add(RRRoundNumText);
        StartS.add(RRBoutNumText);
        StartS.add(DEBoutNumText);
        StartS.add(DEFighterNumText);
        //Labels that will take data from user before starting app

        JLabel fileLabel = new JLabel("Participant .txt file:"),
                selectedFileLabel = new JLabel("Selected File:");

        selectedFilePathLabel = new JLabel("");
        fileLabel.setBounds(230, 220, 120, 30);
        selectedFileLabel.setBounds(230, 265, 120, 30);
        selectedFilePathLabel.setBounds(230, 298, 350, 30);
        fileLabel.setForeground(Color.yellow);
        selectedFileLabel.setForeground(Color.yellow);
        selectedFilePathLabel.setForeground(Color.yellow);

        fileSelect = new JButton("Select File:");
        fileSelect.addActionListener(this);
        fileSelect.setBounds(360, 220, 100, 30);
        startButton = new JButton("START");
        startButton.addActionListener(this);
        startButton.setBounds(450, 350, 100, 40);
        startButton.setEnabled(false);

        StartS.add(fileLabel);
        StartS.add(selectedFileLabel);
        StartS.add(selectedFilePathLabel);
        StartS.add(fileSelect);
        StartS.add(startButton);
        return StartS;
    }
    //returns a JPanel that serves as the start screen for the server

    public JPanel makeStartScreenClient(){
        JPanel StartC = new JPanel();
        StartC.setBounds(0, 0, 586, 412);
        StartC.setBorder(border);
        StartC.setBackground(Color.black);
        StartC.setLayout(null);
        //This is stuff for the start screen for the client

        JLabel logoLabel = new JLabel("HEMA Tourney Runner CLIENT");
        logoLabel.setIcon(logo);
        logoLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 20));
        logoLabel.setForeground(Color.yellow);
        logoLabel.setHorizontalTextPosition(JLabel.CENTER);
        logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
        logoLabel.setBounds(145, 30, 350, 175);
        StartC.add(logoLabel);
        //Logo that will be in the top middle of the start screen

        JLabel IPLabel = new JLabel("IP Address of Server Computer:");
        IPText = new JTextField("127.0.0.1");
        IPText.getDocument().addDocumentListener(this);
        IPText.getDocument().putProperty("owner", IPText);

        IPLabel.setBounds(200, 220, 200, 30);
        IPLabel.setForeground(Color.yellow);
        IPText.setPreferredSize(new Dimension(70, 30));
        IPText.setBounds(190, 250, 200, 30);

        StartC.add(IPLabel);
        StartC.add(IPText);

        startButtonC = new JButton("START");
        startButtonC.addActionListener(this);
        startButtonC.setBounds(240, 320, 100, 40);
        startButtonC.setEnabled(false);
        startButtonC.setHorizontalAlignment(SwingConstants.CENTER);

        StartC.add(startButtonC);
        return StartC;
    }
    //returns a JPanel that serves as the start screen for the client

    public void remakeServerContinue(){
        ContinueS.removeAll();
        JLabel selectLabel = new JLabel("Select who won tiebreaker(s):");
        selectLabel.setFont(new Font("JetBrains Mono",Font.BOLD,20));
        selectLabel.setForeground(Color.yellow);
        selectLabel.setBounds(10,165,285,20);
        //Header for displaying that the user needs to select who won tiebreaker matches

        currSelectLabel = new JLabel(this.numCurrSelectedTiebreak+"/"+numTiebreak);
        currSelectLabel.setFont(new Font("JetBrains Mono",Font.BOLD,20));
        currSelectLabel.setForeground(Color.yellow);
        currSelectLabel.setBounds(135,205,50,20);
        //displays data about what pools are done or not

        tiebreakPanel = new JPanel();
        tiebreakPanel.setBackground(Color.black);
        tiebreakPanel.setLayout(new BoxLayout(tiebreakPanel, BoxLayout.Y_AXIS));
        tiebreakPanel.setBounds(295,10,281,392);
        tiebreakPanel.setBorder(border);
        continueScrollPane = new JScrollPane(tiebreakPanel, continueScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, continueScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        continueScrollPane.setBackground(Color.black);
        continueScrollPane.setBounds(295,10,281,392);
        continueScrollPane.setBorder(border);
        //The Panel that will display who is up for tiebreaker

        startDualElimButton = new JButton("Continue");
        startDualElimButton.addActionListener(this);
        startDualElimButton.setBounds(100, 280, 100, 30);
        startDualElimButton.setEnabled(false);
        //the button that will continue the program

        /*this.numCurrSelectedTiebreak.addPropertyChangeListener("owner", new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent p){
                Integer source =p.getSource();
                if(source==numCurrSelectedTiebreak){
                    currSelectLabel.setText(numCurrSelectedTiebreak+"/"+numTiebreak);
                }
            }
        });*/

        ContinueS.add(selectLabel);
        ContinueS.add(currSelectLabel);
        ContinueS.add(continueScrollPane);
        ContinueS.add(startDualElimButton);
        ContinueS.revalidate();
        ContinueS.repaint();
    }
    //If there are participants who need to be involved in a tiebreaker. remakes the server's continue screen to allow
    //the server to select who makes it through
}