

/* First created by JCasGen Wed Feb 29 14:15:25 CET 2012 */
package Germandeli;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.cas.StringArray;


/** 
 * Updated by JCasGen Wed Feb 29 14:15:25 CET 2012
 * XML source: /Users/mjakasovic/Documents/ArbeitCoTeSys/uima-prolog/uima-PrologInterface-Examples/descriptors/GermandeliProduct.xml
 * @generated */
public class Product extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Product.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Product() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Product(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Product(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Product(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: ingredients

  /** getter for ingredients - gets 
   * @generated */
  public StringArray getIngredients() {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_ingredients == null)
      jcasType.jcas.throwFeatMissing("ingredients", "Germandeli.Product");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Product_Type)jcasType).casFeatCode_ingredients)));}
    
  /** setter for ingredients - sets  
   * @generated */
  public void setIngredients(StringArray v) {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_ingredients == null)
      jcasType.jcas.throwFeatMissing("ingredients", "Germandeli.Product");
    jcasType.ll_cas.ll_setRefValue(addr, ((Product_Type)jcasType).casFeatCode_ingredients, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for ingredients - gets an indexed value - 
   * @generated */
  public String getIngredients(int i) {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_ingredients == null)
      jcasType.jcas.throwFeatMissing("ingredients", "Germandeli.Product");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Product_Type)jcasType).casFeatCode_ingredients), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Product_Type)jcasType).casFeatCode_ingredients), i);}

  /** indexed setter for ingredients - sets an indexed value - 
   * @generated */
  public void setIngredients(int i, String v) { 
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_ingredients == null)
      jcasType.jcas.throwFeatMissing("ingredients", "Germandeli.Product");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Product_Type)jcasType).casFeatCode_ingredients), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Product_Type)jcasType).casFeatCode_ingredients), i, v);}
   
    
  //*--------------*
  //* Feature: price

  /** getter for price - gets 
   * @generated */
  public String getPrice() {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_price == null)
      jcasType.jcas.throwFeatMissing("price", "Germandeli.Product");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Product_Type)jcasType).casFeatCode_price);}
    
  /** setter for price - sets  
   * @generated */
  public void setPrice(String v) {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_price == null)
      jcasType.jcas.throwFeatMissing("price", "Germandeli.Product");
    jcasType.ll_cas.ll_setStringValue(addr, ((Product_Type)jcasType).casFeatCode_price, v);}    
   
    
  //*--------------*
  //* Feature: brand

  /** getter for brand - gets 
   * @generated */
  public String getBrand() {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_brand == null)
      jcasType.jcas.throwFeatMissing("brand", "Germandeli.Product");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Product_Type)jcasType).casFeatCode_brand);}
    
  /** setter for brand - sets  
   * @generated */
  public void setBrand(String v) {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_brand == null)
      jcasType.jcas.throwFeatMissing("brand", "Germandeli.Product");
    jcasType.ll_cas.ll_setStringValue(addr, ((Product_Type)jcasType).casFeatCode_brand, v);}    
   
    
  //*--------------*
  //* Feature: name

  /** getter for name - gets 
   * @generated */
  public String getName() {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "Germandeli.Product");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Product_Type)jcasType).casFeatCode_name);}
    
  /** setter for name - sets  
   * @generated */
  public void setName(String v) {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "Germandeli.Product");
    jcasType.ll_cas.ll_setStringValue(addr, ((Product_Type)jcasType).casFeatCode_name, v);}    
   
    
  //*--------------*
  //* Feature: weightInG

  /** getter for weightInG - gets 
   * @generated */
  public float getWeightInG() {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_weightInG == null)
      jcasType.jcas.throwFeatMissing("weightInG", "Germandeli.Product");
    return jcasType.ll_cas.ll_getFloatValue(addr, ((Product_Type)jcasType).casFeatCode_weightInG);}
    
  /** setter for weightInG - sets  
   * @generated */
  public void setWeightInG(float v) {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_weightInG == null)
      jcasType.jcas.throwFeatMissing("weightInG", "Germandeli.Product");
    jcasType.ll_cas.ll_setFloatValue(addr, ((Product_Type)jcasType).casFeatCode_weightInG, v);}    
   
    
  //*--------------*
  //* Feature: weightInOZ

  /** getter for weightInOZ - gets 
   * @generated */
  public float getWeightInOZ() {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_weightInOZ == null)
      jcasType.jcas.throwFeatMissing("weightInOZ", "Germandeli.Product");
    return jcasType.ll_cas.ll_getFloatValue(addr, ((Product_Type)jcasType).casFeatCode_weightInOZ);}
    
  /** setter for weightInOZ - sets  
   * @generated */
  public void setWeightInOZ(float v) {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_weightInOZ == null)
      jcasType.jcas.throwFeatMissing("weightInOZ", "Germandeli.Product");
    jcasType.ll_cas.ll_setFloatValue(addr, ((Product_Type)jcasType).casFeatCode_weightInOZ, v);}    
   
    
  //*--------------*
  //* Feature: perishability

  /** getter for perishability - gets 
   * @generated */
  public short getPerishability() {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_perishability == null)
      jcasType.jcas.throwFeatMissing("perishability", "Germandeli.Product");
    return jcasType.ll_cas.ll_getShortValue(addr, ((Product_Type)jcasType).casFeatCode_perishability);}
    
  /** setter for perishability - sets  
   * @generated */
  public void setPerishability(short v) {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_perishability == null)
      jcasType.jcas.throwFeatMissing("perishability", "Germandeli.Product");
    jcasType.ll_cas.ll_setShortValue(addr, ((Product_Type)jcasType).casFeatCode_perishability, v);}    
   
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets 
   * @generated */
  public String getId() {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "Germandeli.Product");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Product_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets  
   * @generated */
  public void setId(String v) {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "Germandeli.Product");
    jcasType.ll_cas.ll_setStringValue(addr, ((Product_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: description

  /** getter for description - gets Product description
   * @generated */
  public String getDescription() {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_description == null)
      jcasType.jcas.throwFeatMissing("description", "Germandeli.Product");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Product_Type)jcasType).casFeatCode_description);}
    
  /** setter for description - sets Product description 
   * @generated */
  public void setDescription(String v) {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_description == null)
      jcasType.jcas.throwFeatMissing("description", "Germandeli.Product");
    jcasType.ll_cas.ll_setStringValue(addr, ((Product_Type)jcasType).casFeatCode_description, v);}    
   
    
  //*--------------*
  //* Feature: sourceUrl

  /** getter for sourceUrl - gets The sourceUrl
   * @generated */
  public String getSourceUrl() {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_sourceUrl == null)
      jcasType.jcas.throwFeatMissing("sourceUrl", "Germandeli.Product");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Product_Type)jcasType).casFeatCode_sourceUrl);}
    
  /** setter for sourceUrl - sets The sourceUrl 
   * @generated */
  public void setSourceUrl(String v) {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_sourceUrl == null)
      jcasType.jcas.throwFeatMissing("sourceUrl", "Germandeli.Product");
    jcasType.ll_cas.ll_setStringValue(addr, ((Product_Type)jcasType).casFeatCode_sourceUrl, v);}    
   
    
  //*--------------*
  //* Feature: supertypes

  /** getter for supertypes - gets The supertypes
   * @generated */
  public StringArray getSupertypes() {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_supertypes == null)
      jcasType.jcas.throwFeatMissing("supertypes", "Germandeli.Product");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Product_Type)jcasType).casFeatCode_supertypes)));}
    
  /** setter for supertypes - sets The supertypes 
   * @generated */
  public void setSupertypes(StringArray v) {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_supertypes == null)
      jcasType.jcas.throwFeatMissing("supertypes", "Germandeli.Product");
    jcasType.ll_cas.ll_setRefValue(addr, ((Product_Type)jcasType).casFeatCode_supertypes, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for supertypes - gets an indexed value - The supertypes
   * @generated */
  public String getSupertypes(int i) {
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_supertypes == null)
      jcasType.jcas.throwFeatMissing("supertypes", "Germandeli.Product");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Product_Type)jcasType).casFeatCode_supertypes), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Product_Type)jcasType).casFeatCode_supertypes), i);}

  /** indexed setter for supertypes - sets an indexed value - The supertypes
   * @generated */
  public void setSupertypes(int i, String v) { 
    if (Product_Type.featOkTst && ((Product_Type)jcasType).casFeat_supertypes == null)
      jcasType.jcas.throwFeatMissing("supertypes", "Germandeli.Product");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Product_Type)jcasType).casFeatCode_supertypes), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Product_Type)jcasType).casFeatCode_supertypes), i, v);}
  }

    