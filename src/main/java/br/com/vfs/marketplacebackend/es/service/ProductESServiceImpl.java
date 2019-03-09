package br.com.vfs.marketplacebackend.es.service;

import br.com.vfs.marketplacebackend.es.entity.ProductES;
import br.com.vfs.marketplacebackend.es.repository.ProductESRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductESServiceImpl {

    private final ProductESRepository productsESRepository;

    @Autowired
    public ProductESServiceImpl(ProductESRepository productsESRepository) {
        this.productsESRepository = productsESRepository;
    }

    public List<String> findTop5ByNameIsLike(String name) {
        return productsESRepository.findTop5ByNameIsLike(name, PageRequest.of(1, 5))
                .stream()
                .map(ProductES::getName)
                .collect(Collectors.toList());
    }

    public Page<ProductES> findProducts(int page, int size, String name) {
        return productsESRepository.findByNameIsLike(name, PageRequest.of(page, size));
    }

    public ProductES add(ProductES build) {
        return productsESRepository.save(build);
    }
}
