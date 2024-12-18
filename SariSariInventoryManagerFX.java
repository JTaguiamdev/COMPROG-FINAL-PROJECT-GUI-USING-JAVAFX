package com.example.sarisaristoreinventorymanagment;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.beans.binding.Bindings;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class SariSariInventoryManagerFX extends Application {

    private TableView<Product> inventoryTable;
    private ObservableList<Product> inventory;
    private TextField searchField;
    private int nextProductId = 1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new LoginPage(this::showInventoryManager).start(new Stage());
    }

    private void showInventoryManager(Stage primaryStage) {
        inventory = FXCollections.observableArrayList();
        primaryStage.setTitle("Sari-Sari Store Inventory Manager");

        inventoryTable = new TableView<>();
        inventoryTable.setEditable(false);

        TableColumn<Product, Integer> idColumn = new TableColumn<>("Product ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText("#" + id);
                }
            }
        });


        TableColumn<Product, String> nameColumn = new TableColumn<>("Product Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        // Add a cell factory to format the quantity column
        quantityColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);
                if (empty) {
                    setText(null);
                } else {
                    // Format the quantity with "pc" or "pcs"
                    setText(quantity + (quantity == 1 ? " pc" : " pcs"));
                }
            }
        });

        TableColumn<Product, Double> purchasePriceColumn = new TableColumn<>("Purchase Price");
        purchasePriceColumn.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        purchasePriceColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText("₱" + String.format("%.2f", price));
                }
            }
        });

        TableColumn<Product, Double> sellingPriceColumn = new TableColumn<>("Selling Price");
        sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        sellingPriceColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText("₱" + String.format("%.2f", price));
                }
            }
        });

        inventoryTable.getColumns().addAll(idColumn, nameColumn, quantityColumn, purchasePriceColumn, sellingPriceColumn);
        inventoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button addButton = new Button("Add Item");
        addButton.setOnAction(e -> addItem());

        Button editButton = new Button("Edit Item");
        editButton.setOnAction(e -> editItem());

        Button deleteButton = new Button("Delete Item");
        deleteButton.setOnAction(e -> deleteItem());

        Button generateReportButton = new Button("Generate Report");
        generateReportButton.setOnAction(e -> generateReport());

        Button revenueVsCostButton = new Button("Revenue vs Cost");
        revenueVsCostButton.setOnAction(e -> createLineChart());

        Button productQuantityPieChartButton = new Button("Product Quantity Pie Chart");
        productQuantityPieChartButton.setOnAction(e -> createPieChart());

        searchField = new TextField();
        searchField.setPromptText("Quick search");
        searchField.setOnAction(e -> quickSearch(searchField.getText()));

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10));
        topBar.getChildren().addAll(searchField, addButton, editButton, deleteButton,
                generateReportButton, revenueVsCostButton, productQuantityPieChartButton);

        VBox tableAndButtonsPane = new VBox(10);
        tableAndButtonsPane.setPadding(new Insets(10));
        tableAndButtonsPane.getChildren().addAll(topBar, inventoryTable);
        tableAndButtonsPane.setSpacing(20);
        tableAndButtonsPane.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY)));

        Pane titlePane = new Pane();
        titlePane.setPrefHeight(100);
        titlePane.setBackground(new Background(new BackgroundFill(Color.web("#BDBDBD"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label titleLabel = new Label("Sari-Sari Store Inventory Manager");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 44));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.layoutXProperty().bind(titlePane.widthProperty().subtract(titleLabel.widthProperty()).divide(2));
        titleLabel.layoutYProperty().bind(titlePane.heightProperty().subtract(titleLabel.heightProperty()).divide(2));
        titlePane.getChildren().add(titleLabel);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(titlePane);
        root.setCenter(tableAndButtonsPane);
        root.setBackground(new Background(new BackgroundFill(Color.web("#BDBDBD"), CornerRadii.EMPTY, Insets.EMPTY)));

        addButton.setPrefHeight(40);
        editButton.setPrefHeight(40);
        deleteButton.setPrefHeight(40);
        generateReportButton.setPrefHeight(40);
        revenueVsCostButton.setPrefHeight(40);
        productQuantityPieChartButton.setPrefHeight(40);
        searchField.setPrefHeight(50);

        addButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-background-radius: 10;");
        editButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-background-radius: 10;");
        deleteButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-background-radius: 10;");
        generateReportButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-background-radius: 10;");
        revenueVsCostButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-background-radius: 10;");
        productQuantityPieChartButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-background-radius: 10;");

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        loadInventoryFromFile();
        findNextProductId();
    }

    private void addItem() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add New Item");
        dialog.setHeaderText("Enter item details:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        TextField quantityField = new TextField("0");
        TextField purchasePriceField = new TextField();
        TextField sellingPriceField = new TextField();

        grid.add(new Label("Product Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Quantity:"), 0, 1);

        HBox quantityBox = new HBox(5);
        Button minusButton = new Button("-");
        minusButton.setOnAction(_ -> {
            int currentQuantity = Integer.parseInt(quantityField.getText());
            if (currentQuantity > 0) {
                quantityField.setText(String.valueOf(currentQuantity - 1));
            }
        });
        Button plusButton = new Button("+");
        plusButton.setOnAction(e -> {
            int currentQuantity = Integer.parseInt(quantityField.getText());
            quantityField.setText(String.valueOf(currentQuantity + 1));
        });
        quantityBox.getChildren().addAll(minusButton, quantityField, plusButton);
        grid.add(quantityBox, 1, 1);

        grid.add(new Label("Purchase Price:"), 0, 2);
        grid.add(purchasePriceField, 1, 2);
        grid.add(new Label("Selling Price:"), 0, 3);
        grid.add(sellingPriceField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        nameField.requestFocus();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText();
                    int quantity = Integer.parseInt(quantityField.getText());

                    if (quantity <= 0) {
                        showAlert("Error", "Quantity cannot be 0 or negative.");
                        quantityField.clear();
                        quantityField.requestFocus();
                        return null; // Prevent dialog from closing
                    }

                    int id = generateNextProductId();
                    double purchasePrice = Double.parseDouble(purchasePriceField.getText());
                    double sellingPrice = Double.parseDouble(sellingPriceField.getText());
                    return new Product(id, name, quantity, purchasePrice, sellingPrice);

                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid input. Please enter numbers for quantity and prices.");
                    // Clear the input fields
                    nameField.clear();
                    quantityField.setText("0");
                    purchasePriceField.clear();
                    sellingPriceField.clear();
                    nameField.requestFocus(); // Set focus back to name field
                    return null; // Prevent dialog from closing
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(product -> {
            inventory.add(product);
            saveInventoryToFile();
        });
    }

    private void editItem() {
        Product selectedProduct = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Error", "Please select an item to edit.");
            return;
        }

        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Edit Item");
        dialog.setHeaderText("Edit item details:");

        ButtonType editButtonType = new ButtonType("Edit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(editButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(selectedProduct.getName());
        TextField quantityField = new TextField(String.valueOf(selectedProduct.getQuantity()));
        TextField purchasePriceField = new TextField(String.valueOf(selectedProduct.getPurchasePrice()));
        TextField sellingPriceField = new TextField(String.valueOf(selectedProduct.getSellingPrice()));

        grid.add(new Label("Product Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Quantity:"), 0, 1);

        HBox quantityBox = new HBox(5);
        Button minusButton = new Button("-");
        minusButton.setOnAction(_ -> {
            int currentQuantity = Integer.parseInt(quantityField.getText());
            if (currentQuantity > 0) {
                quantityField.setText(String.valueOf(currentQuantity - 1));
            }
        });
        Button plusButton = new Button("+");
        plusButton.setOnAction(e -> {
            int currentQuantity = Integer.parseInt(quantityField.getText());
            quantityField.setText(String.valueOf(currentQuantity + 1));
        });
        quantityBox.getChildren().addAll(minusButton, quantityField, plusButton);
        grid.add(quantityBox, 1, 1);

        grid.add(new Label("Purchase Price:"), 0, 2);
        grid.add(purchasePriceField, 1, 2);
        grid.add(new Label("Selling Price:"), 0, 3);
        grid.add(sellingPriceField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        nameField.requestFocus();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == editButtonType) {
                try {
                    int id = selectedProduct.getId();
                    String name = nameField.getText();
                    int quantity = Integer.parseInt(quantityField.getText());
                    if (quantity <= 0) { // Check if quantity is zero or negative
                        showAlert("Error", "Quantity cannot be zero or negative.");
                        return null; // Prevent the dialog from closing
                    }
                    double purchasePrice = Double.parseDouble(purchasePriceField.getText());
                    double sellingPrice = Double.parseDouble(sellingPriceField.getText());
                    return new Product(id, name, quantity, purchasePrice, sellingPrice);
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid input. Please enter numbers for quantity and prices.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(product -> {
            int selectedIndex = inventoryTable.getSelectionModel().getSelectedIndex();
            inventory.set(selectedIndex, product);
            saveInventoryToFile();
        });
    }

    private void deleteItem() {
        Product selectedProduct = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Error", "Please select an item to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Are you sure you want to delete this item?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                inventory.remove(selectedProduct);
                saveInventoryToFile();
            }
        });
    }

    private void generateReport() {
        double totalValue = 0;
        double potentialProfit = 0;
        for (Product p : inventory) {
            totalValue += p.getQuantity() * p.getPurchasePrice();
            potentialProfit += p.getQuantity() * (p.getSellingPrice() - p.getPurchasePrice());
        }

        String report = "Total Items: " + inventory.size() + "\n" +
                "Total Value: ₱" + String.format("%.2f", totalValue) + "\n" +
                "Potential Profit: ₱" + String.format("%.2f", potentialProfit);

        TextArea textArea = new TextArea(report);
        textArea.setEditable(false);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inventory Report");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private void createLineChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Product Name");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount (₱)");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Revenue vs Cost");

        List<Product> products = new ArrayList<>(inventory);
        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Revenue");
        XYChart.Series<String, Number> costSeries = new XYChart.Series<>();
        costSeries.setName("Cost");

        for (Product p : products) {
            revenueSeries.getData().add(new XYChart.Data<>(p.getName(), p.getSellingPrice() * p.getQuantity()));
            costSeries.getData().add(new XYChart.Data<>(p.getName(), p.getPurchasePrice() * p.getQuantity()));
        }

        lineChart.getData().addAll(revenueSeries, costSeries);

        Stage chartStage = new Stage();
        chartStage.setTitle("Revenue vs Cost Chart");
        Scene scene = new Scene(lineChart, 800, 600);
        chartStage.setScene(scene);
        chartStage.show();
    }

    private void createPieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Product p : inventory) {
            pieChartData.add(new PieChart.Data(p.getName(), p.getQuantity()));
        }

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Product Quantity Distribution");

        pieChart.setLabelLineLength(10);
        pieChart.setLabelsVisible(true);
        pieChart.setStartAngle(180);

        for (PieChart.Data data : pieChart.getData()) {
            data.nameProperty().bind(
                    Bindings.concat(
                            data.getName(), " ",
                            Bindings.format("%.1f%%", Bindings.multiply(data.pieValueProperty(), 100.0 / pieChart.getData().stream().mapToDouble(PieChart.Data::getPieValue).sum()))
                    )
            );
        }

        Stage chartStage = new Stage();
        chartStage.setTitle("Product Quantity Pie Chart");
        Scene scene = new Scene(pieChart, 800, 600);
        chartStage.setScene(scene);
        chartStage.show();
    }

    private void quickSearch(String searchText) {
        for (int i = 0; i < inventory.size(); i++) {
            Product p = inventory.get(i);
            if (String.valueOf(p.getId()).equals(searchText) ||
                    p.getName().contains(searchText)) {
                inventoryTable.getSelectionModel().select(i);
                inventoryTable.scrollTo(i);
                return;
            }
        }
        showAlert("Search Result", "Product not found.");
    }

    private void loadInventoryFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("inventory.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    int quantity = Integer.parseInt(parts[2]);
                    double purchasePrice = Double.parseDouble(parts[3]);
                    double sellingPrice = Double.parseDouble(parts[4]);
                    inventory.add(new Product(id, name, quantity, purchasePrice, sellingPrice));
                }
            }
        } catch (IOException | NumberFormatException e) {
            showAlert("Error", "Error loading inventory from file.");
        }
        inventoryTable.setItems(inventory);
    }

    private void saveInventoryToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("inventory.txt"))) {
            for (Product p : inventory) {
                writer.write(p.getId() + "," + p.getName() + "," + p.getQuantity() + "," +
                        p.getPurchasePrice() + "," + p.getSellingPrice());
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert("Error", "Error saving inventory to file.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int generateNextProductId() {
        int id = nextProductId;
        nextProductId++;
        return id;
    }

    private void findNextProductId() {
        int maxId = 0;
        for (Product product : inventory) {
            if (product.getId() > maxId) {
                maxId = product.getId();
            }
        }
        nextProductId = maxId + 1;
    }

    public static class Product {
        private int id;
        private String name;
        private int quantity;
        private double purchasePrice;
        private double sellingPrice;

        public Product(int id, String name, int quantity, double purchasePrice, double sellingPrice) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.purchasePrice = purchasePrice;
            this.sellingPrice = sellingPrice;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPurchasePrice() {
            return purchasePrice;
        }

        public double getSellingPrice() {
            return sellingPrice;
        }

        public void setSellingPrice(double sellingPrice) {
            this.sellingPrice = sellingPrice;
        }
    }
}