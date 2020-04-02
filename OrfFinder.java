import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class OrfFinder extends JFrame implements ActionListener {

    private BufferedReader inFile;
    private JButton openButton, predictOrfButton, viewOrfsBlastResultsButton, blastOrfsButton;
    private JFileChooser fileChooser;
    private SequenceObj sequenceObj = new SequenceObj();
    private JTextArea textArea;
    private String[] orfsArray;
    private JCheckBox safeResultBox;
    static JFrame frame;


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            System.out.println("unsupportedlookandfeel error");
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        OrfFinder frame = new OrfFinder();
        frame.setSize(800, 800);
        frame.createGUI();
        frame.setVisible(true);
    }

    private void createGUI() {
        //Maakt een GUI waarin alles wordt uitgevoerd
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container window = getContentPane();
        window.setLayout(new FlowLayout());

        openButton = new JButton("Choose file");
        window.add(openButton);
        openButton.addActionListener(this);

        predictOrfButton = new JButton("Predict ORFs");
        window.add(predictOrfButton);
        predictOrfButton.addActionListener(this);

        blastOrfsButton = new JButton("BLAST ORFs");
        window.add(blastOrfsButton);
        blastOrfsButton.addActionListener(this);

        viewOrfsBlastResultsButton = new JButton("ORF BLAST results");
        window.add(viewOrfsBlastResultsButton);
        viewOrfsBlastResultsButton.addActionListener(this);

        textArea = new JTextArea("Hieronder staan de gevonden ORFs" + "\n", 30, 20);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setVisible(true);

        JScrollPane scroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        window.add(scroll);

        safeResultBox = new JCheckBox("Check if you want to safe the BLAST result");
        safeResultBox.setSelected(false);
        window.add(safeResultBox);
        safeResultBox.addActionListener(this);

    }

    public void actionPerformed(ActionEvent event) {
        //Als er op open wordt gedrukt wordt het bestand ingelezen
        int reply;
        if (event.getSource() == openButton) {
            fileChooser = new JFileChooser();
            reply = fileChooser.showOpenDialog(this);
            if (reply == JFileChooser.APPROVE_OPTION) {
                try {
                    readFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (event.getSource() == predictOrfButton) {
            predictOrfs();
        }
        if (event.getSource() == viewOrfsBlastResultsButton) {
            viewOrfsBlastResults();
        }
        if (event.getSource() == blastOrfsButton) {
            blastOrfs();
        }
    }

    public void readFile() throws IOException {
        try {
            File selectedFile;
            selectedFile = fileChooser.getSelectedFile();
            inFile = new BufferedReader(new java.io.FileReader(selectedFile.getAbsolutePath()));
            String line = inFile.readLine();
            if (line.charAt(0) == '>') {
                StringBuilder sequentie = new StringBuilder();
                while ((line = inFile.readLine()) != null) {
//                    System.out.println(line);
                    sequentie.append(line);
                }
//                System.out.println(sequentie);
                sequenceObj.setSequence(sequentie.toString());
            } else {
                JOptionPane.showMessageDialog(null, "Onjuist formaat, geef een fasta file op");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        inFile.close();
    }

    public void predictOrfs() {
        if (sequenceObj.getSequence() != null) {
            sequenceObj.findOrfs();
            System.out.println(sequenceObj.getOrfs());
            OrfFinder.MultiThreading t1 = new OrfFinder.MultiThreading();
            t1.start();
        } else {
            JOptionPane.showMessageDialog(null, "Geef eerst een fasta file op via de " +
                    "choose file button");
        }
    }

    public void viewOrfsBlastResults() {
        // blast resultaten ophalen uit database ; indien we genoeg tijd hebben om deze functie af te maken
    }

    public void blastOrfs() {
        try {
            boolean safe = false;
            orfsArray = makeArrayOfOrfs();
            JLabel name = new JLabel(orfsArray[0]);
            // longValue bepaalt hoeveel zichtbaar is / aantal tekens. Dus heb een lange tab staan voor nu.
            String selectedName = ListDialog.showDialog(frame,
                    viewOrfsBlastResultsButton,
                    "ORF viewer",
                    "Choose an ORF which you would like to BLAST",
                    orfsArray, name.getText(),
                    "ORF                                                                                       ");
            System.out.println(selectedName);
            if (safeResultBox.isSelected()) {
                safe = true;
            }
            System.out.println(safe);

            if (System.getProperty("os.name").startsWith("Windows")) {
                System.out.println("Windows");
                Runtime rt = Runtime.getRuntime();
                try {
                    String command = "C:\\Users\\Gebruiker\\AppData\\Local\\Programs\\Python\\Python36-32\\Scripts\\pip install biopython\n" +
                            "C:\\Users\\Gebruiker\\AppData\\Local\\Programs\\Python\\Python36-32\\python %CD%\\src\\blaster.py";
                    Process p = rt.exec(command);
                    System.out.println("Ik ben klaar met het uitvoeren van de BLAST.");
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    while ((line = input.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (System.getProperty("os.name").startsWith("Unix")) {
                System.out.println("Linux");
                            //To get the PYTHON_ABSOLUTE_PATH just type
            //
            //which python2.7
            String[] commands = new String[3];
            commands[0] = "/bin/sh";
            commands[1] = "-c";
            commands[2] = "./blaster.py";

            ProcessBuilder pb = new ProcessBuilder(commands);
            Process p = pb.start();
            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));

            bri.close();
            String line;
            while ((line = bri.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("dun" );
            }

            // functie aanroepen van christiaan om geselecteerde ORF te blasten ; afhankelijk van checkbox wordt
            // ook het resultaat direct opgeslagen in de database
        } catch (NullPointerException e) {
            // do nothing ; wordt in makeArrayOfOrfs al met een messagebox gewaarschuwd. Kreeg het niet voor elkaar
            // om deze helemaal af te vangen, dus ik vang hem hier ook af zonder programma te storen.
        }
    }

    public String[] makeArrayOfOrfs() {
        try {
            ArrayList<String> orfsArrayList = new ArrayList<>();
            for (int i = 0; i < sequenceObj.getOrfs().size(); i++) {
                String orfs = sequenceObj.getOrfs().get(i).toString();
                orfsArrayList.add(orfs);
            }
            orfsArray = new String[orfsArrayList.size()];
            orfsArrayList.toArray(orfsArray);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Er zijn geen ORFs om te selecteren ");
        }
        return orfsArray;
    }

    class MultiThreading extends Thread {
        private Thread t;

        public void run() {
            for (int i = 0; i < sequenceObj.getOrfs().size(); i++) {
                String orf = sequenceObj.getOrfs().get(i).toString();
                textArea.append(orf + "\n");
            }
        }


        public void start() {
            if (t == null) {
                t = new Thread(this);
                t.start();
            }
        }
    }
}



