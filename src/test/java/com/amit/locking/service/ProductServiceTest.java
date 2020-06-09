package com.amit.locking.service;

import com.amit.locking.entity.Product;
import com.amit.locking.repository.ProductRepo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import javax.annotation.Resource;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SqlGroup({
        @Sql(value = "classpath:createUser.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:createProduct.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:deleteProduct.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
        @Sql(value = "classpath:deleteUser.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class ProductServiceTest {

    @Resource
    private ProductService productServiceUnderTest;

    @Resource
    private ProductRepo productRepo;

    @Test
    public void productPessimisticRead_tryingToUpdateProductInDifferentTransaction_FailedTransaction() {
        Optional<Product> repoProduct = productServiceUnderTest.getProduct(1L);
        if (repoProduct.isPresent()) {
            assertAll(
                    () -> {
                        assertEquals("Juicer", repoProduct.get().getName());
                    },
                    () -> {
                        assertEquals(1, repoProduct.get().getVersion());
                    }
            );
            assertThrows(PessimisticLockingFailureException.class, () -> {
                productServiceUnderTest.getProductDetailsAndUpdatePriceInNewTransaction(1L);
            }, "Should not be able to obtain lock on record for write");
        }
    }

    @Test
    public void productPessimisticRead_tryingToUpdateProductInSameTransaction_Success() {
        Optional<Product> repoProduct = productServiceUnderTest.getProduct(1L);
        if (repoProduct.isPresent()) {
            assertAll(
                    () -> {
                        assertEquals("Juicer", repoProduct.get().getName());
                    },
                    () -> {
                        assertEquals(1, repoProduct.get().getVersion());
                    }
            );
            Product returnProduct = productServiceUnderTest.getProductDetailsAndUpdatePriceInSameTransaction(1L);
            assertAll(
                    () -> {
                        assertEquals("Juicer", returnProduct.getName());
                    },
                    () -> {
                        assertEquals(2, returnProduct.getVersion());
                    }
            );
        }
    }

    @Test
    public void productPessimisticRead_moreThanOnceInDifferentTransaction_Success() {
        Optional<Product> repoProduct = productServiceUnderTest.getProduct(1L);
        if (repoProduct.isPresent()) {
            assertAll(
                    () -> {
                        assertEquals("Juicer", repoProduct.get().getName());
                    },
                    () -> {
                        assertEquals(1, repoProduct.get().getVersion());
                    }
            );
            Product returnProduct = productServiceUnderTest.getProductDetailsMoreThanOnceInNewTransaction(1L);
            assertAll(
                    () -> {
                        assertEquals("Juicer", returnProduct.getName());
                    },
                    () -> {
                        assertEquals(1, returnProduct.getVersion());
                    }
            );
        }
    }

    @Test
    public void productPessimisticForceIncrement_productUpdateInNewTransaction_OptimisticLockingException() {
        Optional<Product> repoProduct = productServiceUnderTest.getProduct("Juicer");
        if (repoProduct.isPresent()) {
            assertAll(
                    () -> {
                        assertEquals("Juicer", repoProduct.get().getName());
                    },
                    () -> {
                        assertEquals(2, repoProduct.get().getVersion());
                    }
            );
            assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
                productServiceUnderTest.getProductDetailsAndUpdatePriceInNewTransaction("Juicer");
            }, "Should not be able to obtain lock on record for write");
        }
    }

    @Test
    public void productPessimisticForceIncrement_productUpdateInSameTransaction_Success() {
        Optional<Product> repoProduct = productServiceUnderTest.getProduct("Juicer");
        if (repoProduct.isPresent()) {
            assertAll(
                    () -> {
                        assertEquals("Juicer", repoProduct.get().getName());
                    },
                    () -> {
                        assertEquals(2, repoProduct.get().getVersion());
                    }
            );
            Product returnProduct = productServiceUnderTest.getProductDetailsAndUpdatePriceInSameTransaction("Juicer");
            assertAll(
                    () -> {
                        assertEquals("Juicer", returnProduct.getName());
                    },
                    () -> {
                        assertEquals(4, returnProduct.getVersion());
                    }
            );
        }
    }

    @Test
    public void productPessimisticForceIncrement_readMoreThanOnceInNewTransaction_LockException() {
        Optional<Product> repoProduct = productServiceUnderTest.getProduct("Juicer");
        if (repoProduct.isPresent()) {
            assertAll(
                    () -> {
                        assertEquals("Juicer", repoProduct.get().getName());
                    },
                    () -> {
                        assertEquals(2, repoProduct.get().getVersion());
                    }
            );
            assertThrows(PessimisticLockingFailureException.class, () -> {
                productServiceUnderTest.getProductDetailsMoreThanOnceInNewTransaction("Juicer");
            });
        }
    }

    @Test
    public void productPessimisticWrite_updateProductInNewTransaction_LockFailed() {
        Optional<Product> repoProduct = productServiceUnderTest.getProductByPrice(100L);
        if (repoProduct.isPresent()) {
            assertAll(
                    () -> {
                        assertEquals("Juicer", repoProduct.get().getName());
                    },
                    () -> {
                        assertEquals(1, repoProduct.get().getVersion());
                    }
            );
            assertThrows(PessimisticLockingFailureException.class, () -> {
                productServiceUnderTest.getProductDetailsAndUpdateProductInNewTransaction(100L);
            });
        }
    }

    @Test
    public void productPessimisticWrite_readProductInNewTransaction_LockFailed() {
        Optional<Product> repoProduct = productServiceUnderTest.getProductByPrice(100L);
        if (repoProduct.isPresent()) {
            assertAll(
                    () -> {
                        assertEquals("Juicer", repoProduct.get().getName());
                    },
                    () -> {
                        assertEquals(1, repoProduct.get().getVersion());
                    }
            );
            assertThrows(PessimisticLockingFailureException.class, () -> {
                productServiceUnderTest.getProductDetailsAndGetProductInNewTransaction(100L);
            });
        }
    }

    @Test
    public void productPessimisticWrite_updateProductInSameTransaction_Success() {
        Optional<Product> repoProduct = productServiceUnderTest.getProductByPrice(100L);
        if (repoProduct.isPresent()) {
            assertAll(
                    () -> {
                        assertEquals("Juicer", repoProduct.get().getName());
                    },
                    () -> {
                        assertEquals(1, repoProduct.get().getVersion());
                    }
            );
            Product returnProduct = productServiceUnderTest.getProductDetailsAndUpdateProductInSameTransaction(100L);
            assertAll(
                    () -> {
                        assertEquals("Juicer", returnProduct.getName());
                    },
                    () -> {
                        assertEquals(2, returnProduct.getVersion());
                    }
            );
        }
    }


}
