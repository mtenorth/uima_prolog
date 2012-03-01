
/* First created by JCasGen Wed Feb 29 14:15:25 CET 2012 */
package Germandeli;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Wed Feb 29 14:15:25 CET 2012
 * @generated */
public class Product_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Product_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Product_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Product(addr, Product_Type.this);
  			   Product_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Product(addr, Product_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = Product.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("Germandeli.Product");
 
  /** @generated */
  final Feature casFeat_ingredients;
  /** @generated */
  final int     casFeatCode_ingredients;
  /** @generated */ 
  public int getIngredients(int addr) {
        if (featOkTst && casFeat_ingredients == null)
      jcas.throwFeatMissing("ingredients", "Germandeli.Product");
    return ll_cas.ll_getRefValue(addr, casFeatCode_ingredients);
  }
  /** @generated */    
  public void setIngredients(int addr, int v) {
        if (featOkTst && casFeat_ingredients == null)
      jcas.throwFeatMissing("ingredients", "Germandeli.Product");
    ll_cas.ll_setRefValue(addr, casFeatCode_ingredients, v);}
    
   /** @generated */
  public String getIngredients(int addr, int i) {
        if (featOkTst && casFeat_ingredients == null)
      jcas.throwFeatMissing("ingredients", "Germandeli.Product");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_ingredients), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_ingredients), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_ingredients), i);
  }
   
  /** @generated */ 
  public void setIngredients(int addr, int i, String v) {
        if (featOkTst && casFeat_ingredients == null)
      jcas.throwFeatMissing("ingredients", "Germandeli.Product");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_ingredients), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_ingredients), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_ingredients), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_price;
  /** @generated */
  final int     casFeatCode_price;
  /** @generated */ 
  public String getPrice(int addr) {
        if (featOkTst && casFeat_price == null)
      jcas.throwFeatMissing("price", "Germandeli.Product");
    return ll_cas.ll_getStringValue(addr, casFeatCode_price);
  }
  /** @generated */    
  public void setPrice(int addr, String v) {
        if (featOkTst && casFeat_price == null)
      jcas.throwFeatMissing("price", "Germandeli.Product");
    ll_cas.ll_setStringValue(addr, casFeatCode_price, v);}
    
  
 
  /** @generated */
  final Feature casFeat_brand;
  /** @generated */
  final int     casFeatCode_brand;
  /** @generated */ 
  public String getBrand(int addr) {
        if (featOkTst && casFeat_brand == null)
      jcas.throwFeatMissing("brand", "Germandeli.Product");
    return ll_cas.ll_getStringValue(addr, casFeatCode_brand);
  }
  /** @generated */    
  public void setBrand(int addr, String v) {
        if (featOkTst && casFeat_brand == null)
      jcas.throwFeatMissing("brand", "Germandeli.Product");
    ll_cas.ll_setStringValue(addr, casFeatCode_brand, v);}
    
  
 
  /** @generated */
  final Feature casFeat_name;
  /** @generated */
  final int     casFeatCode_name;
  /** @generated */ 
  public String getName(int addr) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "Germandeli.Product");
    return ll_cas.ll_getStringValue(addr, casFeatCode_name);
  }
  /** @generated */    
  public void setName(int addr, String v) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "Germandeli.Product");
    ll_cas.ll_setStringValue(addr, casFeatCode_name, v);}
    
  
 
  /** @generated */
  final Feature casFeat_weightInG;
  /** @generated */
  final int     casFeatCode_weightInG;
  /** @generated */ 
  public float getWeightInG(int addr) {
        if (featOkTst && casFeat_weightInG == null)
      jcas.throwFeatMissing("weightInG", "Germandeli.Product");
    return ll_cas.ll_getFloatValue(addr, casFeatCode_weightInG);
  }
  /** @generated */    
  public void setWeightInG(int addr, float v) {
        if (featOkTst && casFeat_weightInG == null)
      jcas.throwFeatMissing("weightInG", "Germandeli.Product");
    ll_cas.ll_setFloatValue(addr, casFeatCode_weightInG, v);}
    
  
 
  /** @generated */
  final Feature casFeat_weightInOZ;
  /** @generated */
  final int     casFeatCode_weightInOZ;
  /** @generated */ 
  public float getWeightInOZ(int addr) {
        if (featOkTst && casFeat_weightInOZ == null)
      jcas.throwFeatMissing("weightInOZ", "Germandeli.Product");
    return ll_cas.ll_getFloatValue(addr, casFeatCode_weightInOZ);
  }
  /** @generated */    
  public void setWeightInOZ(int addr, float v) {
        if (featOkTst && casFeat_weightInOZ == null)
      jcas.throwFeatMissing("weightInOZ", "Germandeli.Product");
    ll_cas.ll_setFloatValue(addr, casFeatCode_weightInOZ, v);}
    
  
 
  /** @generated */
  final Feature casFeat_perishability;
  /** @generated */
  final int     casFeatCode_perishability;
  /** @generated */ 
  public short getPerishability(int addr) {
        if (featOkTst && casFeat_perishability == null)
      jcas.throwFeatMissing("perishability", "Germandeli.Product");
    return ll_cas.ll_getShortValue(addr, casFeatCode_perishability);
  }
  /** @generated */    
  public void setPerishability(int addr, short v) {
        if (featOkTst && casFeat_perishability == null)
      jcas.throwFeatMissing("perishability", "Germandeli.Product");
    ll_cas.ll_setShortValue(addr, casFeatCode_perishability, v);}
    
  
 
  /** @generated */
  final Feature casFeat_id;
  /** @generated */
  final int     casFeatCode_id;
  /** @generated */ 
  public String getId(int addr) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "Germandeli.Product");
    return ll_cas.ll_getStringValue(addr, casFeatCode_id);
  }
  /** @generated */    
  public void setId(int addr, String v) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "Germandeli.Product");
    ll_cas.ll_setStringValue(addr, casFeatCode_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_description;
  /** @generated */
  final int     casFeatCode_description;
  /** @generated */ 
  public String getDescription(int addr) {
        if (featOkTst && casFeat_description == null)
      jcas.throwFeatMissing("description", "Germandeli.Product");
    return ll_cas.ll_getStringValue(addr, casFeatCode_description);
  }
  /** @generated */    
  public void setDescription(int addr, String v) {
        if (featOkTst && casFeat_description == null)
      jcas.throwFeatMissing("description", "Germandeli.Product");
    ll_cas.ll_setStringValue(addr, casFeatCode_description, v);}
    
  
 
  /** @generated */
  final Feature casFeat_sourceUrl;
  /** @generated */
  final int     casFeatCode_sourceUrl;
  /** @generated */ 
  public String getSourceUrl(int addr) {
        if (featOkTst && casFeat_sourceUrl == null)
      jcas.throwFeatMissing("sourceUrl", "Germandeli.Product");
    return ll_cas.ll_getStringValue(addr, casFeatCode_sourceUrl);
  }
  /** @generated */    
  public void setSourceUrl(int addr, String v) {
        if (featOkTst && casFeat_sourceUrl == null)
      jcas.throwFeatMissing("sourceUrl", "Germandeli.Product");
    ll_cas.ll_setStringValue(addr, casFeatCode_sourceUrl, v);}
    
  
 
  /** @generated */
  final Feature casFeat_supertypes;
  /** @generated */
  final int     casFeatCode_supertypes;
  /** @generated */ 
  public int getSupertypes(int addr) {
        if (featOkTst && casFeat_supertypes == null)
      jcas.throwFeatMissing("supertypes", "Germandeli.Product");
    return ll_cas.ll_getRefValue(addr, casFeatCode_supertypes);
  }
  /** @generated */    
  public void setSupertypes(int addr, int v) {
        if (featOkTst && casFeat_supertypes == null)
      jcas.throwFeatMissing("supertypes", "Germandeli.Product");
    ll_cas.ll_setRefValue(addr, casFeatCode_supertypes, v);}
    
   /** @generated */
  public String getSupertypes(int addr, int i) {
        if (featOkTst && casFeat_supertypes == null)
      jcas.throwFeatMissing("supertypes", "Germandeli.Product");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_supertypes), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_supertypes), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_supertypes), i);
  }
   
  /** @generated */ 
  public void setSupertypes(int addr, int i, String v) {
        if (featOkTst && casFeat_supertypes == null)
      jcas.throwFeatMissing("supertypes", "Germandeli.Product");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_supertypes), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_supertypes), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_supertypes), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Product_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_ingredients = jcas.getRequiredFeatureDE(casType, "ingredients", "uima.cas.StringArray", featOkTst);
    casFeatCode_ingredients  = (null == casFeat_ingredients) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ingredients).getCode();

 
    casFeat_price = jcas.getRequiredFeatureDE(casType, "price", "uima.cas.String", featOkTst);
    casFeatCode_price  = (null == casFeat_price) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_price).getCode();

 
    casFeat_brand = jcas.getRequiredFeatureDE(casType, "brand", "uima.cas.String", featOkTst);
    casFeatCode_brand  = (null == casFeat_brand) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_brand).getCode();

 
    casFeat_name = jcas.getRequiredFeatureDE(casType, "name", "uima.cas.String", featOkTst);
    casFeatCode_name  = (null == casFeat_name) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_name).getCode();

 
    casFeat_weightInG = jcas.getRequiredFeatureDE(casType, "weightInG", "uima.cas.Float", featOkTst);
    casFeatCode_weightInG  = (null == casFeat_weightInG) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_weightInG).getCode();

 
    casFeat_weightInOZ = jcas.getRequiredFeatureDE(casType, "weightInOZ", "uima.cas.Float", featOkTst);
    casFeatCode_weightInOZ  = (null == casFeat_weightInOZ) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_weightInOZ).getCode();

 
    casFeat_perishability = jcas.getRequiredFeatureDE(casType, "perishability", "uima.cas.Short", featOkTst);
    casFeatCode_perishability  = (null == casFeat_perishability) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_perishability).getCode();

 
    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.String", featOkTst);
    casFeatCode_id  = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_id).getCode();

 
    casFeat_description = jcas.getRequiredFeatureDE(casType, "description", "uima.cas.String", featOkTst);
    casFeatCode_description  = (null == casFeat_description) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_description).getCode();

 
    casFeat_sourceUrl = jcas.getRequiredFeatureDE(casType, "sourceUrl", "uima.cas.String", featOkTst);
    casFeatCode_sourceUrl  = (null == casFeat_sourceUrl) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_sourceUrl).getCode();

 
    casFeat_supertypes = jcas.getRequiredFeatureDE(casType, "supertypes", "uima.cas.StringArray", featOkTst);
    casFeatCode_supertypes  = (null == casFeat_supertypes) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_supertypes).getCode();

  }
}



    