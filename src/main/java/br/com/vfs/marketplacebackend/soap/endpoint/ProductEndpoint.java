package br.com.vfs.marketplacebackend.soap.endpoint;

import br.com.vfs.marketplacebackend.service.ProductServiceImpl;
import br.com.vfs.marketplacebackend.soap.dto.ProductWSRequest;
import br.com.vfs.marketplacebackend.soap.dto.ProductWSResponse;
import br.com.vfs.marketplacebackend.soap.dto.StatusWS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ProductEndpoint {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProductEndpoint.class);

    private final ProductServiceImpl productService;

    @Autowired
    public ProductEndpoint(final ProductServiceImpl productService) {
        this.productService = productService;
    }

    @PayloadRoot(
            namespace = "dto.soap.marketplacebackend.vfs.com.br",
            localPart = "ProductRequest")
    @ResponsePayload
    public ProductWSResponse addProduct(@RequestPayload ProductWSRequest request) {

        request.getProductsWS().parallelStream().forEach(productWS -> productService.upsertProduct(productWS));

        LOGGER.info("chegou o produto"+ request.getProductsWS());
        ProductWSResponse response = new ProductWSResponse();
        response.setStatus(StatusWS.SUCCESS);
        return response;
    }
}
