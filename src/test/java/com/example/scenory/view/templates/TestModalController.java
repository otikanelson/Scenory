package com.example.scenory.view.templates;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for test FXML modal.
 */
public class TestModalController {
    
    @FXML
    private Label titleLabel;
    
    public String getTitle() {
        return titleLabel != null ? titleLabel.getText() : null;
    }
    
    public void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }
}
