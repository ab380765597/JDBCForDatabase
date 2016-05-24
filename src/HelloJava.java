import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;


public class HelloJava {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter file name: ");
		String txtName = in.nextLine();
		String fileName = "C:/Users/xi.zhang/Desktop/correct/"+txtName;  //find the right path to file.
		System.out.println("Is it English language file? y/n");
		String ifEnglish = in.nextLine();
		//String res = readFile(fileName);
		String url = "jdbc:mysql://54.254.142.168:3306/sms_development?useUnicode=true&characterEncoding=UTF-8";
		String username = "****";
		String password = "EnterPassword*****";

		System.out.println("Connecting database...");
		Statement stmt = null;
		//generateTurkishString();
		try (Connection con = DriverManager.getConnection(url, username, password)) {
		    System.out.println("Database connected!");
		    System.out.println("Inserting records into the table...");
		    String sql = null;
		    if(ifEnglish.equals("y")||ifEnglish.equals("Y")){
		    	 System.out.println("For locale? y/n");
		    	 String sc = in.nextLine();
		    	 if(sc.equals("y")||sc.equals("Y")){
		    		 sql = readLocaleFile(fileName, stmt, con);
		    	 }else{
		    		 //sql = readFile(fileName, stmt, con);//Caution! would work immediately.
		    		 sql = GVCreadFile(fileName, stmt, con);
				     System.out.println("Finished! Inserted all records into the table!");
		    	 }
		    }else{
		    	System.out.println("Input Language?");
		    	String lang = in.nextLine();
		    	System.out.println("Is it locale file?(eg locale.XX) y/n");
		    	String lc = in.nextLine();
		    	if(lc.equals("Y")||lc.equals("y")){
		    		//sql = readLocalFileForOtherLanguage(fileName, stmt, con, lang);
		    		sql= readLocalFileForOtherLanguage(fileName, stmt, con, lang);
		    		System.out.println("Finished! Updatted all records into the word table for locale!");
		    	}else{
		    		//sql = readFileForOtherLanguage(fileName, stmt, con, lang); 
		    		sql= GVCreadFileForOtherLanguage(fileName, stmt, con, lang);
			    	System.out.println("Finished! Updatted all records into the table!");
		    	}
		    	
		    }
	        
		} catch (SQLException e) {
		    throw new IllegalStateException("Cannot connect the database!", e);
		}
	}
	
	
	private static void updateToDB(String sql, Statement stmt, Connection con) throws SQLException{
		stmt = con.createStatement();
		stmt.executeUpdate(sql);
		System.out.println("Inserted records into the table!!");
	}
	
	private static String checkInput(String s){
		int ind = s.indexOf('\'');
		if(ind<0){
			return s; 
		}
		StringBuilder sb = new StringBuilder();
		int length =s.length();
		for(int i=0; i<length; i++){
			if(s.charAt(i)=='\''){
				sb.append("\\");
				sb.append("'");
			}else{
				sb.append(s.charAt(i));
			}
		}
		return sb.toString();
	}
	
	private static String readFileForOtherLanguage(String fileName, Statement stmt, Connection con, String language) throws IOException, SQLException {
		InputStream isr = new FileInputStream(fileName);
	    BufferedReader br = new BufferedReader(new InputStreamReader(isr,"UTF8"));
	    try {
	        String line = br.readLine();
	        // lineCounter is used to omit the illegal lines in font-head 
	        int lineCounter = 0;
	        while (line != null) {
	        	/*lineCounter++;			//gsp1400 to others need to delete 3 lines jumper.
	        	if(lineCounter<=3){
	        		line = br.readLine();
	        		continue;
	        	}*/
	        	int length = line.length();
	        	boolean Bflag = true;
	        	boolean SecBflag = false;
	        	int twoDotCounter = 0;
            	String index = null;
            	String stringcontent=null;
            	String descrp = null;
	        	for(int i = 0; i<length; i++){
	        		//=============extract index from string=======
	        		if(line.charAt(i)=='{' && Bflag){
	        			int j = i+1;
	        			int commaCounter = 0;
	        			while(j<length && commaCounter<2){
	        				j++;
	        				if(line.charAt(j)==','){
	        					commaCounter++;
	        				}
	        			}
	        			index = line.substring(i+1, j);
	        			System.out.println(index);
	        			Bflag = false;
	        		}
	        		if(line.charAt(i)=='}'){
	        			SecBflag = true;
	        		}
	        		//=============extracting content from string========
	        		if(line.charAt(i)=='"'){
	        			if(twoDotCounter<1){
	        				int j=length-1;
		        			while(j>i && line.charAt(j)!='"'){
		        				j--;
		        			}
		        			stringcontent = line.substring(i+1, j);
		        			System.out.println(stringcontent);
	        			}
	        			twoDotCounter++;
	        		}
	        		//============ NO extracting optional description from string=====
	        		
	        	}
	        	
	        	if(index==null ||stringcontent==null){
	        		line = br.readLine();
	        		continue;
	        	}
	        	String sql = "UPDATE words SET "+language+" = '"+checkInput(stringcontent)+"' where string_index = '"+index+"' and template_id = '56';";//careful for the temp_id!!
	        	
	        	//update to database here
	        	updateToDB(sql, stmt, con);
	            line = br.readLine();
	        }
	        
	        return "to final";
	    } finally {
	        br.close();
	    }
	}
	
	
	private static String readLocalFileForOtherLanguage(String fileName, Statement stmt, Connection con, String language) throws IOException, SQLException {
		InputStream isr = new FileInputStream(fileName);
	    BufferedReader br = new BufferedReader(new InputStreamReader(isr,"UTF8"));
	    try {
	        String line = br.readLine();
	        // lineCounter is used to omit the illegal lines in font-head 
	        int lineCounter = 0;
	        while (line != null) {
	        	lineCounter++;
	        	if(lineCounter<=4){
	        		line = br.readLine();
	        		continue;
	        	}
	        	int length = line.length();
	        	boolean Bflag= true;
	        	boolean SecBflag = false;
	        	boolean commaFlag=true;
            	String index = null;
            	String stringcontent=null;
	        	for(int i = 0; i<length; i++){
	        		//=============extract index from string=======
	        		if(line.charAt(i)=='"' && Bflag){
	        			int j = i+1;
	        			while(j<length && line.charAt(j)!='"'){
	        				j++;
	        			}
	        			index = line.substring(i+1, j);
	        			System.out.println(index);
	        			Bflag = false;
	        		}
	        		if(line.charAt(i)==':' && commaFlag){
	        			SecBflag = true;
	        			commaFlag= false;
	        		}
	        		//=============extracting content from string========
	        		if(line.charAt(i)=='"' && SecBflag){
	        			int j=length-1;
	        			while(j>i && line.charAt(j)!='"'){
	        				j--;
	        			}
	        			stringcontent = line.substring(i+1, j);
	        			System.out.println(stringcontent);
	        			SecBflag = false;
	        			
	        		}
	        		//============NO description from string=====
	        		
	        	}
	        	
	        	if(index==null ||stringcontent==null){
	        		line = br.readLine();
	        		continue;
	        	}
	        	String sql = "UPDATE words SET "+language+" = '"+checkInput(stringcontent)+"' where string_index = '"+index+"' and template_id = '69';";  //careful to the template_id
	        	
	        	//update to database here
	        	updateToDB(sql, stmt, con);
	            line = br.readLine();
	        }
	        
	        return "to final";
	    } finally {
	        br.close();
	    }
	}
	
	
	private static String readFile(String fileName, Statement stmt, Connection con) throws IOException, SQLException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        String line = br.readLine();
	        // lineCounter is used to omit the illegal lines in font-head 
	        int lineCounter = 0;
	        while (line != null) {
	        	/*lineCounter++;			//gsp1400 to others need to delete 3 lines jumper.
	        	if(lineCounter<=3){
	        		line = br.readLine();
	        		continue;
	        	}*/
	        	int length = line.length();
	        	boolean Bflag = true;
	        	boolean SecBflag = false;
	        	int twoDotCounter = 0;
            	String index = null;
            	String stringcontent=null;
            	String descrp = null;
	        	for(int i = 0; i<length; i++){
	        		//=============extract index from string=======
	        		if(line.charAt(i)=='{' && Bflag){
	        			int j = i+1;
	        			int commaCounter = 0;
	        			while(j<length && commaCounter<2){
	        				j++;
	        				if(line.charAt(j)==','){
	        					commaCounter++;
	        				}
	        			}
	        			index = line.substring(i+1, j);
	        			System.out.println(index);
	        			Bflag = false;
	        		}
	        		if(line.charAt(i)=='}'){
	        			SecBflag = true;
	        		}
	        		//=============extracting content from string========
	        		if(line.charAt(i)=='"'){
	        			if(twoDotCounter<1){
	        				int j=length-1;
		        			while(j>i && line.charAt(j)!='"'){
		        				j--;
		        			}
		        			stringcontent = line.substring(i+1, j);
		        			System.out.println(stringcontent);
		        			//flag = false;
	        			}
	        			twoDotCounter++;
	        		}
	        		//============extracting optional description from string=====
	        		if(line.charAt(i)=='(' && twoDotCounter==2 ){
	        			int j = length-1;
	        			while(line.charAt(j)!=')' ){
	        				j--;
	        			}
	        			descrp = line.substring(i+1,j);
	        			System.out.println("Optional Description: "+descrp);
	        		}
	        		
	        		if(line.charAt(i)=='{' && SecBflag){
	        			int j= i+1;
	        			while(line.charAt(j)!='}'){
	        				j++;
	        			}
	        			descrp = line.substring(i+1, j);
	        			System.out.println("=Optional Description: "+descrp);
	        		}
	        	}
	        	
	        	if(index==null ||stringcontent==null){
	        		line = br.readLine();
	        		continue;
	        	}
	        	String sql;
	        	if(descrp==null){
	        		sql = "INSERT INTO words (string_index, english, template_id, created_at, updated_at) VALUES ('"+index+"','"+checkInput(stringcontent)+"','56',now(),now())"; //careful for the temp_id!!
	        	}else{
	        		sql = "INSERT INTO words (string_index, english, description, template_id, created_at, updated_at) VALUES ('"+index+"','"+checkInput(stringcontent)+"','"+checkInput(descrp)+"','56',now(),now())";
	        	}
	        	//update to database here
	        	updateToDB(sql, stmt, con);
	            line = br.readLine();
	        }
	        
	        return "to final";
	    } finally {
	        br.close();
	    }
	}
	
	
	private static String readLocaleFile(String fileName, Statement stmt, Connection con) throws IOException, SQLException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        String line = br.readLine();
	        // lineCounter is used to omit the illegal lines in font-head 
	        int lineCounter = 0;
	        while (line != null) {
	        	lineCounter++;
	        	if(lineCounter<=4){
	        		line = br.readLine();
	        		continue;
	        	}
	        	int length = line.length();
	        	boolean Bflag= true;
	        	boolean SecBflag = false;
	        	boolean commaFlag=true;
            	String index = null;
            	String stringcontent=null;
	        	for(int i = 0; i<length; i++){
	        		//=============extract index from string=======
	        		if(line.charAt(i)=='"' && Bflag){
	        			int j = i+1;
	        			while(j<length && line.charAt(j)!='"'){
	        				j++;
	        			}
	        			index = line.substring(i+1, j);
	        			System.out.println(index);
	        			Bflag = false;
	        		}
	        		if(line.charAt(i)==':' && commaFlag){
	        			SecBflag = true;
	        			commaFlag= false;
	        		}
	        		//=============extracting content from string========
	        		if(line.charAt(i)=='"' && SecBflag){
	        			int j=length-1;
	        			while(j>i && line.charAt(j)!='"'){
	        				j--;
	        			}
	        			stringcontent = line.substring(i+1, j);
	        			System.out.println(stringcontent);
	        			SecBflag = false;
	        			
	        		}
	        		//============NO description from string=====
	        		
	        	}
	        	
	        	if(index==null ||stringcontent==null){
	        		line = br.readLine();
	        		continue;
	        	}
	        	String sql;
	        	sql = "INSERT INTO words (string_index, english, template_id, created_at, updated_at) VALUES ('"+index+"','"+checkInput(stringcontent)+"','58',now(),now())"; //58 is static
	        	
	        	//update to database here
	        	updateToDB(sql, stmt, con);
	            line = br.readLine();
	        }
	        
	        return "to final";
	    } finally {
	        br.close();
	    }
	}
	
	private static String GVCreadFile(String fileName, Statement stmt, Connection con) throws IOException, SQLException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		 try {
		        String line = br.readLine();
		        while (line != null) {
		        	int length = line.length();
		        	boolean Bflag = true;
		        	boolean SecBflag = false;
		        	int twoDotCounter = 0;
	            	String index = null;
	            	String stringcontent=null;
	            	String descrp = null;
		        	for(int i = 0; i<length; i++){
		        		//=============extract index from string=======
		        		if(line.charAt(i)==',' && Bflag){
		        			
		        			index = line.substring(0,i);
		        			stringcontent = line.substring(i+1);
		        			System.out.println(index);
		        			Bflag = false;
		        		}
		        		
		        	}
		        	String sql;
		        	sql = "INSERT INTO words (string_index, english, template_id, created_at, updated_at) VALUES ('"+index+"','"+checkInput(stringcontent)+"','68',now(),now())"; //careful for the temp_id!!64forGVC, 66forGVCGAV 67forGAC 68forGS_WAVE
		        	
		        	//update to database here
		        	updateToDB(sql, stmt, con);
		            line = br.readLine();
		        }
		        
		        return "to final";
		    } finally {
		        br.close();
		    }
	}

	private static String GVCreadFileForOtherLanguage(String fileName, Statement stmt, Connection con, String language) throws IOException, SQLException {
		InputStream isr = new FileInputStream(fileName);
	    BufferedReader br = new BufferedReader(new InputStreamReader(isr,"UTF8"));
	    try {
	        String line = br.readLine();
	        while (line != null) {
	        	
	        	int length = line.length();
	        	boolean Bflag = true;
	        	boolean SecBflag = false;
	        	int twoDotCounter = 0;
            	String index = null;
            	String stringcontent=null;
            	String descrp = null;
	        	for(int i = 0; i<length; i++){
	        		//=============extract index from string=======
	        		if(line.charAt(i)==',' && Bflag){
	        			index = line.substring(0,i);
	        			stringcontent = line.substring(i+1);
	        			System.out.println(index);
	        			Bflag = false;
	        		}
	        	}
	        	String sql = "UPDATE words SET "+language+" = '"+stringcontent+"' where string_index = '"+index+"' and template_id = '68' ;";//careful for the temp_id!!64forGVC 66forGVC_GAV 67forGAC 68forGS_WAVE
	        	//update to database here
	        	updateToDB(sql, stmt, con);
	            line = br.readLine();
	        }
	        
	        return "to final";
	    } finally {
	        br.close();
	    }
	}


	private static String UCMLocalereadFile(String fileName, Statement stmt, Connection con) throws IOException, SQLException{
		 BufferedReader br = new BufferedReader(new FileReader(fileName));
		    try {
		        String line = br.readLine();
		        int lineCounter = 0;
		        while (line != null) {
		        	lineCounter++;			//gsp1400 to others need to delete 3 lines jumper.
		        	if(lineCounter<=2){
		        		line = br.readLine();
		        		continue;
		        	}
		        	int length = line.length();
		        	boolean Bflag = true;
		        	boolean SecBflag = false;
		        	int twoDotCounter = 0;
	            	String index = null;
	            	String stringcontent=null;
	            	String descrp = null;
		        	for(int i = 0; i<length; i++){
		        		//=============extract index from string=======
		        		if(line.charAt(i)=='{' && Bflag){
		        			int j = i+1;
		        			int commaCounter = 0;
		        			while(j<length && commaCounter<2){
		        				j++;
		        				if(line.charAt(j)==','){
		        					commaCounter++;
		        				}
		        			}
		        			index = line.substring(i+1, j);
		        			System.out.println(index);
		        			Bflag = false;
		        		}
		        		if(line.charAt(i)=='}'){
		        			SecBflag = true;
		        		}
		        		//=============extracting content from string========
		        		if(line.charAt(i)=='"'){
		        			if(twoDotCounter<1){
		        				int j=length-1;
			        			while(j>i && line.charAt(j)!='"'){
			        				j--;
			        			}
			        			stringcontent = line.substring(i+1, j);
			        			System.out.println(stringcontent);
			        			//flag = false;
		        			}
		        			twoDotCounter++;
		        		}
		        		//============extracting optional description from string=====
		        		
		        		if(line.charAt(i)=='{' && SecBflag){
		        			int j= i+1;
		        			while(line.charAt(j)!='}'){
		        				j++;
		        			}
		        			descrp = line.substring(i+1, j);
		        			System.out.println("=Optional Description: "+descrp);
		        		}
		        	}
		        	
		        	if(index==null ||stringcontent==null){
		        		line = br.readLine();
		        		continue;
		        	}
		        	String sql;
		        	if(descrp==null){
		        		sql = "INSERT INTO words (string_index, english, template_id, created_at, updated_at) VALUES ('"+index+"','"+checkInput(stringcontent)+"','65',now(),now())"; //careful for the temp_id!!
		        	}else{
		        		sql = "INSERT INTO words (string_index, english, description, template_id, created_at, updated_at) VALUES ('"+index+"','"+checkInput(stringcontent)+"','"+checkInput(descrp)+"','65',now(),now())";
		        	}
		        	//update to database here
		        	updateToDB(sql, stmt, con);
		            line = br.readLine();
		        }
		        
		        return "to final";
		    } finally {
		        br.close();
		    }
	}

	private static String UCMLocalereadFileForOtherLanguage(String fileName, Statement stmt, Connection con, String language) throws IOException, SQLException {
		InputStream isr = new FileInputStream(fileName);
	    BufferedReader br = new BufferedReader(new InputStreamReader(isr,"UTF8"));
	    try {
	        String line = br.readLine();
	        // lineCounter is used to omit the illegal lines in font-head 
	        int lineCounter = 0;
	        while (line != null) {
	        	lineCounter++;			//gsp1400 to others need to delete 3 lines jumper.
	        	if(lineCounter<=2){
	        		line = br.readLine();
	        		continue;
	        	}
	        	int length = line.length();
	        	boolean Bflag = true;
	        	boolean SecBflag = false;
	        	int twoDotCounter = 0;
            	String index = null;
            	String stringcontent=null;
            	String descrp = null;
	        	for(int i = 0; i<length; i++){
	        		//=============extract index from string=======
	        		if(line.charAt(i)=='{' && Bflag){
	        			int j = i+1;
	        			int commaCounter = 0;
	        			while(j<length && commaCounter<2){
	        				j++;
	        				if(line.charAt(j)==','){
	        					commaCounter++;
	        				}
	        			}
	        			index = line.substring(i+1, j);
	        			System.out.println(index);
	        			Bflag = false;
	        		}
	        		if(line.charAt(i)=='}'){
	        			SecBflag = true;
	        		}
	        		//=============extracting content from string========
	        		if(line.charAt(i)=='"'){
	        			if(twoDotCounter<1){
	        				int j=length-1;
		        			while(j>i && line.charAt(j)!='"'){
		        				j--;
		        			}
		        			stringcontent = line.substring(i+1, j);
		        			System.out.println(stringcontent);
		        			//flag = false;
	        			}
	        			twoDotCounter++;
	        		}
	        		//============ NO extracting optional description from string=====
	        		
	        	}
	        	
	        	if(index==null ||stringcontent==null){
	        		line = br.readLine();
	        		continue;
	        	}
	        	String sql = "UPDATE words SET "+language+" = '"+checkInput(stringcontent)+"' where string_index = '"+index+"' and template_id = '65';";//careful for the temp_id!!
	        	//update to database here
	        	updateToDB(sql, stmt, con);
	            line = br.readLine();
	        }
	        
	        return "to final";
	    } finally {
	        br.close();
	    }
	}


}
