package com.jakirbd.empapp.service.product;

import java.util.List;

import com.jakirbd.empapp.model.Product;

public interface IProductService {
	Product addProduct(Product product);

	Product getProductById(Long id);

	void deleteProductById(Long id);

	Product updateProduct(Product product);

	List<Product> getAllProducts();

	List<Product> getProductsByCategory();

	List<Product> getProductsByBrand();

	List<Product> getProductsByCategoryAndBrand();

	List<Product> getProductsByName();

	List<Product> geProductsByBrandAndName();

	Long countProductsByBrandAndName();
}
