package com.cts.Hacked.CrackerDistribution.Utils;

import javax.servlet.http.HttpServletRequest;
import com.cts.Hacked.CrackerDistribution.Model.CartInformation;

public class Utils {
	   // Products in the cart, stored in Session.
	   public static CartInformation getCartInSession(HttpServletRequest request) {
	 
	      CartInformation cartInfo = (CartInformation) request.getSession().getAttribute("myCart");
	 
	    
	      if (cartInfo == null) {
	         cartInfo = new CartInformation();
	          
	         request.getSession().setAttribute("myCart", cartInfo);
	      }
	 
	      return cartInfo;
	   }
	 
	   public static void removeCartInSession(HttpServletRequest request) {
	      request.getSession().removeAttribute("myCart");
	   }
	 
	   public static void storeLastOrderedCartInSession(HttpServletRequest request, CartInformation cartInfo) {
	      request.getSession().setAttribute("lastOrderedCart", cartInfo);
	   }
	 
	   public static CartInformation getLastOrderedCartInSession(HttpServletRequest request) {
	      return (CartInformation) request.getSession().getAttribute("lastOrderedCart");
	   }

}
