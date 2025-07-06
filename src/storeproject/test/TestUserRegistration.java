package storeproject.test;


import storeproject.view.UserCreationView;

import javax.swing.*;
/*
* Classe para testar se a interface gráfica interage com o banco de dados
* */
public class TestUserRegistration {
    public static void main(String[] args) {
        // Configura o look and feel do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Erro ao configurar look and feel: " + ex.getMessage());
        }

        // Cria e exibe a janela na thread de eventos do Swing
        javax.swing.SwingUtilities.invokeLater(() -> {
            UserCreationView view = new UserCreationView();
            view.setVisible(true);
        });
    }
}
