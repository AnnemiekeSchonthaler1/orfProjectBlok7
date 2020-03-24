import java.util.ArrayList;


public class sequenceObj {
    String sequence;
    ArrayList<Integer> orfPosities;

    /**
     * Dit is de constructor van sequenceObj.
     * De constructor neemt niets aan, het wordt aangemaakt met setters om de data te valideren.
     */
    public sequenceObj() {
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        // Dit controleert of er ALLEEN ATGU in de sequentie zit en dus of het een geldige seq is
        String regex = "[AUGC]{"+sequence.length()+"}";
        if (sequence.matches(regex)){
            this.sequence = sequence;
            System.out.println("Dit is een sequentie");
        } else {
            this.sequence = "0";
        }
    }

    public ArrayList<Integer> getOrfPosities() {
        return orfPosities;
    }

    public void setOrfPosities(ArrayList<Integer> orfPosities) {
        this.orfPosities = orfPosities;
    }
}
