package quan.coma;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.insert.metadata.CSVParser;
import de.wdilab.coma.insert.metadata.ListParser;
import de.wdilab.coma.insert.metadata.OWLParser_V3;
import de.wdilab.coma.insert.metadata.SQLParser;
import de.wdilab.coma.insert.metadata.XDRParser;
import de.wdilab.coma.insert.metadata.XSDParser;
import de.wdilab.coma.matching.Resolution;
import de.wdilab.coma.matching.Strategy;
import de.wdilab.coma.matching.Workflow;
import de.wdilab.coma.matching.execution.ExecWorkflow;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Source;

public class Coma {
	
	public static final String DIR="C:\\Users\\NTQuan\\workspace\\Coma3.0\\";
	public static final int MATCH_MAX_NUMBER=6;
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/coma-project";
	
	static final String USER = "root";
	static final String PASS = "1";
	
	public static void main(String[] args){
		Coma coma = new Coma();
		MatchResult result;
		
		
		System.out.println("--------------- Result:\n\n\n");
		result = coma.matchModelsDefault("sources/product1.xsd", "sources/product2.xsd", "PO_abbrevs.txt", "PO_syns.txt");
		System.out.println(result);
		
	}
	
	public Result[] match(int price){
		Coma coma = new Coma();
		
		return coma.match1(price,DIR+"sources\\product.xsd",DIR+"PO_abbrevs.txt", DIR+"PO_syns.txt");
	}

	
	public Result[] match1(int price, String fileSrc, String fileAbbreviations, String fileSynonyms){
		
		Connection conn=null;
		Statement stmt = null;
		int tmp=0,i=0,j=0,k=0, l=0, cost=0, ab=0;
		float matchResult=0.0f, ratio=0.0f;
		String fileTrg="",var="";
		Result tmp1, tmp2;
		Product mp1, mp2;
		Result[] result = new Result[6];
		Product top[] = new Product[MATCH_MAX_NUMBER];
		
		
		//init array Result
		for(i=0;i<6;i++){
			result[i] = new Result();
		}
		//init array top
		for(i=0;i<MATCH_MAX_NUMBER;i++){
			top[i] = new Product();
		}
		
		
		//Load graph 1
		Graph graphSrc = loadGraph(fileSrc, null);
		
		
 		ListParser parser = new ListParser(false);
 	// load abbreviations to lists
 		parser.parseSingleSource(fileAbbreviations);
 		ArrayList<String> abbrevList = parser.getList1();
 		ArrayList<String> fullFormList =  parser.getList2();
 		// load synonyms to lists		
 		parser.parseSingleSource(fileSynonyms);
 		ArrayList<String> wordList = parser.getList1();
 		ArrayList<String> synonymList = parser.getList2();
 		
 		// init ExecWorkflow with abbreviatons and synonyms
 		ExecWorkflow exec = new ExecWorkflow(abbrevList, fullFormList, wordList, synonymList);

 		//define strategy
 		Strategy strategy = new Strategy(Strategy.COMA_OPT);
 		graphSrc = graphSrc.getGraph(Graph.PREP_SIMPLIFIED);
 		
 		//define workflow
 		Workflow workflow = new Workflow();		
 		workflow.setSource(graphSrc);
 		workflow.setBegin(strategy);
 		workflow.setUseSynAbb(true);
		
		try{
			//connect db
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");
			
			stmt = conn.createStatement();
			String sql = "SELECT * FROM products";
			ResultSet rs = stmt.executeQuery(sql);
			
			
//			System.out.println("\n\n\nList:\n\n\n");
			//get MATCH_MAX_NUMBER schema
			while(rs.next()){
				 int id  = rs.getInt("id");
		         String name = rs.getString("name");
//		         fileTrg=DIR+"sources\\"+name;
				 cost = Integer.parseInt(rs.getString("price"));
				 ab=price-cost;
				 ab=Math.abs(ab);
				 if(price!=0)
					 ratio = (float)ab/price;
				 else
					 ratio = 1.0f;
//				 System.out.println("Ratio:"+ratio+"    ID:"+id+"    price:"+price+"    cost:"+cost+"    ab:"+ab);
				 
				//get list MATCH_MAX_NUMBER product by price
		         j=0;
		         tmp=0;
		         for(j=MATCH_MAX_NUMBER-1;j>=0;j--){
		        	 if(ratio > top[j].getRatio())
		        		 break;
		        	 else
		        		 tmp++;
		         }
		         if(tmp>0){
		        	 if(tmp==MATCH_MAX_NUMBER){
		        		 mp1 = new Product(id, ratio, name);
			        	 for(k=0;k<MATCH_MAX_NUMBER;k++){
			        		 mp2 = top[k];
			        		 top[k]=mp1;
			        		 mp1=mp2;
			        	 }
		        	 }else{
		        		 mp1 = new Product(id, ratio, name);
			        	 for(k=j+1;k<MATCH_MAX_NUMBER;k++){
			        		 mp2 = top[k];
			        		 top[k]=mp1;	 
			        		 mp1=mp2;
			        	 }
		        	 }
		        	 
		         }
				 
		         
		      }
			
		      rs.close();
			
		}catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		// Close connection
		try{
			if(conn!=null){
				conn.close();
				System.out.println("Closed!");
			}
		}catch(SQLException se){
			se.printStackTrace();
		}
		
		
		
		//matching with MATCH_MAX_NUMBER schema
        l=0;
        for(l=0;l<MATCH_MAX_NUMBER;l++){
       	 fileTrg=DIR+"sources\\"+top[l].getName();
       	 
       	//Load graph2 and matching
		 		Graph graphTrg = loadGraph(fileTrg, null);
		 		graphTrg = graphTrg.getGraph(Graph.PREP_SIMPLIFIED);
		 		workflow.setTarget(graphTrg);
		 		MatchResult[] results =  exec.execute(workflow);
		 		if (results==null){
		 			System.err.println("COMA_API.matchModelsDefault results unexpected null");
		 			return null;
		 		}
		 		if (results.length>1){
		 			System.err.println("COMA_API.matchModelsDefault results unexpected more than one, only first one returned");
		 		}
		         
		         //print maching result
		 		System.out.println("Schema"+l+":"+results[0]);
		 		
		        //get matching result of 2 schemas
		 		var=results[0].toString();
				var=var.substring(var.indexOf("product <-> product:"));
				var=var.replaceAll("^.*product:", "");
				matchResult=Float.parseFloat(var.substring(0, var.indexOf("+")));
		         
		        
		         //get list 6 good results
		         j=0;
		         tmp=0;
		         for(j=5;j>=0;j--){
		        	 if(matchResult < result[j].getValue())
		        		 break;
		        	 else
		        		 tmp++;
		         }
		         if(tmp>0){
		        	 if(tmp==6){
		        		 tmp1 = new Result(top[l].getId(), matchResult);
			        	 for(k=0;k<6;k++){
			        		 tmp2 = result[k];
			        		 result[k]=tmp1;
			        		 tmp1=tmp2;
			        	 }
		        	 }else{
		        		 tmp1 = new Result(top[l].getId(), matchResult);
			        	 for(k=j+1;k<6;k++){
			        		 tmp2 = result[k];
			        		 result[k]=tmp1;	 
			        		 tmp1=tmp2;
			        	 }
		        	 }
		        	 
		         }
        }
		
		
		
		
		return result;
	}
	
	public Graph loadGraph(String file, String name){
		if (file==null){
			System.out.println("COMA_API.loadGraph Error file is null");
			return null;
		}
		boolean insertDB = false;
		String filetype = file.toLowerCase();
		filetype = filetype.substring(filetype.lastIndexOf("."));
		InsertParser par = null;
		if (filetype.equals(InsertParser.XSD)){
			par = new XSDParser(insertDB);
		} else if (filetype.equals(InsertParser.XDR)){
			par = new XDRParser(insertDB);
		} else if (filetype.equals(InsertParser.CSV)){
			par = new CSVParser(insertDB);
		} else if (filetype.equals(InsertParser.SQL)){
			par = new SQLParser(insertDB);
		} else if (filetype.equals(InsertParser.OWL) || filetype.equals(InsertParser.RDF)){
			par = new OWLParser_V3(insertDB);
		}
		
		if (par==null){
			System.out.println("COMA_API.loadGraph Error filetype not recognized");
			return null;
		}
		
		par.parseSingleSource(file);
		Graph graph = par.getGraph();
		return graph;
	}
	
	
	public MatchResult matchModelsDefault(String fileSrc, String fileTrg, String fileAbbreviations, String fileSynonyms){
		Graph graphSrc = loadGraph(fileSrc, null);
		Graph graphTrg = loadGraph(fileTrg, null);
		
		// load abbreviations to lists
		ListParser parser = new ListParser(false);
		parser.parseSingleSource(fileAbbreviations);
		ArrayList<String> abbrevList = parser.getList1();
		ArrayList<String> fullFormList =  parser.getList2();
		// load synonyms to lists		
		parser.parseSingleSource(fileSynonyms);
		ArrayList<String> wordList = parser.getList1();
		ArrayList<String> synonymList = parser.getList2();
		
		// init ExecWorkflow with abbreviatons and synonyms
		ExecWorkflow exec = new ExecWorkflow(abbrevList, fullFormList, wordList, synonymList);
		Strategy strategy = new Strategy(Strategy.COMA_OPT);
		if (graphSrc.getSource().getType()== Source.TYPE_ONTOLOGY
				|| graphTrg.getSource().getType()== Source.TYPE_ONTOLOGY){
			strategy.setResolution( new Resolution(Resolution.RES1_NODES));
		} else {
			graphSrc = graphSrc.getGraph(Graph.PREP_SIMPLIFIED);
			graphTrg = graphTrg.getGraph(Graph.PREP_SIMPLIFIED);
		}
		Workflow workflow = new Workflow();		
		
		workflow.setSource(graphSrc);
		workflow.setTarget(graphTrg);
		workflow.setBegin(strategy);
		workflow.setUseSynAbb(true);
		MatchResult[] results =  exec.execute(workflow);
		if (results==null){
			System.err.println("COMA_API.matchModelsDefault results unexpected null");
			return null;
		}
		if (results.length>1){
			System.err.println("COMA_API.matchModelsDefault results unexpected more than one, only first one returned");
		}
		return results[0];
	}
	

	
}