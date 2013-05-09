/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl2kaos;



import java.io.*;
import java.net.URL;
import java.util.*;

import javax.xml.datatype.DatatypeConstants.Field;
import edu.upc.freeling.*;

/**
 *
 * @author unalmed
 */
public class NL2KAOS {

    /**
     * @param args the command line arguments
     */


  // Modify this line to be your FreeLing installation directory
  private static final String FREELINGDIR = "/home/unalmed/Documentos/freeling-3.0";
  private static final String DATA = "/usr/local/share/freeling/";
  private static final String LANG = "es";
  private ArrayList<String> words=new ArrayList<String>();
  private ArrayList<Agent> concepts=new ArrayList<Agent>();
  private ArrayList<String> stopwords=new ArrayList<String>();//Lista predefinida de stopwords español
  private static ArrayList<String> subjs=new ArrayList<String>();
  private static ArrayList<String> vbs=new ArrayList<String>();
  private static ArrayList<String> objs=new ArrayList<String>();
  private static ArrayList<Agent> ags=new ArrayList<Agent>();
  private static ArrayList<Goal> gls=new ArrayList<Goal>();
  private ArrayList<String> simwords=new ArrayList<String>();
  private ArrayList<String> fullList=new ArrayList<String>();
  private static boolean proxy=true;
  private int total=0;
  
  
  public NL2KAOS(){
	  File file = new File("sw");
      try {
          Scanner scanner = new Scanner(file);
          while (scanner.hasNextLine()) {
              String line = scanner.nextLine();
              //System.out.println("@@@@   "+line);
              stopwords.add(line);
          }
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      }
	  
  }
  
  public ArrayList<Goal> getGoals(){
      return gls;
  }
  
  public ArrayList<Agent> getAgents(){
      return ags;
  }
  

  public void procesar( String text ) throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	  
	  System.setProperty( "java.library.path", "/home/unalmed/Documentos/freeling-3.0/APIs/java" );
	  //System.setProperty( "java.library.path", "/usr/local/share/freeling" );
	  java.lang.reflect.Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
	  fieldSysPath.setAccessible( true );
	  fieldSysPath.set( null, null );
  
	  System.loadLibrary( "freeling_javaAPI" );

    Util.initLocale( "default" );

    NL2KAOS a = new NL2KAOS();
    
    // Create options set for maco analyzer.
    // Default values are Ok, except for data files.
    MacoOptions op = new MacoOptions( LANG );

    op.setActiveModules(
      false, true, true, true, true, true, true, true, true, true, false );

    op.setDataFiles(
      "",
      DATA + LANG + "/locucions.dat",
      DATA + LANG + "/quantities.dat",
      DATA + LANG + "/afixos.dat",
      DATA + LANG + "/probabilitats.dat",
      DATA + LANG + "/dicc.src",
      DATA + LANG + "/np.dat",
      DATA + "common/punct.dat",
      DATA + LANG + "/corrector/corrector.dat" );

    // Create analyzers.
    Tokenizer tk = new Tokenizer( DATA + LANG + "/tokenizer.dat" );
    Splitter sp = new Splitter( DATA + LANG + "/splitter.dat" );
    Maco mf = new Maco( op );

    HmmTagger tg = new HmmTagger( LANG, DATA + LANG + "/tagger.dat", true, 2 );
    ChartParser parser = new ChartParser(
      DATA + LANG + "/chunker/grammar-chunk.dat" );
    DepTxala dep = new DepTxala( DATA + LANG + "/dep/dependences.dat",
      parser.getStartSymbol() );
    Nec neclass = new Nec( DATA + LANG + "/nec/nec-ab.dat" );

    UkbWrap dis = new UkbWrap( DATA + LANG + "/ukb.dat" );
    
   

    // Instead of "UkbWrap", you can use a "Senses" object, that simply
    // gives all possible WN senses, sorted by frequency.
    // Senses dis = new Senses(DATA+LANG+"/senses.dat");
    //
    // Make sure the encoding matches your input text (utf-8, iso-8859-15, ...)
    //BufferedReader input = new BufferedReader(
     // new InputStreamReader( System.in, "utf-8" ) );
    String line = text;
    Senses sen = new Senses(DATA+"es/senses.dat");
    Word w = new Word(line);
    
    
    
    if( line != null ) {
      // Extract the tokens from the line of text.
      ListWord l = tk.tokenize( line );

      // Split the tokens into distinct sentences.
      ListSentence ls = sp.split(l, false );
      

      // Perform morphological analysis
      mf.analyze( ls );

      // Perform part-of-speech tagging.
      tg.analyze( ls );

      // Perform named entity (NE) classificiation.
      neclass.analyze( ls );
      
      for(int i=0;i<ls.size();i++){
    	  for(int j=0;j<ls.get(i).size();j++){
    		  //System.out.println("#####   "+ls.get(i).get(j).getForm());
    		  //System.out.println("#####2   "+ls.get(i).get(j).getShortTag());
    		  if(ls.get(i).get(j).getForm().equals("FIN")){
    			  //a.finalizar();
    			  //System.exit(0);
    		  }
    		  if(!ls.get(i).get(j).getShortTag().startsWith("F")){
    			  a.words.add(ls.get(i).get(j).getLemma());
    		  }
    	  }
      }

      // sen.analyze(ls);
      dis.analyze( ls );
      printResults( ls, "tagged" );
      System.out.println("*************");
      sen.analyze(ls);
      
      

      // Chunk parser
      parser.analyze( ls );
      printResults( ls, "parsed", line );//POR RESOLVER: varias frases en una linea
      
      // Dependency parser
      dep.analyze( ls );
      printResults( ls, "dep");
      a.printSubjs();
      //a.printAll();
     // line = input.readLine();
      a.finalizar();
    }
  }

  public void finalizar() {
	  
	  System.out.println("- PALABRAS: ");
	  for(int i=0;i<words.size();i++){
		  if(!stopwords.contains(words.get(i))){
			  //System.out.println(words.get(i));
			  total++;
		  }else{
			  words.remove(i);
			  i--;
		  }
	  }
	  
	  /*
	  String[] words=(String[])this.words.toArray();
	  HashMap<String,Integer> frequencies=new HashMap<String,Integer>();
	  for (String w: Arrays.asList(words)){
	    Integer num=frequencies.get(w);
	    if (num!=null)
	      frequencies.put(w,num+1);
	    else
	      frequencies.put(w,1);
	  }*/
	  /*
	  Iterator iterator = frequencies.keySet().iterator();  
	   
	  while (iterator.hasNext()) {  
	     String key = iterator.next().toString();  
	     String value = frequencies.get(key).toString();  
	     
	     System.out.println("$$$$$  "+key + " " + value);  
	  } 
	  */
	  
	  for(int i=0;i<words.size();i++){
		  concepts.add(new Agent(words.get(i)));
		  for(int j=i+1;j<words.size();j++){
			  if(words.get(i).equals(words.get(j))){
				  concepts.get(i).setFreq(concepts.get(i).getFreq()+1);
				  words.remove(j);
				  j--;
			  }
		  }
	  }
	  float freq=0;
	  for(int i=0;i<concepts.size();i++){
		  freq=concepts.get(i).getFreq()*100/total;
		  System.out.println("$$$ PALABRA: "+concepts.get(i).getName()+", ocurrencias: "+concepts.get(i).getFreq()+", frecuencia: "+freq);
			
	  }
	  printAll();
	  System.out.println("$$$ TOTAL: "+total);
          total=0;
	  
  }
  
  private static void printSenses( Word w ) {
	  String ss = w.getSensesString();
	  ListAnalysis anl = w.getAnalysis();
	    if (anl.size()>0) {
	        Analysis a = anl.get(0);
	        for(int i=1;i<anl.size();i++)    {
	            a = anl.get(i);
	        }
	    }
    // The senses for a FreeLing word are a list of
    // pair<string,double> (sense and page rank). From java, we
    // have to get them as a string with format
    // sense:rank/sense:rank/sense:rank
    // which will have to be splitted to obtain the info.
    //
    // Here, we just output it:
    System.out.print( " +++ " + ss );
  }

  private static void printResults( ListSentence ls, String format ) {
    if( format == "parsed" ) {
      System.out.println( "-------- CHUNKER results -----------" );

      for( int i = 0; i < ls.size(); i++ ) {
        TreeNode tree = ls.get( i ).getParseTree();
        printParseTree( 0, tree );
      }
    }
    else if( format == "dep" ) {
      System.out.println( "-------- DEPENDENCY PARSER results -----------" );

      for( int i = 0; i < ls.size(); i++ ) {
        TreeDepnode deptree = ls.get( i ).getDepTree();
        printDepTree( 0, deptree, false, false );
      }
    }
  }
    
  
  private static void printResults( ListSentence ls, String format, String line ) {

      System.out.println( "-------- TAGGER results -----------" );
      int aux=0;
      // get the analyzed words out of ls.
      for( int i = 0; i < ls.size(); i++ ) {
        Sentence s = ls.get( i );
        aux=-18;
        int forma=0;
          int comienzo=0;
          String verbo="";
        for( int j = 0; j < s.size()-2; j++ ) {//*** Agregué -2 porque a veces sacaba error
          Word w = s.get( j );

         
          printSenses( w );
         
          if(w.getTag().contains("V"))
          {
          
              if(j>0&&(s.get(j-1).getTag().contains("N") && (s.get(j+1).getTag().contains("NC")||s.get(j+1).getTag().contains("DA"))))
              {
                  System.out.println("\nEsta frase tiene la forma 2");
                          forma=2;
                           comienzo=j;
              }
              if(j>0&&(aux==j-1 || (aux==j-2 && s.get(j-1).getTag().contains("CS"))))
              {
                  if(s.get(j+1).getTag().contains("DA")&&s.get(j+2).getTag().contains("NCMS"))
                  {
                      System.out.println("\nEsta frase tiene la forma 1");
                                  forma=1;
                                   comienzo=j;
                  }
                          else{
                                System.out.println("\nEsta frase tiene la forma 4");
                                forma=4;
                                 comienzo=j;
                          }
              }
              else
              {
                  aux=j;
                  if((s.get(j+1).getTag().contains("CS")&&(s.get(j+2).getTag().contains("N") || s.get(j+2).getTag().contains("DA"))&&(s.get(j+3).getTag().contains("V")||s.get(j+3).getTag().contains("N"))))
                  {
                      System.out.println("\nEsta frase tiene la forma 3");
                                  forma=3;
                                   comienzo=j;
                  }
              }
                  verbo=w.getLemma();
             
          }
         
        
          System.out.println();
        }
        line=" ";
         if(forma==1 || forma==2 || forma==3 || forma==4)
          {
              for( int k = comienzo; k < s.size(); k++ )
              {
                  if(k==comienzo)
                    line=line+s.get(k).getLemma();
                  else
                      line=line+" "+s.get(k).getLemma();
              }
               gls.add(new Goal(line,clasificarverbo(verbo),forma));

          }

        System.out.println();
      }
    }

  
  
  
  
  
  
  
  
  /*
    private static void printResults( ListSentence ls, String format, String line ) {

      System.out.println( "-------- TAGGER results -----------" );
      int aux=0;
      // get the analyzed words out of ls.
      for( int i = 0; i < ls.size(); i++ ) {
        Sentence s = ls.get( i );
        aux=-18;
        for( int j = 0; j < s.size()-2; j++ ) {//*** Agregué -2 porque a veces sacaba error
          Word w = s.get( j );

          
          printSenses( w );
          if(w.getTag().contains("V"))
          {
        	int forma=0;  
              if(j>0&&(s.get(j-1).getTag().contains("N") && (s.get(j+1).getTag().contains("NC")||s.get(j+1).getTag().contains("DA"))))
        	  {
        		  System.out.println("\nEsta frase tiene la forma 2");
                          forma=2;
        	  }
        	  if(j>0&&(aux==j-1 || (aux==j-2 && s.get(j-1).getTag().contains("CS"))))
        	  {
        		  if(s.get(j+1).getTag().contains("DA")&&s.get(j+2).getTag().contains("NCMS"))
        		  {
        			  System.out.println("\nEsta frase tiene la forma 1");
                                  forma=1;
        		  }
                          else{
                                System.out.println("\nEsta frase tiene la forma 4");
                                forma=4;
                          }
        	  }
        	  else
        	  {
        		  aux=j;
        		  if((s.get(j+1).getTag().contains("CS")&&(s.get(j+2).getTag().contains("N") || s.get(j+2).getTag().contains("DA"))&&(s.get(j+3).getTag().contains("V")||s.get(j+3).getTag().contains("N"))))
        		  {
        			  System.out.println("\nEsta frase tiene la forma 3");
                                  forma=3;
        		  }
        	  }
        	  if(!s.get(j+1).getTag().contains("V")&&!s.get(j+2).getTag().contains("V"))
        	  {
                      gls.add(new Goal(line,clasificarverbo(w.getLemma()),forma));
                      
        	  }
          }
          System.out.println();
        }

        System.out.println();
      }
    }
    */
  
  private static String clasificarverbo(String verbo)
  {
        String clasificacion="";
        try
        {
            ArrayList<String> allLines=new ArrayList<String>();
            String line="causar/dirigir/formular/componer/disponer/promover/confeccionar/efectuar/hacer/producir/ejecutar/meter/avanzar/fabricar/obrar/crear/formar/preparar/desarrollar/reducir/agrandar/aumentar/decrecer/disminuir/fomentar/mejorar/incrementar/registrar/construir/elaborar/postrar";
            String verboslogro[]=line.split("/");
            line="adminsitrar/avalar/conservar/garantizar/gestionar/guardar/obtener/mantener/ofrecer/preservar/reconocer/prolongar/perdurar";
            String verbosmantener[]=line.split("/");
            line="evitar/prevenir/eludir/obstaculizar/esquivar/sortear";
            String verbosevitar[]=line.split("/");
            line="parar/detener/cesar/finalizar/concluir/terminar/suspender/interrumpir";
            String verbosparar[]=line.split("/");
            if(proxy){
                System.setProperty("http.proxyHost", "proxym.unalmed.edu.co");
                System.setProperty("http.proxyPort", "8080");
            }
            URL oracle = new URL("http://adimen.si.ehu.es/cgi-bin/wei/public/wei.consult.perl?item="+verbo+"&button1=Look_up&metode=Word&pos=Verbs&llengua=Spanish_3.0&search=near_synonym&estructura=Spanish_3.0&glos=Gloss&levin=1&spa-30=Spanish_3.0");
            BufferedReader in = new BufferedReader(
            new InputStreamReader(oracle.openStream()));

            String inputLine;
            allLines.clear();
            while ((inputLine = in.readLine()) != null){
                    //System.out.println(inputLine);
                    allLines.add(inputLine);
            }
            in.close();
            int vl=0;
            for(int j=0;j<verboslogro.length;j++)
            {
                    for(int i=0;i<allLines.size();i++){
                            if(allLines.get(i).contains(verboslogro[j])){
                                    vl++;
                            }
                    }
            }
            int vm=0;
            for(int j=0;j<verbosmantener.length;j++)
            {
                    for(int i=0;i<allLines.size();i++){
                            if(allLines.get(i).contains(verbosmantener[j])){
                                    vm++;
                            }
                    }
            }
            int ve=0;
            for(int j=0;j<verbosevitar.length;j++)
            {
                    for(int i=0;i<allLines.size();i++){
                            if(allLines.get(i).contains(verbosevitar[j])){
                                    ve++;
                            }
                    }
            }
            int vp=0;
            for(int j=0;j<verbosparar.length;j++)
            {
                    for(int i=0;i<allLines.size();i++){
                            if(allLines.get(i).contains(verbosparar[j])){
                                    vp++;
                            }
                    }
            }
            allLines.clear();
            if(vl>vm && vl>vp && vl>ve)
            {
                    System.out.println("el objetivo es de tipo lograr");
                    clasificacion="Lograr";
            }
            else if(vm>vl&&vm>ve&&vm>vp)
            {
                    System.out.println("el objetivo es de tipo mantener");
                    clasificacion="Mantener";
            }
            else if(vp>vm&&vp>ve&&vp>vl)
            {
                    System.out.println("el objetivo es de tipo parar");
                    clasificacion="Parar";
            }
            else if(ve>vm&&ve>vp&&ve>vl)
            {
                    System.out.println("el objetivo es de tipo evitar");
                    clasificacion="Evitar";
            }
            else if(vl==0)
            {
                    System.out.print("No se puede clasificar, no se encontraron sinonimos.");
                    clasificacion=null;
            }
            else
            {
                    System.out.print("No se puede clasificar, tiene ambiguedad en mas de un tipo de categoria de verbo");
                    clasificacion=null;

            }
            int totalv=vl+vm+ve+vp;
            vl=100*vl/totalv;
            vm=100*vm/totalv;
            vp=100*vp/totalv;
            ve=100*ve/totalv;
            System.out.println("Lograr:"+vl+"% Mantener:"+vm+"% Evitar: "+ve+"% Parar: "+vp+"%");


      }
      catch(Exception e)
      {
              System.out.print("Error");
      }
    return clasificacion;
  }

  public void printSubjs(){//Imprime sujetos
	  // *******ADFFFFFFFFFFFFF  YA NO HAY NADA AQUÍ ******************
  }
  public void printVbs(int n){//Imprime verbos
	  System.out.print(vbs.get(n));
  }
  
  public void printObjs(int n){//Imprime objetos
	  System.out.print(" "+objs.get(n));
  }
  
  public void printAll(){//Imprime sujetos
	  for(int i=0;i<ags.size()-1;i++){
		  for(int j=i+1;j<ags.size();j++){
			  if(ags.get(i).getName().equals(ags.get(j).getName())){
				  System.out.print("Se borra agente: "+ags.get(j).getName());
				  ags.remove(j);
				  j--;
			  }
		  }
		  //System.out.print(". Operación: ");
		  try{printVbs(i);}catch(IndexOutOfBoundsException e){
			  System.out.print("Un agente no puede no hacer nada. No se considera agente: "+subjs.get(i));
			  //subjs.remove(i);
			  //i--;
		  }
		  //printObjs(i); //Corregir
		  System.out.println();
	  }
	  System.out.println("Quedaron los agentes:");
	  for(int i=0;i<ags.size();i++){
		  System.out.println(ags.get(i).getName());
	  }
          
          
	  
	  
	  System.out.println("&&&&&&&&&&&&&&& Cargando... espere por favor &&&&&&&&&&&&&&&&&&&&&&&&&");
	  
	  /*
	  for(int i=0;i<subjs.size();i++){
		  ags.add(new Agent(subjs.get(i)));  
	  }*/
	 /* int nSubjs=subjs.size()-1;
	  int nVbs=vbs.size()-1;
	  int nObjs=objs.size()-1;
	  if(nSubjs>=0){
		  ags.add(new Agent(subjs.get(nSubjs) /*, new Operation(vbs.get(nVbs),objs.get(nObjs))*));
	  }else{
		  System.out.println("No se encontró ningún agente");
		  return;
	  }
		*/  
	 // System.out.println("Agente: "+ags.get(ags.size()-1).getName());
//	  readOntConc();
	  //int nAgs=ags.size()-1;
	  //System.out.println("Agente: "+ags.get(nAgs).getName()+" "+ags.get(nAgs).getOp(0).getVerbo()+" "+ags.get(nAgs).getOp(0).getObjeto());
          System.out.println("AGENTES IDENTIFICADOS:");
	  for(int i=0; i<ags.size();i++){
		  double freq=0;
		  
		  //System.out.println("LEWMMMA "+lemma+" word"+name.getLemma()+" *"+ags.get(i).getName());
		  for(int j=0;j<concepts.size();j++){
			  if(ags.get(i).getLemma().equals(concepts.get(j).getName())){
				  freq=concepts.get(j).getFreq();
				  j=concepts.size();
			  }
		  }
		  int cat=ags.get(i).getCategory();
		  if(cat==-1){
			  cat=classify(ags.get(i).getLemma());
			  ags.get(i).setCategory(cat);
		  }
                  //ags.get(i).getFreq()
		  switch(cat){
		  		case -1: System.out.println("Agente sin clasificar: "+ags.get(i).getLemma()+" Freq "+freq*100/total);break;  
		  		case 0: System.out.println("Agente sin clasificar: "+ags.get(i).getLemma()+" Freq "+freq*100/total);break;
		  		case 1: System.out.println("Agente de ambiente: "+ags.get(i).getLemma()+" Freq "+freq*100/total);break;
		  		case 2: System.out.println("Agente de software: "+ags.get(i).getLemma()+" Freq "+freq*100/total);break;
		  }
		  
		  
		  /*System.out.println(". Operación: "/*+ags.get(i).getOp(0).getVerbo()+" "+ags.get(i).getOp(0).getObjeto()*);
		  String name=ags.get(i).getName();
		  boolean related;
		  if(name.contains(" ")){
			  related=similarity(name.substring(0,name.indexOf(' ')));  
		  }else{
			  related=similarity(name);
		  }
		  
		  if(related){
			  System.out.println("Sí se relacionan!!");
		  }else{
			  System.out.println("NO se relacionan XX");
		  }*/
		  //similarity(ags.get(i).getName());
	  }
          System.out.println("OBJETIVOS IDENTIFICADOS:");
	  for(int i=0;i<gls.size();i++){
		  System.out.println((i+1)+". "+gls.get(i).getDef()+"\n de tipo "+gls.get(i).getCategory()+", encontrado en frase de forma "+gls.get(i).getForma());
	  }
	  //System.out.println();
  }
  
  public void readOntConc(){
	  //Leer conceptos ontologia
	  /*Consultas cons=new Consultas();
	  concepts=cons.print();
	  for(int i=0;i<concepts.size();i++){
		  try{
		  similarOnto(concepts.get(i));
		  }catch(Exception e){}
	  }*/
  }
  
  public void similarOnto(String w){
	  //Traer 80 conceptos más similares
	 /* try{
		  simwords=Disco2.simWords(w);
		  for(int i=0;i<simwords.size();i++){
			  fullList.add(simwords.get(i));
		  }
	  }catch(Exception e){}*/
  }
  public boolean similarity(String w){
	  return fullList.contains(w);
  }
  
  
  public int classify(String ag){
	  try{
		  int cat;
		  if(ag.contains(" ")){
			  cat=LeerURL.classifyAg(ag.substring(0,ag.indexOf(' ')), proxy);
		  }else{
			  cat=LeerURL.classifyAg(ag, proxy);
		  } 
		  return cat;
		  
	  }
	  catch(Exception e){
		  return -1;
	  }
	  
  }  
  private static void printParseTree( int depth, TreeNode tr ) {
    Word w;
    TreeNode child;
    long nch;

    // Indentation
    for( int i = 0; i < depth; i++ ) {
      System.out.print( "  " );
    }

    nch = tr.numChildren();

    if( nch == 0 ) {
      // The node represents a leaf
      if( tr.getInfo().isHead() ) {
        System.out.print( "+" );
      }
      w = tr.getInfo().getWord();
      System.out.print(
        "(" + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
      printSenses( w );
      System.out.println( ")" );
      
      if(w.getTag().contains("V"))
      {
    	  System.out.println("Encontre un verbo: "+ w.getLemma() +" "+w.getTag());
      }
      if(w.getTag().contains("N"))
      {
    	  if(w.getTag().contains("NP"))
          {
        	  System.out.println("Encontre un nombre propio: "+ w.getLemma() +" "+w.getTag());
          }
    	  else
    	  {  
    		  System.out.println("Encontre un nombre común: "+ w.getLemma() +" "+w.getTag());
    	  }
      }
    }
    else
    {
      // The node probably represents a tree
      if( tr.getInfo().isHead() ) {
        System.out.print( "+" );
      }

      System.out.println( tr.getInfo().getLabel() + "_[" );

      for( int i = 0; i < nch; i++ ) {
        child = tr.nthChildRef( i );

        if( child != null ) {
          printParseTree( depth + 1, child );
        }
        else {
          System.err.println( "ERROR: Unexpected NULL child." );
        }
      }
      for( int i = 0; i < depth; i++ ) {
        System.out.print( "  " );
      }

      System.out.println( "]" );
    }
  }

  private static void printDepTree( int depth, TreeDepnode tr, boolean b1, boolean b2) {
    TreeDepnode child = null;
    TreeDepnode fchild = null;
    Depnode childnode;
    long nch;
    int last, min;
    Boolean trob;

    for( int i = 0; i < depth; i++ ) {
      System.out.print( "  " );
    }

    System.out.print(
      tr.getInfo().getLinkRef().getInfo().getLabel() + "/" +
      tr.getInfo().getLabel() + "/" );
    
	if(tr.getInfo().getLabel().equals("co-n") && !b2){
		subjs.add(tr.getInfo().getWord().getForm());
		ags.add(new Agent(tr.getInfo().getWord().getForm(), tr.getInfo().getWord().getLemma()));
	}
	if(tr.getInfo().getLabel().equals("subj")){
		b1=true;
	}else if(tr.getInfo().getLabel().equals("dobj")){
		b1=false;
		b2=true;
	}
	if(tr.getInfo().getLabel().equals("subj") && !tr.getInfo().getLinkRef().getInfo().getLabel().equals("coor-n") && tr.getInfo().getLinkRef().getInfo().getLabel().equals("sn") && !b2){
		subjs.add(tr.getInfo().getWord().getForm());
		ags.add(new Agent(tr.getInfo().getWord().getForm(), tr.getInfo().getWord().getLemma()));
	}
	if(tr.getInfo().getLabel().equals("obj-prep") && tr.getInfo().getLinkRef().getInfo().getLabel().equals("sn") && b1){
		int totalSbj=subjs.size()-1;
		subjs.set(totalSbj,subjs.get(totalSbj)+" de "+tr.getInfo().getWord().getForm());
		ags.set(totalSbj, new Agent(ags.get(totalSbj).getName()+" de "+tr.getInfo().getWord().getForm(), ags.get(totalSbj).getLemma()));
	}
	if((tr.getInfo().getLabel().equals("sn-mod") && tr.getInfo().getLinkRef().getInfo().getLabel().equals("w-ms")) ||((tr.getInfo().getLabel().equals("modnomatch") && tr.getInfo().getLinkRef().getInfo().getLabel().equals("sn"))) && b1){
		int totalSbj=subjs.size()-1;
		subjs.set(totalSbj,subjs.get(totalSbj)+" "+tr.getInfo().getWord().getForm());
		ags.set(totalSbj, new Agent(ags.get(totalSbj).getName()+" "+tr.getInfo().getWord().getForm(), ags.get(totalSbj).getLemma()));
	}
	if(tr.getInfo().getLabel().equals("adj-mod") && b1){
		int totalSbj=subjs.size()-1;
		subjs.set(totalSbj,subjs.get(totalSbj)+" "+tr.getInfo().getWord().getForm());
		ags.set(totalSbj, new Agent(ags.get(totalSbj).getName()+" "+tr.getInfo().getWord().getForm(), ags.get(totalSbj).getLemma()));
	}
	if((tr.getInfo().getLinkRef().getInfo().getLabel().equals("grup-verb") || tr.getInfo().getLinkRef().getInfo().getLabel().equals("grup-verb-inf")) && !tr.getInfo().getWord().getLemma().equals("ser")){
		vbs.add(tr.getInfo().getWord().getLemma());
	}
	if(tr.getInfo().getLabel().equals("dobj") && tr.getInfo().getLinkRef().getInfo().getLabel().equals("sn")){
		objs.add(tr.getInfo().getWord().getLemma());
		b2=true;
	}
	if(b2 && tr.getInfo().getLabel().equals("obj-prep") && tr.getInfo().getLinkRef().getInfo().getLabel().equals("sn")){
		//objs.add(tr.getInfo().getWord().getLemma());
		//System.out.println("***AGrego***"+tr.getInfo().getWord().getLemma());
	}
	
	/*if(tr.getInfo().getLinkRef().getInfo().getLabel().equals("sn") && !tr.getInfo().getLabel().equals("subj")){
		subjs.add(tr.getInfo().getWord().getForm());
	}*/
	/*
	 HashSet hs = new HashSet();
	 //Lo cargamos con los valores del array, esto hace quite los repetidos
	 hs.addAll(subjs);
	 //Limpiamos el array
	 subjs.clear();
	 //Agregamos los valores sin repetir
	 subjs.addAll(hs);
	*/
	/*
	 HashSet hs2 = new HashSet();
	 //Lo cargamos con los valores del array, esto hace quite los repetidos
	 hs2.addAll(ags);
	 //Limpiamos el array
	 ags.clear();
	 //Agregamos los valores sin repetir
	 ags.addAll(hs2);
	 */
	 /*
	for(int i=0;i<ags.size();i++){
		System.out.println("OEEEE "+ags.get(i).getName()+ " lema "+ags.get(i).getLemma());	
	}*/
	
    Word w = tr.getInfo().getWord();

    System.out.print(
      "(" + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
    printSenses( w );
    System.out.print( ")" );

    nch = tr.numChildren();

    if( nch > 0 ) {
      System.out.println( " [" );

      for( int i = 0; i < nch; i++ ) {
        child = tr.nthChildRef( i );

        if( child != null ) {
          if( !child.getInfo().isChunk() ) {
            printDepTree( depth + 1, child , b1, b2);
          }
        }
        else {
          System.err.println( "ERROR: Unexpected NULL child." );
        }
      }

      // Print chunks (in order)
      last = 0;
      trob = true;

      // While an unprinted chunk is found, look for the one with lower
      // chunk_ord value.
      while( trob ) {
        trob = false;
        min = 9999;

        for( int i = 0; i < nch; i++ ) {
          child = tr.nthChildRef( i );
          childnode = child.getInfo();

          if( childnode.isChunk() ) {
            if( (childnode.getChunkOrd() > last) &&
                (childnode.getChunkOrd() < min) ) {
              min = childnode.getChunkOrd();
              fchild = child;
              trob = true;
            }
          }
        }
        if( trob && (child != null) ) {
          printDepTree( depth + 1, fchild, b1,b2);
        }

        last = min;
      }

      for( int i = 0; i < depth; i++ ) {
        System.out.print( "  " );
      }

      System.out.print( "]" );
    }

    System.out.println( "" );
  }
}


