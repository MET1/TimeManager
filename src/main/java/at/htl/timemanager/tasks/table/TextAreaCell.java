package at.htl.timemanager.tasks.table;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Cell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

/**
 * @timeline .
 * 05.12.2016: MET 001  created class
 * 05.12.2016: MET 090  TextAreaCell: createTextArea, update Item
 */
public class TextAreaCell<S, T> extends TableCell<S, T> {

    private TextArea textArea;
    private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>(this, "converter");

    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return forTableColumn(new DefaultStringConverter());
    }

    public static <S, T> Callback<TableColumn<S, T>,
            TableCell<S, T>> forTableColumn(final StringConverter<T> converter) {
        return list -> new TextAreaCell<>(converter);
    }

    private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
        return converter == null ? cell.getItem() == null ? "" : cell.getItem()
                .toString() : converter.toString(cell.getItem());
    }

    private static <T> TextArea createTextArea(final Cell<T> cell, final StringConverter<T> converter) {
        TextArea textArea = new TextArea(getItemText(cell, converter));
        textArea.setWrapText(true);
        textArea.setMinHeight(0);
        textArea.prefRowCountProperty().bind(Bindings.size(textArea.getParagraphs()).subtract(1));
        textArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                cell.commitEdit(converter.fromString(textArea.getText()));
            }
        });
        textArea.setOnKeyPressed(t -> {
            if (t.isShiftDown() && t.getCode() == KeyCode.ENTER) {
                textArea.appendText("\n");
            } else if (t.getCode() == KeyCode.ESCAPE || t.getCode() == KeyCode.ENTER) {
                if (converter == null) {
                    throw new IllegalStateException(
                            "Attempting to convert text input into Object, but provided "
                                    + "StringConverter is null. Be sure to set a StringConverter "
                                    + "in your cell factory.");
                }
                cell.commitEdit(converter.fromString(textArea.getText()));
                t.consume();
            }
        });
        return textArea;
    }

    private void startEdit(final Cell<T> cell, final StringConverter<T> converter) {
        textArea.setText(getItemText(cell, converter));
        cell.setText(null);
        cell.setGraphic(textArea);
        textArea.selectAll();
        textArea.requestFocus();
    }

    private static <T> void cancelEdit(Cell<T> cell, final StringConverter<T> converter) {
        cell.setText(getItemText(cell, converter));
        cell.setGraphic(null);
    }

    private void updateItem(final Cell<T> cell, final StringConverter<T> converter) {
        if (cell.isEmpty()) {
            cell.setText(null);
            cell.setGraphic(null);
        } else {
            if (cell.isEditing()) {
                if (textArea != null) {
                    textArea.setText(getItemText(cell, converter));
                }
                cell.setText(null);
                cell.setGraphic(textArea);
            } else {
                cell.setText(getItemText(cell, converter));
                cell.setGraphic(null);
            }
        }
    }

    public TextAreaCell() {
        this(null);
    }

    public TextAreaCell(StringConverter<T> converter) {
        this.getStyleClass().add("text-area-table-cell");
        setConverter(converter);
    }

    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }

    @Override
    public void startEdit() {
        if (isEditable() && getTableView().isEditable() && getTableColumn().isEditable()) {
            super.startEdit();
            if (isEditing()) {
                if (textArea == null) {
                    textArea = createTextArea(this, getConverter());
                }
                startEdit(this, getConverter());
            }
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        cancelEdit(this, getConverter());
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        updateItem(this, getConverter());
    }

}