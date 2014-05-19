package ImageHarvest;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 */

/**
 * @author Daniel Aineah
 *
 */
public class ImageHarvest implements Runnable{
	
	private static List<Integer> objectIDsList = new ArrayList<Integer>();
	private static List<List<Integer>> objectsForThreads = new ArrayList<List<Integer>>();
	private static String outputFolder = "";
	private static String sourceFolder = "";
	private List<Integer> objectsList;
	private final static Logger logger = Logger.getLogger(ImageHarvest.class.getName());
	private static FileHandler fh = null;
	
	public ImageHarvest(List<Integer> list) {
		objectsList = list;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long start = System.nanoTime();
		Options options = new Options();
		options.addOption("o", true, "output folder for image xml files");
		options.addOption("s", true, "source folder for computing object IDs");
		CommandLineParser parser = new PosixParser();
		CommandLine cmd;
		try {
			cmd = parser.parse( options, args);
			outputFolder = cmd.getOptionValue("o");
			if(outputFolder == null) {
				throw new Exception("Output folder must be specified");
			}
			sourceFolder = cmd.getOptionValue("s");
			if(sourceFolder == null) {
				throw new Exception("Source folder must be specified");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e.toString());
			return;
		}
		
		//get all object ids
		File folder = new File(sourceFolder);
		File[] listOfFiles = folder.listFiles();
		logger.log(Level.INFO,"Generating object IDs" );
	    for (int i = 0; i < listOfFiles.length; i++) {
			  if (listOfFiles[i].isFile()) {
			        String fileName = listOfFiles[i].getName();
			        objectIDsList.add(Integer.parseInt(fileName.split("_")[2].split(".xml")[0]));
			  }
	    }
	    /*try {
	    	logger.log(Level.INFO,"Cleaning up output folder" );
			FileUtils.deleteDirectory(new File(outputFolder));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.WARNING, e.toString());
		}*/
	    harvest();
	    long end = System.nanoTime();
	    logger.log(Level.INFO,"Harvesting took " + normalizeTime(end-start) + "\n");
	}
	
	public static void initLogger(){
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy_HH-mm");
		SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat("MM-dd-yyyy");
		
		Date date = new Date();
        String dateStr = DATE_FORMAT.format(date);
		dateStr = Utilities.implodeArray(dateStr.split(":"), "_");
		dateStr = Utilities.implodeArray(dateStr.split(" "), "_");
		
		String folderName = "logs\\" + DATE_FORMAT2.format(date);
		String logFileName = folderName + "\\log_" + dateStr;
		new File(folderName).mkdirs();
		
		 try {
			 fh=new FileHandler(logFileName, false);
		 } catch (SecurityException | IOException e) {
			 e.printStackTrace();
		 }
		 Logger l = Logger.getLogger("");
		 fh.setFormatter(new SimpleFormatter());
		 l.addHandler(fh);
		 l.setLevel(Level.CONFIG);
	 }
	
	private static String normalizeTime(long duration) {
		double durationInSecs = (double)duration / 1000000000.0;
		double durationInMins = durationInSecs / 60.0;
		int hrs = (int)durationInMins / 60;
		String hrsStr = (String) (hrs < 10 ? "0" + hrs : "" + hrs);
		int mins = (int)(durationInMins - (hrs * 60));
		String minsStr = (String) (mins < 10 ? "0" + mins : "" + mins);
		int secs = (int)Math.round(durationInSecs - ((mins + (hrs * 60)) * 60));
		String secsStr = (String) (secs < 10 ? "0" + secs : "" + secs);
		return   hrsStr + ":" + minsStr + ":" + secsStr + " (HH:MM:SS)";
	}

	private static void harvest() {
		int numThreads = 40;
		int  objectsPerThread = (int)Math.ceil((double)objectIDsList.size()/(double)numThreads);

		for(int i = 0; i < numThreads; i++) {
			List<Integer> list = new ArrayList<Integer>();
			for(int j = i*objectsPerThread; j <  objectIDsList.size(); j++){
				list.add(objectIDsList.get(j));
				if((j + 1)%objectsPerThread == 0)
					break;
			}
			if(list.size() > 0)
				objectsForThreads.add(list);
		}
		Thread[] threads = new Thread[objectsForThreads.size()];
		for (int n=0; n<threads.length; n++) {
			threads[n] = new Thread(new ImageHarvest(objectsForThreads.get(n)));
			threads[n].start();
        }
		
		for (int n=0; n<threads.length; n++) {
			try {
				threads[n].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.log(Level.WARNING, e.toString());
			}
        }
		
	}
	
	@Override
	public void run() {
		for (int k = 0; k < objectsList.size(); k++) {
			try {
				String html = "http://deliver.odai.yale.edu/info/repository/YCBA/object/"+objectsList.get(k)+"/type/2?output=xml";
				
				URL url;
		    	url = new URL(html);
				URLConnection conn = url.openConnection();
				logger.log(Level.INFO,"Harvesting object "+objectsList.get(k));
		        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder builder = factory.newDocumentBuilder();
		        Document doc = builder.parse(conn.getInputStream());
		        Transformer transformer = TransformerFactory.newInstance().newTransformer();
		        new File(outputFolder).mkdirs();
		        File outputFile = new File(outputFolder+"/"+objectsList.get(k)+".xml");
		        Result output = new StreamResult(outputFile);
		        Source input = new DOMSource(doc);
		        transformer.transform(input, output);
				
			} catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
