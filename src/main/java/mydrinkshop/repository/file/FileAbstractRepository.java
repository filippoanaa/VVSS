package mydrinkshop.repository.file;

import mydrinkshop.repository.AbstractRepository;
import mydrinkshop.repository.RepositoryException;

import java.io.*;

public abstract class FileAbstractRepository<I, E>
        extends AbstractRepository<I, E> {

    protected String fileName;

    protected FileAbstractRepository(String fileName) {
        this.fileName = fileName;
    }

    protected void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                processLine(line);
            }
        } catch (IOException e) {
            throw new RepositoryException("Failed to load from file: " + fileName, e);
        }
    }

    private void processLine(String line) {
        try {
            E entity = extractEntity(line);
            if (entity != null) {
                super.save(entity);
            }
        } catch (Exception e) {
            throw new RepositoryException("Error processing line: " + line, e);
        }
    }

    private void writeToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (E entity : entities.values()) {
                bw.write(createEntityAsString(entity));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RepositoryException("Failed to write to file: " + fileName, e);
        }
    }

    @Override
    public E save(E entity) {
        E e = super.save(entity);
        writeToFile();
        return e;
    }

    @Override
    public E delete(I id) {
        E e = super.delete(id);
        writeToFile();
        return e;
    }

    @Override
    public E update(E entity) {
        E e = super.update(entity);
        writeToFile();
        return e;
    }

    protected abstract E extractEntity(String line);

    protected abstract String createEntityAsString(E entity);
}
