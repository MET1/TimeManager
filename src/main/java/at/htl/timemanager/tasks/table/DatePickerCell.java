package at.htl.timemanager.tasks.table;

import at.htl.timemanager.tasks.entity.Task;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @timeline .
 * 05.12.2016: MET 001  created class
 * 05.12.2016: MET 050  DatePickerCell: update Item, createDatePicker
 */
public class DatePickerCell extends TableCell<Task, Date> {

    private DatePicker datePicker;

    public DatePickerCell() {
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createDatePicker();
            setText(null);
            setGraphic(datePicker);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getDate() == null ? "" : getDate().toString());
        //setText(getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        setGraphic(null);
    }

    @Override
    public void updateItem(Date item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (datePicker != null) {
                    datePicker.setValue(getDate());
                }
                setText(null);
                setGraphic(datePicker);
            } else {
                setText(getDate() == null ? "" : getDate().toString());
                setGraphic(null);
            }
        }
    }

    private void createDatePicker() {
        datePicker = new DatePicker(getDate());
        datePicker.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        datePicker.setOnAction(e -> {
            if (datePicker.getValue() == null) {
                commitEdit(null);
            } else {
                commitEdit(Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
        });
    }

    private LocalDate getDate() {
        return getItem() == null ? null : getItem().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // setText(getDate() == null ? "" : getDate().toString());
    }
}