import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/** 

* @Since:         <JDK1.7>                              
* @author：        Zelin Zheng            
* @version：       2013.10.27 
*/ 


/**
 * A class representing the result of a query.
 * <p>
 *  It store the total number of posts as well as the post objects of a specified page.
 * </p>
 * <p>
 *	An external library "Jsoup" is used for HTML parsing.
 * </p>
 * @author  Zelin Zheng
 * @version 2013.10.27
 */
public class QueryResult {
	
	// pageLimit is the number limit of products showed in a page. It is related to the setting of shopping.com, which is 40.
	// pageLimit will be used to judge about whether the page number user asked has already past the maximum page number.
	// For details, see setCurrentPage() function.
	private final int pageLimit = 40;
	
	// Store the total number of return results for the query
	private int number = -1;
	
	// Store the keyword user used for searching
	private String keyword = null;
	
	// Store the page number used asked to show the results
	private int currentPageNumber = 1;
	
	// An array list collection to store all the result objects in the specified page. Post class is used to store each result object.
	private ArrayList<Post> postList = null;
	
	// Store the post number of the specified page. For calculation details, see setCurrentPage() function.
	private int postNumber = -1;
	
	// Store the DOM file for the specified query.
	private Document domTree = null;
	
	// Store the query used for results.
	String query = null;


	/**
     * Description: the constructor of a QueryResult.
     *  First use the keyword used specified to generate the URL by calling generateQuery() function.
     * 	Then it calls the getDom() function to get the DOM file for the query.
     *  At last, if the DOM file is successfully retrieved, retrieveNumber() function is used to extract the total number of results
     *  from the DOM file.
     * @param  keyword: the keyword user specified for searching
     * @return  no return
     * @exception no exception
     */
	public QueryResult (String keyword) {
		// Initialize the keyword
		this.keyword = keyword; 
		// Create the query string based on the keyword
		this.query = this.generateQuery();
		
		// Based on the return int value of getDom() function, we can know whether we successfully get DOM file.
		if(this.getDom()>0) {
			// If yes, we extract the total number of results
			this.number = retrieveNumber();
		}
	}
	
	/**
     * Description: return the total number of results.
     * @param  no parameters
     * @return  a int value for the total number of results
     * @exception no exception
     */
	public int getNumber () {
		return this.number;
	}
	
	/**
     * Description: set the page number, and initialize the postList through using retrievePostList() function.
     * @param  currentPageNumber: an int value from user
     * @return  if the currentPageNumber is within the page number range, return true;else, false.
     * @exception no exception
     */
	public boolean setCurrentPageNumber (int currentPageNumber) {
		
		// To see if the currentPage number is within the range in which each page can have full posts (refers the pageLimit).
		// For example, if the total number of results is 433, the pageLimit is 40, then the range would be 1 to 10.
		if (this.number/this.pageLimit >= currentPageNumber) {
			// In this case, each page would have the number of posts as the pageLimit
			this.currentPageNumber = currentPageNumber;
			this.postNumber = this.pageLimit;
		}	
			// If the total number of resutls can not be devied exactly by the pageLimit and the currentPage equals the page number of last page
			else if (this.number%this.pageLimit > 0 && (this.number/this.pageLimit + 1) ==currentPageNumber ) {
				// In this case, the post number of the page would be the remainder of the devision
				this.currentPageNumber = currentPageNumber;
				this.postNumber = this.number%pageLimit;
			} 
				// If the currentPageNumber is not in the reasonable range
				else {
					// Return false
					return false;
			}
		
		//Generate a new query based on the currentPageNumber
		this.query = this.generateQuery();
		
		//Retrieve the posts back in that page
		this.retrievePostList();
		return true;
	}
	
	/**
     * Description: return the posts for the results.
     * @param  no parameters
     * @return  an array list collection reference 
     * @exception no exception
     */
	public ArrayList<Post> getPostList () {
		return this.postList;
	}
	
	/**
     * Description: retrieve the total number of posts from the DOM file.
     * @param  no parameters
     * @return  If the number is successfully retrieved from the DOM file, return the total number;
     * 			Else, return -1;
     * @exception no exception
     */
	private int retrieveNumber () {
		// Get the element by the Id "sortFiltersBox", whcih stores the information about the total number of results
		Element es = this.domTree.getElementById("sortFiltersBox");
		
		// Since the total number information is stored as attribute value for name, and its format is like "ItemReturned: 1333".
		// We use a pattern to find out this information within the "sortFiltersBox" element.
		Pattern pt = Pattern.compile("\\w+:\\d+");
		Elements esForPrice = es.getElementsByAttributeValueMatching("name", pt);
		
		// If we find more than one element with the attribute pattern, then something may goes wrong.
		if(esForPrice.size() != 1) return -1;
		
		// Otherwise, we pull the value of the number from the attribute
		String attr = esForPrice.get(0).attr("name");
		Pattern ptForNumber = Pattern.compile("\\d+");
		Matcher m = ptForNumber.matcher(attr);
		
		//If we find the data, return the data
		if(m.find()) return Integer.parseInt(m.group());
		
		// If we could not find the data, return -1
		return -1;
	}
	
	/**
     * Description: retrieve the posts from the DOM file and store them in an array list collection of Post object (postList).
     * @param  no return
     * @exception no exception
     */
	private void retrievePostList () {
		// If currentPageNumber is not successfully set up, then there is no need to extract posts from the DOM file.
		if (currentPageNumber > 0) {
			
			//If the DOM file is successfully retrieved, parse the posts from the DOM file.
			if(this.getDom()>0){
			
				//Initialize the postList with the capacity as the postNumber of the specified page
				postList = new ArrayList<Post> (this.postNumber);
				
				
				//Since all the posts are within the element with ID "searchResultsContainer", we use getElementById to get it.
				Element e = domTree.getElementById("searchResultsContainer");
				
				//A loop to retrieve each post
				for(int i =1; i<= this.postNumber; i++) {
					
					// Four temporary variables to store the four attributes for a post object
					String vendor = null;
					String shippingInfo = null;
					String title = null;
					String productPrice = null;
					
					// Retrieve the element which store all the information for a post
					Element eI = e.getElementById("quickLookItem-" + i);
					
					//Block to deal with title for a post
					Elements eProductName = eI.getElementsByClass("productName");
					//Check if we successfully get some elements by using the class"productName"
					if(eProductName.size() >= 1) {
						title = eProductName.get(0).attr("title");
						
						//If the title is none, then the title may is store within the span tag inside the a tag
						if(title.length() <= 0 && eProductName.get(0).childNodeSize()>0) 
							title = eProductName.get(0).child(0).attr("title");
					} 
					// If still the title is not within the childNode of the a tag, then we can not find the title for the product
					if( title == null || title.length() <=0) title="Sorry, we couldn't find the product name.";
					
					//Block to deal with product price for a post
					Elements ePrice = eI.getElementsByClass("productPrice");
					//Check if we successfully find some element including the information we want to find
					if(ePrice.size() >= 1) {
						productPrice = ePrice.get(0).text();
					} else {
						//If not, then print out the information that we can not find the price information
						productPrice = "Sorry, we couldn't find the price information.";
					}
					//Check if we successfully get the price information
					if(productPrice.length()<=0) productPrice = "Sorry, we couldn't find the price information.";
					
					//Block to deal with vendor of a post
					Elements eVendor = eI.getElementsByClass("newMerchantName");
					//Check if we successfully get some elements including the information we need
					if(eVendor.size() >=1) {
						vendor = eVendor.get(0).text();
					} else {
						vendor = "Sorry, we couldn't find the vendor information.";
					}
					//Check if we successfully get the vendor information
					if(vendor.length()<=0) vendor = "Sorry, we couldn't find the vendor information.";
					
					//Block to deal with shipping information of a post
					Elements shipping = eI.getElementsByClass("freeShip");
					//Check if we successfully get some elements including free shipping information
					if(shipping.size() != 0) {
						shippingInfo = shipping.get(0).text();
					} else {
						//See if there is some taxShipping information
						shipping = eI.getElementsByClass("taxShippingArea");
						if( shipping.size() >0) {
							if(shipping.get(0).childNodeSize()>0)
								shippingInfo = shipping.get(0).child(0).text();
						}
						//If both case doesn't work, then we can not find the shipping information
						else shippingInfo = "Sorry, we can not find the shipping information.";
					}
					//Check if we successfully get the shipping information
					if(shippingInfo.length()<=0) shippingInfo = "Sorry, we can not find the shipping information.";
					
					//Add a post with the 4 attributes extracted from the DOM file into the postList
					this.postList.add(new Post(title,productPrice,shippingInfo, vendor));
					}
			
			}
		}
	}
	
	/**
     * Description: generate the query based on the keyword for searching
     * @param  no parameters
     * @return  the query URL
     * @exception no exception
     */
	private String generateQuery () {
		return "http://www.shopping.com/"+this.keyword.replaceAll("\\s+", "-")+"/products~PG-"
				+ this.currentPageNumber+ "?KW="+keyword.replaceAll("\\s+", "+");
	}
	
	/**
     * Description: retrieve the DOM file based on the query.
     * @param  no parameters
     * @return  A status indicator. Return 1 if the DOM is successfully returned.
     * 			Return -1 if there is a SocketTimeoutException. And domTree will be null.
     * 			Return -2 if there is a UnknowHostException. And domTree will be null.
     * 			Return -3 if there is a IOException. And domTree will be null.
     * @exception no exception
     * @see org.Jsoup
     */
	private int getDom() {
		try {
			this.domTree = Jsoup.connect(query).get();		
			return 1;
		} catch (SocketTimeoutException ste) {
			System.out.print("The network is not stable. Pleaes try later.");
			this.domTree = null;
			return -1;
		} catch (UnknownHostException ue) {
			System.out.println("The website is not available at this time. Please try later.");
			this.domTree = null;
			return -2;
		} catch (IOException e) {
			System.out.println("The connection is borken. Please make sure the Internet is working and then try again.");
			this.domTree = null;
			return -3;
			
		} 
	}
	
	public static void main (String[] args) {
		// See if the number of arguments is correct
		if (args.length >2 || args.length == 0) {
			
				System.out.println("Please specify correct number of arguments: 1 \"Keyword\" ; 2 \"Keyword\", \"PageNumber\".");

		} 
		
		// If there is only one argument
		else if (args.length == 1) {
			System.out.println("The keyword used for searching is "+ args[0]);
			//Initialize a QueryResult object using the keyword argument from user
			QueryResult qr = new QueryResult(args[0]);
			//CHeck if we successfully retrieve the total number of results
			if(qr.getNumber()>=0)
				System.out.println("The number of results returned is " + qr.getNumber());
			System.out.println("Program ends");
		} 
		
		// If there is two arguments
		else if (args.length == 2) {
			// Check if the second argument has the correct format: number
			if (args[1].matches("\\d+")) {
				
				System.out.println("The keyword used for searching is "+ args[0]);
				QueryResult qr = new QueryResult(args[0]);
				
				//If we can successfully retrieve the total number of results
				if(qr.getNumber()>0) {
					
					System.out.println("The number of results returned is " + qr.getNumber());
					System.out.println("The page number of results is " + args[1]);
					
					boolean flag = qr.setCurrentPageNumber(Integer.parseInt(args[1]));
					
					//See if the currentPageNumber can be set as the second argument
					if(flag ) {
						
						//Block get the postList from the QueryResult object and print them
						List<Post> ts = qr.getPostList();
						int i = 0;
						for(Post p:ts) {
							i++;
							System.out.print("Item " + i + "\n");
							System.out.println(p);
						}
						
						System.out.println("Program ends");
						
					}   // If the second argument is too large for the QueryResult object
						else {
						System.out.println("The page number is too large.");
						System.out.println("Program ends");
					} 
				}
			} 
			  // If the second argument is not a number
			  else {
				System.out.println("Please use correct format for pageNumber argument.");
				System.out.println("Program ends");
			}
		}
		
	
		
		
		
		
	}
	
}
