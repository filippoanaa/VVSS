package mydrinkshop.service;

import mydrinkshop.domain.IngredientReteta;
import mydrinkshop.domain.Reteta;
import mydrinkshop.domain.Stoc;
import mydrinkshop.repository.Repository;
import mydrinkshop.repository.file.FileStocRepository;
import mydrinkshop.service.validator.StocValidator;
import mydrinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StocServiceIntegrationTopDownTest {

    // Step 2: integrate V (use real StocValidator), mock R
    @Test
    void step2_add_invalidStoc_throwsValidationAndDoesNotSave() {
        Repository<Integer, Stoc> mockRepo = mock(Repository.class);
        StocService service = new StocService(mockRepo, new StocValidator());

        Stoc invalid = new Stoc(0, "", -5.0, -1.0);

        assertThrows(ValidationException.class, () -> service.add(invalid));

        verify(mockRepo, never()).save(any());
    }

    @Test
    void step2_update_valid_callsValidateAndUpdate() {
        Repository<Integer, Stoc> mockRepo = mock(Repository.class);
        StocService service = new StocService(mockRepo, new StocValidator());

        Stoc valid = new Stoc(1, "Zahar", 10.0, 1.0);

        assertDoesNotThrow(() -> service.update(valid));

        verify(mockRepo, times(1)).update(valid);
    }

    // Step 3: integrate R (use real FileStocRepository) along with real StocValidator

    @Test
    void step3_add_persistsToFile_and_canBeReadBack() throws Exception {
        Path tmp = Files.createTempFile("stocrepo_step3", ".txt");
        tmp.toFile().deleteOnExit();

        FileStocRepository repo = new FileStocRepository(tmp.toString());
        StocService service = new StocService(repo);

        Stoc s = new Stoc(1, "Lapte", 50.0, 1.0);
        service.add(s);

        List<Stoc> all = service.getAll();
        assertEquals(1, all.size());
        assertEquals("Lapte", all.get(0).getIngredient());

        FileStocRepository repo2 = new FileStocRepository(tmp.toString());
        List<Stoc> all2 = repo2.findAll();
        assertEquals(1, all2.size());
        assertEquals(50.0, all2.get(0).getCantitate(), 1e-9);
    }

    @Test
    void step3_consuma_updatesEntriesAndPersists() throws Exception {
        Path tmp = Files.createTempFile("stocrepo_step3_consuma", ".txt");
        tmp.toFile().deleteOnExit();

        FileStocRepository repo = new FileStocRepository(tmp.toString());
        StocService service = new StocService(repo);

        Stoc st1 = new Stoc(1, "Lapte", 2.0, 0.0);
        Stoc st2 = new Stoc(2, "Lapte", 2.0, 0.0);
        repo.save(st1);
        repo.save(st2);

        Reteta r = new Reteta(1, List.of(new IngredientReteta("Lapte", 3.0)));

        assertTrue(service.areSuficient(r));

        service.consuma(r);

        FileStocRepository repo2 = new FileStocRepository(tmp.toString());
        List<Stoc> lapteList = repo2.findAll().stream()
                .filter(s -> s.getIngredient().equalsIgnoreCase("Lapte"))
                .toList();

        assertEquals(2, lapteList.size());

        Stoc a1 = lapteList.stream().filter(s -> s.getId() == 1).findFirst().orElseThrow();
        Stoc a2 = lapteList.stream().filter(s -> s.getId() == 2).findFirst().orElseThrow();

        assertEquals(0.0, a1.getCantitate(), 1e-9);
        assertEquals(1.0, a2.getCantitate(), 1e-9);
    }
}

