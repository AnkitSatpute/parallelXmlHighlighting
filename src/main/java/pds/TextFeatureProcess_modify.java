package pds;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;


import org.apache.commons.lang3.StringUtils;

/**
 * compares the txt files for deletions, additions, changes, 
 * outputs these changes next to the word length differences 
 * and corrects the positions of the XML tags. 
 * Then the XML tags are inserted into the modify file 
 * using the new position information.
 *
 * @author Marco Beck
 */

public class TextFeatureProcess_modify {
	
		public static void main(String[] args)  throws IOException {

			
	///// compare the original text and modify text
       
	        // String filePath_extract_txt = "src/main/resources/output_plain.txt";
	        
	        String filePath_extract_txt = args[0];	        
	        StringBuilder stringBuilder_extract_txt = new StringBuilder();
	        try (Stream stream = Files.lines( Paths.get(filePath_extract_txt), StandardCharsets.UTF_8)) 
	        {
	         stream.forEach(s -> stringBuilder_extract_txt.append(s).append("\n"));
	        }
	        catch (IOException e) 
	        {
	         e.printStackTrace();
	        }

	        
	        //modified-File as txt
	    //    String filePath_mod_txt = "src/main/resources/modify_plain.txt";
	        
	        String filePath_mod_txt = args[1];
	        
	        StringBuilder stringBuilder_mod_txt = new StringBuilder();
	        try (Stream stream = Files.lines( Paths.get(filePath_mod_txt), StandardCharsets.UTF_8)) 
	        {
	         stream.forEach(s -> stringBuilder_mod_txt.append(s).append("\n"));
	        }
	        catch (IOException e) 
	        {
	         e.printStackTrace();
	        }
	        
	        //mapping-File as txt
	    //    String filePath_mapping_txt = "src/main/resources/output_mapping.txt";
	        String filePath_mapping_txt = args[2];
	        
	        StringBuilder stringBuilder_mapping_txt = new StringBuilder();
	        try (Stream stream = Files.lines( Paths.get(filePath_mapping_txt), StandardCharsets.UTF_8)) 
	        {
	         stream.forEach(s -> stringBuilder_mapping_txt.append(s));
	        }
	        catch (IOException e) 
	        {
	         e.printStackTrace();
	        }
	        
	        //Input-File as xml
	       // String filePath_input_original_xml = "src/main/resources/input_document.xml";
	        String filePath_input_original_xml = args[3];
	        StringBuilder stringBuilder_input_xml_doc = new StringBuilder();
	        try (Stream stream = Files.lines( Paths.get(filePath_input_original_xml), StandardCharsets.UTF_8)) 
	        {
	         stream.forEach(s -> stringBuilder_input_xml_doc.append(s).append("\n"));
	        }
	        catch (IOException e) 
	        {
	         e.printStackTrace();
	        }
	        
	        
	        String stringmod_txt = stringBuilder_mod_txt.toString();
	        String stringextract_txt = stringBuilder_extract_txt.toString();        
	        String stringmapping_txt = stringBuilder_mapping_txt.toString();
	        String stringxml_input = stringBuilder_input_xml_doc.toString();
	        
	        String[] array_extract_txt = stringextract_txt.split(" ");
	        String[] array_modify_txt = stringmod_txt.split(" ");
     

  
	        // Now convert string into ArrayList 
	        ArrayList<String> strList = new ArrayList<String>();

            for (int i=0; i<array_extract_txt.length; i++){
            	strList.add(array_extract_txt[i]);
            }
	        
	        // Now convert string into ArrayList 
	        ArrayList<String> strListmodi = new ArrayList<String>();
	        
            for (int i=0; i<array_modify_txt.length; i++){
            	strListmodi.add(array_modify_txt[i]);
            }

	        
       		 ArrayList<String> z = new ArrayList<String>(strList);
       		 z.removeAll(strListmodi);


       		 
       		 ArrayList<String> u = new ArrayList<String>(strListmodi);
       		 u.removeAll(strList);

       		 
             //output_cleanup-file as txt
             String output_mapping_file ="src/main/resources/words_diff.txt";
             Files.write(Paths.get(output_mapping_file), u.toString().getBytes("UTF8"));

       		 
       		// Now the ArrayList converted back to an array:
       		String[] Array_Differenz_original = z.toArray(new String[0]);
       		String[] Array_Differenz_modfiy = u.toArray(new String[0]);
       		
       		StringBuilder result_modify = new StringBuilder();
       		int nu2 = 0;
    		for(int n= 0;n < Array_Differenz_modfiy.length;n++)   
    		{	
    		
    		int[] position = new int [Array_Differenz_modfiy.length];
    		
    		nu2 = stringmod_txt.indexOf(Array_Differenz_modfiy[n]);  
       	    		
       		result_modify.append(nu2 + ";");
    		}
    		String ausgabe_modify = result_modify.toString();  
    		
    		// input-file original text
    		StringBuilder result_original_txt = new StringBuilder();
       		int nu3 = 0;
    		for(int n= 0;n < Array_Differenz_original.length;n++)   
    		{	
    		
    		int[] position_org = new int [Array_Differenz_original.length];
	
    		nu3 = stringextract_txt.indexOf(Array_Differenz_original[n]);  
    		result_original_txt.append(nu3 + ";");

    		}
    		
    		String ausgabe_original_txt = result_original_txt.toString();
    		
    		StringBuilder result_differenz = new StringBuilder();
       		int diff_ori_modi = 0;
       		
    		for(int n= 0;n < Array_Differenz_modfiy.length;n++)   
    		{	
   		
       		int tdslaenge_original = Array_Differenz_original[n].toString().length();
       		
       		int tdslaenge_modi = Array_Differenz_modfiy[n].toString().length();
       	 
       		diff_ori_modi =  tdslaenge_modi - tdslaenge_original; 

       		result_differenz.append(diff_ori_modi + ";");

    		}
    		
    		String ausgabe_differenz = result_differenz.toString();

    
    //// change the mapping-string

       		String[] array_mapping_tags = stringmapping_txt.split(";");
        	Integer[] numbers_mapping = new Integer[array_mapping_tags.length];
        	  for(int n = 0;n < numbers_mapping.length;n++)        
        	  {
        		  numbers_mapping[n] = Integer.parseInt(array_mapping_tags[n]);    	
  		    }
        	  
        	  for(int n= 2, s=1;n < numbers_mapping.length;n++, s++)   
  			{
        		  
        		  numbers_mapping[n] = s + numbers_mapping[n];

  			}

            		
                    String[] array_position = ausgabe_modify.split(";");
                    Integer[] numbers_position = new Integer[array_position.length];
                    
                    for(int n = 0;n < numbers_position.length;n++)        
        		    {
                    	numbers_position[n] = Integer.parseInt(array_position[n]);   
        		    }
        		    
                    String[] array_differenz = ausgabe_differenz.split(";");
                    Integer[] numbers_differenz = new Integer[array_differenz.length];
                    for(int n = 0;n < numbers_differenz.length;n++)        
        		    {
                    	numbers_differenz[n] = Integer.parseInt(array_differenz[n]);   
        		    }
                    
                    StringBuilder result_new_mapping_sb = new StringBuilder();
                    
                    Integer[] array_new_mappings = new Integer[numbers_mapping.length];
                    
                    int differenz_zaehler = 0;
                    int leerzeichenplus = 1;

        			for(int n= 0;n < numbers_mapping.length-1;n++)   
        			{
        				 for(int v= 0;v < numbers_position.length;v++)   
        			
        				 {   		
        					if(between(numbers_position[v],numbers_mapping[n],numbers_mapping[n+1]))
        					{
        						numbers_mapping[n+1] = v + numbers_mapping[n+1] + (numbers_differenz[v]);

        					}
        					else{	  
        						if(numbers_position[v] <numbers_mapping[n])
            					{
        							differenz_zaehler = differenz_zaehler+numbers_differenz[v];
        							
            						numbers_mapping[n+1] = v+ numbers_mapping[n+1] + (numbers_differenz[v]);
            					}
        						
        					}

            			}	

    		            //Output-mapping-file as txt
    		            String output_mapping_modify_file ="src/main/resources/output_mapping_modify.txt";
    		            Files.write(Paths.get(output_mapping_modify_file), Arrays.deepToString(numbers_mapping).getBytes("UTF8"));

    		    		

        			}// closed for-loop
        			
        			
/////  extract xml-tags and insert them with separator at the appropriate place
        			
        		
        	         String newstr = stringxml_input.replaceAll(">", ">POP");
        	         String newstrto = newstr.replaceAll("<", "POP<");
        	         String newstrtodd = newstrto.replaceAll("  ", " ");
        	         String[] array_xml_content_rogi = newstrtodd.split("POP");


        	     for (int w=1; w<array_xml_content_rogi.length; w++){

        	    		
        	    		String array_vaule = array_xml_content_rogi[w];
        	    	
        	    	            if (array_vaule.startsWith("<") || array_xml_content_rogi[w].isEmpty())
        	    	            		{ 
        	    	            }
        	    	            else
        	    	            {
        	    	            	array_xml_content_rogi[w] = "MMM";
        	    	            }

        	     }
        	     
        	     StringBuilder strB = new StringBuilder();
        	     
        	     for(String value: array_xml_content_rogi){
        	         strB.append(value);
        	     }
        	     
        	     
        	     String output_xml_tags_trennzeichen = strB.toString().replaceAll("MMM", "==");

        			
      //// insert xml-tags into the modify using the new mapping-string 
		            

        		    String[] array_string_plain_txt_mod = new String [numbers_mapping.length];
        		    
        		    for(int n = 0;n < numbers_mapping.length-2;n++)        
        		    {	
        		    	array_string_plain_txt_mod[n] = stringmod_txt.substring(numbers_mapping[n], numbers_mapping[n+1]);
        		    	
        		    }
        		    
        		    String[] array_xml_tagsarr = output_xml_tags_trennzeichen.split("==");
        		    String[] array_xml_tagsarr_new = merge(array_xml_tagsarr, array_string_plain_txt_mod);
        		    String joined = String.join("", array_xml_tagsarr_new); //returns "android"
        		    
       	         	joined = joined.replaceAll("null", "");

        		    
       // paste the em-tags

        		    String new_xml_string_with_em = null;
        		    
        		    
        		    Object [] differenz_array = u.toArray();
        		    
        		    for(int n = 0;n < differenz_array.length;n++)        
        		    {	
            		    String word = differenz_array[n].toString();

               		    new_xml_string_with_em = joined.replaceAll(word, "<em>" + word + "</em>");
        		    	
        		    }

        		    
        		  //Output-mapping-file as txt
		            String output_xml_document_file ="src/main/resources/output_document.xml";
		            Files.write(Paths.get(output_xml_document_file), new_xml_string_with_em.getBytes("UTF8"));
		            

		    		System.out.println(new_xml_string_with_em);
        		    
   }  // closed main
        			



// method between pos and pos+1
public static boolean between(int i, int minValueInclusive, int maxValueInclusive) 
{
		    if (i >= minValueInclusive && i <= maxValueInclusive)
		        return true;
		    else
		        return false;


 } //closed method
		

public static String[] merge(String[] a1, String[] a2) {
    String[] combinedArray = new String[a1.length + a2.length];
    int idx = 0;
    for (int i = 0; i < Math.max(a1.length, a2.length); i++) {
        if (i < a1.length)
            combinedArray[idx] = a1[i];
        else
            idx--;
        if (i < a2.length)
            combinedArray[idx + 1] = a2[i];
        else
            idx--;
        idx += 2;           
    }
    return combinedArray;
}


} //closed class

