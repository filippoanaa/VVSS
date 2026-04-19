package mydrinkshop.service;


import mydrinkshop.domain.IngredientReteta;
import mydrinkshop.domain.Reteta;
import mydrinkshop.domain.Stoc;
import mydrinkshop.repository.Repository;
import mydrinkshop.service.validator.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StocServiceLab4Test {

    @Mock
    private Repository<Integer, Stoc> stocRepo;

    @Mock
    private Validator<Stoc> stocValidator;

    private StocService stocService;

    @BeforeEach
    public void setup() {
        stocService = new StocService(stocRepo, stocValidator);
    }

    @Test
    public void add_validStoc_callsValidateAndSave() {
        Stoc s = new Stoc(1, "Zahar", 10.0, 1.0);

        stocService.add(s);

        verify(stocValidator, times(1)).validate(s);
        verify(stocRepo, times(1)).save(s);
    }

    @Test
    public void consuma_whenSufficient_consumesAndUpdates() {
        IngredientReteta ir = new IngredientReteta("Lapte", 3.0);
        Reteta reteta = new Reteta(1, List.of(ir));

        Stoc st1 = new Stoc(1, "Lapte", 2.0, 0.5);
        Stoc st2 = new Stoc(2, "Lapte", 2.0, 0.5);
        List<Stoc> repoList = new ArrayList<>();
        repoList.add(st1);
        repoList.add(st2);

        when(stocRepo.findAll()).thenReturn(repoList);

        assertTrue(stocService.areSuficient(reteta));

        stocService.consuma(reteta);

        assertEquals(0.0, st1.getCantitate(), 1e-9);
        assertEquals(1.0, st2.getCantitate(), 1e-9);

        verify(stocRepo, times(1)).update(st1);
        verify(stocRepo, times(1)).update(st2);
    }
}