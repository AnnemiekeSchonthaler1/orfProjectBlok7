package Blok7ApplicatieORF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Program opens up an interactable interface. The user can load up a nucleotide sequence in fasta format. When the correct
 * file is loaded, the user can search for ORFs by pressing the ... button. If any are found they will be visible in the
 * right text box, as well as the amount of ORFs found. The user can choose to BLAST an ORF by pressing the ... button
 * and choosing the desired ORF. When selected the BLAST will commence and results will be printed down in the console.
 * <p>
 * Unfinished feature: There is a checkbox which is supposed to be checked to save BLAST results in the database. The
 * queries on this code however were not finished in time so BLAST results can not yet be saved in the database.
 *
 * @author Harm laurense
 * @since 10-04-2020
 * <p>
 * Code was tested in windows environment for various conditions as described in the test documentation.
 * Known bugs: The user can input a correct file to unlock the other buttons functionality, and then choose another file
 * which is incorrect. The other buttons are still accessible this way but won't result in a crash/error and will simply
 * just display 0 ORFs.
 */
public class OrfFinder extends JFrame implements ActionListener {

    static JFrame frame;
    private JButton openButton, predictOrfButton, blastOrfsButton;
    private JFileChooser fileChooser;
    private SequenceObj sequenceObj = new SequenceObj();
    private JTextArea textArea;
    private String[] orfsArray;
    private JCheckBox safeResultBox;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        OrfFinder frame = new OrfFinder();
        frame.setSize(1400, 800);
        frame.createGUI();
        frame.setVisible(true);
    }

    /**
     * Deze methode creërt de interface. Hierin worden de verschillende knoppen en tekstvakken gemaakt.
     */
    private void createGUI() {
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

        textArea = new JTextArea("<<< Voorspelde ORFs >>> " + "\n", 30, 90);
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

    /**
     * Deze methode zorgt ervoor dat bij een muisklik op een knop de bijbehorende functionaliteit wordt uitgevoerd.
     *
     * @param event Dit wordt gebruikt als waarde voor een muisklik.
     */
    public void actionPerformed(ActionEvent event) {
        int reply;
        if (event.getSource() == openButton) {
            fileChooser = new JFileChooser();
            reply = fileChooser.showOpenDialog(this);
            // Waarde reply is afhankelijk of de gebruiker op "open" of "cancel" klikt
            if (reply == JFileChooser.APPROVE_OPTION) {
                readFile();
            }
        }
        if (event.getSource() == predictOrfButton) {
            predictOrfs();
        }
        if (event.getSource() == blastOrfsButton) {
            blastOrfs();
        }
    }

    /**
     * Deze methode gebruikt een gekozen bestand via fileChooser. Indien het bestand niet voldoet aan de eisen, wordt
     * de bijbehorende foutmelding gegeven en blijft de andere functionaliteit (knoppen) vergrendeld. Indien wel het juiste
     * bestand wordt gegeven zal de variabele juistBestand op true gezet worden en worden de andere knoppen beschikbaar.
     */
    public void readFile() {
        boolean juistBestand = false;
        try {
            File selectedFile;
            selectedFile = fileChooser.getSelectedFile();
            BufferedReader inFile = new BufferedReader(new java.io.FileReader(selectedFile.getAbsolutePath()));
            String line = inFile.readLine();
            // Kijk of het een fasta file is door te kijken of het begint met een '>'
            if (line.charAt(0) == '>') {
                juistBestand = true;
                StringBuilder sequentie = new StringBuilder();
                while ((line = inFile.readLine()) != null) {
                    // Bestand mag maar één sequentie bevatten, indien meerdere headers zijn gevonden dan wordt de actie afgebroken.
                    if (line.contains(">")) {
                        JOptionPane.showMessageDialog(null, "Bestand mag maar één sequentie bevatten");
                        sequentie.append("0");
                        juistBestand = false;
                        break;
                    }
                    sequentie.append(line);
                }
                sequenceObj.setSequence(sequentie.toString());
                // Indien ongeldige tekens zijn gevonden, dan krijgt getSequence() een "0" in SequenceObj.
                // Dit wordt hierin gecontroleerd en afgevangen
                if (sequenceObj.getSequence().equals("0")) {
                    juistBestand = false;
                    JOptionPane.showMessageDialog(null, "Ongeldige sequentie; fasta file mag" +
                            " alleen één nucleotide sequentie bevatten");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Onjuist formaat, geef een fasta file op");
            }
            inFile.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        // Als een nucleotide sequentie als fasta is ingeladen worden de andere knoppen beschikbaar.
        if (juistBestand) {
            predictOrfButton.setEnabled(true);
            blastOrfsButton.setEnabled(true);
        }
    }

    /**
     * Deze methode zorgt dat ORFs voorspeld worden van de ingeladen sequentie. Het zoeken naar ORFs gebeurt via de
     * aanroep van .findOrfs(). Indien er ORFs zijn gevonden wordt het aantal in het tekstvak van de GUI geplaatst, samen
     * met de ORFs als posities.
     */
    public void predictOrfs() {
        sequenceObj.findOrfs();
        if (sequenceObj.getOrfs().size() > 0) {
            textArea.append("Aantal gevonden Orfs: " + sequenceObj.getOrfs().size() + "\n");
            OrfFinder.MultiThreading t1 = new OrfFinder.MultiThreading();
            t1.start();
        }
    }

    /**
     * Deze functie blast het geselecteerde ORF door een python script aan te roepen.
     * Indien de checkbox is aangevinkt zal safe op true staan, dit zorgt ervoor dat bij het uitvoeren van een BLAST
     * de resultaten in de database worden opgeslagen.
     * sequentieOrf is de sequentie van het geblaste ORF.
     */
    public void blastOrfs() {
        try {
            boolean safe = false;
            orfsArray = makeArrayOfOrfs();
            JLabel name = new JLabel(orfsArray[0]);
            // longValue bepaalt het aantal zichtbare tekens in het selectiemenu van ORFs.
            String selectedName = ListDialog.showDialog(frame,
                    predictOrfButton,
                    "ORF viewer",
                    "Choose an ORF which you would like to BLAST",
                    orfsArray, name.getText(),
                    "<ORF lijst>                                                                           ");
            String[] result = selectedName.split(":");
            int id = Integer.parseInt(result[0]);
            ArrayList<Integer> positie = (ArrayList<Integer>) sequenceObj.orfs.get(id);
            String sequentieOrf = sequenceObj.getSequence().substring(positie.get(0), positie.get(1));

            if (safeResultBox.isSelected()) {
                safe = true;
            }

            //Dit stuk code is om de link te leggen met het python script wat de blast uit voert.
            // Ik controleer welk os wordt gebruikt, aangezien niet elke os supported is.
            if (System.getProperty("os.name").startsWith("Windows")) {
                // Als het windows is voer ik het gepaste commandline commando uit om de BLAST uit te voeren
                Runtime rt = Runtime.getRuntime();
                try {
                    // Ik voer blaster (het python script voor BLAST) uit met de sequentie die binnenkomt
                    System.out.println("hier");
                    String command = "blaster.exe " + sequentieOrf;
                    System.out.println("hier2");
                    Process p = rt.exec(command, null, new File(System.getProperty("user.dir")));
                    p.waitFor();
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    textArea.setText("");
                    while ((line = input.readLine()) != null) {
                        textArea.append(line + "\n");
                        System.out.println(line);
                    }
                    System.out.println("klaar");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Dit OS wordt momenteel niet ondersteund: " +
                        "probeer een ander OS");
            }

            if (safe) {
                // Hier moeten de resultaten meegegeven die nodig zijn voor de queries
                // safeBlast();    <-- methode die zou moeten worden aangeroepen
                System.out.println("Functie moet nog geimplementeerd worden");
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deze methode maakt een ArrayList</String> aan met daarin alle posities van elk ORF. Vervolgens wordt deze in een
     * Array omgezet met dezelfde grootte. Dit is nodig voor de showDialog methode in ListDialog.
     *
     * @return Een array met alle ORF posities.
     */
    public String[] makeArrayOfOrfs() {
        try {
            ArrayList<String> orfsArrayList = new ArrayList<>();
            for (int i = 0; i < sequenceObj.getOrfs().size(); i++) {
                String orfs = sequenceObj.getOrfs().get(i).toString();
                orfsArrayList.add(i + ": " + orfs);
            }
            orfsArray = new String[orfsArrayList.size()];
            orfsArrayList.toArray(orfsArray);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Er zijn geen ORFs om te selecteren ");
        }
        return orfsArray;
    }

    /**
     * Deze class zorgt dat meerdere taken uitgevoerd kunnen worden, zodat de GUI direct resultaten visualiseert.
     */
    class MultiThreading extends Thread {
        private Thread t;

        /**
         * Deze methode loopt door de ORF lijst heen en plaatst elk ORF met positie in het tekstvak van de GUI.
         */
        public void run() {
            for (int i = 0; i < sequenceObj.getOrfs().size(); i++) {
                String orf = sequenceObj.getOrfs().get(i).toString();
                textArea.append(orf + "\n");
            }
        }

        /**
         * Deze methode start de taak indien deze wordt aangeroepen in predictOrfs().
         */
        public void start() {
            if (t == null) {
                t = new Thread(this);
                t.start();
            }
        }
    }
}
