/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eggweb.inventario;

/**
 *
 * @author carna
 */
public class ProdottiClass {
    private String nome;
    private int id_cat;
    private float vendita;
    private float acquisto;
    private int quantita;
    private String descrizione;
    private String codice;
    
    public ProdottiClass(String nome, int id_cat, float vendita, float acquisto, int quantita, String descrizione, String codice){
        this.nome = nome;
        this.id_cat = id_cat;
        this.vendita = vendita;
        this.acquisto = acquisto;
        this.descrizione = descrizione;
        this.codice = codice;
    }
}
