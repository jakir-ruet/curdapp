package com.jakirbd.empapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jakirbd.empapp.model.Product;

public interface ProductRepository extends JpaRepository <Product, Long> {

}
