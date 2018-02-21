//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

public class URLprocessing {

  public interface URLhandler {
    void takeUrl(String url);
  }

  public static URLhandler handler = new URLhandler() {
    public void takeUrl(String url) {
      System.out.println(url);
    }
  };

  /**
   * Parse the given buffer to fetch embedded links and call the handler to
   * process these links.
   * 
   * @param data
   *          the buffer containing the http document
   */
  public static void parseDocument(CharSequence data) {
        // call handler.takeUrl for each matched url
	  String url = "";
	  int i = 0;
	  while ((i+11) < data.length()) {
		  //3-char string starting a link
		  String startCompare = (data.subSequence(i, i+3)).toString();
		  //link found
		  if (startCompare.equals("<A ") || startCompare.equals("<a ")) {
			  i = i + 3;
			  while (!Character.toString(data.charAt(i)).equals(">")) {
				  String hrefCompare = (data.subSequence(i, i+8)).toString();
				  //url found
				  
				  if (hrefCompare.matches(".http://")) {
					  String apostroph = Character.toString(data.charAt(i));
					  i++;
					  while(!(Character.toString(data.charAt(i))).equals(apostroph)) {
						  url = url.concat(Character.toString(data.charAt(i)));
						  i++;
					  }
				      // call handler.takeUrl for each matched url
					  handler.takeUrl(url);
					  url = "";
				  }
				  //url not found
				  else
					  i++;	  
			  }		
		  }
		  //link not found
		  else
			  i++;
	  }
  }

}