package storeproject.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import storeproject.model.User;

import storeproject.mysql.UserDAO;
import storeproject.model.UserRole;

public class UserCreationView extends JFrame {
    private UserDAO userDAO;

    // Componentes da interface
    private JTextField txtNome;
    private JTextField txtCpf;
    private JPasswordField txtPassword;
    private JComboBox<UserRole> cmbRole;
    private JTextField txtAddress;
    private JTextField txtCity;
    private JTextField txtState;
    private JTextField txtZip;
    private JButton btnRegister;
    private JButton btnClear;
    private JTextArea txtLog;

    public UserCreationView() {
        this.userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Cadastro de Usuários");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel de formulário
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));

        // Campos do formulário
        formPanel.add(new JLabel("Nome Completo:"));
        txtNome = new JTextField();
        formPanel.add(txtNome);

        formPanel.add(new JLabel("CPF:"));
        txtCpf = new JTextField();
        formPanel.add(txtCpf);

        formPanel.add(new JLabel("Senha:"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Tipo de Usuário:"));
        cmbRole = new JComboBox<>(UserRole.values());
        formPanel.add(cmbRole);

        formPanel.add(new JLabel("Endereço:"));
        txtAddress = new JTextField();
        formPanel.add(txtAddress);

        formPanel.add(new JLabel("Cidade:"));
        txtCity = new JTextField();
        formPanel.add(txtCity);

        formPanel.add(new JLabel("Estado (UF):"));
        txtState = new JTextField();
        formPanel.add(txtState);

        formPanel.add(new JLabel("CEP:"));
        txtZip = new JTextField();
        formPanel.add(txtZip);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnRegister = new JButton("Cadastrar");
        btnRegister.addActionListener(this::registerUser);
        buttonPanel.add(btnRegister);

        btnClear = new JButton("Limpar");
        btnClear.addActionListener(e -> clearFields());
        buttonPanel.add(btnClear);

        // Área de log
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtLog);

        // Adiciona componentes ao painel principal
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void registerUser(ActionEvent event) {
        try {
            // Validação básica
            if (txtNome.getText().isEmpty() || txtCpf.getText().isEmpty() || txtPassword.getPassword().length == 0) {
                appendLog("Erro: Nome, CPF e Senha são obrigatórios!");
                return;
            }

            // Cria objeto User
            User newUser = new User(
                    txtNome.getText(),
                    txtCpf.getText(),
                    new String(txtPassword.getPassword()),
                    (UserRole) cmbRole.getSelectedItem(),
                    txtAddress.getText().isEmpty() ? null : txtAddress.getText(),
                    txtCity.getText().isEmpty() ? null : txtCity.getText(),
                    txtState.getText().isEmpty() ? null : txtState.getText(),
                    txtZip.getText().isEmpty() ? null : txtZip.getText()
            );

            appendLog("Tentando cadastrar usuário...");
            appendLog("Dados: " + newUser.toString());

            // Chama o DAO para criar o usuário
            boolean success = userDAO.createUser(newUser);

            if (success) {
                appendLog("Usuário cadastrado com sucesso! ID: " + newUser.getId());
                clearFields();
            } else {
                appendLog("Falha ao cadastrar usuário (sem erros detectados)");
            }
        } catch (Exception ex) {
            appendLog("ERRO: " + ex.getMessage());
            if (ex.getCause() != null) {
                appendLog("Causa: " + ex.getCause().getMessage());
            }
        }
    }

    private void clearFields() {
        txtNome.setText("");
        txtCpf.setText("");
        txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
        txtAddress.setText("");
        txtCity.setText("");
        txtState.setText("");
        txtZip.setText("");
    }

    private void appendLog(String message) {
        txtLog.append(message + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
}