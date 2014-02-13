/** 
                                                                          
* @Since:         <JDK1.7>                              
* @author：        Zelin Zheng            
* @version：       2013.10.27 
               
*/ 


/**
 * * A class representing a result(post) object
*/
public class Post {
	
	/**
	 *  title: title of the product
	 *  productPrice: the price of the product
	 *  shippingInfo: shipping information of the prodct
	 *  Vendor: name of the vendor of the product
	 */
	private String title = null;
	private String productPrice = null;
	private String shippingInfo = null;
	private String Vendor = null;
	

/**
     * Description: the constructor of a post
     * @param  title: title of a post
     * @param  productPrice: price information of a post
     * @param shippingInfo: shipping information of a post
     * @param vendor: vendor of a post
     * @return  no return
     * @exception no exception
     */
	public Post(String title, String productPrice, String shippingInfo,
			String vendor) {
		super();
		this.title = title;
		this.productPrice = productPrice;
		this.shippingInfo = shippingInfo;
		Vendor = vendor;
	}

	/**
     * Description: this function will return the title value of the referenced post
     * @return  return the title of the post
     * @exception no exception
     */
	public String getTitle() {
		return title;
	}
	
	/**
     * Description: set the value of title
     * @param  title: title of a post
     * @return  no return
     * @exception no exception
     */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
     * Description: this function will return the price value of the referenced post
     * @return  return the product price of the post
     * @exception no exception
     */
	public String getProductPrice() {
		return productPrice;
	}

	/**
     * Description: set the product price of title
     * @param  productPrice: product price of a post
     * @return  no return
     * @exception no exception
     */
	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

	/**
     * Description: this function will return the shipping information of the referenced post
     * @return  return the shipping information of the post
     * @exception no exception
     */
	public String getShippingInfo() {
		return shippingInfo;
	}

	/**
     * Description: set the value of shippingInfo
     * @param  shippingInfo: shipping information of a post
     * @return  no return
     * @exception no exception
     */
	public void setShippingInfo(String shippingInfo) {
		this.shippingInfo = shippingInfo;
	}

	/**
     * Description: this function will return the vendor name of the referenced post
     * @return  return the vendor name of the post
     * @exception no exception
     */
	public String getVendor() {
		return Vendor;
	}
	
	/**
     * Description: set the value of vendor
     * @param  vendor: vendor name of a post
     * @return  no return
     * @exception no exception
     */
	public void setVendor(String vendor) {
		Vendor = vendor;
	}
	
	/**
     * Description: override the toString function in order to format the input of a post
     * @return  a formatted String value of the content of the referenced post
     * @exception no exception
     */
	public String toString () {
		return "    Title:" + this.title + "\n    Product Price:" + this.productPrice + "\n    Vendor:" + this.Vendor + "\n    Shipping Info:" + this.shippingInfo + "\n";
	}
}
