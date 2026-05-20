package com.jakirbd.empapp.service.product;

import java.util.List;

import com.jakirbd.empapp.exceptions.ProductNotFoundException;
import com.jakirbd.empapp.model.Product;
import com.jakirbd.empapp.repository.ProductRepository;

public class ProductService implements IProductService {

	private ProductRepository productRepository;

	@Override
	public Product addProduct(Product product) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'addProduct'");
	}

	@Override
	public Product getProductById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product Not Found"));
	}

	@Override
	public void deleteProductById(Long id) {
		productRepository.findById(id).orElseThrow(()-> new ProductNotFoundException("Product Deleted"));
	}

	@Override
	public Product updateProduct(Product product) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updateProduct'");
	}

	@Override
	public List<Product> getAllProducts() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAllProducts'");
	}

	@Override
	public List<Product> getProductsByCategory() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getProductsByCategory'");
	}

	@Override
	public List<Product> getProductsByBrand() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getProductsByBrand'");
	}

	@Override
	public List<Product> getProductsByCategoryAndBrand() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getProductsByCategoryAndBrand'");
	}

	@Override
	public List<Product> getProductsByName() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getProductsByName'");
	}

	@Override
	public List<Product> geProductsByBrandAndName() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'geProductsByBrandAndName'");
	}

	@Override
	public Long countProductsByBrandAndName() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'countProductsByBrandAndName'");
	}

}
