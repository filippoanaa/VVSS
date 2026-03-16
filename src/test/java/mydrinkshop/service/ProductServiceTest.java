package mydrinkshop.service;

import mydrinkshop.domain.*;
import mydrinkshop.repository.Repository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    private final Repository<Integer, Product> repo = new FakeProductRepository();
    private final ProductService service = new ProductService(repo);

    @Test
    void TC_ECP_Valid_Add_Product() {

        Product p = new Product(
                1,
                "Espresso",
                5.0,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC
        );

        assertDoesNotThrow(() -> service.addProduct(p));
    }

    @Test
    void TC_ECP_Invalid_Add_Product() {

        Product p = new Product(
                2,
                "",
                5.0,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC
        );

        assertThrows(Exception.class,
                () -> service.addProduct(p));
    }

    @Test
    void TC_BVA_Valid_Add_Product() {

        Product p = new Product(
                3,
                "Latte",
                0.01,
                CategorieBautura.MILK_COFFEE,
                TipBautura.DAIRY
        );

        assertDoesNotThrow(() -> service.addProduct(p));
    }

    @Test
    void TC_BVA_Invalid_Add_Product() {

        Product p = new Product(
                4,
                "Latte",
                0,
                CategorieBautura.MILK_COFFEE,
                TipBautura.DAIRY
        );

        assertThrows(Exception.class,
                () -> service.addProduct(p));
    }
}
class FakeProductRepository implements Repository<Integer, Product> {

    private final List<Product> list = new ArrayList<>();

    @Override
    public Product save(Product entity) {
        list.add(entity);
        return entity;
    }

    @Override
    public Product update(Product entity) {
        return entity;
    }

    @Override
    public Product delete(Integer id) {
        return null;
    }

    @Override
    public List<Product> findAll() {
        return list;
    }

    @Override
    public Product findOne(Integer id) {
        return null;
    }
}