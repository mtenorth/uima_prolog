/**
 * CASPool - contains casToProlog(CAS) method to transform a Common Analysis Structure 
 *   (UIMA CAS) into Prolog predicates
 */

package prolog_cas;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jpl.Compound;
import jpl.JPL;
import jpl.Query;
import jpl.Term;
import jpl.Variable;

//import org.apache.commons.cli.CommandLine;
//import org.apache.commons.cli.CommandLineParser;
//import org.apache.commons.cli.GnuParser;
//import org.apache.commons.cli.Options;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.BooleanArrayFS;
import org.apache.uima.cas.ByteArrayFS;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.DoubleArrayFS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.FloatArrayFS;
import org.apache.uima.cas.IntArrayFS;
import org.apache.uima.cas.LongArrayFS;
import org.apache.uima.cas.ShortArrayFS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.FsIndexDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XmlCasDeserializer;
import org.apache.uima.util.XmlCasSerializer;

/**
 * @author Paul Fodor 
 * @author Maximilian Jakasovic
 */
public class PrologCAS {
	Logger logger = UIMAFramework.getLogger(PrologCAS.class);
	
	private PrologCAS(){
		//initialize the Prolog engine
		JPL.init();
		// consult Prolog rule file into the Prolog system
		//Query consultQuery=new Query("consult('"+ruleFileName+"')");
		//logger.log(Level.ALL,"consult: " + (consultQuery.hasSolution() ? "succeded" : "failed"));
	}
	
	private static String ruleFileName = new String();
	private static PrologCAS INSTANCE;
	
	public static PrologCAS getInstance(String newRuleFileName) {
		fsSequence=0;
		fSs=new Vector<FSMapping>();
		predicates=new Hashtable<String,Vector<String>>();
		idPredicates=new Vector<String>();
		
		if(INSTANCE==null){
			ruleFileName = newRuleFileName;
			INSTANCE=new PrologCAS();
		}
		return INSTANCE;
	} 
		
	// feature structure sequence
	public static int fsSequence=0;
	public static int getNewFSSequence(){
		fsSequence++;
		return fsSequence;
	}
	public static int getFSSequence(){
		return fsSequence;
	}
	
	// feature structure index - mapping from the UIMA FeatureStructure to an integer id of 
	//  the set of predicates that represent in Prolog this FeatureStructure
	class FSMapping{
		FeatureStructure fs;
		int id;
		public FSMapping(FeatureStructure ifs, int iid){
			fs=ifs;
			id=iid;
		}
		public FeatureStructure getFs(){
			return fs;
		}
		public int getId(){
			return id;
		}
		public String toString(){
			return "FSMapping("+fs+","+id+")";
		}
	}
	
	// Vector of "FSMapping"s - is used for two purposes: 
	//  - check for/represent cycles in the CAS structure
	//  - create new Prolog feature structures with references to existing feature structures
	public static Vector<FSMapping> fSs=new Vector<FSMapping>();
	// Check if a FeatureStructure fS is in the vector of FSMappings
	// returns the id for the predicates that represent this FS in Prolog
	//   or 0 if the Feature Structure was not found
	public int memberFS(FeatureStructure fS){
		for (Enumeration e = fSs.elements(); e.hasMoreElements();)
		{
			FSMapping fsm = (FSMapping) e.nextElement();
			if(fsm.getFs().equals(fS)){
				return fsm.getId();
			}
		}
		return 0;
	}
	// Check if a Prolog predicates FeatureStructure id is in the vector of FSMappings
	// returns the FeatureStructure or null if the id was not found
	/**
	 * Check if a Prolog predicates FeatureStructure id is in the vector of FSMappings
	 * returns the FeatureStructure or null if the id was not found
	 * @param id
	 * @return
	 */
	public FeatureStructure memberFS(int id){
		for (Enumeration e = fSs.elements(); e.hasMoreElements();)
		{
			FSMapping fsm = (FSMapping) e.nextElement();
			if(fsm.getId()==id){
				return fsm.getFs();
			}
		}
		return null;
	}
	
	
	int format=3; // Prolog trasformation formats: 1 - property(objectId,propertyName,value/reference); 2 - propertyName(objectId,value/reference);
	//Cotesys format
	// Hashtable used for storing the set of predicates for a FeatureStructure
	//  hashed on the id of the FeatureStructure, each element contains a Vector of 
	//  strings representing the Prolog predicates that represent this FeatureStructure
	public static Hashtable<String,Vector<String>> predicates=new Hashtable<String,Vector<String>>();
	//  strings representing the hashing id of the Prolog predicates that represent this FeatureStructure
	public static Vector<String> idPredicates=new Vector<String>();
	
	// Types in UIMA need check for inclusion in the topType
	public CAS mCAS;
	/**
	 * Transforms a JCas into prolog
	 * @param aJCas
	 * @return The HashTable of prolog predicates
	 */
	public Hashtable<String,Vector<String>> casToProlog(JCas aJCas){
		Logger logger = UIMAFramework.getLogger(PrologCAS.class);
		//logger.log(Level.ALL,"PrologCASPool.casToProlog(CAS) started: ");
		predicates=new Hashtable<String,Vector<String>>();
		mCAS=aJCas.getCas();
		Type topType = mCAS.getTypeSystem().getType(CAS.TYPE_NAME_TOP);
		// Iterate over all the features of a CAS and call the addFeatureToProlog(aFS)
		//  to transform these features in Prolog predicates
		FSIterator iter = mCAS.getIndexRepository().getAllIndexedFS(topType);
		while (iter.isValid()) {
			FeatureStructure aFS = (FeatureStructure) iter.get();
			addFeatureToProlog(aFS);
			iter.moveToNext();
		}
		return predicates;	
	}
	
	// Vector of predicate names - is used for discontiguous predicates
	public Vector<String> predicateNames=new Vector<String>();
	// add a predicate name if is not already in the vector
	/**
	 * Add a predicate name if is not already in the vector
	 * @param pm Predicate Name 
	 */
	public void addPM(String pm){
		if(predicateNames.contains(pm)){
				return;
		}else{
			predicateNames.add(pm);
		}
	}
	
	// clear the covered text of useless characters
	public String clean(String input){
		return input.
					trim().
					replace("\\","\\\\").
					replace("\"","\\\"").
					replace("'","\\'").
					replace('$',' ').
					replace('\r',' ').
					replace('\n',' ')
					;
	}
	
	// Creates a String representing a Prolog predicate representing a Feature
	//  the feature value is a String
	/**
	 * Creates a String representing a Prolog predicate representing a Feature
	 * @param featName
	 * @param objectId
	 * @param featVal
	 * @return
	 */
	public String createPredicateString(String featName, int objectId, String featVal){
		String newPredicate;
		this.addPM(featName);
		if(format==1){
			newPredicate=new String("property("+objectId+",\""+
					featName+
					"\","+
					featVal+
					")");
		}
		else if (format==2 ){
			newPredicate=new String("'cas_"+featName+"'("+objectId+","+featVal+")");
		}
		else {
			newPredicate=new String("property("+objectId+","+
					featName+
					","+
					featVal+
					")");
		}
		//logger.log(Level.ALL, newPredicate);
		return newPredicate;
	}
	// Creates a String representing a Prolog predicate representing a Feature
	//  the feature value is a Vector
	/**
	 * Creates a String representing a Prolog predicate representing a Feature
	 * @param featName
	 * @param objectId
	 * @param featValVector
	 * @return
	 */
	//Warning use lowercapitals for feature names otherwise prolog treats them as variables
	public String createPredicateString(String featName, int objectId, Vector featValVector){
		this.addPM(featName);
		StringBuffer newPredicate = new StringBuffer();
		if(format==1){
			newPredicate.append("property("+objectId+",\""+featName+"\",[");
		}
		else if (format==2){
			newPredicate.append("'cas_"+featName+"'"+"("+objectId+",[");
		}
		//Cotesys Format
		else {
			newPredicate.append("property("+objectId+",'"+featName+"',[");
		}
		boolean commaNeeded=false;
		for (int i = 0; i < featValVector.size(); i++) {
			String pVal = (String)featValVector.get(i);
			if(commaNeeded){newPredicate.append(",");}
			//edited by Maxi
			newPredicate.append(""+pVal+"");
			commaNeeded=true;
		}
		newPredicate.append("])");
		//logger.log(Level.ALL, newPredicate.toString());
		return newPredicate.toString();
	}
	
	public int prologOrder=1; // order of Prolog predicates: 1 - order on feature id; 2 - order on feature name
	// Adds a predicate to the predicates hashtable
	/**
	 * Adds a predicate to the predicates hashtable
	 * @param objectId
	 * @param featureName
	 * @param newPredicate
	 */
	public void addPredicateToHashtable(String objectId,String featureName,String newPredicate){
		String hashId=new String();
		if (prologOrder==1){
			hashId=objectId;
		}else{
			hashId=featureName;
		}
		if(predicates.get(hashId+"")==null){
			idPredicates.add(hashId);
			Vector<String> v = new Vector<String>();
			v.add(newPredicate);
			predicates.put(hashId+"",v);
		}else{
			Vector<String> v = (Vector<String>)predicates.get(hashId);
			v.add(newPredicate);
		}
	}
	
	// Add one feature structure to Prolog and returns the unique identifier 
	//  (the identifier is used for arrays or complex feature structures that reference to this FeatureStructure)
	/**
	 * Add one feature structure to Prolog and returns the unique identifier
	 * (the identifier is used for arrays or complex feature structures that reference to this FeatureStructure)
	 * @param aFS
	 * @return
	 */
	public int addFeatureToProlog(FeatureStructure aFS){
		//logger.log(Level.ALL,"PrologCASPool.addFeatureToProlog(CAS) started: ");
		int objectId = 0;
		String newPredicate;
		if(aFS==null){
			objectId = this.getNewFSSequence();
			newPredicate=this.createPredicateString("null", objectId, "null");
			this.addPredicateToHashtable(objectId+"","null",newPredicate);
			//logger.log(Level.INFO,"check point 1a:");
			//logger.log(Level.INFO,"\n**predicate: "+newPredicate+"\n\n");
			return objectId; 
		}
		//check if this object was not seen before
		int uid=this.memberFS(aFS);
		if(uid>0){
			return uid; // we found a loop => return the id of the Prolog predicate representing this feature structure 
		}else{
			// a new id is generated for each new feature structure
			objectId = this.getNewFSSequence();
			this.fSs.add(this.new FSMapping(aFS,objectId));
		}
		
		// A special Prolog predicate for covered text
		try{
			String coveredText=clean(((AnnotationFS) aFS).getCoveredText());
			//edited by Maxi
			newPredicate=this.createPredicateString("coveredText", objectId, "'"+coveredText+"'");
			this.addPredicateToHashtable(objectId+"","coveredText",newPredicate);
			//logger.log(Level.INFO,"check point 1b:");
			//logger.log(Level.INFO,"\n**predicate: "+newPredicate+"\n\n");
		}catch(Exception e){
			//e.printStackTrace();
		}
		// A special predicate for the type of the feature structure
		String typeAnnotation=clean(aFS.getType().getName());
		newPredicate = this.createPredicateString("type", objectId, "'"+typeAnnotation+"'");
		this.addPredicateToHashtable(objectId+"","type",newPredicate);
		//logger.log(Level.INFO,"check point 1c:");
		//logger.log(Level.INFO,"\n**predicate: "+newPredicate+"\n\n");
		
		Type mStringType = mCAS.getTypeSystem().getType(CAS.TYPE_NAME_STRING);
		Type mFsArrayType = mCAS.getTypeSystem().getType(CAS.TYPE_NAME_FS_ARRAY);
		List aFeatures = aFS.getType().getFeatures();
		// Iterate over all featuresl of this FeatureStructure and add them to the Prolog predicates
		Iterator iter = aFeatures.iterator();
		while (iter.hasNext()) {
			Feature feat = (Feature) iter.next();
			String featName = clean(feat.getShortName());
			// How we get feature value depends on feature\"s range type)
			String featVal = "null";
			Type rangeType = feat.getRange();
			String rangeTypeName = rangeType.getName();
			if (mCAS.getTypeSystem().subsumes(mStringType, rangeType)) {
				if(aFS.getStringValue(feat) != null){
					featVal = clean(aFS.getStringValue(feat));
				}else{
					featVal = "null";
				}
				String delimitator="'";
				if(featName.equals("begin")||featName.equals("end")||featName.equals("confidence")||featName.equals("wordBegin")||featName.equals("wordEnd")||featName.equals("sofaNum")||featName.equals("parseScore")||featName.equals("seqNo")){
					delimitator="";
				}
				newPredicate = this.createPredicateString(featName, objectId, delimitator+featVal+delimitator);
				this.addPredicateToHashtable(objectId+"",featName,newPredicate);
				//logger.log(Level.INFO,"check point 1d:");
				//logger.log(Level.INFO,"\n**predicate: "+newPredicate+"\n\n");
			} else if (rangeType.isPrimitive()) { // primitive type
				featVal = clean(aFS.getFeatureValueAsString(feat));
				String delimitator="'";
				if(featName.equals("begin")||featName.equals("end")||featName.equals("confidence")||featName.equals("wordBegin")||featName.equals("wordEnd")||featName.equals("sofaNum")||featName.equals("parseScore")||featName.equals("seqNo")){
					delimitator="";
				}
				newPredicate = this.createPredicateString(featName, objectId, delimitator+featVal+delimitator);
				this.addPredicateToHashtable(objectId+"",featName,newPredicate);
				//logger.log(Level.INFO,"check point 1e:");
				//logger.log(Level.INFO,"\n**predicate: "+newPredicate+"\n\n");
			} else if (mCAS.getTypeSystem().subsumes(mFsArrayType, rangeType)) {
				ArrayFS arrayFS = (ArrayFS) aFS.getFeatureValue(feat);
				if (arrayFS != null) {
					Vector<String> displayVal = new Vector<String>();
					for (int i = 0; i < arrayFS.size(); i++) {
						FeatureStructure fsVal = arrayFS.get(i);
						int newElementObjectId = addFeatureToProlog((FeatureStructure)fsVal);
						displayVal.add(clean((newElementObjectId+"")));
					}
					newPredicate = this.createPredicateString(featName, objectId, displayVal);
					this.addPredicateToHashtable(objectId+"",featName,newPredicate);
					//logger.log(Level.INFO,"check point 1f:");
					//logger.log(Level.INFO,"\n**predicate: "+newPredicate+"\n\n");
				}
				continue;
			} else if (rangeType.isArray()) { // primitive array
				String[] vals = null;
				if (CAS.TYPE_NAME_STRING_ARRAY.equals(rangeTypeName)) {
					// StringArrayFSImpl arrayFS = (StringArrayFSImpl) aFS.getFeatureValue(feat);
					StringArray arrayFS = (StringArray) aFS.getFeatureValue(feat);
					if (arrayFS != null) 
						vals = arrayFS.toArray();
				} else if (CAS.TYPE_NAME_INTEGER_ARRAY.equals(rangeTypeName)) {
					IntArrayFS arrayFS = (IntArrayFS) aFS.getFeatureValue(feat);
					if (arrayFS != null)
						vals = arrayFS.toStringArray();
				} else if (CAS.TYPE_NAME_FLOAT_ARRAY.equals(rangeTypeName)) {
					FloatArrayFS arrayFS = (FloatArrayFS) aFS.getFeatureValue(feat);
					if (arrayFS != null)
						vals = arrayFS.toStringArray();
				} else if (CAS.TYPE_NAME_BOOLEAN_ARRAY.equals(rangeTypeName)) {
					BooleanArrayFS arrayFS = (BooleanArrayFS) aFS.getFeatureValue(feat);
					if (arrayFS != null)
						vals = arrayFS.toStringArray();
				} else if (CAS.TYPE_NAME_BYTE_ARRAY.equals(rangeTypeName)) {
					ByteArrayFS arrayFS = (ByteArrayFS) aFS.getFeatureValue(feat);
					if (arrayFS != null)
						vals = arrayFS.toStringArray();
				} else if (CAS.TYPE_NAME_SHORT_ARRAY.equals(rangeTypeName)) {
					ShortArrayFS arrayFS = (ShortArrayFS) aFS.getFeatureValue(feat);
					if (arrayFS != null)
						vals = arrayFS.toStringArray();
				} else if (CAS.TYPE_NAME_LONG_ARRAY.equals(rangeTypeName)) {
					LongArrayFS arrayFS = (LongArrayFS) aFS.getFeatureValue(feat);
					if (arrayFS != null)
						vals = arrayFS.toStringArray();
				}
				if (CAS.TYPE_NAME_DOUBLE_ARRAY.equals(rangeTypeName)) {
					DoubleArrayFS arrayFS = (DoubleArrayFS) aFS.getFeatureValue(feat);
					if (arrayFS != null)
						vals = arrayFS.toStringArray();
				}
				if (vals == null) {
					featVal = "null";
				} else {
					//edited by Maxi
					Vector<String> displayVal = new Vector<String>();
					for (int i = 0; i < vals.length; i++) {
						displayVal.add(
								"'"+
								clean(vals[i])+
								"'");
					}
					newPredicate = this.createPredicateString(featName, objectId, displayVal);
					this.addPredicateToHashtable(objectId+"",featName,newPredicate);
					//logger.log(Level.INFO,"check point 1g:");
					//logger.log(Level.INFO,"\n**predicate: "+newPredicate+"\n\n");
					continue;
				}
			} else { // single feature value
				FeatureStructure fsVal = aFS.getFeatureValue(feat);
				if (fsVal != null) {
					int newElementObjectId = addFeatureToProlog(fsVal);
					newPredicate = this.createPredicateString(featName, objectId, newElementObjectId+"");
					this.addPredicateToHashtable(objectId+"",featName,newPredicate);
					//logger.log(Level.INFO,"\ncheck point 1h:");
					//logger.log(Level.INFO,"\n**predicate: "+newPredicate+"\n\n");
					continue;
				}
			}
		}
		return objectId;
	}
	
	// Parses a Prolog LIST of compound terms and return a Java Vector of Prolog compound terms
	
	public Vector<Term> termVector;
	public Vector<Term> parsePrologList(Term term){
		termVector=new Vector<Term>();
		if(term.isCompound()){
			addToTermVector((Compound)term);
		}
		return termVector;
	}
	public void addToTermVector(Compound term){
		if (term.arity()>0){
			termVector.add(term.arg(1));
			addToTermVector((Compound)term.arg(2));
		}else{
			return;
		}
	}
	
	
	// Procedure to extract a string from a Prolog list
	// checks the type of the term and calls addToStringBuffer(Compound c) if the term was a list 
	public String transformToString(Term t){
		sb=new StringBuffer();
		if(t.isCompound()){
			Compound c = (Compound)(t);
			addToStringBuffer(c);
			return sb.toString();	
		}else if(t.isInteger()){
			return t.intValue()+"";	
		}else if(t.isFloat()){
			return t.floatValue()+"";	
		}else if(t.isVariable()){
			return t.toString();
		}else{
			return t.toString();
		}
	}
	//  - strings in prolog are represented as lists of ascii characters
	//  - a list in prolog is represented as a functor '.'(head,tail)
	//  - to iterate over a JPL list one has to write a recursive procedure
	public StringBuffer sb;
	public void addToStringBuffer(Compound c){
		if (c.arity()>0){
			String aChar = new Character((char)c.arg(1).intValue()).toString();
			sb.append(aChar);
			//logger.log(Level.ALL,c.arg(1));
			addToStringBuffer((Compound)c.arg(2));
		}else{
			return;
		}
	}
	
	// Reads a UIMA type file and an xcas file in xmi format and returns the UIMA CAS
	/**
	 * Reads a UIMA type file and an xcas file in xmi format and returns the UIMA CAS
	 * @param typeFileName
	 * @param xcasFileName
	 * @return
	 */
	public CAS deserialize(String typeFileName, String xcasFileName) {
		Logger logger = UIMAFramework.getLogger(PrologCAS.class);
		//logger.log(Level.ALL,"PrologCASPool.deserialize(String typeFileName, String xcasFileName) started: ");
		try {
			// load a CAS and call casToPrologCASPool(CAS) to generate a Prolog annotation			
			File descriptorFile=new File(typeFileName);
			Object descriptor = UIMAFramework.getXMLParser().parse(new XMLInputSource(descriptorFile));
			TypeSystemDescription tsDesc = (TypeSystemDescription) descriptor;
			tsDesc.resolveImports();
			CAS cas = CasCreationUtils.createCas(tsDesc, null, new FsIndexDescription[0]);			
			// deserialize XCAS into CAS
			FileInputStream xcasInStream = null;
			File xcasFile=new File(xcasFileName);
			xcasInStream = new FileInputStream(xcasFile);
			XmlCasDeserializer.deserialize(xcasInStream, cas, true);
			if (xcasInStream != null)
				xcasInStream.close();
			//logger.log(Level.ALL,cas.toString());
			return cas;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	// Transfer the Cas to Prolog
	/**
	 * Transfer the Cas to Prolog
	 * @param prologPredicates
	 */

	public void transferCasToProlog(Hashtable<String,Vector<String>> prologPredicates){
		try {
			String queryString=new String("dynamic(cas:id/1)");
			Query query=new Query(queryString);
			query.hasSolution();
			
			queryString=new String("retractall(cas:id(_))");
			query=new Query(queryString);
			query.hasSolution();
			
			queryString=new String("assert(cas:id("+this.getFSSequence()+"))");
			query=new Query(queryString);
			query.hasSolution();
			
			for (Enumeration ePn = predicateNames.elements(); ePn.hasMoreElements();){
				String pn = (String)(ePn.nextElement());
				queryString=new String("dynamic(cas:'cas_"+pn+"'/2)");
				query=new Query(queryString);
				query.hasSolution();
				
				queryString=new String("retractall(cas:'cas_"+pn+"'(_,_))");
				query=new Query(queryString);
				query.hasSolution();
			}
			
			for (Enumeration enfs = idPredicates.elements(); enfs.hasMoreElements();){
				String id = (String)(enfs.nextElement());
				Vector<String> v = (Vector<String>)prologPredicates.get(id);;
				for (Enumeration e2 = v.elements(); e2.hasMoreElements();){
					queryString="assert(cas:"+((String) e2.nextElement())+")";
					if(queryString.length()<1000){
						System.out.println(queryString);
						query=new Query(queryString);
						System.out.println(query.hasSolution());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Writes a set (hashtable) of Prolog predicates into a file
	/**
	 * Writes a set (hashtable) of Prolog predicates into a file
	 *  
	 * @param prologPredicates
	 * @param prologFileName
	 */
	public void writeCasToPrologFile(Hashtable<String,Vector<String>> prologPredicates, String prologFileName){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(prologFileName));
			String predicateString;
			// add module information
			out.write(":- module(cas,[]).\n\n");
			out.write(":-dynamic(id/1).\n");
			out.write(":-assert(id("+this.getFSSequence()+")).\n\n");
			for (Enumeration ePn = predicateNames.elements(); ePn.hasMoreElements();){
				String pn = (String)(ePn.nextElement());
				out.write(":-dynamic('cas_"+pn+"'/2).\n");
				out.write(":-discontiguous('cas_"+pn+"'/2).\n");
				out.write(":-retractall('cas_"+pn+"'/2).\n");
			}
			out.write("\n");
			for (Enumeration enfs = idPredicates.elements(); enfs.hasMoreElements();){
				String id = (String)(enfs.nextElement());
				Vector<String> v = (Vector<String>)prologPredicates.get(id);;
				for (Enumeration e2 = v.elements(); e2.hasMoreElements();){
					predicateString = (String) e2.nextElement();
					out.write(predicateString+".\n");
				}
			}				
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Retrieve the Prolog query results
	/**
	 * Retrieve the Prolog query results
	 * @param result
	 * @param cas
	 */
	public void retrievePrologQueryResults(Compound result, CAS cas){
		try {
			Vector<Term> nfsVector=this.parsePrologList(result);  // transform the Prolog list of feature structures into a Java vector of prolog terms - each term is of the form: annotation(Id,Type,ListFeatures) 
			for (Enumeration enfs = nfsVector.elements(); enfs.hasMoreElements();){ // Extract the result from the Prolog term result and add the FeatureStructure's to the cas
				Compound c = (Compound)(enfs.nextElement());				// Each FeatureStructure has the structure: annotation(Identifier,Type,List of features of the form feature(FeatureName,FeatureValue))
				int annotationId=c.arg(1).intValue();						// the feature's identifier
				String annotationType=this.transformToString(c.arg(2)); 	// the feature's type
				TypeSystem ts = cas.getTypeSystem();
				Type type = ts.getType(annotationType);
				FeatureStructure fs = cas.createFS(type);
				this.fSs.add(this.new FSMapping(fs,annotationId));			// add the new FeatureStructure to the vector of FSMappings: fSs
				Vector<Term> features=this.parsePrologList(c.arg(3));		// iterate over the vector of features and assign the values in the FeatureStructure fs
				for (Enumeration eFeat = features.elements(); eFeat.hasMoreElements();){
					Compound featureTerm = (Compound)eFeat.nextElement();
					String featureName=this.transformToString(featureTerm.arg(1));
					Feature feature = type.getFeatureByBaseName(featureName);
					if (feature==null) {
						// empty feature => do nothing
					}else if (feature.getRange().isPrimitive()){ // primitive value: integer, string, boolean, float
						String featureValue=this.transformToString(featureTerm.arg(2));
						fs.setFeatureValueFromString(feature,(String)featureValue);
					}else if (feature.getRange().isArray()) { // array
						if (feature.getRange().getName().equals("uima.tcas.Annotation[]")) {	//handle array of references
							Vector<Term> fsVector=this.parsePrologList((Term)(featureTerm.arg(2)));
							ArrayFS newFSArray = cas.createArrayFS(fsVector.size());
							int pos=0;
							for (Enumeration eFSArray = fsVector.elements(); eFSArray.hasMoreElements();){
								try{
									String featureValue=this.transformToString((Term)(eFSArray.nextElement()));
									int refId=Integer.parseInt((String)featureValue);
									FeatureStructure value = this.memberFS(refId);
									newFSArray.set(pos, value);
									pos++;
								}catch(Exception refE){
									refE.printStackTrace();
								}
							}
							fs.setFeatureValue(feature,newFSArray);
						}else if (CAS.TYPE_NAME_STRING_ARRAY.equals(feature.getRange().getName())){
							Vector<Term> stringVector=this.parsePrologList((Term)(featureTerm.arg(2)));
							String[] sArray=new String[stringVector.size()];
							int pos=0;
							int length=stringVector.size();
							for (Enumeration eStringArray = stringVector.elements(); eStringArray.hasMoreElements();){
								try{
									sArray[pos]=this.transformToString((Term)(eStringArray.nextElement()));
									pos++;
								}catch(Exception refE){
									refE.printStackTrace();
								}
							}
							StringArray newArray = (StringArray)cas.createStringArrayFS(stringVector.size());
							newArray.copyFromArray(sArray,0,0,length);
							fs.setFeatureValue(feature,newArray);
						}else if (CAS.TYPE_NAME_INTEGER_ARRAY.equals(feature.getRange().getName())){
							Vector<Term> stringVector=this.parsePrologList((Term)(featureTerm.arg(2)));
							String[] sArray=new String[stringVector.size()];
							int pos=0;
							int length=stringVector.size();
							for (Enumeration eStringArray = stringVector.elements(); eStringArray.hasMoreElements();){
								try{
									sArray[pos]=this.transformToString((Term)(eStringArray.nextElement()));
									pos++;
								}catch(Exception refE){
									refE.printStackTrace();
								}
							}
							IntArrayFS newArray = (IntArrayFS)cas.createIntArrayFS(stringVector.size());
							newArray.copyFromArray(sArray,0,0,length);
							fs.setFeatureValue(feature,newArray);
						}else{
							//newArray.copyFromArray(src, srcOffset, destOffset, length)
							//Type arg = feature.getRange().getComponentType();
							FeatureStructure value = null;
							fs.setFeatureValue(feature,value);
						}
					}else{ // reference to an annotation
						try{
							String featureValue=this.transformToString(featureTerm.arg(2));
							int refId=Integer.parseInt((String)featureValue);
							FeatureStructure value = this.memberFS(refId);
							fs.setFeatureValue(feature,value);
						}catch(Exception refE){
							//refE.printStackTrace();
						}
					}
				}
				try{
					((Annotation)fs).addToIndexes();
				}catch(Exception annoE){
					System.out.println("feature cannot be added");
					annoE.printStackTrace();
				}
			}

		} catch(Exception e) {
			System.out.println("some conversion error");
			e.printStackTrace();
		}
	}
	/**
	 * Test suite for the casToProlog(CAS) method 
	 */
	public static void main(String[] args) {
		Logger logger = UIMAFramework.getLogger(PrologCAS.class);
		//logger.log(Level.ALL,"PrologCASPool.main(args) started: ");
		try {
			String typeFileName= "descriptors/GermandeliProduct.xml";
			String inputFileName= "results/PrologTest.xml";;
			String prologFileName= "results/PrologTest.pl";
			String newRuleFileName= "empty";
			String outputFileName=new String();
			/*
			Options options = new Options();
			options.addOption("t", true, "Type file");
			options.addOption("i", true, "Input file");
			options.addOption("p", true, "Prolog file");
			options.addOption("r", true, "Prolog rule file");
			options.addOption("o", true, "Output file");
			CommandLineParser clp = new GnuParser();
			CommandLine cl = clp.parse(options, args);
			if (cl.hasOption("t")) {
				typeFileName = cl.getOptionValue("t");
			}
			if (cl.hasOption("i")) {
				inputFileName = cl.getOptionValue("i");
			}
			if (cl.hasOption("p")) {
				prologFileName = cl.getOptionValue("p");
			}
			if (cl.hasOption("r")) {
				newRuleFileName = cl.getOptionValue("r");
			}
			if (cl.hasOption("o")) {
				outputFileName = cl.getOptionValue("o");
			}*/
			System.out.println("typeFileName: "+typeFileName);
			System.out.println("inputFileName: "+inputFileName);
			System.out.println("prologFileName: "+prologFileName);
			System.out.println("newRuleFileName: "+newRuleFileName);
			System.out.println("outputFileName: "+outputFileName);
			
			PrologCAS pCAS=PrologCAS.getInstance(newRuleFileName);
			
			// read an UIMA type file and an xcas and deserialize into a UIMA CAS
			CAS cas=pCAS.deserialize(typeFileName,inputFileName);
			
			// transforms the CAS into prolog predicates 
			Hashtable<String,Vector<String>> prologPredicates = pCAS.casToProlog(cas.getJCas());

			// writes the prolog predicates into a file
			if(prologFileName!=null){
				pCAS.writeCasToPrologFile(prologPredicates, prologFileName);
			}
			
			// transfer the cas to Prolog
			pCAS.transferCasToProlog(prologPredicates);
			
			//mycas
			Variable X = new Variable();
			Variable Y = new Variable();
			Variable Z = new Variable();
			Query testQuery = new Query("cas:property(X,Y,Z)");
			Hashtable[] solutions = testQuery.allSolutions();
			for (int i = 0; i < solutions.length;i++) {
				System.out.print("X: "+solutions[i].get("X")+" ");
				System.out.print("Y: "+solutions[i].get("Y")+" ");
				System.out.println("Z: "+solutions[i].get("Z")+" ");
			}
			/*
			// query the file to get the new "FeatureStructure"s
			String queryRuleString=new String("queryPrologCas(L)");
			String queryVariable=new String("L");
			Query queryRule=new Query(queryRuleString);
			//Compound result = (Compound)(queryRule.oneSolution().get(queryVariable));
			*//*
			// Retrieve the new feature structures from the Prolog result and add them into the cas
			pCAS.retrievePrologQueryResults(result,cas);
			
			// Serialize the UIMA CAS into an "xmi" file
			File xcasOutFile=new File(outputFileName);
			FileOutputStream xcasOutStream = new FileOutputStream(xcasOutFile);
			XmlCasSerializer.serialize(cas,xcasOutStream);
			if (xcasOutStream != null) xcasOutStream.close();*/
		} catch(Exception e) {
				e.printStackTrace();
		}
		System.out.println("The pcas was annotated. End.");
	}
}