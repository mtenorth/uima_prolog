/**
 * PrologAnnotator - UIMA Annotator for UIMA CAS Prolog predicates
 */
package prolog_cas;

import java.util.Hashtable;
import java.util.Vector;
import jpl.Compound;
import jpl.JPL;
import jpl.Query;
import org.apache.uima.UIMAFramework;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

/**
 * @author Paul Fodor 
 * 
 */
public class PrologCASAnnotator extends JCasAnnotator_ImplBase {
  
  private static final String PARAM_RULE_FILE = "RuleFile";
  
  private String ruleFileName;
  
	@Override
  public void initialize(UimaContext aUimaContext) throws ResourceInitializationException {
    // TODO Auto-generated method stub
    super.initialize(aUimaContext);
    ruleFileName = (String)aUimaContext.getConfigParameterValue(PARAM_RULE_FILE);
  }

  public void process(JCas aJCas){
		CAS cas = aJCas.getCas();
		Logger logger = UIMAFramework.getLogger(PrologCAS.class);
		logger.log(Level.ALL,"PrologCASAnnotator.process(JCas aJCas) started: ");
		System.out.println("PrologCASAnnotator.process(JCas aJCas) started: ");
		PrologCAS pCAS=PrologCAS.getInstance(ruleFileName);
		
		try {
			// transforms the CAS into prolog predicates 
			Hashtable<String,Vector<String>> prologPredicates = pCAS.casToProlog(cas.getJCas());

			// write the prolog translation to a file for debug purposes only
			String prologFileName=new String("../prolog_cas/prolog/cas.pl");
			// writes the prolog predicates into a file
			if(prologFileName!=null){
				pCAS.writeCasToPrologFile(prologPredicates, prologFileName);
			}

			// initialize the Prolog engine
			JPL.init();
			
			// transfer the cas to Prolog
			pCAS.transferCasToProlog(prologPredicates);

			// query the file to get the new "FeatureStructure"s
			String queryRuleString=new String("queryPrologCas(L)");
			String queryVariable=new String("L");
			Query queryRule=new Query(queryRuleString);
			Compound result = (Compound)(queryRule.oneSolution().get(queryVariable));
			
			// Retrieve the new feature structures from the Prolog result and add them into the cas
			pCAS.retrievePrologQueryResults(result,cas);
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("PrologCASAnnotator.process(JCas aJCas) ended. ");
	}
}