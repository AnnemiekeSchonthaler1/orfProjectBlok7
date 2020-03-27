import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindOrfInSeq {
    ArrayList<Integer> orf;

    /**
     * Deze vernaggelde main neemt geen argumenten van de commandline aan, maar argumenten van sequenceObj.
     * @param sequenceObjBinnen is het object waar de orfs van moeten worden gevonden
     */
    public static HashMap main(SequenceObj sequenceObjBinnen) {
        String sequence = sequenceObjBinnen.getSequence();
        System.out.println("Ik ben aangeroepen");
        HashMap orfs = findOrf(sequence);
        return orfs;
    }

    private static HashMap<Integer, ArrayList<Integer>> findOrf(String sequence){
        // Dit is de (simpele) regex van een ORF
        String regex = "(ATG){1}.*(TAA|TAG|TGA){1}";
        // deze is om de sequentie in een hashmap te stoppen, zodat er meerdere ORF's op kunnen worden geslagen per seq
        int id = 0;
        // En dit is die hashmap
        HashMap<Integer, ArrayList<Integer>> orfs = new HashMap<>(300);

        // onderstaande code is bedoeld om met regex orf's te zoeken in de sequentie
        // Ik heb een vraagteken toegevoegd omdat ik niet 1 groot ORF wou, maar meerdere
        Pattern pattern = Pattern.compile("(ATG){1}.*?(TAA|TAG|TGA){1}");
        Matcher matcher = pattern.matcher(sequence);
        while (matcher.find()){
            int beginPositie = matcher.start();
            int eindPositie = matcher.end();
            // Hier komen tijdelijk de posities in om het in de hashmap te kunnen stoppen
            ArrayList<Integer> temp = new ArrayList<>(2);
            temp.add(beginPositie);
            temp.add(eindPositie);

            orfs.put(id, temp);
            id++;
        }
        return orfs;
    }
}
