import javax.swing.plaf.SplitPaneUI;
import java.util.ArrayList;
import java.util.HashMap;


public class SequenceObj {
    String sequence;
    ArrayList<Integer> orfPosities;
    HashMap orfs;

    /**
     * Dit is de constructor van sequenceObj.
     * De constructor neemt niets aan, het wordt aangemaakt met setters om de data te valideren.
     */
    public SequenceObj() {
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        String sequenceWithoutN = sequence.replace("\n", "");
        // Dit controleert of er ALLEEN ATGU in de sequentie zit en dus of het een geldige seq is
        String regex = "[ATGC]{"+sequenceWithoutN.length()+"}";
        if (sequenceWithoutN.matches(regex)){
            // Als dit zo is slaat het de sequentie op in de variabele
            this.sequence = sequenceWithoutN;
            System.out.println("Dit is een sequentie");
        } else {
            // Als de sequentie niet valide is wordt het "0".
            // Het afvangen van verdere handelingen moet in de GUI class.
            this.sequence = "0";
        }
    }


    // Deze functie moet worden aangeroepen als er ORF's moeten worden gezocht.
    public void findOrfs(){
        orfs = FindOrfInSeq.main(this);
    }
    public HashMap getOrfs(){
        return this.orfs;
    }
}
