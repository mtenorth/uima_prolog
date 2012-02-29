package Germandeli;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import net.htmlparser.jericho.*;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.StringList;

public class GermanDeliProductAE extends JCasAnnotator_ImplBase {
//private Pattern name = Pattern.compile("<div id=\"item-contenttitle\"><div class=\"title\"><div class=\"title-inner\">([^<]+)</div>");
//private Pattern hsensitiv = Pattern.compile("<div class=\"perishable\"><img src=\"([^\"]+)");
//private Pattern brand = Pattern.compile("class=\"brand\">.*?<td>([^<]+)");
//private Pattern id = Pattern.compile("Item #:</th><td>([^<]+)</td>");
//private Pattern weight = Pattern.compile("Weight/Size:</th><td>([^<]+)</td>");
//private Pattern price = Pattern.compile("Our Price:</th><td>([^<]+)</td>");
//private Pattern ingredients = Pattern.compile("<div name=\"ingredients\".*?-->([^<]+)");
//private Pattern description = Pattern.compile("name=\"description\".*?-->([^<]+)");
private Pattern descrIngred = Pattern.compile("Ingredients: (.*?)\\.");
private final String sensitiv1 = "gdcom_2194_304287"; // 1 not sens.
private final String sensitiv2 = "gdcom_2194_844664"; // 2 heat sens.
private final String sensitiv3 = "not defined"; //3
private final String sensitiv4 = "gdcom_2194_59878433"; //4 frozen

/*
 *Specifiers to find the feature in html source.
 *ToDo: Make a template
 */
private String rootAttributeSpecifier = "id";
private String rootAttributeSpecifierValue = "bodycontent";
private String nameAttributeSpecifier = "class";
private String nameAttributeSpecifierValue = "title-inner";
private String imageAttributeSpecifier = "class";
private String imageAttributeSpecifierValue = "item-images";
private String persAttributeSpecifier = "class";
private String persAttributeSpecifierValue = "perishable";
private String brandAttributeSpecifier = "class";
private String brandAttributeSpecifierValue = "brand";
private String weightSizeAttributeSpecifier = "class";
private String weightSizeAttributeSpecifierValue = "weight";
private String idAttributeSpecifier = "class";
private String idAttributeSpecifierValue = "code";
private String priceAttributeSpecifier = "class";
private String priceAttributeSpecifierValue = "sale-price";
private String descrAttributeSpecifier = "name";
private String descrAttributeSpecifierValue = "description";
private String ingrAttributeSpecifier = "name";
private String ingrAttributeSpecifierValue = "ingredients";

/*
 * Flags
 */
private Boolean nameFound = false;
private Boolean imageFound = false;
private Boolean persFound = false;
private Boolean brandFound = false;
private Boolean weightFound = false;
private Boolean idFound = false;
private Boolean priceFound = false;
private Boolean descrFound = false;
private Boolean ingredientsFound = false;




	

	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String docText = aJCas.getDocumentText();
		initFlags();
		//System.out.println(hyperLinkPattern.pattern());
		System.out.println("++");
		/*
		 * 
		 *Parse the document
		 *To do:
		 *Implement a automaton extractor, which extracts features with an automate.
		 *The automate should be given as a parameter.
		 *
		 */
		Source src = new Source(docText);
		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister();
		src.fullSequentialParse();
		
		List<Element> orderElements = src.getAllElements(HTMLElementName.DIV);
		
		//search for the body or root Elment 
		Element bodyElement = null;
		for (Element tmpElement : orderElements) {
			String bodyAttribute = tmpElement.getAttributeValue(rootAttributeSpecifier);
			if (bodyAttribute == null) continue; 
			else {
				if (bodyAttribute.equals(rootAttributeSpecifierValue)) {
					bodyElement = tmpElement;
					break;
				}
			}
		}
		//check if root was found
		if (bodyElement != null) {
			List<Element> bodyElements = bodyElement.getAllElements();
			int count = 0;
			String productName = null;
			String brand = null;
			String id = null;
			String weightSize = null;
			String price = null;
			String description = null;
			String ingredients = null;
			String pers = null;
			Product product = new Product(aJCas);
			aJCas.setDocumentLanguage("en");
			for (Element tmpElement : bodyElements) {
				
				//Get the name 
				if (!nameFound) {
					String nameElementAttr = tmpElement.getAttributeValue(nameAttributeSpecifier);
					//System.out.println(nameElementAttr);
					product.setBegin(tmpElement.getBegin());
					if (nameElementAttr != null) {
						if (nameElementAttr.equals(nameAttributeSpecifierValue)) {
							productName = tmpElement.getContent().getTextExtractor().toString();
							System.out.println("Extracted Product: " + productName);
							nameFound = true;
						}
					}
				}
				
				
				//get image Path
				if (!imageFound && nameFound) {
					String persElementAttr = tmpElement.getAttributeValue(imageAttributeSpecifier);
					if (persElementAttr != null) {
						if (persElementAttr.equals(imageAttributeSpecifierValue)) {
							//check for the image
							List<Element> innerImages = tmpElement.getAllElements(HTMLElementName.IMG);
							if (!innerImages.isEmpty()) {
								Element img = innerImages.get(0);
								pers = img.getAttributeValue("src");
								if (pers != null) {
									System.out.println("Extracted imageurl:" + pers);
									/*
									try {
										ScanProductPage.loadImageAndSaveToFile(imgSrc, productName.substring(0, 15));
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}*/
									imageFound = true;
								}
							}
						}
					}
				}
				//Get the peris
				if (!persFound) {
					String persElementAttr = tmpElement.getAttributeValue(persAttributeSpecifier);
					if (persElementAttr != null) {
						if (persElementAttr.equals(persAttributeSpecifierValue)) {
							//check for the image
							List<Element> innerImages = tmpElement.getAllElements(HTMLElementName.IMG);
							if (!innerImages.isEmpty()) {
								Element img = innerImages.get(0);
								String imgSrc = img.getAttributeValue("src");
								if (imgSrc != null) {
									System.out.println("Extracted perimageurl " + imgSrc);
									persFound = true;
								}
							}
						}
					}
				}
				
				//Get the brand
				if (!brandFound) {
					String brandElementAttr = tmpElement.getAttributeValue(brandAttributeSpecifier);
					if (brandElementAttr != null) {
						if (brandElementAttr.equals(brandAttributeSpecifierValue) && tmpElement.getName().equals(HTMLElementName.TR)) {
							brand = tmpElement.getContent().getTextExtractor().toString();
							brand = brand.substring(7);
							System.out.println("Extracted Brand: " + brand);
							brandFound = true;
						}
					}
				}
				
				//Get the id
				if (!idFound) {
					String idElementAttr = tmpElement.getAttributeValue(idAttributeSpecifier);
					if (idElementAttr != null) {
						if (idElementAttr.equals(idAttributeSpecifierValue)  && tmpElement.getName().equals(HTMLElementName.TR)) {
							id = tmpElement.getContent().getTextExtractor().toString();
							id = id.substring(8);
							System.out.println("Extracted id: " + id);
							idFound = true;
						}
					}
				}
				
				//Get the weightSize
				if (!weightFound) {
					String weightSizeElementAttr = tmpElement.getAttributeValue(weightSizeAttributeSpecifier);
					if (weightSizeElementAttr != null) {
						if (weightSizeElementAttr.equals(weightSizeAttributeSpecifierValue) && tmpElement.getName().equals(HTMLElementName.TR)) {
							weightSize = tmpElement.getContent().getTextExtractor().toString();
							weightSize = weightSize.substring(13);
							System.out.println("Extracted Weight: " + weightSize);
							weightFound = true;
						}
					}
				}

				//Get the price
				if (!priceFound) {
					String priceElementAttr = tmpElement.getAttributeValue(priceAttributeSpecifier);
					if (priceElementAttr != null) {
						if (priceElementAttr.equals(priceAttributeSpecifierValue) && tmpElement.getName().equals(HTMLElementName.TR)) {
							price = tmpElement.getContent().getTextExtractor().toString();
							price = price.substring(12);
							System.out.println("Extracted Price: " + price);
							priceFound = true;
						}
					}
				}
				
				//Get the descripton
				if (!descrFound) {
					String descrElementAttr = tmpElement.getAttributeValue(descrAttributeSpecifier);
					if (descrElementAttr != null) {
						if (descrElementAttr.equals(descrAttributeSpecifierValue) && tmpElement.getName().equals(HTMLElementName.DIV)) {
							description = tmpElement.getContent().getTextExtractor().toString();
							System.out.println("Extracted description: " + description);
							descrFound = true;
							product.setEnd(tmpElement.getEnd());
						}
					}
				}
				//Get the ingredients
				if (!ingredientsFound) {
					String ingrElementAttr = tmpElement.getAttributeValue(ingrAttributeSpecifier);
					if (ingrElementAttr != null) {
						if (ingrElementAttr.equals(ingrAttributeSpecifierValue) && tmpElement.getName().equals(HTMLElementName.DIV)) {
							ingredients = tmpElement.getContent().getTextExtractor().toString();
							System.out.println("Extracted ingredients:" + ingredients);
							ingredientsFound = true;
							product.setEnd(tmpElement.getEnd());
						}
					}
				}
				/*
				 * Token extracting done
				 * check if everything is extracted
				 */				
				count++; //debugging
			}
			//System.out.println(count);
			/*
			 * Token extracting done
			 * check if everything is extracted
			 */		
			
			if (nameFound) {
				product.setName(productName);
			}
			if (persFound) {
				if (pers.endsWith(sensitiv1)) {
					product.setPerishability((short) 1);
					//System.out.println(1);
				}
				else if (pers.endsWith(sensitiv2)) {
					product.setPerishability((short) 2);
					//System.out.println(2);
				}
				else if (pers.endsWith(sensitiv3)) {
					product.setPerishability((short) 3);
					//System.out.println(3);
				}
				else if	(pers.endsWith(sensitiv4)) {
					product.setPerishability((short) 4);
					//System.out.println(4);
				}
				else {
					product.setPerishability((short) 5);
					//System.out.println(5);
				}
			}
			if (brandFound) {
				product.setBrand(brand);
			}
			if (idFound) {
				product.setId(id);
			}
			if (weightFound) {
				float weightFloat[] = getGAndOZ(weightSize);
				product.setWeightInG(weightFloat[0]);
				product.setWeightInOZ(weightFloat[1]);
			}
			if (priceFound) {
				product.setPrice(price);
			}
			if (descrFound) {
				product.setDescrtiption(description);
			}
			if (!ingredientsFound && descrFound) {
				Matcher ingreMatcher = descrIngred.matcher(description);
				if (ingreMatcher.find()) {
					product.setIngredients(makeStringList(ingreMatcher.group(1),aJCas));
					System.out.println("Extracted with Regex from descr:" + ingreMatcher.group(1));
				}
			}
			if (ingredientsFound) {
				product.setIngredients(makeStringList(ingredients,aJCas));
			}
			product.addToIndexes();
		}
		
		/*
			
			//nameElement.
			String cssClass = nameElement.getAttributeValue("class");
			//System.out.println(cssClass);
			if (cssClass == null) continue;
			System.out.println(cssClass);
			if (!cssClass.equals("title-inner")) continue;
			//Name tag found
			else {
				String productName = nameElement.getContent().getTextExtractor().toString();
				System.out.println("Extracted with tag identifiers: " + productName + cssClass);
			}
		}*/
		
		
		
		
		
		
		/*
		int begin = 0;
		Product product = new Product(aJCas);
		Matcher nameMatcher = name.matcher(docText);
		//System.out.println(docText);
		if (nameMatcher.find()) {
			System.out.println(nameMatcher.group(1));
			product.setName(nameMatcher.group(1));
			product.setBegin(nameMatcher.start(1));
			begin = nameMatcher.end(1);
			Matcher sensMatcher = hsensitiv.matcher(docText.substring(begin,docText.length()));
			if (sensMatcher.find()) {
				System.out.println(sensMatcher.group(1));
				if (sensMatcher.group(1).endsWith(sensitiv1)) {
					product.setPerishability((short) 1);
					//System.out.println(1);
				}
				else if (sensMatcher.group(1).endsWith(sensitiv2)) {
					product.setPerishability((short) 2);
					//System.out.println(2);
				}
				else if (sensMatcher.group(1).endsWith(sensitiv3)) {
					product.setPerishability((short) 3);
					//System.out.println(3);
				}
				else if	(sensMatcher.group(1).endsWith(sensitiv4)) {
					product.setPerishability((short) 4);
					//System.out.println(4);
				}
				else {
					product.setPerishability((short) 5);
					//System.out.println(5);
				}
				begin = sensMatcher.end(1);
			}
			Matcher brandMatcher = brand.matcher(docText.substring(begin,docText.length()));
			if (brandMatcher.find()) {
				System.out.println(brandMatcher.group(1));
				product.setBrand(brandMatcher.group(1));
				begin = brandMatcher.end(1);
			}
			Matcher idMatcher = id.matcher(docText.substring(begin,docText.length()));
			if (idMatcher.find()) {
				System.out.println(idMatcher.group(1));
				product.setId(idMatcher.group(1));
				begin = idMatcher.end(1);
				
			}
			Matcher weightMatcher = weight.matcher(docText.substring(begin,docText.length()));
			if (weightMatcher.find()) {
				System.out.println(weightMatcher.group(1));
				float weight[] = getGAndOZ(weightMatcher.group(1));
				product.setWeightInG(weight[0]);
				product.setWeightInOZ(weight[1]);
				begin = weightMatcher.end(1);
			}
			Matcher priceMatcher = price.matcher(docText.substring(begin,docText.length()));
			
			if (priceMatcher.find()) {
				System.out.println(priceMatcher.group(1));
				product.setPrice(priceMatcher.group(1));
				begin = priceMatcher.end(1);
			}
			Matcher descripMatcher = description.matcher(docText.substring(begin,docText.length()));
			if (descripMatcher.find()) {
				System.out.println("finddes" + descripMatcher.group(1));
				product.setDescrtiption(descripMatcher.group(1));
				begin = descripMatcher.end(1);
			}
			Matcher ingreMatcher = ingredients.matcher(docText.substring(begin,docText.length()));
			if (ingreMatcher.find()) {
				//System.out.println(ingreMatcher.group(1).trim());
				product.setIngredients(makeStringList(ingreMatcher.group(1),aJCas));
				begin = ingreMatcher.end(1);
			}
			else {
				ingreMatcher = descrIngred.matcher(docText.substring(descripMatcher.start(1),docText.length()));
				if (ingreMatcher.find()) {
					product.setIngredients(makeStringList(ingreMatcher.group(1),aJCas));
					begin = ingreMatcher.end(1);
				}
				
			}
			product.setEnd(begin);
			product.addToIndexes();
		}*/
	}
	/**
	 * Makes a list from a string 
	 * @param input
	 * @param aJCas
	 * @return
	 */
	private StringArray makeStringList(String input,JCas aJCas) {
		String[] strArray;
		input = input.trim();
		strArray = input.split(",");
		if (strArray != null) {
			for(int i = 0; i < strArray.length; i++) {
				strArray[i].trim();
			}
		}
		StringArray result = new StringArray(aJCas,strArray.length); 
		result.copyFromArray(strArray, 0, 0, strArray.length);
		return result;
	}
	/**
	 * Gets the G and OZ from a string
	 * @param input
	 * @return 1 = G,2 = OZ
	 */
	private float[] getGAndOZ(String input) {
		float[] result = new float[2];
		Pattern gPattern = Pattern.compile("(\\d+)g");
		Matcher gM = gPattern.matcher(input);
		if (gM.find()) {
			result[0] = Float.valueOf(gM.group(1));
			System.out.println(gM.group(1));
		}
		else {
			result[0] = 0;
		}		
		Pattern ozPattern = Pattern.compile("(\\d+)oz");
		Matcher ozM = ozPattern.matcher(input);
		if (ozM.find()) {
			result[1] = Float.valueOf(ozM.group(1));
		}
		else {
			result[1] = 0;
		}
		return result;
	}
	private void initFlags() {
		 nameFound = false;
		 persFound = false;
		 brandFound = false;
		 weightFound = false;
		 idFound = false;
		 priceFound = false;
		 descrFound = false;
		 imageFound = false;
		 ingredientsFound = false;
	}

}
