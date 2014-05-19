/**
 * 
 */
package converter;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.cli.*;
import org.xml.sax.SAXException;

/**
 * @author Daniel Aineah
 *
 */

public class RDFConverter implements Runnable{
	class ConverterExp extends Exception {
		private static final long serialVersionUID = 3434299950695253003L;
		public ConverterExp(String msg) {
	        super(msg);
	    }
	}

	//Folder paths
	private static String source = "";
	private static String config = "";
	private static String output = "";
	private static String containedIn = "";
	
	//Software paths
	private static String rdf2rdf = "";
	
	private static String threadType;
	private String conversionType;
	private static Boolean produceTtl = false;
	private static int numThreads = 40;
	private static List<List<Integer>> objectsForThreads = new ArrayList<List<Integer>>();
	private static List<Integer> objectIDsList = new ArrayList<Integer>();
	private static int objectsProcessed = 0;
	
	private List<Integer> objectsList;
	private static long startTime;
	private static long endTime;
	private static String stats = "";
	
	private final static Logger logger = Logger.getLogger(RDFConverter.class.getName());
	private static FileHandler fh = null;

	public RDFConverter(List<Integer> list, String convType, String tType) {
		objectsList = list;
		conversionType = convType;
		threadType = tType;
	}

	public static void main(String[] args) {

		initLogger();
				
		startTime = System.nanoTime();
		
		long start = System.nanoTime();
		int status = preProcess(args);
		if(status != 0) return;
		long end = System.nanoTime();
		stats += "Preprocessing took " + normalizeTime(end-start) + "\n";
		
		if(objectsForThreads.size() > 0) {
			
			//preClean();
			
			objectsProcessed = 0;
			
			start = System.nanoTime();
			convert("rdf");
			end = System.nanoTime();
			stats += "rdf transformation took " + normalizeTime(end-start) + "\n";
			
			if(produceTtl){
				objectsProcessed = 0;
				start = System.nanoTime();
				convert("ttl");
				end = System.nanoTime();
				stats += "ttl transformation took " + normalizeTime(end-start) + "\n";
			}
			
			endTime = System.nanoTime();
			printStats();
			return;
		}
		logger.log(Level.INFO, "No objects to process. Exiting...");
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
	
	private static void printStats() {
		long duration = endTime - startTime;
		logger.log(Level.INFO, "=======================End==========================");
		logger.log(Level.INFO, stats + "TOTAL TIME taken is " + normalizeTime(duration));
		logger.log(Level.INFO, "=======================Done=========================");
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
	
	private static void postCleanUp() {
		logger.log(Level.INFO, "Deleting rdf folder");
		try {
			FileUtils.deleteDirectory(new File(output + "\\rdf"));
			FileUtils.deleteDirectory(new File(output + "\\xml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.WARNING, e.toString());
		}
	}
	
	private static void preClean() {
		logger.log(Level.INFO, "Deleting ttl and rdf folders");
		try {
			FileUtils.deleteDirectory(new File(output + "\\ttl"));
			FileUtils.deleteDirectory(new File(output + "\\rdf"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.WARNING, e.toString());
		}
	}
	
	private static int preProcess(String[] args) {
		Options options = new Options();
		options.addOption("c", true, "config file to use");
		options.addOption("t", false, "generate .ttl files");
		CommandLineParser parser = new PosixParser();
		CommandLine cmd;
		try {
			cmd = parser.parse( options, args);
			if(cmd.hasOption("t")) {
				produceTtl = true;
			}
			String propFile = cmd.getOptionValue("c");
			if(propFile == null) {
				throw new Exception("Property file must be specified");
			}
			Properties prop = new Properties();
			InputStream input = null;
			input = new FileInputStream(propFile);
			 
			// load a properties file
			prop.load(input);
			source = prop.getProperty("source");
			config = prop.getProperty("config");
			output = prop.getProperty("output");
			rdf2rdf = prop.getProperty("rdf2rdf");
			containedIn = prop.getProperty("containedIn");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e.toString());
			return 1;
		}
				
		//get the time the object ids file was last modified
		Calendar currentDate = Calendar.getInstance(); //Get the current date
		SimpleDateFormat formatter= new SimpleDateFormat("MM/dd/yyyy"); //format it as per your requirement
		String dateNow = formatter.format(currentDate.getTime());
		File objectIDsFile = new File(containedIn+"\\objects_sampleAll.txt");
		String lastModified = formatter.format(objectIDsFile.lastModified());
		
		//get number of files in source and those in the object ids file
		File folder = new File(source);
		File[] listOfFiles = folder.listFiles();
		int numObjectIDsFromSource = listOfFiles.length;
		int numObjectIDsFromFile = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(objectIDsFile));
			String line;
			while ((line = br.readLine()) != null) {
				numObjectIDsFromFile++;
			}
			br.close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e2.toString());
			return 1;
		}
		
		//if objectIDsFile was not modified today, create a new one
		if(!lastModified.equals(dateNow) || numObjectIDsFromFile != numObjectIDsFromSource){
			logger.log(Level.INFO, "Generating object IDs");
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(objectIDsFile));
			    for (int i = 0; i < listOfFiles.length; i++) {
					  if (listOfFiles[i].isFile()) {
					        String fileName = listOfFiles[i].getName();
					        int id = -1;
					        try{
					        	id = Integer.parseInt(fileName.split("_")[2].split(".xml")[0]);
					        }
					        catch(Exception e){
					        	id = Integer.parseInt(fileName.split(".xml")[0]);
					        }
					        finally{
					        	objectIDsList.add(id);
					        	writer.write(id+"");
					        	writer.newLine();
					        }
					  }
			    }
			    writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				logger.log(Level.SEVERE, e1.toString());
				return 1;
			}
		}
		
		else {
			try{
				String line;
				BufferedReader br = new BufferedReader(new FileReader(objectIDsFile));
				while ((line = br.readLine()) != null) {
					objectIDsList.add(Integer.parseInt(line));
				}
				br.close();
			}
			catch (IOException e) {
				logger.log(Level.SEVERE, e.toString());
				return 1;
			}
		}
				
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
		return 0;
	}
	
	private static void convert(String convType) {
		Thread[] threads = new Thread[objectsForThreads.size()];
		for (int n=0; n<threads.length; n++) {
			threads[n] = new Thread(new RDFConverter(objectsForThreads.get(n), convType, "child"));
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
	
	private void toTtl(String outputPathLocal, int id) throws IOException, InterruptedException, ConverterExp {
		//String cmd = "java -cp \"" + any23jar + "\" -Xmx256M org.deri.any23.cli.Rover \"" + outputPathLocal.replace("ttl", "rdf") + "\\" + id + ".rdf\" -o \""  + outputPathLocal + "\\" + id + ".ttl\"";
		String cmd =  "java -jar \"" + rdf2rdf + "\" \""  + outputPathLocal.replace("ttl", "rdf") + "\\" + id + ".rdf\" \""  + outputPathLocal + "\\" + id + ".ttl\"";
		Process pr = Runtime.getRuntime().exec(cmd);
		InputStreamReader istream = new  InputStreamReader(pr.getErrorStream());
		BufferedReader br = new BufferedReader(istream);
		String line;
		String error = "";
		int counter = 0;
		while ((line = br.readLine()) != null){
			//skip first line which always prints to show which is file is being converted to what
			//if not skipped, an error would always be thrown
			if(counter++ > 0) error += line + "\n";
		}
		if(error != "") throw new ConverterExp(error);
		pr.waitFor();
	}
	
	private void toRdf(String outputPathLocal, int id) throws TransformerException, ParserConfigurationException, IOException, SAXException {
		Source xslt = new StreamSource(new File(config));
		TransformerFactory fact = new net.sf.saxon.TransformerFactoryImpl();
		Transformer trans = fact.newTransformer(xslt);
		//String in = lido + "\\oai_tms.ycba.yale.edu_" + id + ".xml";
		
		String out = outputPathLocal + "\\" + id + ".rdf";
		try{
			String in = source + "\\oai_tms.ycba.yale.edu_" + id + ".xml";
			FileInputStream fis = new FileInputStream(in);
			trans.transform(new StreamSource(new InputStreamReader(fis, "UTF-8")), new StreamResult(new File(out)) );
		}
		catch(Exception e){
			String in = source + "\\" + id + ".xml";
			FileInputStream fis = new FileInputStream(in);
			trans.transform(new StreamSource(new InputStreamReader(fis, "UTF-8")), new StreamResult(new File(out)) );
		}
	}
	
	public void run() {
		
		for (int i = 0; i < objectsList.size(); i++) {
			int curId = objectsList.get(i);
			String outputPathLocal = conversionType.equals("rdf") ? output + "\\rdf" : output + "\\ttl";
			outputPathLocal += "\\" + curId%100;
			
			File f1 = new File(outputPathLocal);
			f1.mkdirs();
			
			try {
				logger.log(Level.INFO, conversionType + " processing object " + curId);
				switch(conversionType) {
					case "ttl" : toTtl(outputPathLocal, curId); break;
					case "rdf" : toRdf(outputPathLocal, curId); break;
				}
				objectsProcessed++;
			} catch (TransformerException | IOException | InterruptedException | ParserConfigurationException | SAXException | ConverterExp e) {
				
				objectsProcessed--;
				
				//If subChild, conversion failed before
				//	since it's failing again, fail over gracefully and report the error
				if(!threadType.equals("subChild")) {
					logger.log(Level.WARNING, e.toString());
					List<Integer> list = new ArrayList<Integer>();
					list.add(objectsList.get(i));
					Thread contingency = new Thread(new RDFConverter(list, conversionType, "subChild"));
					contingency.start();
					
					try {
						contingency.join();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						logger.log(Level.WARNING, "Error " + conversionType + " processing object id " + curId + ": " + e.toString());
					}
				}
				
				else {
					logger.log(Level.SEVERE, "Error " + conversionType + " processing object id " + curId + ": " + e.toString());
				}
			}
			finally {
				printProgress();
			}
		}
	}
	
	private static void printProgress() {
		double percentage = ((double)objectsProcessed / (double)objectIDsList.size()) * 100;
		String percentageString = String.format("%.2f", percentage);
		logger.log(Level.INFO, percentageString + "%(" + objectsProcessed + " out of " + objectIDsList.size() + ") processed");
	}
}


