import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLprocessing2 {

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
	  String hrefCompare = "";
	  int i = 0;
	  Pattern p = Pattern.compile("http://.+", Pattern.CASE_INSENSITIVE);
	  Matcher m;
	  while ((i+11) < data.length()) {
		  //3-char string starting a link
		  String startCompare = (data.subSequence(i, i+3)).toString();
		  //link found
		  if (startCompare.equals("<A ") || startCompare.equals("<a ")) {
			  i = i + 3;
			  while (!Character.toString(data.charAt(i)).equals(">")) {
				  hrefCompare = hrefCompare.concat(Character.toString(data.charAt(i)));
				  i++;
			  }
			  m = p.matcher(hrefCompare);
			  while (m.find()) {
				  url = m.group();
				  String [] a = url.split("'");
				  url = a[0];
			      // call handler.takeUrl for each matched url
				  handler.takeUrl(url);
				  url = "";
			  }
		  }
		  //link not found
		  else
			  i++;
	  }
  }

}