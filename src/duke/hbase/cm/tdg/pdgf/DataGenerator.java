
import pdgf.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import pdgf.core.exceptions.ConfigurationException;
import pdgf.core.exceptions.InvalidArgumentException;
import pdgf.core.exceptions.InvalidElementException;
import pdgf.core.exceptions.InvalidStateException;
import pdgf.core.exceptions.XmlException;

public class DataGenerator{
  public static void main(String[] agrs){
  try {
       Controller controller = new Controller();
       controller.executeCommand(new String[] {"load","xml_schema/z.xml"});
       controller.executeCommand(new String[] {"start"});
      // controller.executeCommand(new String[] {"exit"} );
   } catch (IOException e) {
       e.printStackTrace();
   } catch (InvalidArgumentException e) {
       e.printStackTrace();
   } catch (InstantiationException e) {
       e.printStackTrace();
   } catch (IllegalAccessException e) {
       e.printStackTrace();
   } catch (XmlException e) {
       e.printStackTrace(); 
   } catch (ConfigurationException e) {
       e.printStackTrace();
   } catch (ParserConfigurationException e) {
       e.printStackTrace();       
   } catch (SAXException e) {
      e.printStackTrace();
   } catch (ClassNotFoundException e) {
      e.printStackTrace();
   } catch (InvalidElementException e) {
      e.printStackTrace();
   } catch (InvalidStateException e) {
      e.printStackTrace();
   }
 
  }
} 

