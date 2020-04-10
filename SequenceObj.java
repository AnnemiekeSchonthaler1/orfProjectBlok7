import java.util.HashMap;

/**
 * Deze class maakt de sequentie objecten aan die de informatie vasthouden van de sequentie die binnenkomt
 */
public class SequenceObj {
    // Dit is de sequentie die binnenkomt.
    String sequence;
    //Dit zijn de orfs die worden gevonden. Het is een hashmap met als key een id en als value een lijst met het begin
    // en het einde van het orf (de locatie)
    HashMap orfs;

    /**
     * Dit is de constructor van sequenceObj.
     * De constructor neemt niets aan, het wordt gevuld met setters om de data te valideren.
     */
    public SequenceObj() {
    }

    // Dit haalt de sequentie op
    public String getSequence() {
        return sequence;
    }

    /**
     * Deze functie set de sequentie. Hij controleert ook of de sequentie uit "ATGCN" bestaat.
     * @param sequence Dit is de sequentie.
     */
    public void setSequence(String sequence) {
        String sequenceWithoutN = sequence.replace("\n", "");
        // Dit controleert of er ALLEEN ATGU in de sequentie zit en dus of het een geldige seq is
        String regex = "[ATGCN]*";
        if (sequenceWithoutN.matches(regex)) {
            // Als dit zo is slaat het de sequentie op in de variabele
            this.sequence = sequenceWithoutN;
        } else {
            // Als de sequentie niet valide is wordt het "0".
            // Het afvangen van verdere handelingen wordt afgevangen in de GUI class.
            this.sequence = "0";
        }
    }

    /**
     * Deze functie wordt aangeroepen als er ORF's moeten worden gezocht.
     */
    public void findOrfs() {
        // Hij roept een andere class aan die de ORF's gaat zoeken en retourneert.
        orfs = FindOrfInSeq.main(this);
    }

    /**
     * Deze functie retourneert de hashmap met ORF's
     * @return retourneert een Hashmap met daarin een Key en Value: <id, [startpositie, eindpositie]>
     */
    public HashMap getOrfs() {
        return this.orfs;
    }
}
