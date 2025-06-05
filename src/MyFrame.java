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

public class MyFrame extends JFrame implements ActionListener, DocumentListener{
    private JButton fileSelect, startButton;
    public JButton startDualElimButton;
    public CardLayout screen;
    public JLabel selectedFilePathLabel;
    public JTextField poolNumText, RRRoundNumText, RRBoutNumText, DEBoutNumText, DEFighterNumText;
    private boolean StartMenuDone, StartDualElim;
    private ImageIcon logo;
    private Border border;
    public JPanel parentPanel, tiebreakPanel, ContinueS;
    public int numPools, numCompletedPools, numTiebreak, numCurrSelectedTiebreak, numBattles, numCompletedBattles;
    public JTextArea RRConsole, DEConsole;
    public JLabel poolProgressLabel, currSelectLabel, battleLabel, battleProgressLabel, finalWinner;

    public MyFrame() {
        border = BorderFactory.createLineBorder(Color.yellow, 4);
        logo = new ImageIcon("TESTIMAGE.jpg");
        this.StartMenuDone = false;
        this.StartDualElim = false;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        screen = new CardLayout();
        parentPanel = new JPanel(screen);
        this.setLayout(screen);
        this.setSize(600, 450);
        this.setResizable(false);

        this.setTitle("HEMA Tourney SERVER");
        numCurrSelectedTiebreak=0;
        numCompletedBattles = 0;
        this.numCurrSelectedTiebreak=0;
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

    public static void main(String[] args) {
        new MyFrame();
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
    }

    public void removeUpdate(DocumentEvent d) {
        Object source = d.getDocument().getProperty("d");
        startButton.setEnabled(false);
        //if the values in the start screen are removed, button won't work

    }

    public void actionPerformed(ActionEvent e) {
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
    }

    public boolean getStartMenuDone() {
        return this.StartMenuDone;
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
    public void increaseNumCurrSelectedTiebreak(){
        this.numCurrSelectedTiebreak++;
    }
    public void decreaseNumCurrSelectedTiebreak(){
        this.numCurrSelectedTiebreak--;
    }

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
        ContinueS.add(tiebreakPanel);
        ContinueS.add(startDualElimButton);
        ContinueS.revalidate();
        ContinueS.repaint();
    }
    //If there are participants who need to be involved in a tiebreaker. remakes the server's continue screen to allow
    //the server to select who makes it through
}