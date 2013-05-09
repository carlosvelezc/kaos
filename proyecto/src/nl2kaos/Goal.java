package nl2kaos;

import java.util.ArrayList;

/**
 *
 * @author personal
 */
public class Goal {
    //Un agente realiza operaciones
    //Un agente tiene nombre, definición y una o varias operaciones
    private String lemma;
    private String def;
    private String category;
    private int forma;
    
    
    public Goal(String cat) {
            super();
            category=cat;
    }
    
    
    public Goal(String def, String cat, int forma) {
            super();
            this.def = def;
            category=cat;
            this.forma=forma;
    }

    public String getCategory() {
            return category;
    }

    /*
    public void setCategory(int category) {
        switch(category){
            case 0: this.category="Mantener";
                break;
            case 1: this.category="Alcanzar";
                break;
            case 2: this.category="Evitar";
                break;
            case 3: this.category="Parar";
                break;
                
        }
    }
    */
    
    public void setCategory(String c){
        category=c;
    }
    
    
    
    public int getForma() {
            return forma;
    }
    public void setForma(int forma) {
            this.forma = forma;
    }
    public String getDef() {
            return def;
    }
    public void setDef(String def) {
            this.def = def;
    }
    
}
