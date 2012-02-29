package Germandeli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.impl.XCASSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;
/**
 * Saves a Product FS to a file
 * @author Maximilian Jakasovic
 *
 */
public class ProductSaveToXMLAE extends JCasAnnotator_ImplBase {
	public static final String PARAM_OUTPUTDIR = "Results";

	  private File mOutputDir;

	  private int mDocNum;
	  private boolean init = false;

	  public void initialize() throws ResourceInitializationException {
	    mDocNum = 0;
	    //mOutputDir = new File((String) getConfigParameterValue(PARAM_OUTPUTDIR));
	    mOutputDir = new File(PARAM_OUTPUTDIR);
	    if (!mOutputDir.exists()) {
	      mOutputDir.mkdirs();
	    }
	  }

	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if (!init) {
			try {
				initialize();
				init = true;
				// retreive the filename of the input file from the CAS
			    FSIterator it = aJCas.getAnnotationIndex(Product.type).iterator();
			    File outFile = null;
			    if (it.hasNext()) {
			    	Product fileLoc = (Product) it.next();
			        outFile = new File(mOutputDir, fileLoc.getName() + ".xml");
			    }
			    if (outFile == null) {
			      outFile = new File(mOutputDir, "doc" + mDocNum++ + ".xml");
			    }
			    // serialize XCAS and write to output file
			    
			    try {
					writeXCas(aJCas.getCas(), outFile);
				} catch (IOException e) {
					System.err.println("Could not write to output file");
					e.printStackTrace();
				} catch (SAXException e) {
					System.out.println("SAX Failure");
					e.printStackTrace();
				}
			} catch (ResourceInitializationException e) {
				System.err.println("Could not create output file");
				e.printStackTrace();
			}
		}
		
	}
	/**
	   * Serialize a CAS to a file in XCAS format
	   * 
	   * @param aCas
	   *          CAS to serialize
	   * @param name
	   *          output file
	   * 
	   * @throws IOException
	   *           if an I/O failure occurs
	   * @throws SAXException
	   *           if an error occurs generating the XML text
	   */
	  private void writeXCas(CAS aCas, File name) throws IOException, SAXException {
	    FileOutputStream out = null;

	    try {
	      out = new FileOutputStream(name);
	      XCASSerializer ser = new XCASSerializer(aCas.getTypeSystem());
	      XMLSerializer xmlSer = new XMLSerializer(out, true);
	      ser.serialize(aCas, xmlSer.getContentHandler());
	    } finally {
	      if (out != null) {
	        out.close();
	      }
	    }
	  }

}
