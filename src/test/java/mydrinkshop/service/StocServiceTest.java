package mydrinkshop.service;

import mydrinkshop.domain.IngredientReteta;
import mydrinkshop.domain.Reteta;
import mydrinkshop.domain.Stoc;
import mydrinkshop.repository.Repository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StocServiceTest {

    @Test
    void F02_TC01_insufficientStock_lapte100_reteta300() {
        FakeStocRepository repo = new FakeStocRepository();
        StocService service = new StocService(repo);

        Stoc s1 = new Stoc(1, "lapte", 100, 1);
        repo.save(s1);

        List<IngredientReteta> ingrediente = new ArrayList<>();
        ingrediente.add(new IngredientReteta("lapte", 300));
        Reteta r = new Reteta(1, ingrediente);

        assertThrows(IllegalStateException.class, () -> service.consuma(r));
        assertEquals(100, s1.getCantitate(), 0.001);
    }

    @Test
    void F02_TC02_emptyRecipe_stockUnchanged() {
        FakeStocRepository repo = new FakeStocRepository();
        StocService service = new StocService(repo);

        Stoc s1 = new Stoc(1, "lapte", 100, 1);
        repo.save(s1);

        Reteta r = new Reteta(2, new ArrayList<>());

        assertDoesNotThrow(() -> service.consuma(r));
        assertEquals(100, s1.getCantitate(), 0.001);
    }

    @Test
    void F02_TC03_zeroStock_zahar50_exception() {
        FakeStocRepository repo = new FakeStocRepository();
        StocService service = new StocService(repo);

        Stoc s1 = new Stoc(1, "zahar", 0, 1);
        repo.save(s1);

        List<IngredientReteta> ingrediente = new ArrayList<>();
        ingrediente.add(new IngredientReteta("zahar", 50));
        Reteta r = new Reteta(3, ingrediente);

        assertThrows(IllegalStateException.class, () -> service.consuma(r));
        assertEquals(0, s1.getCantitate(), 0.001);
    }

    @Test
    void F02_TC04_twoStockEntries_sameIngredient() {
        FakeStocRepository repo = new FakeStocRepository();
        StocService service = new StocService(repo);

        Stoc lapte1 = new Stoc(1, "lapte", 100, 1);
        Stoc lapte2 = new Stoc(2, "lapte", 50, 1);
        repo.save(lapte1);
        repo.save(lapte2);

        List<IngredientReteta> ingrediente = new ArrayList<>();
        ingrediente.add(new IngredientReteta("lapte", 100));
        Reteta r = new Reteta(4, ingrediente);

        assertDoesNotThrow(() -> service.consuma(r));
        assertEquals(0, lapte1.getCantitate(), 0.001);
        assertEquals(50, lapte2.getCantitate(), 0.001);
    }

    @Test
    void F02_TC05_singleStockEntry_normalConsume() {
        FakeStocRepository repo = new FakeStocRepository();
        StocService service = new StocService(repo);

        Stoc s1 = new Stoc(1, "lapte", 150, 1);
        repo.save(s1);

        List<IngredientReteta> ingrediente = new ArrayList<>();
        ingrediente.add(new IngredientReteta("lapte", 100));
        Reteta r = new Reteta(5, ingrediente);

        assertDoesNotThrow(() -> service.consuma(r));
        assertEquals(50, s1.getCantitate(), 0.001);
    }
}

class FakeStocRepository implements Repository<Integer, Stoc> {

    private final List<Stoc> list = new ArrayList<>();

    @Override
    public Stoc save(Stoc entity) {
        list.add(entity);
        return entity;
    }

    @Override
    public Stoc update(Stoc entity) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == entity.getId()) {
                list.set(i, entity);
                return entity;
            }
        }
        return entity;
    }

    @Override
    public Stoc delete(Integer id) {
        return null;
    }

    @Override
    public List<Stoc> findAll() {
        return list;
    }

    @Override
    public Stoc findOne(Integer id) {
        for (Stoc s : list) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }
}