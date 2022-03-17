package com.cts.Hacked.CrackerDistribution.DAO;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import com.cts.Hacked.CrackerDistribution.Entity.Order;
import com.cts.Hacked.CrackerDistribution.Entity.OrderDetail;
import com.cts.Hacked.CrackerDistribution.Entity.Product;
import com.cts.Hacked.CrackerDistribution.Model.CartInformation;
import com.cts.Hacked.CrackerDistribution.Model.CartLineInformation;
import com.cts.Hacked.CrackerDistribution.Model.CustomerInformation;
import com.cts.Hacked.CrackerDistribution.Model.OrderDetailInformation;
import com.cts.Hacked.CrackerDistribution.Model.OrderInformation;
import com.cts.Hacked.CrackerDistribution.Pagination.PaginationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class OrderDAO {
	

    @Autowired
    private SessionFactory sessionFactory;
 
    @Autowired
    private ProductDAO productDAO;
 
    private int getMaxOrderNum() {
        String sql = "Select max(o.orderNum) from " + Order.class.getName() + " o ";
        Session session = this.sessionFactory.getCurrentSession();
        Query<Integer> query = session.createQuery(sql, Integer.class);
        Integer value = (Integer) query.getSingleResult();
        if (value == null) {
            return 0;
        }
        return value;
    }
 
    @Transactional(rollbackFor = Exception.class)
    public void saveOrder(CartInformation cartInfo) {
        Session session = this.sessionFactory.getCurrentSession();
 
        int orderNum = this.getMaxOrderNum() + 1;
        Order order = new Order();
 
        order.setId(UUID.randomUUID().toString());
        order.setOrderNum(orderNum);
        order.setOrderDate(new Date());
        order.setAmount(cartInfo.getAmountTotal());
 
        CustomerInformation customerInfo = cartInfo.getCustomerInfo();
        order.setCustomerName(customerInfo.getName());
        order.setCustomerEmail(customerInfo.getEmail());
        order.setCustomerPhone(customerInfo.getPhone());
        order.setCustomerAddress(customerInfo.getAddress());
 
        session.persist(order);
 
        List<CartLineInformation> lines = cartInfo.getCartLines();
 
        for (CartLineInformation line : lines) {
            OrderDetail detail = new OrderDetail();
            detail.setId(UUID.randomUUID().toString());
            detail.setOrder(order);
            detail.setAmount(line.getAmount());
            detail.setPrice(line.getProductInfo().getPrice());
            detail.setQuanity(line.getQuantity());
 
            String code = line.getProductInfo().getCode();
            Product product = this.productDAO.findProduct(code);
            detail.setProduct(product);
 
            session.persist(detail);
        }
 
        // Order Number!
        cartInfo.setOrderNum(orderNum);
        // Flush
        session.flush();
    }
 
    // @page = 1, 2, ...
    public PaginationResult<OrderInformation> listOrderInfo(int page, int maxResult, int maxNavigationPage) {
        String sql = "Select new " + OrderInformation.class.getName()//
                + "(ord.id, ord.orderDate, ord.orderNum, ord.amount, "
                + " ord.customerName, ord.customerAddress, ord.customerEmail, ord.customerPhone) " + " from "
                + Order.class.getName() + " ord "//
                + " order by ord.orderNum desc";
 
        Session session = this.sessionFactory.getCurrentSession();
        Query<OrderInformation> query = session.createQuery(sql, OrderInformation.class);
        return new PaginationResult<OrderInformation>(query, page, maxResult, maxNavigationPage);
    }
 
    public Order findOrder(String orderId) {
        Session session = this.sessionFactory.getCurrentSession();
        return session.find(Order.class, orderId);
    }
 
    public OrderInformation getOrderInfo(String orderId) {
        Order order = this.findOrder(orderId);
        if (order == null) {
            return null;
        }
        return new OrderInformation(order.getId(), order.getOrderDate(), //
                order.getOrderNum(), order.getAmount(), order.getCustomerName(), //
                order.getCustomerAddress(), order.getCustomerEmail(), order.getCustomerPhone());
    }
 
    public List<OrderDetailInformation> listOrderDetailInfos(String orderId) {
        String sql = "Select new " + OrderDetailInformation.class.getName() //
                + "(d.id, d.product.code, d.product.name , d.quanity,d.price,d.amount) "//
                + " from " + OrderDetail.class.getName() + " d "//
                + " where d.order.id = :orderId ";
 
        Session session = this.sessionFactory.getCurrentSession();
        Query<OrderDetailInformation> query = session.createQuery(sql, OrderDetailInformation.class);
        query.setParameter("orderId", orderId);
 
        return query.getResultList();
    }

}
