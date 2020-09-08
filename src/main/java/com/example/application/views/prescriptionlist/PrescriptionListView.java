package com.example.application.views.prescriptionlist;

import com.example.application.enums.Priority;
import com.example.application.model.Prescription;
import com.example.application.service.PrescriptionService;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Route(value = "prescriptions", layout = MainView.class)
@PageTitle("Prescription List")
@CssImport(value = "./styles/views/prescriptionlist/prescription-list-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class PrescriptionListView extends Div {

    private GridPro<Prescription> grid;
    private ListDataProvider<Prescription> dataProvider;

    private PrescriptionService prescriptionService;

    private Button addPrescriptionButton;
    private Button updatePrescriptionButton;
    private Button deletePrescriptionButton;

    private Dialog createPrescriptionDialog;
    private Dialog updatePrescriptionDialog;
    private Dialog deletePrescriptionDialog;

    private Notification notification = new Notification("", 3000);

    private Grid.Column<Prescription> idColumn;
    private Grid.Column<Prescription> descriptionColumn;
    private Grid.Column<Prescription> patientIdColumn;
    private Grid.Column<Prescription> doctorIdColumn;
    private Grid.Column<Prescription> prescriptionDateColumn;
    private Grid.Column<Prescription> prescriptionExpirationDateColumn;
    private Grid.Column<Prescription> priorityColumn;

    public PrescriptionListView(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;

        setId("prescription-list-view");
        setSizeFull();

        createButtons();
        add(addPrescriptionButton);
        add(updatePrescriptionButton);
        add(deletePrescriptionButton);

        createDialogs();
        add(createPrescriptionDialog);
        add(updatePrescriptionDialog);
        add(deletePrescriptionDialog);

        createGrid();
        add(grid);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        addFiltersToGrid();
    }

    private void updateGrid() {
        dataProvider = new ListDataProvider<Prescription>(getPrescriptions());
        grid.setDataProvider(dataProvider);
    }

    private void createButtons() {
        addPrescriptionButton = new Button();
        addPrescriptionButton.setText("Create a new prescription");
        addPrescriptionButton.addClickListener(event -> createPrescriptionDialog.open());

        updatePrescriptionButton = new Button();
        updatePrescriptionButton.setText("Update prescription info");
        updatePrescriptionButton.addClickListener(event -> updatePrescriptionDialog.open());

        deletePrescriptionButton = new Button();
        deletePrescriptionButton.setText("Delete prescription");
        deletePrescriptionButton.addClickListener(event -> deletePrescriptionDialog.open());
    }

    private void createDialogs() {
        createPrescriptionCreateDialog();
        createPrescriptionUpdateDialog();
        createPrescriptionDeleteDialog();
    }

    private void createPrescriptionCreateDialog() {
        createPrescriptionDialog = new Dialog();

        Input descriptionInput = new Input();
        descriptionInput.setPlaceholder("Enter the description:");
        Input patientIdInput = new Input();
        patientIdInput.setPlaceholder("Enter the patient Id:");
        Input doctorIdInput = new Input();
        doctorIdInput.setPlaceholder("Enter the doctor Id:");
        Input prescriptionDateInput = new Input();
        prescriptionDateInput.setPlaceholder("Enter date:");
        Input prescriptionExpirationDateInput = new Input();
        prescriptionExpirationDateInput.setPlaceholder("Enter exp. date:");
        Input priorityInput = new Input();
        priorityInput.setPlaceholder("Enter priority:");

        createPrescriptionDialog.add(descriptionInput, patientIdInput, doctorIdInput,
                prescriptionDateInput, prescriptionExpirationDateInput, priorityInput);

        Button confirmButton = new Button("Confirm", event -> {

            Optional<String> optionalValueDescription = descriptionInput.getOptionalValue();
            Optional<String> optionalValuePatientId = patientIdInput.getOptionalValue();
            Optional<String> optionalValueDoctorId = doctorIdInput.getOptionalValue();
            Optional<String> optionalValuePrescriptionDate = prescriptionDateInput.getOptionalValue();
            Optional<String> optionalValuePrescriptionExpirationDate =
                    prescriptionExpirationDateInput.getOptionalValue();
            Optional<String> optionalValuePriority = priorityInput.getOptionalValue();

            if (optionalValueDescription.isPresent() &&
                    optionalValuePatientId.isPresent() &&
                    optionalValueDoctorId.isPresent() &&
                    optionalValuePrescriptionDate.isPresent() &&
                    optionalValuePrescriptionExpirationDate.isPresent() &&
                    optionalValuePriority.isPresent()) {

                String valueDescription = optionalValueDescription.get();
                String valuePatientId = optionalValuePatientId.get();
                String valueDoctorId = optionalValueDoctorId.get();
                String valuePrescriptionDate = optionalValuePrescriptionDate.get();
                String valuePrescriptionExpirationDate =
                        optionalValuePrescriptionExpirationDate.get();
                String valuePriority = optionalValuePriority.get();

                try {

                    Prescription prescription = new Prescription();
                    prescription.setDescription(valueDescription);
                    prescription.setPatientId(Long.parseLong(valuePatientId));
                    prescription.setDoctorId(Long.parseLong(valueDoctorId));
                    Date prescriptionDate = Date.valueOf(valuePrescriptionDate);
                    Date prescriptionExpirationDate = Date.valueOf(valuePrescriptionExpirationDate);

                    if (prescriptionExpirationDate.compareTo(prescriptionDate) <= 0)
                        throw new IllegalArgumentException();

                    prescription.setPrescriptionDate(prescriptionDate);
                    prescription.setPrescriptionExpirationDate(prescriptionExpirationDate);
                    prescription.setPriority(Priority.valueOf(valuePriority.toUpperCase()));
                    boolean successful = prescriptionService.create(prescription);

                    if (successful) {
                        notification.setText("Prescription was successfully created");
                        updateGrid();
                        createPrescriptionDialog.close();
                    } else {
                        notification.setText("There is problem with creating prescription");
                    }
                } catch (NumberFormatException ex) {
                    notification.setText("Id must be a number!");
                } catch (IllegalArgumentException ex) {
                    notification.setText(
                            "Date format: YYYY-MM-DD. Example: 2015-03-31. " +
                            "Prescription end date must be later than prescription date. " +
                            "Priority types: NORMAL, CITO, STATIM");
                }
            } else {
                notification.setText("Complete all fields!");
            }
            notification.open();
        });

        Button cancelButton = new Button("Cancel", event -> {
            notification.setText("Cancelled...");
            notification.open();
            createPrescriptionDialog.close();
        });

        createPrescriptionDialog.add(confirmButton, cancelButton);
    }

    private void createPrescriptionUpdateDialog() {
        updatePrescriptionDialog = new Dialog();

        Input idInput = new Input();
        idInput.setPlaceholder("Enter the id:");
        Input descriptionInput = new Input();
        descriptionInput.setPlaceholder("Enter the new description:");
        Input patientIdInput = new Input();
        patientIdInput.setPlaceholder("Enter the new patient id:");
        Input doctorIdInput = new Input();
        doctorIdInput.setPlaceholder("Enter the new doctor id:");
        Input prescriptionDateInput = new Input();
        prescriptionDateInput.setPlaceholder("Enter new date:");
        Input prescriptionExpirationDateInput = new Input();
        prescriptionExpirationDateInput.setPlaceholder("Enter new exp. date:");
        Input priorityInput = new Input();
        priorityInput.setPlaceholder("Enter priority:");

        updatePrescriptionDialog.add(idInput, descriptionInput, patientIdInput, doctorIdInput,
                prescriptionDateInput, prescriptionExpirationDateInput, priorityInput);
        Button confirmButton = new Button("Confirm", event -> {

            Optional<String> optionalValueId = idInput.getOptionalValue();
            Optional<String> optionalValueDescription = descriptionInput.getOptionalValue();
            Optional<String> optionalValuePatientId = patientIdInput.getOptionalValue();
            Optional<String> optionalValueDoctorId = doctorIdInput.getOptionalValue();
            Optional<String> optionalValuePrescriptionDate = prescriptionDateInput.getOptionalValue();
            Optional<String> optionalValuePrescriptionExpirationDate =
                    prescriptionExpirationDateInput.getOptionalValue();
            Optional<String> optionalValuePriority = priorityInput.getOptionalValue();

            if (optionalValueId.isPresent() &&
                    optionalValueDescription.isPresent() &&
                    optionalValuePatientId.isPresent() &&
                    optionalValueDoctorId.isPresent() &&
                    optionalValuePrescriptionDate.isPresent() &&
                    optionalValuePrescriptionExpirationDate.isPresent() &&
                    optionalValuePriority.isPresent()) {

                String valueId = optionalValueId.get();
                String valueDescription = optionalValueDescription.get();
                String valuePatientId = optionalValuePatientId.get();
                String valueDoctorId = optionalValueDoctorId.get();
                String valuePrescriptionDate = optionalValuePrescriptionDate.get();
                String valuePrescriptionExpirationDate =
                        optionalValuePrescriptionExpirationDate.get();
                String valuePriority = optionalValuePriority.get();

                try {
                    Prescription prescription = new Prescription();
                    prescription.setId(Long.parseLong(valueId));
                    prescription.setDescription(valueDescription);
                    prescription.setPatientId(Long.parseLong(valuePatientId));
                    prescription.setDoctorId(Long.parseLong(valueDoctorId));

                    Date prescriptionDate = Date.valueOf(valuePrescriptionDate);
                    Date prescriptionExpirationDate = Date.valueOf(valuePrescriptionExpirationDate);

                    if (prescriptionExpirationDate.compareTo(prescriptionDate) <= 0)
                        throw new IllegalArgumentException();

                    prescription.setPrescriptionDate(prescriptionDate);
                    prescription.setPrescriptionExpirationDate(prescriptionExpirationDate);
                    prescription.setPriority(Priority.valueOf(valuePriority.toUpperCase()));
                    boolean successful = prescriptionService.update(prescription);

                    if (successful) {
                        notification.setText("Prescription was successfully updated");
                        updatePrescriptionDialog.close();
                        updateGrid();
                    } else {
                        notification.setText("There is problem with prescription update");
                    }
                } catch (NumberFormatException ex) {
                    notification.setText("Id must be a number!");
                } catch (IllegalArgumentException ex) {
                    notification.setText(
                            "Date format: YYYY-MM-DD. Example: 2015-03-31. " +
                                    "Prescription end date must be later, than prescription date. " +
                                    "Priority types: NORMAL, CITO, STATIM");
                }
            } else {
                notification.setText("Complete all fields!");
            }
            notification.open();
        });

        Button cancelButton = new Button("Cancel", event -> {
            notification.setText("Cancelled...");
            notification.open();
            updatePrescriptionDialog.close();
        });


        updatePrescriptionDialog.add(confirmButton, cancelButton);
    }

    private void createPrescriptionDeleteDialog() {
        deletePrescriptionDialog = new Dialog();

        Input idToDeleteInput = new Input();
        idToDeleteInput.setPlaceholder("Enter the id to delete:");

        deletePrescriptionDialog.add(idToDeleteInput);

        Notification notification = new Notification("", 3000);

        Button confirmButton = new Button("Confirm", event -> {
            Optional<String> optionalValue = idToDeleteInput.getOptionalValue();
            if (optionalValue.isPresent()) {

                String value = optionalValue.get();

                try {
                    Long idToDelete = Long.parseLong(value);

                    boolean successful = prescriptionService.deleteById(idToDelete);

                    if (successful) {
                        notification.setText("Prescription with ID= " + idToDelete + " successfully deleted");
                        updateGrid();
                        deletePrescriptionDialog.close();
                    } else {
                        notification.setText("There is problem with delete prescription with ID= " + idToDelete);
                    }
                } catch (NumberFormatException ex) {
                    notification.setText("Id must be a number!");
                }
            } else {
                notification.setText("Incorrect id value!");
            }
            notification.open();
        });

        Button cancelButton = new Button("Cancel", event -> {
            notification.setText("Cancelled...");
            notification.open();
            deletePrescriptionDialog.close();
        });

        deletePrescriptionDialog.add(confirmButton, cancelButton);
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");

        dataProvider = new ListDataProvider<Prescription>(getPrescriptions());
        grid.setDataProvider(dataProvider);
    }

    private void addColumnsToGrid() {
        createIdColumn();
        createDescriptionColumn();
        createPatientIdColumn();
        createDoctorIdColumn();
        createPrescriptionDateColumn();
        createPrescriptionExpirationDateColumn();
        createPriorityColumn();
    }

    private void createIdColumn() {
        idColumn = grid.addColumn(Prescription::getId, "id").setHeader("ID")
                .setWidth("120px").setFlexGrow(0);
    }

    private void createDescriptionColumn() {
        descriptionColumn = grid.addColumn(new ComponentRenderer<>(prescription -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Span span = new Span();
            span.setClassName("name");
            span.setText(prescription.getDescription());
            hl.add(span);
            return hl;
        })).setComparator(prescription -> prescription.getDescription()).setHeader("Description");
    }

    private void createPatientIdColumn() {
        patientIdColumn = grid.addColumn(Prescription::getPatientId, "id").setHeader("Patient ID")
                .setWidth("200px").setFlexGrow(0);
    }

    private void createDoctorIdColumn() {
        doctorIdColumn = grid.addColumn(Prescription::getDoctorId, "id").setHeader("Doctor ID")
                .setWidth("200px").setFlexGrow(0);
    }

    private void createPrescriptionDateColumn() {
        prescriptionDateColumn = grid
                .addColumn(new LocalDateRenderer<>(
                        prescription -> prescription.getPrescriptionDate().toLocalDate(),
                        DateTimeFormatter.ofPattern("M/d/yyyy")))
                .setComparator(prescription -> prescription.getPrescriptionDate()).setHeader("Date")
                .setWidth("180px").setFlexGrow(0);
    }

    private void createPrescriptionExpirationDateColumn() {
        prescriptionExpirationDateColumn = grid
                .addColumn(new LocalDateRenderer<>(
                        prescription -> prescription.getPrescriptionExpirationDate().toLocalDate(),
                        DateTimeFormatter.ofPattern("M/d/yyyy")))
                .setComparator(prescription -> prescription.getPrescriptionExpirationDate()).setHeader("Exp. Date")
                .setWidth("180px").setFlexGrow(0);
    }

    private void createPriorityColumn() {
        priorityColumn = grid.addColumn(new ComponentRenderer<>(prescription -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Span span = new Span();
            span.setClassName("name");
            span.setText(prescription.getPriority().toString());
            hl.add(span);
            return hl;
        })).setComparator(prescription -> prescription.getPriority()).setHeader("Priority");
    }

    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        TextField idFilter = new TextField();
        idFilter.setPlaceholder("Filter");
        idFilter.setClearButtonVisible(true);
        idFilter.setWidth("100%");
        idFilter.setValueChangeMode(ValueChangeMode.EAGER);
        idFilter.addValueChangeListener(
                event -> dataProvider.addFilter(prescription -> StringUtils
                        .containsIgnoreCase(Integer.toString(prescription.getId().intValue()),
                                idFilter.getValue())));
        filterRow.getCell(idColumn).setComponent(idFilter);

        TextField descriptionFilter = new TextField();
        descriptionFilter.setPlaceholder("Filter");
        descriptionFilter.setClearButtonVisible(true);
        descriptionFilter.setWidth("100%");
        descriptionFilter.setValueChangeMode(ValueChangeMode.EAGER);
        descriptionFilter.addValueChangeListener(event -> dataProvider.addFilter(
                prescription -> StringUtils.containsIgnoreCase(prescription.getDescription(),
                        descriptionFilter.getValue())));
        filterRow.getCell(descriptionColumn).setComponent(descriptionFilter);

        TextField patientIdFilter = new TextField();
        patientIdFilter.setPlaceholder("Filter");
        patientIdFilter.setClearButtonVisible(true);
        patientIdFilter.setWidth("100%");
        patientIdFilter.setValueChangeMode(ValueChangeMode.EAGER);
        patientIdFilter.addValueChangeListener(
                event -> dataProvider.addFilter(prescription -> StringUtils
                        .containsIgnoreCase(Integer.toString(prescription.getPatientId().intValue()),
                                patientIdFilter.getValue())));
        filterRow.getCell(patientIdColumn).setComponent(patientIdFilter);

        TextField doctorIdFilter = new TextField();
        doctorIdFilter.setPlaceholder("Filter");
        doctorIdFilter.setClearButtonVisible(true);
        doctorIdFilter.setWidth("100%");
        doctorIdFilter.setValueChangeMode(ValueChangeMode.EAGER);
        doctorIdFilter.addValueChangeListener(
                event -> dataProvider.addFilter(prescription -> StringUtils
                        .containsIgnoreCase(Integer.toString(prescription.getDoctorId().intValue()),
                                doctorIdFilter.getValue())));
        filterRow.getCell(doctorIdColumn).setComponent(doctorIdFilter);

        DatePicker prescriptionDateFilter = new DatePicker();
        prescriptionDateFilter.setPlaceholder("Filter");
        prescriptionDateFilter.setClearButtonVisible(true);
        prescriptionDateFilter.setWidth("100%");
        prescriptionDateFilter.addValueChangeListener(event -> dataProvider
                .addFilter(prescription -> areDatesEqual(prescription, prescriptionDateFilter)));
        filterRow.getCell(prescriptionDateColumn).setComponent(prescriptionDateFilter);

        DatePicker prescriptionExpirationDateFilter = new DatePicker();
        prescriptionExpirationDateFilter.setPlaceholder("Filter");
        prescriptionExpirationDateFilter.setClearButtonVisible(true);
        prescriptionExpirationDateFilter.setWidth("100%");
        prescriptionExpirationDateFilter.addValueChangeListener(event -> dataProvider
                .addFilter(prescription -> areDatesEqual2(prescription, prescriptionExpirationDateFilter)));
        filterRow.getCell(prescriptionExpirationDateColumn).setComponent(prescriptionExpirationDateFilter);


        TextField priorityFilter = new TextField();
        priorityFilter.setPlaceholder("Filter");
        priorityFilter.setClearButtonVisible(true);
        priorityFilter.setWidth("100%");
        priorityFilter.setValueChangeMode(ValueChangeMode.EAGER);
        priorityFilter.addValueChangeListener(event -> dataProvider.addFilter(
                prescription -> StringUtils.containsIgnoreCase(prescription.getPriority().toString(),
                        priorityFilter.getValue())));
        filterRow.getCell(priorityColumn).setComponent(priorityFilter);
    }

    private boolean areDatesEqual(Prescription prescription, DatePicker dateFilter) {
        LocalDate dateFilterValue = dateFilter.getValue();
        if (dateFilterValue != null) {
            LocalDate clientDate = prescription.getPrescriptionDate().toLocalDate();
            return dateFilterValue.equals(clientDate);
        }
        return true;
    }

    private boolean areDatesEqual2(Prescription prescription, DatePicker dateFilter) {
        LocalDate dateFilterValue = dateFilter.getValue();
        if (dateFilterValue != null) {
            LocalDate clientDate = prescription.getPrescriptionExpirationDate().toLocalDate();
            return dateFilterValue.equals(clientDate);
        }
        return true;
    }

    private List<Prescription> getPrescriptions() {
        return prescriptionService.findAll();
    }
};
