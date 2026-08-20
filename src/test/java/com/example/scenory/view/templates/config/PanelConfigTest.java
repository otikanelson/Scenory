package com.example.scenory.view.templates.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for PanelConfig abstract base class.
 * <p>
 * Tests cover:
 * - Defensive copying of style classes
 * - Style classes getter behavior
 * - Null handling
 * - Abstract validate() method enforcement
 * </p>
 */
@DisplayName("PanelConfig Tests")
class PanelConfigTest {
    
    /**
     * Concrete test implementation of PanelConfig for testing purposes.
     * This validates successfully (no-op validation).
     */
    private static class TestPanelConfig extends PanelConfig {
        public TestPanelConfig(List<String> styleClasses) {
            super(styleClasses);
        }
        
        @Override
        protected void validate() {
            // No-op validation for testing
        }
    }
    
    /**
     * Concrete test implementation with validation logic.
     * Throws exception if styleClasses contains "invalid".
     */
    private static class ValidatingPanelConfig extends PanelConfig {
        public ValidatingPanelConfig(List<String> styleClasses) {
            super(styleClasses);
            validate();
        }
        
        @Override
        protected void validate() {
            if (styleClasses.contains("invalid")) {
                throw new IllegalArgumentException(
                    "[ValidatingPanelConfig] Style classes cannot contain 'invalid'"
                );
            }
        }
    }
    
    @Nested
    @DisplayName("Constructor and Defensive Copying")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should accept null style classes and initialize to empty list")
        void testNullStyleClasses() {
            TestPanelConfig config = new TestPanelConfig(null);
            
            List<String> styleClasses = config.getStyleClasses();
            assertNotNull(styleClasses);
            assertTrue(styleClasses.isEmpty());
        }
        
        @Test
        @DisplayName("Should accept empty style classes list")
        void testEmptyStyleClasses() {
            List<String> emptyList = new ArrayList<>();
            TestPanelConfig config = new TestPanelConfig(emptyList);
            
            List<String> styleClasses = config.getStyleClasses();
            assertNotNull(styleClasses);
            assertTrue(styleClasses.isEmpty());
        }
        
        @Test
        @DisplayName("Should accept style classes with single item")
        void testSingleStyleClass() {
            List<String> classList = Arrays.asList("class1");
            TestPanelConfig config = new TestPanelConfig(classList);
            
            List<String> styleClasses = config.getStyleClasses();
            assertEquals(1, styleClasses.size());
            assertEquals("class1", styleClasses.get(0));
        }
        
        @Test
        @DisplayName("Should accept style classes with multiple items")
        void testMultipleStyleClasses() {
            List<String> classList = Arrays.asList("class1", "class2", "class3");
            TestPanelConfig config = new TestPanelConfig(classList);
            
            List<String> styleClasses = config.getStyleClasses();
            assertEquals(3, styleClasses.size());
            assertTrue(styleClasses.contains("class1"));
            assertTrue(styleClasses.contains("class2"));
            assertTrue(styleClasses.contains("class3"));
        }
        
        @Test
        @DisplayName("Should create defensive copy in constructor")
        void testConstructorDefensiveCopy() {
            List<String> originalList = new ArrayList<>(Arrays.asList("class1", "class2"));
            TestPanelConfig config = new TestPanelConfig(originalList);
            
            // Modify original list after construction
            originalList.add("class3");
            originalList.remove("class1");
            
            // Config should be unchanged
            List<String> styleClasses = config.getStyleClasses();
            assertEquals(2, styleClasses.size());
            assertTrue(styleClasses.contains("class1"));
            assertTrue(styleClasses.contains("class2"));
            assertTrue(!styleClasses.contains("class3"));
        }
    }
    
    @Nested
    @DisplayName("Getter Defensive Copying")
    class GetterTests {
        
        @Test
        @DisplayName("Should return defensive copy from getter")
        void testGetterDefensiveCopy() {
            List<String> classList = Arrays.asList("class1", "class2");
            TestPanelConfig config = new TestPanelConfig(classList);
            
            List<String> styleClasses1 = config.getStyleClasses();
            List<String> styleClasses2 = config.getStyleClasses();
            
            // Should be different instances
            assertNotSame(styleClasses1, styleClasses2);
            
            // But with same content
            assertEquals(styleClasses1, styleClasses2);
        }
        
        @Test
        @DisplayName("Modifying returned list should not affect config")
        void testGetterImmutability() {
            List<String> classList = Arrays.asList("class1", "class2");
            TestPanelConfig config = new TestPanelConfig(classList);
            
            List<String> styleClasses = config.getStyleClasses();
            styleClasses.add("class3");
            styleClasses.remove("class1");
            
            // Original config should be unchanged
            List<String> unchangedClasses = config.getStyleClasses();
            assertEquals(2, unchangedClasses.size());
            assertTrue(unchangedClasses.contains("class1"));
            assertTrue(unchangedClasses.contains("class2"));
            assertTrue(!unchangedClasses.contains("class3"));
        }
        
        @Test
        @DisplayName("Multiple getter calls should return independent lists")
        void testMultipleGetterCalls() {
            List<String> classList = Arrays.asList("class1");
            TestPanelConfig config = new TestPanelConfig(classList);
            
            List<String> list1 = config.getStyleClasses();
            List<String> list2 = config.getStyleClasses();
            List<String> list3 = config.getStyleClasses();
            
            // Modify each list independently
            list1.add("modified1");
            list2.add("modified2");
            list3.add("modified3");
            
            // All should have different modifications
            assertTrue(list1.contains("modified1"));
            assertTrue(!list1.contains("modified2"));
            assertTrue(!list1.contains("modified3"));
            
            assertTrue(!list2.contains("modified1"));
            assertTrue(list2.contains("modified2"));
            assertTrue(!list2.contains("modified3"));
            
            assertTrue(!list3.contains("modified1"));
            assertTrue(!list3.contains("modified2"));
            assertTrue(list3.contains("modified3"));
            
            // Original config should still have only "class1"
            List<String> original = config.getStyleClasses();
            assertEquals(1, original.size());
            assertEquals("class1", original.get(0));
        }
    }
    
    @Nested
    @DisplayName("Abstract Validate Method")
    class ValidateTests {
        
        @Test
        @DisplayName("Subclass should implement validate method")
        void testValidateImplementation() {
            // ValidatingPanelConfig calls validate() in constructor
            List<String> validClasses = Arrays.asList("class1", "class2");
            
            // Should not throw with valid classes
            ValidatingPanelConfig config = new ValidatingPanelConfig(validClasses);
            assertNotNull(config);
        }
        
        @Test
        @DisplayName("Validate method should be called and enforce constraints")
        void testValidateEnforcesConstraints() {
            List<String> invalidClasses = Arrays.asList("class1", "invalid", "class2");
            
            // Should throw when validation fails
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ValidatingPanelConfig(invalidClasses)
            );
            
            assertTrue(exception.getMessage().contains("Style classes cannot contain 'invalid'"));
        }
        
        @Test
        @DisplayName("Validate method has access to protected styleClasses field")
        void testValidateAccessToStyleClasses() {
            // This test verifies that validate() can access the protected styleClasses field
            // by confirming that ValidatingPanelConfig can check the contents
            
            List<String> classes1 = Arrays.asList("valid1", "valid2");
            ValidatingPanelConfig config1 = new ValidatingPanelConfig(classes1);
            assertEquals(2, config1.getStyleClasses().size());
            
            List<String> classes2 = Arrays.asList("invalid");
            assertThrows(
                IllegalArgumentException.class,
                () -> new ValidatingPanelConfig(classes2)
            );
        }
    }
    
    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle style classes with duplicate values")
        void testDuplicateStyleClasses() {
            List<String> classList = Arrays.asList("class1", "class2", "class1", "class3", "class2");
            TestPanelConfig config = new TestPanelConfig(classList);
            
            List<String> styleClasses = config.getStyleClasses();
            // Should preserve duplicates as provided
            assertEquals(5, styleClasses.size());
        }
        
        @Test
        @DisplayName("Should handle style classes with null values")
        void testNullValuesInStyleClasses() {
            List<String> classList = new ArrayList<>();
            classList.add("class1");
            classList.add(null);
            classList.add("class2");
            
            TestPanelConfig config = new TestPanelConfig(classList);
            
            List<String> styleClasses = config.getStyleClasses();
            assertEquals(3, styleClasses.size());
            assertTrue(styleClasses.contains(null));
        }
        
        @Test
        @DisplayName("Should handle style classes with empty strings")
        void testEmptyStringInStyleClasses() {
            List<String> classList = Arrays.asList("class1", "", "class2");
            TestPanelConfig config = new TestPanelConfig(classList);
            
            List<String> styleClasses = config.getStyleClasses();
            assertEquals(3, styleClasses.size());
            assertTrue(styleClasses.contains(""));
        }
        
        @Test
        @DisplayName("Should handle very large style classes list")
        void testLargeStyleClassesList() {
            List<String> classList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                classList.add("class" + i);
            }
            
            TestPanelConfig config = new TestPanelConfig(classList);
            
            List<String> styleClasses = config.getStyleClasses();
            assertEquals(1000, styleClasses.size());
            assertEquals("class0", styleClasses.get(0));
            assertEquals("class999", styleClasses.get(999));
        }
    }
    
    @Nested
    @DisplayName("Integration with Subclasses")
    class SubclassIntegrationTests {
        
        @Test
        @DisplayName("Multiple subclass instances should be independent")
        void testSubclassIndependence() {
            List<String> list1 = Arrays.asList("config1-class");
            List<String> list2 = Arrays.asList("config2-class");
            
            TestPanelConfig config1 = new TestPanelConfig(list1);
            TestPanelConfig config2 = new TestPanelConfig(list2);
            
            assertEquals(1, config1.getStyleClasses().size());
            assertEquals("config1-class", config1.getStyleClasses().get(0));
            
            assertEquals(1, config2.getStyleClasses().size());
            assertEquals("config2-class", config2.getStyleClasses().get(0));
        }
        
        @Test
        @DisplayName("Subclass can extend with additional fields")
        void testSubclassExtension() {
            // This demonstrates that subclasses can add their own fields
            class ExtendedPanelConfig extends PanelConfig {
                private final String customField;
                
                public ExtendedPanelConfig(List<String> styleClasses, String customField) {
                    super(styleClasses);
                    this.customField = customField;
                    validate();
                }
                
                @Override
                protected void validate() {
                    if (customField == null || customField.isEmpty()) {
                        throw new IllegalArgumentException("Custom field cannot be null or empty");
                    }
                }
                
                public String getCustomField() {
                    return customField;
                }
            }
            
            List<String> classList = Arrays.asList("class1");
            ExtendedPanelConfig config = new ExtendedPanelConfig(classList, "customValue");
            
            assertEquals(1, config.getStyleClasses().size());
            assertEquals("customValue", config.getCustomField());
            
            // Validation should work
            assertThrows(
                IllegalArgumentException.class,
                () -> new ExtendedPanelConfig(classList, "")
            );
        }
    }
}
