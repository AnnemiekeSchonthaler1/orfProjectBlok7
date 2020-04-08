from Bio.Blast import NCBIWWW, NCBIXML
import sys

sequence=str(sys.argv[0])

def do_blast(sequence):
    print(sequence)
    html_results =""
    results_handle = NCBIWWW.qblast("blastn", "nr", sequence)
    print (results_handle)
    blast_records = NCBIXML.parse(results_handle)
    blast_record = next(blast_records)

    print("It worky")
    for alignment in blast_record.alignments:
        for hsp in alignment.hsps:
            align_coverage = round(hsp.align_length / len(sequence),3)
            html_results += """[
             sequence_title:  {},
             length:    {},
             E-value:   {},
             Coverage:  {}]
             """.format(alignment.title, alignment.length, hsp.expect,(align_coverage*100))
    html_results += """</body>"""
    print( """sequence{}: data:{} """.format(sequence,html_results))

do_blast(sequence)