from Bio.Blast import NCBIWWW, NCBIXML

keyword="""CATATCATTAGCCAAGAAAGAGGGAAAAAGGAAGCTTTTGGAACTTTAGGAATAATTTATGCTATACTAG
CTATTGGTTTATTAGGATTTGTTGTATGAGCCCACCATATATTTACTGTAGGTATAGATGTTGATACACG
AGCCTATTTCACTTCTGCTACTATAATTATTGCAGTTCCTACAGGAATTAAAATTTTTAGATGATTGGCC
ACTCTTCATGGAACTCAGCTTAACTATAGCCCTTCAATACTTTGAGCTTTAGGGTTTGTCTTTCTTTTTA
CAGTGGGGGGACTTACTGGAGTTATTCTTGCTAATTCTTCTATTGATATTATTCTTCATGATACTTATTA
TGTTGTAGCTCATTTTCATTATGTTTTATCTATAGGGGCAGTATTTGCTATTATGGCGGGCCTTGTTCAT
TGGTTTCCTTTATTTACAGGATTAATTCTTAGTCCAACTTTATTAAAAATTCAATTTTTTACTATATTTA
TTGGGGTAAATTTAACTTTTTTCCCCCAACATTTCTTGGGCCTTTCAGGAATACCCCGCCGATACTCTGA
TTATCCTGATGCATATATACAATGAAATATCATTTCATCAATTGGGTCTCTAATTTCATTAATTAGAGTT
TTTATACTCCTTTATACTATTTGAGAGAGATTTATTTCTAACCGAAAAAGAATTTTCCCATTAAATATGC
CTTCTTCAATTGAATGATTCCAG"""


def do_blast(keyword):

    html_results =""
    results_handle = NCBIWWW.qblast("blastn", "nr", keyword)
    print (results_handle)
    blast_records = NCBIXML.parse(results_handle)
    blast_record = next(blast_records)

    for alignment in blast_record.alignments:
        for hsp in alignment.hsps:
            align_coverage = round(hsp.align_length / len(keyword),3)
            html_results += """[
             sequence_title:  {},   
             length:    {},
             E-value:   {},
             Coverage:  {}] 
             """.format(alignment.title, alignment.length, hsp.expect,(align_coverage*100))
    html_results += """</body>"""
    print( """keyword{}: data:{} """.format(keyword,html_results))

do_blast(keyword)