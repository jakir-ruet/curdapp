package com.jakirbd.curdapp.repository;

import com.jakirbd.curdapp.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByProductId(Long productId);
    void deleteByProductId(Long productId);
    Optional<Image> findByProductIdAndIsPrimaryTrue(Long productId);
    long countByProductId(Long productId);
}
