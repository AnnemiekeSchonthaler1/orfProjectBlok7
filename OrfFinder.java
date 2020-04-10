//Bug: je kunt een goed bestand kiezen om de knoppen te ontgrendelen, en dan een fout bestand kiezen zodat je op
// functionele knoppen kunt klikken. Dit wordt wel afgevangen, dus de knoppen doen niks (en predictorf knop geeft 0 orfs aan).
// Je moet dan opnieuw een bestand kiezen die wel geldig is.

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

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
                            " alleen één nucleotide sequentie bevatten");
                }
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

    /** Deze functie blast het geselecteerde ORF door een python bestand aan te roepen met de code hiervoor.
     * safe = of de resultaten op moeten worden geslagen in de database
     * sequentieOrf is de sequentie van het ORF wat is geblast
     */
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
            String[] result = selectedName.split(":");
            int id = Integer.parseInt(result[0]);
            System.out.println(sequenceObj.orfs.get(id));
            ArrayList<Integer> positie = (ArrayList<Integer>) sequenceObj.orfs.get(id);
            String sequentieOrf = sequenceObj.getSequence().substring(positie.get(0), positie.get(1));
            System.out.println(sequentieOrf);

            if (safeResultBox.isSelected()) {
                safe = true;
            }
            System.out.println(safe);

            //Dit stuk code is om de link te leggen met het python script wat de blast uit voert.
            // Ik controleer welk os wordt gebruikt, aangezien niet elke os supported is.
            if (System.getProperty("os.name").startsWith("Windows")) {
                System.out.println("Windows");
                // Als het windows is voer ik het gepaste commandline commando uit om de BLAST uit te voeren
                Runtime rt = Runtime.getRuntime();
                try {
                    // Ik voer blaster (het python script voor BLAST) uit met de sequentie die binnenkomt
                    String command = "blaster.exe " + sequentieOrf;
                    Process p = rt.exec(command, null, new File(System.getProperty("user.dir")));
                    p.waitFor();
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    while ((line = input.readLine()) != null) {
                        System.out.println(line);
                    }
                    System.out.println("Ik ben klaar met het uitvoeren van de BLAST.");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Dit os is niet supported");
                //todo Harm maak hier een leuke gui foutmelding van zodat de gebruiker dit ook kan zien
            }

            if (safe) {
                // Hier moeten de resultaten meegegeven die nodig zijn voor de queries
                safeBlast();
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            // do nothing ; wordt in makeArrayOfOrfs al met een messagebox gewaarschuwd. Kreeg het niet voor elkaar
            // om deze helemaal af te vangen, dus ik vang hem hier ook af zonder programma te storen.
        }
    }

    private void safeBlast() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://hannl-hlo-bioinformatica-mysqlsrv.mysql.database.azure.com:3306/rucia?serverTimezone=UTC",
                    "rucia@hannl-hlo-bioinformatica-mysqlsrv",
                    "kip");
//here sonoo is database name, root is username and password
            //for(blast in blastList){
            /*
insert into sequence(  {seq_id } , {seq_varchar } )
insert into ORF( {ORF_id },{ Sequence_ORF},{Sequence_sequence_id})
insert into Blast_res ( { description}, {coverage},{e_value}, {loc_start},{Loc_end},{blast_id},{ORF_ORF_id})
 */

//            Statement stmt = con.prepareStatement("insert into blast_res (column1, column2) values (?, ?)");
//            stmt.execute(//blast.id, blast.name);
//                    con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

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



