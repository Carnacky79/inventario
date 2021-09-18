/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eggweb.inventario;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carna
 */
public class Db {
    private Connection c = null;
    
    public void openConnection(){
      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:mydb.db");
      } catch ( Exception e ) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         System.exit(0);
      }
    }
    
    public ArrayList<String> seleziona(String select) throws SQLException{
        openConnection(); 
       
        ArrayList<String> result = new ArrayList<String>();
       
        Statement stmt=c.createStatement();  
        String SQL_Query = "select prodotti.nome_prodotto, prodotti.quantita, prodotti.prezzo_vendita, prodotti.prezzo_acquisto, categorie.nome_categoria" +
" from prodotti inner join categorie on prodotti.id_categoria = categorie.id order by prodotti.nome_prodotto ASC";
        
        select = select.toLowerCase();
        switch(select){
            case "alfabetico":
                SQL_Query = "select prodotti.nome_prodotto, prodotti.quantita, prodotti.prezzo_vendita, prodotti.prezzo_acquisto, categorie.nome_categoria" +
" from prodotti inner join categorie on prodotti.id_categoria = categorie.id order by prodotti.nome_prodotto ASC";
                break;
            case "alfabetico inv":
                SQL_Query = "select prodotti.nome_prodotto, prodotti.quantita, prodotti.prezzo_vendita, prodotti.prezzo_acquisto, categorie.nome_categoria" +
" from prodotti inner join categorie on prodotti.id_categoria = categorie.id order by prodotti.nome_prodotto DESC";
                break;
            case "prezzo più alto":
                SQL_Query = "select prodotti.nome_prodotto, prodotti.quantita, prodotti.prezzo_vendita, prodotti.prezzo_acquisto, categorie.nome_categoria" +
" from prodotti inner join categorie on prodotti.id_categoria = categorie.id order by prodotti.prezzo_vendita DESC";
                break;
            case "prezzo più basso":
                SQL_Query = "select prodotti.nome_prodotto, prodotti.quantita, prodotti.prezzo_vendita, prodotti.prezzo_acquisto, categorie.nome_categoria" +
" from prodotti inner join categorie on prodotti.id_categoria = categorie.id order by prodotti.prezzo_vendita ASC";
                break;
            case "num. prodotti +":
                SQL_Query = "select prodotti.nome_prodotto, prodotti.quantita, prodotti.prezzo_vendita, prodotti.prezzo_acquisto, categorie.nome_categoria" +
" from prodotti inner join categorie on prodotti.id_categoria = categorie.id order by prodotti.quantita DESC";
                break;
            case "num. prodotti -":
                SQL_Query = "select prodotti.nome_prodotto, prodotti.quantita, prodotti.prezzo_vendita, prodotti.prezzo_acquisto, categorie.nome_categoria" +
" from prodotti inner join categorie on prodotti.id_categoria = categorie.id order by prodotti.quantita ASC";
                break;
        }
        
        
        ResultSet rs = stmt.executeQuery(SQL_Query);  
        
        String record = "";
        
        while(rs.next()){
            record = rs.getString(1) + "     " + Integer.toString(rs.getInt(2)) + "     €" + Float.toString(rs.getFloat(3)) + "     €" +  Float.toString(rs.getFloat(4)) + "     " + rs.getString(5);
            result.add(record);
        }
        
        closeConnection();
        
        return result;
    }
    
    public ArrayList<String> selezionaCategorie() throws SQLException{
        openConnection(); 
       
        ArrayList<String> result = new ArrayList<String>();
       
        Statement stmt=c.createStatement();  
        String SQL_Query = "select *" + " from categorie";
        
        ResultSet rs = stmt.executeQuery(SQL_Query);  
        String record = "";
        
        while(rs.next()){
            String id_categoria = Integer.toString(rs.getInt(1));
            if(id_categoria.length() == 1){
                id_categoria = "0"+id_categoria;
            }
            
            record = id_categoria + " -- " + rs.getString(2);
            result.add(record);
        }
        
        closeConnection();
        
        return result;
    
    }
    
    public void closeConnection(){
        try {
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String selectNomeFromCodice(String cod) throws SQLException {
        String nome = "";
 
            openConnection();
            String SQL_Query = "SELECT DISTINCT " + "prodotti.nome_prodotto" + " FROM " +"prodotti" +" WHERE " + " prodotti.cod_prodotto = " + cod + "";
            
            
            Statement stmt = c.createStatement();
            
            
            ResultSet rs = stmt.executeQuery(SQL_Query);

            while(rs.next()){
                nome = rs.getString(1);
            }
    
            closeConnection();
            
        return nome;
    }
    
    public boolean vendiFromCodice(String cod, int quantita) throws SQLException{
        boolean result = false;
        int quantitaInMagazzino = selezionaQuantitaFromCodice(cod);
        
        if(quantita > quantitaInMagazzino)
            return result;
        
        openConnection();
        
        
        String SQL_Query = "insert into vendite (cod_prodotto, quantita_venduta) values(?,?)";
        String SQL_subtract = "update prodotti set quantita = quantita - ?";
                
        PreparedStatement stmt=c.prepareStatement(SQL_Query);  
        PreparedStatement stmt2=c.prepareStatement(SQL_subtract); 
        
        stmt.setString(1,cod);  
        stmt.setInt(2, quantita);
        
        stmt2.setInt(1, quantita);
        
        int i = stmt.executeUpdate(); 
        int i2 = stmt2.executeUpdate();
        
        closeConnection();
        
        if(i > 0 && i2 > 0){
            result = true;
        }
        
        return result;
    }
    
    private int selezionaQuantitaFromCodice(String cod) throws SQLException{
        int quantita = 0;
        openConnection();
        
        String SQL_Query = "SELECT DISTINCT " + "prodotti.quantita" + " FROM " +"prodotti" +" WHERE " + " prodotti.cod_prodotto = " + cod + "";
        
        Statement stmt = c.createStatement();
            
            
            ResultSet rs = stmt.executeQuery(SQL_Query);

            while(rs.next()){
                quantita = rs.getInt(1);
            }
    
            closeConnection();
        
        return quantita;
    }
    
    public boolean aggiuntaProdotti(String nome, int id_cat, float vendita, float acquisto, int quantita, String desc, String cod) throws SQLException{
        boolean result = false;
        
        if(desc.length() == 0){
            desc = "Nessun descrizione";
        }
        
        openConnection();
        
        String SQL_Query = "insert into prodotti (nome_prodotto, id_categoria, prezzo_vendita, prezzo_acquisto, quantita, desc_prodotto, cod_prodotto) values(?,?,?,?,?,?,?)";
        
        PreparedStatement stmt=c.prepareStatement(SQL_Query);  
        
        stmt.setString(1,nome);  
        stmt.setInt(2, id_cat);
        stmt.setFloat(3, vendita);
        stmt.setFloat(4, acquisto);
        stmt.setInt(5, quantita);
        stmt.setString(6, desc);
        stmt.setString(7, cod);
        
        int i = stmt.executeUpdate();  
        
        closeConnection();
        
        if(i > 0){
            result = true;
        }
        
        return result;
    }
    
}
