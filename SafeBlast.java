///* Deze feature is niet gelukt. Het is niet gelukt om in de queries argumenten mee te geven. Door gebrek
//aan tijd zou dit in een later stadium moeten worden ge-update.
// */
//
//private voidz safeBlast(String ORFSequence,ArrayList tempSaveBlastData) {
//        // function to save the data to the database, makes use of 3 queries to do so
//        try {
//        Class.forName("com.mysql.jdbc.Driver");
//        Connection con = DriverManager.getConnection(
//        //connection requirements for the connection with the database
//
//        "jdbc:mysql://hannl-hlo-bioinformatica-mysqlsrv.mysql.database.azure.com:3306/rucia?serverTimezone=UTC",
//        "rucia@hannl-hlo-bioinformatica-mysqlsrv",
//        "kip");
///* Queries:
//insert into sequence(  {seq_id } , {seq_varchar } )
//insert into ORF( {ORF_id },{ Sequence_ORF},{Sequence_sequence_id})
//insert into Blast_res ( { description}, {coverage},{e_value}, {loc_start},{Loc_end},{blast_id},{ORF_ORF_id})
// */
//        String FullSequence = sequenceObj.getSequence();
//        // String ORFSequence
//
//
//        Statement stmt = con.prepareStatement("insert into sequence (Sequence_id, Sequence) values ()");// in values : auto_increment, FullSequence
//        Statement stmt2 = con.prepareStatement("insert into ORF (ORF_id, Sequence_ORF, Sequence_Sequence_id) values ()");// in values : auto_increment,sequentieOrf, sequence ( fullseq id)
//        stmt.execute(//excuteSequence);
//        stmt2.execute(//excuteORF);
//        int i;
//        for(i=0;i<5;i=0) {
//        Statement stmt3 = con.prepareStatement("insert into blast_res (description, coverage, e_value, location_start, location_end, Blast_id, ORF_ORF_id) values ()");
//        // description, coverage, e_value, locstart, locend, blast ID, ORF_ORF_id ( id ORF)
//        stmt3.execute(//excuteBlast);
//        }
//        con.close();
//        } catch (Exception e) {
//        System.out.println(e);
//        }}