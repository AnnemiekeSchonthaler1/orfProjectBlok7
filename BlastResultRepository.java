//package Blok7ApplicatieORF;
//
//import java.sql.*;
//
//public class BlastResultRepository {
//
//    public static void main(String args[]) {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            Connection con = DriverManager.getConnection(
//                    "jdbc:mysql://hannl-hlo-bioinformatica-mysqlsrv.mysql.database.azure.com:3306/rucia?serverTimezone=UTC",
//                    "rucia@hannl-hlo-bioinformatica-mysqlsrv",
//                    "kip");
////here sonoo is database name, root is username and password
//            //for(blast in blastList){
//            Statement stmt = con.prepareStatement("insert into blast_res (column1, column2) values (?, ?)");
//            stmt.execute(//blast.id, blast.name);
//                    con.close();
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//    }
//}
//}
//
///*
//insert into sequence(  {seq_id } , {seq_varchar } )
//
//insert into ORF( {ORF_id },{ Sequence_ORF},{Sequence_sequence_id})
//
//insert into Blast_res ( { description}, {coverage},{e_value}, {loc_start},{Loc_end},{blast_id},{ORF_ORF_id})
// */