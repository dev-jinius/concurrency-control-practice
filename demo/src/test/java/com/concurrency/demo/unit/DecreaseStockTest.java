package com.concurrency.demo.unit;

import com.concurrency.demo.domain.Product;
import com.concurrency.demo.domain.ReentrantLockProductService;
import com.concurrency.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class DecreaseStockTest {

    @InjectMocks
    ReentrantLockProductService sutReentrant;

    @Mock
    ProductRepository productRepository;

    @Test
    public void DecreaseStock() throws Exception {
        //given
        Long id = 1L;
        Long quantity = 5L;
        Product product = new Product(id, 100L);
        Product requestProduct = new Product(id, quantity);

        //when
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        sutReentrant.decreaseStock(requestProduct);

        //then
        assertThat(sutReentrant.findProduct(id).getStock()).isEqualTo(95L);
    }

}
