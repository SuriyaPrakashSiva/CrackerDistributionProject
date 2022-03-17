package com.cts.Hacked.CrackerDistribution.Model;

public class CartLineInformation {
	
    private ProductInformation productInfo;
    private int quantity;
  
    public CartLineInformation() {
        this.quantity = 0;
    }
  
    public ProductInformation getProductInfo() {
        return productInfo;
    }
  
    public void setProductInfo(ProductInformation productInfo) {
        this.productInfo = productInfo;
    }
  
    public int getQuantity() {
        return quantity;
    }
  
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
  
    public double getAmount() {
        return this.productInfo.getPrice() * this.quantity;
    }


}
