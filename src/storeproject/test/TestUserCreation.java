package storeproject.test;
import storeproject.model.User;
import storeproject.mysql.UserDAO;
import storeproject.model.UserRole;
/*
*
* Classe para testar se o programa estava criando tabelas no banco de dados
* */

public class TestUserCreation {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        User testUser = new User(
                "Usuário Teste",
                "566.222.333-44", // Altere para um CPF único
                "senhateste",
                UserRole.cashier,
                "Rua Teste, 123",
                "Testópolis",
                "TS",
                "12345678"
        );

        try {
            boolean success = userDAO.createUser(testUser);
            System.out.println("Sucesso? " + success);
            System.out.println("ID gerado: " + testUser.getId());
        } catch (Exception e) {
            System.err.println("Erro ao criar usuário:");
            e.printStackTrace();
        }
    }
}