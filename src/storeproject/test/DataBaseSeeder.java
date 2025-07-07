package storeproject.test;

import storeproject.mysql.UserDAO;
import storeproject.mysql.ProductDAO;
import storeproject.mysql.SaleDAO;
import storeproject.model.User;
import storeproject.model.UserRole;
import storeproject.model.Product;
import storeproject.model.Sale;
import storeproject.model.PaymentMethod;

public class DataBaseSeeder {
    public static void main(String[] args) {
        // Instancia os DAOs
        UserDAO userDAO = new UserDAO();
        ProductDAO productDAO = new ProductDAO();
        SaleDAO saleDAO = new SaleDAO();

        // 1) Criar usuários de teste
        User alice = new User("Alice Silva", "999.888.777-66", "senha1", UserRole.client,
                "Rua A, 10", "Manaus", "AM", "69001000");
        User bruno = new User("Bruno Costa", "888.777.666-55", "senha2", UserRole.client,
                "Av. B, 20", "Manaus", "AM", "69002000");

        if (userDAO.createUser(alice)) {
            System.out.println("Usuário criado: " + alice.getName() + " (ID=" + alice.getId() + ")");
        } else {
            System.out.println("Falha ao criar usuário ou já existente: " + alice.getCpf());
            alice = userDAO.getUserByCpf(alice.getCpf());
        }
        if (userDAO.createUser(bruno)) {
            System.out.println("Usuário criado: " + bruno.getName() + " (ID=" + bruno.getId() + ")");
        } else {
            System.out.println("Falha ao criar usuário ou já existente: " + bruno.getCpf());
            bruno = userDAO.getUserByCpf(bruno.getCpf());
        }

        // 2) Criar produtos de teste
        Product teclado = new Product("Teclado Mecânico", "123456789001", "unit", 150.00, 20);
        Product mouse = new Product("Mouse Gamer", "123456789002", "unit", 80.00, 35);

        if (productDAO.createProduct(teclado)) {
            System.out.println("Produto criado: " + teclado.getDescription() + " (ID=" + teclado.getId() + ")");
        } else {
            System.out.println("Falha ao criar produto ou já existente: " + teclado.getBarCode());
            teclado = productDAO.getProductByBarcode(teclado.getBarCode());
        }
        if (productDAO.createProduct(mouse)) {
            System.out.println("Produto criado: " + mouse.getDescription() + " (ID=" + mouse.getId() + ")");
        } else {
            System.out.println("Falha ao criar produto ou já existente: " + mouse.getBarCode());
            mouse = productDAO.getProductByBarcode(mouse.getBarCode());
        }

        // 3) Registrar vendas
        // Venda 1: Alice compra 2 teclados e 1 mouse
        Sale sale1 = new Sale(alice.getId(), PaymentMethod.DEBIT_CARD);
        sale1.addItem(teclado, 2);
        sale1.addItem(mouse, 1);

        if (saleDAO.registerSale(sale1)) {
            System.out.println("Venda registrada para " + alice.getName() + ", total: " + sale1.getTotalAmount());
        } else {
            System.out.println("Falha ao registrar venda para " + alice.getName());
        }

        // Venda 2: Bruno compra 3 mouses
        Sale sale2 = new Sale(bruno.getId(), PaymentMethod.CREDIT_CARD);
        sale2.addItem(mouse, 3);

        if (saleDAO.registerSale(sale2)) {
            System.out.println("Venda registrada para " + bruno.getName() + ", total: " + sale2.getTotalAmount());
        } else {
            System.out.println("Falha ao registrar venda para " + bruno.getName());
        }

        System.out.println("Seeding concluído.");
    }
}