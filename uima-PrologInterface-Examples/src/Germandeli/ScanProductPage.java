package Germandeli;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

public class ScanProductPage {

	/**
	 * Scans a germandeli product page
	 * @param args
	 */
	public static void main(String[] args) {
				
		/*
		 * Initialization of the analysis engines
		 */

		//Create Analysis engine for Product features
		AnalysisEngine ae = null;
		try {
			ae = ScanProductPage.loadAnalysisEngine("Descriptors/ProductAEDescriptor.xml");
		} catch (InvalidXMLException e) {
			System.err.println("Descriptor wrong");
			e.printStackTrace();
		} catch (ResourceInitializationException e) {
			System.err.println("Initialization Problem");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Could not read the file");
			e.printStackTrace();
		}
		
		//Create Analysis engine for writing the Product features to xml
		AnalysisEngine writer = null;
		try {
			writer = ScanProductPage.loadAnalysisEngine("Descriptors/ProductSaveToXMLAE.xml");
		} catch (InvalidXMLException e) {
			System.err.println("Descriptor wrong");
			e.printStackTrace();
		} catch (ResourceInitializationException e) {
			System.err.println("Initialization Problem");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Create Jcas
		if (ae != null) {
			ArrayList<String> pagesToScan = ScanProductPage.readInStringsFromDirectory("examplePagesGermanDeli");
			System.out.println(pagesToScan.size());
			if (pagesToScan != null) {
				while (!pagesToScan.isEmpty()) {
					String page = pagesToScan.remove(0);
					JCas aJCas = null;
					try {
						aJCas = ae.newJCas();
					} catch (ResourceInitializationException e) {
						System.err.println("Initialization Problem");
						e.printStackTrace();
					}
					
					//set document
					aJCas.setDocumentText(page);
					
					//process document
					try {
						ae.process(aJCas);
					} catch (AnalysisEngineProcessException e) {
						System.err.println("Error processing aJcas");
						e.printStackTrace();
					}
					
					//write to file
					if (writer != null) {
						try {
							writer.process(aJCas);
						} catch (AnalysisEngineProcessException e) {
							System.err.println("Problem writting to the xml file");
							e.printStackTrace();
						}
					}
				}
			}
			else {
				System.out.println("Directory is empty");
			}
		}
	}
	public static String readStringFromFile(String fileName) throws IOException {
		File file = new File(fileName);
		FileReader fileReader = new FileReader(file);
		BufferedReader buffReader = new BufferedReader(fileReader);
		String result = "";
		while (true) {
			String line = buffReader.readLine();
			//System.out.println(line);
			if (line == null) {
				break;
			}
			else {
				result += line;
			}
		}
		buffReader.close();
		fileReader.close();
		return result;
	}
	/**
	 * Loads the Analysis Engine from a descriptor file
	 * @param descriptor
	 * @return
	 * @throws IOException 
	 * @throws InvalidXMLException 
	 * @throws ResourceInitializationException
	 */
	public static  AnalysisEngine loadAnalysisEngine(String descriptor) throws IOException, InvalidXMLException, ResourceInitializationException {
		XMLInputSource in = null;
		ResourceSpecifier specifier = null;
		
		
		//Load the Descriptor
		in = new XMLInputSource(descriptor);
		specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);
		
		//Create Analysis Engine
		//ae = 
		return UIMAFramework.produceAnalysisEngine(specifier);
	}
	/**
	 * Reads in a directory and returns the files as strings
	 * @param directory
	 * @return
	 */
	public static ArrayList<String> readInStringsFromDirectory(String directory) {
		File file = new File(directory);
		ArrayList<String> resultList = new ArrayList<String>();
		if (!file.isDirectory()) {
			
			return null;
		}
		//To do implement filenameFilter to filter files
		File[] files = file.listFiles();
		
		//read each file
		for (int i = 0;i < files.length;i++) {
			if (files[i].isDirectory()) {
				resultList.addAll(readInStringsFromDirectory(file.getName()));
			}
			else {
				try {
					resultList.add(readStringFromFile(files[i].getPath()));
				} catch (IOException e) {
					System.err.println("Error reading file: " + files[i].getName() + "the file will not be added to the list");
					e.printStackTrace();
				}
			}
		}
		return resultList;
	}
	/**
	 * Load an Image and save it to a File
	 * @param urlName
	 * @param fileName
	 * @throws IOException
	 */
	public static void loadImageAndSaveToFile(String urlName,String fileName) throws IOException {
		URL start = null;
		try {
		start = new URL(urlName); }
		catch(MalformedURLException e) {
			start = new URL("http://"+urlName);
		}
		BufferedImage img = null;
		try {
		    img = ImageIO.read(start.openStream());
		} catch (IOException e) {
			
		}
		if (img != null) {
			File outputFile = new File("Results/" + fileName + ".jpg");
			ImageIO.write(img, "jpg", outputFile);
		}
		else {
			System.err.println("Image " + fileName + "could not be read");
		}
	}
}
