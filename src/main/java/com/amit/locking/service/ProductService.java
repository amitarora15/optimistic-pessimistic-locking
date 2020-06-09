package com.amit.locking.service;

import com.amit.locking.entity.Product;
import com.amit.locking.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ProductService self;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product updateProductInNewTransactionWithDoublePrice(Product product){
        product.setPrice(product.getPrice() * 2);
        return productRepo.save(product);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<Product> getProduct(Long productId){
        return productRepo.findById(productId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<Product> getProduct(String name){
        return productRepo.findByName(name);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<Product> getProductByPrice(Long price){
        return productRepo.findByPrice(price);
    }

    @Transactional
    public Product getProductDetailsAndUpdatePriceInNewTransaction(Long productId){
        Optional<Product> optionalProduct = productRepo.findById(productId);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            Product returnProduct = self.updateProductInNewTransactionWithDoublePrice(product);
            return returnProduct;
        }
        return  null;
    }

    @Transactional
    public Product getProductDetailsAndUpdatePriceInSameTransaction(Long productId){
        Optional<Product> optionalProduct = productRepo.findById(productId);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            product.setPrice(product.getPrice() * 2);
            Product returnProduct = productRepo.save(product);
            return returnProduct;
        }
        return  null;
    }

    @Transactional
    public Product getProductDetailsMoreThanOnceInNewTransaction(Long productId){
        Optional<Product> optionalProduct = productRepo.findById(productId);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            Optional<Product> returnProduct = self.getProduct(productId);
            return returnProduct.get();
        }
        return  null;
    }

    @Transactional
    public Product getProductDetailsAndUpdatePriceInNewTransaction(String name){
        Optional<Product> optionalProduct = productRepo.findByName(name);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            Product returnProduct = self.updateProductInNewTransactionWithDoublePrice(product);
            return returnProduct;
        }
        return  null;
    }

    @Transactional
    public Product getProductDetailsAndUpdatePriceInSameTransaction(String name){
        Optional<Product> optionalProduct = productRepo.findByName(name);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            product.setPrice(product.getPrice() * 2);
            Product returnProduct = productRepo.save(product);
            return returnProduct;
        }
        return  null;
    }

    @Transactional
    public Product getProductDetailsMoreThanOnceInNewTransaction(String name){
        Optional<Product> optionalProduct = productRepo.findByName(name);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            Optional<Product> returnProduct = self.getProduct(name);
            Optional<Product> returnProductById = self.getProduct(returnProduct.get().getId());
            return returnProductById.get();
        }
        return  null;
    }

    @Transactional
    public Product getProductDetailsAndUpdateProductInNewTransaction(Long price){
        Optional<Product> optionalProduct = productRepo.findByPrice(price);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            Product returnProduct = self.updateProductInNewTransactionWithDoublePrice(product);
            return returnProduct;
        }
        return  null;
    }

    @Transactional
    public Product getProductDetailsAndUpdateProductInSameTransaction(Long price){
        Optional<Product> optionalProduct = productRepo.findByPrice(price);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            product.setPrice(product.getPrice() * 2);
            Product returnProduct = productRepo.save(product);
            return returnProduct;
        }
        return  null;
    }

    @Transactional
    public Product getProductDetailsAndGetProductInNewTransaction(Long price){
        Optional<Product> optionalProduct = productRepo.findByPrice(price);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            Product returnProduct = self.getProduct(product.getId()).get();
            return returnProduct;
        }
        return  null;
    }

}
