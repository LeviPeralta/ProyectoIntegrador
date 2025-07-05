import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;

public class Main extends Application {

    private VBox formContainer;
    private VBox welcomeContainer;
    private boolean isSignIn = true;
    private Font customFont;
    private String verificationCode;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        loadCustomFont();

        primaryStage.setTitle("Login / Registro");

        HBox mainContainer = new HBox();
        mainContainer.setPrefSize(800, 500);
        mainContainer.setMaxSize(800, 500);
        mainContainer.setStyle("-fx-background-color: white; -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 0);");

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(800, 500);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        mainContainer.setClip(clip);

        formContainer = new VBox();
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setSpacing(15);
        formContainer.setPadding(new Insets(30));
        formContainer.setPrefWidth(400);

        welcomeContainer = new VBox();
        welcomeContainer.setAlignment(Pos.CENTER);
        welcomeContainer.setSpacing(20);
        welcomeContainer.setPadding(new Insets(40, 50, 40, 50));
        welcomeContainer.setPrefWidth(400);
        welcomeContainer.setStyle("-fx-background-color: #3e7bb2; -fx-border-radius: 0 15 15 0;");

        formContainer.setTranslateX(0);
        welcomeContainer.setTranslateX(0);

        updateForm();
        updateWelcome();

        mainContainer.getChildren().addAll(formContainer, welcomeContainer);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #F0F4F3;");
        root.getChildren().add(mainContainer);
        root.setPrefSize(800, 500);

        Scene scene = new Scene(root, 800, 500);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadCustomFont() {
        InputStream fontStream = getClass().getResourceAsStream("/resources/fonts/InstrumentSans-VariableFont_wdth,wght.ttf");
        if (fontStream != null) {
            customFont = Font.loadFont(fontStream, 14);
        } else {
            System.out.println("No se pudo cargar la fuente, se usará la predeterminada.");
            customFont = Font.getDefault();
        }
    }

    private void updateForm() {
        formContainer.getChildren().clear();

        if (isSignIn) {
            Label title = new Label("Iniciar Sesión");
            title.setFont(Font.font(customFont.getFamily(), 24));

            TextField emailField = new TextField();
            emailField.setPromptText("ejemplo@correo.com");
            styleTextField(emailField);

            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("••••••••");
            styleTextField(passwordField);

            VBox emailBox = createLabeledField("Correo electrónico", emailField);
            VBox passwordBox = createLabeledField("Contraseña", passwordField);

            Hyperlink forgotPassword = new Hyperlink("¿Olvidaste tu contraseña?");
            forgotPassword.setFont(customFont);
            forgotPassword.setStyle("-fx-text-fill: #134074; -fx-underline: true;");
            forgotPassword.setOnAction(e -> showPasswordResetWindow());

            Button signInButton = new Button("Iniciar sesión");
            styleButton(signInButton);

            formContainer.getChildren().addAll(title, emailBox, passwordBox, forgotPassword, signInButton);
        } else {
            Label title = new Label("Registrarse");
            title.setFont(Font.font(customFont.getFamily(), 24));

            TextField nameField = new TextField();
            nameField.setPromptText("ejemplo@correo.com");
            styleTextField(nameField);

            PasswordField passwordField2 = new PasswordField();
            passwordField2.setPromptText("••••••••");
            styleTextField(passwordField2);

            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("••••••••");
            styleTextField(passwordField);

            VBox nameBox = createLabeledField("Correo electronico", nameField);
            VBox emailBox = createLabeledField("Contraseña", passwordField2);
            VBox passwordBox = createLabeledField("Confirme su contraseña", passwordField);

            Button signUpButton = new Button("Registrarse");
            styleButton(signUpButton);

            signUpButton.setOnAction(e -> {
                String email = nameField.getText().trim();
                String password = passwordField2.getText().trim();
                String confirmPassword = passwordField.getText().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    showAlert("Campos incompletos", "Por favor complete el correo y la contraseña.");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    showAlert("Error", "Las contraseñas no coinciden.");
                    return;
                }

                // Abrir nueva pantalla si todo está correcto
                showPatientForm();
            });

            formContainer.getChildren().addAll(title, nameBox, emailBox, passwordBox, signUpButton);

        }
    }

    private void updateWelcome() {
        welcomeContainer.getChildren().clear();

        Label title = new Label(isSignIn ? "¡Bienvenido!" : "¡Hola!");
        title.setFont(Font.font(customFont.getFamily(), 30));
        title.setTextFill(Color.WHITE);

        Label message = new Label(isSignIn ?
                "Ingresa tus datos personales para usar todas las funciones del sitio" :
                "Regístrese con sus datos personales para usar todas las funciones del sitio");
        message.setWrapText(true);
        message.setTextFill(Color.WHITE);
        message.setFont(Font.font(customFont.getFamily(), 14));
        message.setAlignment(Pos.CENTER);
        message.setMaxWidth(300);
        message.setStyle("-fx-text-alignment: center;");

        Button toggleButton = new Button(isSignIn ? "REGISTRARSE" : "INICIAR SESIÓN");
        toggleButton.setFont(Font.font(customFont.getFamily(), 14));
        toggleButton.setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-border-width: 2; -fx-text-fill: white;");
        toggleButton.setOnAction(e -> {
            isSignIn = !isSignIn;
            updateForm();
            updateWelcome();
        });

        animatePanelColor(isSignIn);
        animatePanelSlide(isSignIn);

        welcomeContainer.setStyle(isSignIn
                ? "-fx-border-radius: 250 0 0 250;"
                : "-fx-border-radius: 0 250 250 0;");

        welcomeContainer.getChildren().addAll(title, message, toggleButton);
    }

    private VBox createLabeledField(String labelText, TextField inputField) {
        Label label = new Label(labelText);
        label.setFont(Font.font(customFont.getFamily(), 12));
        label.setTextFill(Color.web("#333"));

        VBox fieldBox = new VBox(5);
        fieldBox.getChildren().addAll(label, inputField);
        return fieldBox;
    }

    private void styleTextField(TextField tf) {
        tf.setFont(Font.font(customFont.getFamily(), 13));
        tf.setPrefWidth(250);
        tf.setPrefHeight(40);
        tf.setStyle("-fx-background-color: #EEEEEE; -fx-background-radius: 5; -fx-border-radius: 5;");
    }
    private void styleTextField(ComboBox<?> cb) {
        cb.setPrefWidth(250);
        cb.setPrefHeight(40);
        cb.setStyle("-fx-background-color: #EEEEEE; -fx-background-radius: 5; -fx-border-radius: 5;");
    }
    private void styleTextField(DatePicker dp) {
        dp.setPrefWidth(250);
        dp.setPrefHeight(40);
        dp.setStyle("-fx-background-color: #EEEEEE; -fx-background-radius: 5; -fx-border-radius: 5;");
    }


    private void styleButton(Button btn) {
        btn.setFont(Font.font(customFont.getFamily(), 15));
        btn.setPrefWidth(200);
        btn.setPrefHeight(35);
        String color = isSignIn ? "#134074" : "#13315C";
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5;");

        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
            btn.setStyle("-fx-background-color: " + darken(color) + "; -fx-text-fill: white; -fx-background-radius: 5;");
        });

        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
            btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5;");
        });
    }

    private String darken(String hexColor) {
        Color color = Color.web(hexColor);
        color = color.deriveColor(0, 1, 0.85, 1);
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void animatePanelColor(boolean toSignIn) {
        Color fromColor = toSignIn ? Color.web("#13315C") : Color.web("#134074");
        Color toColor = toSignIn ? Color.web("#134074") : Color.web("#13315C");

        CornerRadii corner = toSignIn
                ? new CornerRadii(250, 0, 0, 250, false)
                : new CornerRadii(0, 250, 250, 0, false);

        Background fromBg = new Background(new BackgroundFill(fromColor, corner, Insets.EMPTY));
        Background toBg = new Background(new BackgroundFill(toColor, corner, Insets.EMPTY));

        Timeline colorTransition = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(welcomeContainer.backgroundProperty(), fromBg)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(welcomeContainer.backgroundProperty(), toBg))
        );
        colorTransition.play();
    }

    private void animatePanelSlide(boolean toSignIn) {
        double offset = 400;

        TranslateTransition welcomeSlide = new TranslateTransition(Duration.seconds(0.5), welcomeContainer);
        TranslateTransition formSlide = new TranslateTransition(Duration.seconds(0.5), formContainer);

        welcomeSlide.setFromX(toSignIn ? -offset : 0);
        welcomeSlide.setToX(toSignIn ? 0 : -offset);

        formSlide.setFromX(toSignIn ? offset : 0);
        formSlide.setToX(toSignIn ? 0 : offset);

        welcomeSlide.play();
        formSlide.play();
    }

    // Funciones de recuperación de contraseña
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(emailRegex, email);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showPasswordResetWindow() {
        Stage window = new Stage();
        window.setTitle("Cambio de contraseña");
        window.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: white;");

        Label title = new Label("Cambio de contraseña");
        title.setFont(Font.font(customFont.getFamily(), 20));
        title.setTextFill(Color.web("#13315C"));

        Label message = new Label("Ingrese el correo del cual desea cambiar la contraseña, le enviaremos un código para reestablecerla");
        message.setFont(Font.font(customFont.getFamily(), 14));
        message.setTextFill(Color.web("#13315C"));
        message.setWrapText(true);
        message.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        styleTextField(emailField);

        Button sendButton = new Button("Enviar");
        styleButton(sendButton);

        sendButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            if (!isValidEmail(email)) {
                showAlert("Correo inválido", "Por favor ingrese un correo electrónico válido.");
                return;
            }
            sendEmailWithCode(email);
            showCodeVerificationWindow();
            window.close();
        });

        layout.getChildren().addAll(title, message, emailField, sendButton);

        Scene scene = new Scene(layout, 500, 300);
        window.setScene(scene);
        window.showAndWait();
    }

    private void sendEmailWithCode(String toEmail) {
        verificationCode = String.valueOf(new Random().nextInt(900000) + 100000);

        final String username = "leviytp@gmail.com";
        final String password = "xccq ujfn cvgo oyqs";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Código de recuperación");
            message.setText("Tu código de recuperación es: " + verificationCode);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo enviar el correo: " + e.getMessage());
        }
    }

    private void showCodeVerificationWindow() {
        Stage window = new Stage();
        window.setTitle("Cambio de contraseña");
        window.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: white; -fx-border-radius: 15;");

        Label title = new Label("Cambio de contraseña");
        title.setFont(Font.font(customFont.getFamily(), 20));
        title.setTextFill(Color.web("#13315C"));

        Label instructions = new Label("Ingrese el código enviado a su correo, para cambiar su contraseña");
        instructions.setWrapText(true);
        instructions.setTextFill(Color.web("#13315C"));
        instructions.setFont(Font.font(customFont.getFamily(), 14));
        instructions.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        TextField codeField = new TextField();
        codeField.setPromptText("Código");
        styleTextField(codeField);

        Button sendButton = new Button("Enviar");
        styleButton(sendButton);

        sendButton.setOnAction(e -> {
            String enteredCode = codeField.getText().trim();
            if (enteredCode.equals(verificationCode)) {
                showAlert("Éxito", "Código verificado correctamente. Ahora puedes cambiar tu contraseña.");
                window.close();
                showNewPasswordWindow();
            } else {
                showAlert("Error", "El código ingresado no es correcto.");
            }
        });

        layout.getChildren().addAll(title, instructions, codeField, sendButton);

        Scene scene = new Scene(layout, 500, 300);
        window.setScene(scene);
        window.showAndWait();
    }

    private void showNewPasswordWindow() {
        Stage window = new Stage();
        window.setTitle("Cambio de contraseña");
        window.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: white; -fx-border-radius: 15;");

        Label title = new Label("Cambio de contraseña");
        title.setFont(Font.font(customFont.getFamily(), 20));
        title.setTextFill(Color.web("#13315C"));

        VBox passBox1 = new VBox(5);
        Label passLabel1 = new Label("Ingresa tu contraseña");
        passLabel1.setFont(Font.font(customFont.getFamily(), 13));
        passLabel1.setTextFill(Color.web("#13315C"));
        PasswordField pass1 = new PasswordField();
        pass1.setPromptText("Password");
        styleTextField(pass1);
        passBox1.getChildren().addAll(passLabel1, pass1);

        VBox passBox2 = new VBox(5);
        Label passLabel2 = new Label("Ingresa tu contraseña nuevamente");
        passLabel2.setFont(Font.font(customFont.getFamily(), 13));
        passLabel2.setTextFill(Color.web("#13315C"));
        PasswordField pass2 = new PasswordField();
        pass2.setPromptText("Password");
        styleTextField(pass2);
        passBox2.getChildren().addAll(passLabel2, pass2);

        Button submitButton = new Button("Enviar");
        styleButton(submitButton);

        submitButton.setOnAction(e -> {
            String p1 = pass1.getText().trim();
            String p2 = pass2.getText().trim();

            if (p1.isEmpty() || p2.isEmpty()) {
                showAlert("Error", "Por favor completa ambos campos de contraseña.");
                return;
            }

            if (!p1.equals(p2)) {
                showAlert("Error", "Las contraseñas no coinciden.");
                return;
            }

            window.close();
            showPasswordChangedSuccess();

        });

        layout.getChildren().addAll(title, passBox1, passBox2, submitButton);

        Scene scene = new Scene(layout, 500, 300);
        window.setScene(scene);
        window.showAndWait();
    }
    private void showPasswordChangedSuccess() {
        Stage window = new Stage();
        window.setTitle("Contraseña restablecida");
        window.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 10;");

        Label successLabel1 = new Label("La contraseña se ha reestablecido correctamente");
        successLabel1.setFont(Font.font(customFont.getFamily(), 16));
        successLabel1.setTextFill(Color.web("#13315C"));
        successLabel1.setWrapText(true);
        successLabel1.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label successLabel2 = new Label("Vuelve a iniciar sesión");
        successLabel2.setFont(Font.font(customFont.getFamily(), 16));
        successLabel2.setTextFill(Color.web("#13315C"));

        Button backButton = new Button("Regresar");
        styleButton(backButton);

        backButton.setOnAction(e -> {
            isSignIn = true;
            updateForm();
            updateWelcome();
            window.close();
        });

        layout.getChildren().addAll(successLabel1, successLabel2, backButton);

        Scene scene = new Scene(layout, 500, 250);
        window.setScene(scene);
        window.showAndWait();
    }

    private void showNextScreen() {
        Stage newStage = new Stage();
        newStage.setTitle("Datos del paciente");

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Label label = new Label("Pantalla siguiente");
        label.setFont(Font.font(customFont.getFamily(), 24));
        label.setTextFill(Color.web("#13315C"));

        Button closeButton = new Button("Cerrar");
        styleButton(closeButton);
        closeButton.setOnAction(e -> newStage.close());

        layout.getChildren().addAll(label, closeButton);

        Scene scene = new Scene(layout, 500, 300);
        newStage.setScene(scene);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.showAndWait();
    }
    private void showPatientForm() {
        Stage window = new Stage();
        window.setTitle("Datos personales del paciente");
        window.initModality(Modality.APPLICATION_MODAL);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(30));

        Label sectionTitle = new Label("Datos personales del paciente");
        sectionTitle.setFont(Font.font(customFont.getFamily(), 16));
        sectionTitle.setTextFill(Color.web("#13315C"));
        GridPane.setColumnSpan(sectionTitle, 2);

        TextField nombreField = new TextField();
        nombreField.setPromptText("Ej. Juan Pérez Gómez");

        DatePicker fechaNacimiento = new DatePicker();
        fechaNacimiento.setPromptText("Ej. 1995-06-12");

        ComboBox<String> generoCombo = new ComboBox<>();
        generoCombo.getItems().addAll("Masculino", "Femenino", "Otro");
        generoCombo.setPromptText("Seleccione una opción");

        TextField curpField = new TextField();
        curpField.setPromptText("Ej. PEGR950612HMCLNS08");

        TextField telefonoField = new TextField();
        telefonoField.setPromptText("Ej. 5512345678");


        // Aplicar estilos
        for (TextField field : new TextField[]{nombreField, curpField, telefonoField}) {
            styleTextField(field);
        }
        styleTextField(generoCombo);
        styleTextField(fechaNacimiento);

        // Imagen y botón
        VBox photoBox = new VBox(10);
        photoBox.setAlignment(Pos.CENTER);
        photoBox.setPrefWidth(200);

        Label photo = new Label("?");
        photo.setPrefSize(140, 160);
        photo.setStyle("-fx-border-color: #999; -fx-border-radius: 10; -fx-alignment: center;");
        photo.setFont(Font.font(36));

        Label agregarFoto = new Label("Agregar foto");
        agregarFoto.setFont(Font.font(customFont.getFamily(), 14));
        agregarFoto.setTextFill(Color.web("#13315C"));

        photoBox.getChildren().addAll(photo, agregarFoto);

        Button aceptarBtn = new Button("Aceptar");
        styleButton(aceptarBtn);
        aceptarBtn.setOnAction(e -> window.close());

        formGrid.add(sectionTitle, 0, 0);
        formGrid.add(new Label("Nombre completo"), 0, 1);
        formGrid.add(nombreField, 0, 2);

        formGrid.add(new Label("Fecha de nacimiento"), 0, 3);
        formGrid.add(fechaNacimiento, 0, 4);

        formGrid.add(new Label("Género"), 0, 5);
        formGrid.add(generoCombo, 0, 6);

        formGrid.add(new Label("CURP"), 0, 7);
        formGrid.add(curpField, 0, 8);

        formGrid.add(new Label("Teléfono"), 0, 9);
        formGrid.add(telefonoField, 0, 10);

        VBox leftForm = new VBox(30, formGrid, aceptarBtn);
        leftForm.setAlignment(Pos.TOP_CENTER);
        leftForm.setPrefWidth(400);

        HBox mainContent = new HBox(40, leftForm, photoBox);
        mainContent.setPadding(new Insets(40));
        mainContent.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(mainContent);
        root.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(root, 800, 650);
        window.setScene(scene);
        window.setMinWidth(800);
        window.setMinHeight(650);
        window.centerOnScreen();       // Centra inicialmente
        window.setResizable(true);     // Permitimos redimensionar

        // Centramos de nuevo al maximizar
        window.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
            if (isNowMaximized) {
                window.centerOnScreen();
            }
        });

        window.showAndWait();
    }
}