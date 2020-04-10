import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deze class zoekt de ORF's in de sequentie.
 */
public class FindOrfInSeq {

    /**
     * Deze methode neemt geen argumenten van de commandline aan, maar argumenten van sequenceObj.
     * @param sequenceObjBinnen is het object waar de orfs van moeten worden gevonden.
     */
    public static HashMap main(SequenceObj sequenceObjBinnen) {
        // Dit is de sequentie waarin de ORF's moeten worden gezocht
        String sequence = sequenceObjBinnen.getSequence();
        // Dit roept de functie aan die ORF's moet gaan zoeken
        HashMap orfs = findOrf(sequence);

        // Aan het einde retourneert dit de gevonden ORF's
        return orfs;
    }

    /**
     * Deze functie gaat de ORF's zoeken.
     * @param sequence is de sequentie waarin ORF's moeten worden gezocht.
     * @return retourneert een Hashmap met daarin een Key en Value: <id, [startpositie, eindpositie]>
     */
    private static HashMap<Integer, ArrayList<Integer>> findOrf(String sequence){
        // dit id is om de sequentie in een hashmap te stoppen, zodat er meerdere ORF's op kunnen worden geslagen per seq
        int id = 0;
        // En dit is die Hashmap
        HashMap<Integer, ArrayList<Integer>> orfs = new HashMap<>(300);

        // onderstaande code is bedoeld om met regex orf's te zoeken in de sequentie
        // Het vraagsteken is toegevoegd om meerdere ORFs te zoeken i.p.v. één groot ORF.
        Pattern pattern = Pattern.compile("(ATG){1}.*?(TAA|TAG|TGA){1}");
        // Onderstaande code is om de regex te laten zoeken op de sequentie. Als een match is gevonden wordt deze in de
        // orfs Hashmap gestopt.
        Matcher matcher = pattern.matcher(sequence);
        while (matcher.find()){
            int beginPositie = matcher.start();
            int eindPositie = matcher.end();
            // Hier komen tijdelijk de posities in om het in de Hashmap te kunnen stoppen
            ArrayList<Integer> temp = new ArrayList<>(2);
            temp.add(beginPositie);
            temp.add(eindPositie);
            orfs.put(id, temp);
            id++;
        }
        return orfs;
    }
}
