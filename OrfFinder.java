//Bug: je kunt een goed bestand kiezen om de knoppen te ontgrendelen, en dan een fout bestand kiezen zodat je op
// functionele knoppen kunt klikken. Dit wordt wel afgevangen, dus de knoppen doen niks (en predictorf knop geeft 0 orfs aan).
// Je moet dan opnieuw een bestand kiezen die wel geldig is.

package Blok7ApplicatieORF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
        predictOrfButton.setEnabled(false);

        blastOrfsButton = new JButton("BLAST ORFs");
        window.add(blastOrfsButton);
        blastOrfsButton.addActionListener(this);
        blastOrfsButton.setEnabled(false);

        viewOrfsBlastResultsButton = new JButton("ORF BLAST results");
        window.add(viewOrfsBlastResultsButton);
        viewOrfsBlastResultsButton.addActionListener(this);

        textArea = new JTextArea("<<< Voorspelde ORFs >>> " + "\n", 30, 20);
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
        boolean juistBestand = false;
        try {
            File selectedFile;
            selectedFile = fileChooser.getSelectedFile();
            inFile = new BufferedReader(new java.io.FileReader(selectedFile.getAbsolutePath()));
            String line = inFile.readLine();
            if (line.charAt(0) == '>') {
                juistBestand = true;
                StringBuilder sequentie = new StringBuilder();
                while ((line = inFile.readLine()) != null) {
//                    System.out.println(line);
                    // bestand mag maar één sequentie bevatten, indien meerdere headers gevonden wordt actie afgebroken.
                    // sequentie krijgt een 0 mee mocht de gebruiker toch andere functies hierop uitproberen, dan wordt
                    // deze juist afgevangen
                    if (line.contains(">")) {
                        JOptionPane.showMessageDialog(null, "Bestand mag maar één sequentie bevatten");
                        sequentie.append("0");
                        juistBestand = false;
                        break;
                    }
                    sequentie.append(line);
                }
//                System.out.println(sequentie);
                sequenceObj.setSequence(sequentie.toString());
                if (sequenceObj.getSequence().equals("0")) {
                    juistBestand = false;
                    JOptionPane.showMessageDialog(null, "Ongeldige sequentie; fasta file mag" +
                            " alleen één nucleotide sequentie bevatten"); }
            } else {
                JOptionPane.showMessageDialog(null, "Onjuist formaat, geef een fasta file op");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        inFile.close();
        if (juistBestand) {
            predictOrfButton.setEnabled(true);
            blastOrfsButton.setEnabled(true);
        }
    }

    public void predictOrfs() {
        if (sequenceObj.getSequence() != null) {
            sequenceObj.findOrfs();
            System.out.println(sequenceObj.getOrfs());
            if (sequenceObj.getOrfs().size() > 0) {
                textArea.append("Aantal gevonden Orfs: " + sequenceObj.getOrfs().size() + "\n");
                OrfFinder.MultiThreading t1 = new OrfFinder.MultiThreading();
                t1.start();
            }
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
            // functie aanroepen van christiaan om geselecteerde ORF te blasten ; afhankelijk van checkbox wordt
            // ook het resultaat direct opgeslagen in de database
        }
        catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            // do nothing ; wordt in makeArrayOfOrfs al met een messagebox gewaarschuwd. Kreeg het niet voor elkaar
            // om deze helemaal af te vangen, dus ik vang hem hier ook af zonder programma te storen.
        }
    }

    public String[] makeArrayOfOrfs() {
        try {
            ArrayList<String> orfsArrayList = new ArrayList<>();
            for (int i = 0; i < sequenceObj.getOrfs().size(); i++) {
            String orfs = sequenceObj.getOrfs().get(i).toString();
            orfsArrayList.add(orfs); }
            orfsArray = new String[orfsArrayList.size()];
            orfsArrayList.toArray(orfsArray);
        }
        catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
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



