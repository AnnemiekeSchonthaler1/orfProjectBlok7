package Blok7ApplicatieORF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class OrfFinder extends JFrame implements ActionListener {

    private BufferedReader inFile;
    private JButton openButton, predictOrfButton,  viewOrfsButton, blastOrfsButton;
    private JFileChooser fileChooser;
    private SequenceObj sequenceObj = new SequenceObj();
    private JTextArea textArea;
    static JFrame frame;
    static String[] names = {"Tijdelijk, moet Hashmap omzetten naar String[] voor de List, hier komen de ORFS dan " +
            "uiteindelijk om uit te kiezen"};

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
        frame.setSize(600, 600);
        frame.createGUI();
//        frame.pack();
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

        viewOrfsButton = new JButton("View ORFs");
        window.add( viewOrfsButton);
        viewOrfsButton.addActionListener(this);

        blastOrfsButton = new JButton("BLAST ORFs");
        window.add( blastOrfsButton);
        blastOrfsButton.addActionListener(this);

        textArea = new JTextArea();
        window.add(textArea);
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
        if (event.getSource() == viewOrfsButton) {
            viewOrfs();
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
            }
            else {
                JOptionPane.showMessageDialog(null, "Onjuist formaat, geef een fasta file op");
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        inFile.close();
    }

    public void predictOrfs(){
        if (sequenceObj.getSequence() != null) {
            sequenceObj.findOrfs();
            System.out.println(sequenceObj.getOrfs());

        }
        else {
            JOptionPane.showMessageDialog(null, "Geef eerst een fasta file op via de " +
                    "choose file button");
        }
    }

    public void viewOrfs(){
        JLabel name = new JLabel(names[0]);
        // longValue bepaalt hoeveel zichtbaar is / aantal tekens. Dus heb een lange tab staan voor nu.
        String selectedName = ListDialog.showDialog(frame,
                viewOrfsButton,
                "ORF result viewer",
                "Choose an ORF to see its BLAST result",
                names, name.getText(),
                "ORF                                                                                       ");
        textArea.append(selectedName);
    }

    public void blastOrfs(){
        // functie aanroepen van christiaan
    }

}