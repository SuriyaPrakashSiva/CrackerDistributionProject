package com.cts.Hacked.CrackerDistribution.DAO;

import java.io.IOException;
import java.util.Date;
 
import javax.persistence.NoResultException;
 
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import com.cts.Hacked.CrackerDistribution.Entity.Product;
import com.cts.Hacked.CrackerDistribution.Form.ProductForm;
import com.cts.Hacked.CrackerDistribution.Model.ProductInformation;
import com.cts.Hacked.CrackerDistribution.Pagination.PaginationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class ProductDAO {
	
    @Autowired
    private SessionFactory sessionFactory;
 
    public Product findProduct(String code) {
        try {
            String sql = "Select e from " + Product.class.getName() + " e Where e.code =:code ";
 
            Session session = this.sessionFactory.getCurrentSession();
            Query<Product> query = session.createQuery(sql, Product.class);
            query.setParameter("code", code);
            return (Product) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public void deleteProduct(String code) {
            Session session = this.sessionFactory.getCurrentSession();
            Query query = session.createQuery("delete from " + Product.class.getName() + " e Where e.code =:code ");
            query.setParameter("code", code);
      
        }
 
    public ProductInformation findProductInfo(String code) {
        Product product = this.findProduct(code);
        if (product == null) {
            return null;
        }
        return new ProductInformation(product.getCode(), product.getName(), product.getPrice());
    }
 
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void save(ProductForm productForm) {
 
        Session session = this.sessionFactory.getCurrentSession();
        String code = productForm.getCode();
 
        Product product = null;
 
        boolean isNew = false;
        if (code != null) {
            product = this.findProduct(code);
        }
        if (product == null) {
            isNew = true;
            product = new Product();
            product.setCreateDate(new Date());
        }
        product.setCode(code);
        product.setName(productForm.getName());
        product.setPrice(productForm.getPrice());
 
        if (productForm.getFileData() != null) {
            byte[] image = null;
            try {
                image = productForm.getFileData().getBytes();
            } catch (IOException e) {
            }
            if (image != null && image.length > 0) {
                product.setImage(image);
            }
        }
        if (isNew) {
            session.persist(product);
        }
        // If error in DB, Exceptions will be thrown out immediately
        session.flush();
    }
 
    public PaginationResult<ProductInformation> queryProducts(int page, int maxResult, int maxNavigationPage,
            String likeName) {
        String sql = "Select new " + ProductInformation.class.getName() //
                + "(p.code, p.name, p.price) " + " from "//
                + Product.class.getName() + " p ";
        if (likeName != null && likeName.length() > 0) {
            sql += " Where lower(p.name) like :likeName ";
        }
        sql += " order by p.createDate desc ";
        // 
        Session session = this.sessionFactory.getCurrentSession();
        Query<ProductInformation> query = session.createQuery(sql, ProductInformation.class);
 
        if (likeName != null && likeName.length() > 0) {
            query.setParameter("likeName", "%" + likeName.toLowerCase() + "%");
        }
        return new PaginationResult<ProductInformation>(query, page, maxResult, maxNavigationPage);
    }
    
 
    public PaginationResult<ProductInformation> queryProducts(int page, int maxResult, int maxNavigationPage) {
        return queryProducts(page, maxResult, maxNavigationPage, null);
    }

}
