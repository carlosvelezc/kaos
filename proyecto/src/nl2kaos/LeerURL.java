package nl2kaos;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

/**
 *
 * @author Unalmed
 */
public class LeerURL {
    private static ArrayList<String> allLines=new ArrayList<String>();

//    public static void main(String args[]){
//    	try{ int n=classifyAg("correr",true);}
//    	catch(Exception e){}
//    }
    
    public static int classifyAg(String word, boolean proxy) throws Exception {
        if(proxy){
        	System.setProperty("http.proxyHost", "proxym.unalmed.edu.co");
            System.setProperty("http.proxyPort", "8080");
        }
    	 
    	//String word="componente";
    	/*Ejemplos humanos: secretario (no secretaria, pues no es el lema), ingeniero, 
    	director, abogado, ...
    	Ejemplos software: componente, controlador, sistema, software, módulo, ... pero no computador, porque está mal
    	etiquetada.*/
        //System.out.println("dentro dentro: "+word);
        URL oracle = new URL("http://adimen.si.ehu.es/cgi-bin/wei/public/wei.consult.perl?item="+word+"&button1=Look_up&metode=Word&pos=Nouns&llengua=Spanish_3.0&search=has_hyperonym&estructura=Spanish_3.0&glos=Gloss&levin=1&spa-30=Spanish_3.0");
        BufferedReader in = new BufferedReader(
        new InputStreamReader(oracle.openStream()));

        String inputLine;
        allLines.clear();
        while ((inputLine = in.readLine()) != null){
        	//System.out.println(inputLine);
        	allLines.add(inputLine);
        }
        in.close();
        for(int i=0;i<allLines.size();i++){
        	if(allLines.get(i).contains("Artifact") || allLines.get(i).contains("artifact")){
        		//System.out.println("SOFTWAREEEEEEEEEEEEEEEEEEEEEEEEEEE");
        		//allLines.clear();
        		return 2;
        	}else if(allLines.get(i).contains("Human") || allLines.get(i).contains("person")){
        		//System.out.println("HUMANOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
        		//allLines.clear();
        		return 1;
        	}
        	//System.out.println(allLines.get(i));
        }
        allLines.clear();
        return 0;
    }
    
    
}