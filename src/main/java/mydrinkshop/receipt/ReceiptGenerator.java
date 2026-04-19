package mydrinkshop.receipt;

import mydrinkshop.domain.Order;
import mydrinkshop.domain.OrderItem;
import mydrinkshop.domain.Product;

import java.util.List;

public class ReceiptGenerator {
    private ReceiptGenerator() {
    }

    public static String generate(Order o, List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== BON FISCAL =====\n").append("Comanda #").append(o.getId()).append("\n");
        for (OrderItem i : o.getItems()) {
            Product p = products.stream()
                    .filter(p1 -> i.getProduct().getId() == p1.getId())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Product with id " + i.getProduct().getId() + " not found in product list"));

            sb.append(p.getNume()).append(": ")
                    .append(p.getPret()).append(" x ")
                    .append(i.getQuantity()).append(" = ")
                    .append(i.getTotal()).append(" RON\n");
        }
        sb.append("---------------------\nTOTAL: ").append(o.getTotalPrice()).append(" RON\n=====================\n");
        return sb.toString();
    }
}