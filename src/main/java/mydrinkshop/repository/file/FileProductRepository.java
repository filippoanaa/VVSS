package mydrinkshop.repository.file;

import mydrinkshop.domain.Product;
import mydrinkshop.domain.CategorieBautura;
import mydrinkshop.domain.TipBautura;

public class FileProductRepository
        extends FileAbstractRepository<Integer, Product> {

    public FileProductRepository(String fileName) {
        super(fileName);
        loadFromFile();
    }

    @Override
    protected Integer getId(Product entity) {
        return entity.getId();
    }

    @Override
    protected Product extractEntity(String line) {
        String[] elems = line.split(",");

        if (elems.length < 5) {
            throw new IllegalArgumentException("Format linie invalid pentru Produs (lipsesc coloane): " + line);
        }

        try {
            int id = Integer.parseInt(elems[0]);
            String name = elems[1];
            double price = Double.parseDouble(elems[2]);
            CategorieBautura categorie = CategorieBautura.valueOf(elems[3]);
            TipBautura tip = TipBautura.valueOf(elems[4]);

            return new Product(id, name, price, categorie, tip);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Eroare la conversia datelor pentru produs: " + e.getMessage());
        }
    }

    @Override
    protected String createEntityAsString(Product entity) {
        return entity.getId() + "," +
                entity.getNume() + "," +
                entity.getPret() + "," +
                entity.getCategorie() + "," +
                entity.getTip();
    }
}