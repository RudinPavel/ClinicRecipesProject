package com.example.application.views.patientlist;

import com.example.application.model.Patient;
import com.example.application.service.PatientService;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.button.Button;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;


@Route(value = "patiens", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Patient List")
@CssImport(value = "./styles/views/patientlist/patient-list-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class PatientListView extends Div {

    private GridPro<Patient> grid;
    private ListDataProvider<Patient> dataProvider;

    private PatientService patientService;

    private Button addPatientButton;
    private Button updatePatientButton;
    private Button deletePatientButton;

    private Dialog createPatientDialog;
    private Dialog updatePatientDialog;
    private Dialog deletePatientDialog;

    private Notification notification = new Notification("", 3000);

    private Grid.Column<Patient> idColumn;
    private Grid.Column<Patient> firstNameColumn;
    private Grid.Column<Patient> lastNameColumn;
    private Grid.Column<Patient> middleNameColumn;
    private Grid.Column<Patient> phoneColumn;

    public PatientListView(PatientService patientService) {
        this.patientService = patientService;

        setId("patient-list-view");
        setSizeFull();

        createButtons();
        add(addPatientButton);
        add(updatePatientButton);
        add(deletePatientButton);

        createDialogs();
        add(createPatientDialog);
        add(updatePatientDialog);
        add(deletePatientDialog);

        createGrid();
        add(grid);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        addFiltersToGrid();
    }

    private void updateGrid() {
        dataProvider = new ListDataProvider<Patient>(getPatients());
        grid.setDataProvider(dataProvider);
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");

        dataProvider = new ListDataProvider<Patient>(getPatients());
        grid.setDataProvider(dataProvider);
    }

    private void addColumnsToGrid() {
        createIdColumn();
        createFirstNameColumn();
        createLastNameColumn();
        createMiddleNameColumn();
        createPhoneColumn();
    }

    private void createButtons() {
        addPatientButton = new Button();
        addPatientButton.setText("Create a new patient");
        addPatientButton.addClickListener(event -> createPatientDialog.open());

        updatePatientButton = new Button();
        updatePatientButton.setText("Update patient info");
        updatePatientButton.addClickListener(event -> updatePatientDialog.open());

        deletePatientButton = new Button();
        deletePatientButton.setText("Delete patient");
        deletePatientButton.addClickListener(event -> deletePatientDialog.open());
    }

    private void createPatientCreateDialog() {
        createPatientDialog = new Dialog();

        Input firstNameInput = new Input();
        firstNameInput.setPlaceholder("Enter the first name:");
        Input lastNameInput = new Input();
        lastNameInput.setPlaceholder("Enter the last name:");
        Input middleNameInput = new Input();
        middleNameInput.setPlaceholder("Enter the middle name:");
        Input phoneInput = new Input();
        phoneInput.setPlaceholder("Enter the phone:");

        createPatientDialog.add(firstNameInput, lastNameInput, middleNameInput, phoneInput);

        Button confirmButton = new Button("Confirm", event -> {

            Optional<String> optionalValueFirstName = firstNameInput.getOptionalValue();
            Optional<String> optionalValueLastName = lastNameInput.getOptionalValue();
            Optional<String> optionalValueMiddleName = middleNameInput.getOptionalValue();
            Optional<String> optionalValuePhone = phoneInput.getOptionalValue();

            if (optionalValueFirstName.isPresent() &&
                    optionalValueLastName.isPresent() &&
                    optionalValueMiddleName.isPresent() &&
                    optionalValuePhone.isPresent()) {

                String valueFirstName = optionalValueFirstName.get();
                String valueLastName = optionalValueLastName.get();
                String valueMiddleName = optionalValueMiddleName.get();
                String valuePhone = optionalValuePhone.get();

                Patient patient = new Patient();
                patient.setFirstName(valueFirstName);
                patient.setLastName(valueLastName);
                patient.setMiddleName(valueMiddleName);
                patient.setPhone(valuePhone);

                boolean successful = patientService.create(patient);

                if (successful) {
                    notification.setText("Patient was successfully created");
                    updateGrid();
                    createPatientDialog.close();
                } else {
                    notification.setText("There is problem with creating patient");
                }
            } else {
                notification.setText("Complete all fields!");
            }
            notification.open();
        });

        Button cancelButton = new Button("Cancel", event -> {
            notification.setText("Cancelled...");
            notification.open();
            createPatientDialog.close();
        });

        createPatientDialog.add(confirmButton, cancelButton);
    }

    private void createPatientUpdateDialog() {
        updatePatientDialog = new Dialog();

        Input patientIdInput = new Input();
        patientIdInput.setPlaceholder("Enter the patient ID:");
        Input firstNameInput = new Input();
        firstNameInput.setPlaceholder("Enter new first name:");
        Input lastNameInput = new Input();
        lastNameInput.setPlaceholder("Enter new last name:");
        Input middleNameInput = new Input();
        middleNameInput.setPlaceholder("Enter new middle name:");
        Input phoneInput = new Input();
        phoneInput.setPlaceholder("Enter new phone:");

        updatePatientDialog.add(patientIdInput, firstNameInput, lastNameInput, middleNameInput, phoneInput);

        Button confirmButton = new Button("Confirm", event -> {

            Optional<String> optionalValuePatientId = patientIdInput.getOptionalValue();
            Optional<String> optionalValueFirstName = firstNameInput.getOptionalValue();
            Optional<String> optionalValueLastName = lastNameInput.getOptionalValue();
            Optional<String> optionalValueMiddleName = middleNameInput.getOptionalValue();
            Optional<String> optionalValuePhone = phoneInput.getOptionalValue();

            if (optionalValuePatientId.isPresent() &&
                    optionalValueFirstName.isPresent() &&
                    optionalValueLastName.isPresent() &&
                    optionalValueMiddleName.isPresent() &&
                    optionalValuePhone.isPresent()) {

                String valuePatientId = optionalValuePatientId.get();
                String valueFirstName = optionalValueFirstName.get();
                String valueLastName = optionalValueLastName.get();
                String valueMiddleName = optionalValueMiddleName.get();
                String valuePhone = optionalValuePhone.get();

                try {
                    Long id = Long.parseLong(valuePatientId);
                    Patient patient = new Patient();
                    patient.setId(id);
                    patient.setFirstName(valueFirstName);
                    patient.setLastName(valueLastName);
                    patient.setMiddleName(valueMiddleName);
                    patient.setPhone(valuePhone);

                    boolean successful = patientService.update(patient);

                    if (successful) {
                        notification.setText("Patient info was successfully updated");
                        updateGrid();
                        updatePatientDialog.close();
                    } else {
                        notification.setText("There is problem with patient info update");
                    }
                } catch (NumberFormatException ex) {
                    notification.setText("Id must be a number!");
                }
            } else {
                notification.setText("Complete all fields!");
            }
            notification.open();
        });

        Button cancelButton = new Button("Cancel", event -> {
            notification.setText("Cancelled...");
            notification.open();
            updatePatientDialog.close();
        });

        updatePatientDialog.add(confirmButton, cancelButton);
    }

    private void createPatientDeleteDialog() {
        deletePatientDialog = new Dialog();

        Input idToDeleteInput = new Input();
        idToDeleteInput.setPlaceholder("Enter the id of the patient to be deleted:");

        deletePatientDialog.add(idToDeleteInput);

        Notification notification = new Notification("", 3000);

        Button confirmButton = new Button("Confirm", event -> {
            Optional<String> optionalValue = idToDeleteInput.getOptionalValue();
            if (optionalValue.isPresent()) {

                String value = optionalValue.get();

                try {
                    Long idToDelete = Long.parseLong(value);

                    boolean successful = patientService.deleteById(idToDelete);

                    if (successful) {
                        notification.setText("Patient with ID= " + idToDelete + " successfully deleted");
                        updateGrid();
                        deletePatientDialog.close();
                    } else {
                        notification.setText("There is problem with delete patient with ID= " + idToDelete);
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
            deletePatientDialog.close();
        });

        deletePatientDialog.add(confirmButton, cancelButton);
    }

    private void createDialogs() {
        createPatientCreateDialog();
        createPatientUpdateDialog();
        createPatientDeleteDialog();
    }

    private void createIdColumn() {
        idColumn = grid.addColumn(Patient::getId, "id").setHeader("ID")
                .setWidth("120px").setFlexGrow(0);
    }

    private void createFirstNameColumn() {
        firstNameColumn = grid.addColumn(new ComponentRenderer<>(patient -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            //Image img = new Image(client.getImg(), "");
            Span span = new Span();
            span.setClassName("name");
            span.setText(patient.getFirstName());
            hl.add(span);
            return hl;
        })).setComparator(patient -> patient.getFirstName()).setHeader("First Name");
    }

    private void createLastNameColumn() {
        lastNameColumn = grid.addColumn(new ComponentRenderer<>(patient -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            //Image img = new Image(client.getImg(), "");
            Span span = new Span();
            span.setClassName("name");
            span.setText(patient.getLastName());
            hl.add(span);
            return hl;
        })).setComparator(patient -> patient.getLastName()).setHeader("Last Name");
    }

    private void createMiddleNameColumn() {
        middleNameColumn = grid.addColumn(new ComponentRenderer<>(patient -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Span span = new Span();
            span.setClassName("name");
            span.setText(patient.getMiddleName());
            hl.add(span);
            return hl;
        })).setComparator(patient -> patient.getMiddleName()).setHeader("Middle Name");
    }

    private void createPhoneColumn() {
        phoneColumn = grid.addColumn(new ComponentRenderer<>(patient -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Span span = new Span();
            span.setClassName("name");
            span.setText(patient.getPhone());
            hl.add(span);
            return hl;
        })).setComparator(patient -> patient.getPhone()).setHeader("Phone");
    }

    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        TextField idFilter = new TextField();
        idFilter.setPlaceholder("Filter");
        idFilter.setClearButtonVisible(true);
        idFilter.setWidth("100%");
        idFilter.setValueChangeMode(ValueChangeMode.EAGER);
        idFilter.addValueChangeListener(
                event -> dataProvider.addFilter(patient -> StringUtils
                        .containsIgnoreCase(Integer.toString(patient.getId().intValue()),
                                idFilter.getValue())));
        filterRow.getCell(idColumn).setComponent(idFilter);

        TextField patientFirstNameFilter = new TextField();
        patientFirstNameFilter.setPlaceholder("Filter");
        patientFirstNameFilter.setClearButtonVisible(true);
        patientFirstNameFilter.setWidth("100%");
        patientFirstNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        patientFirstNameFilter.addValueChangeListener(event -> dataProvider.addFilter(
                patient -> StringUtils.containsIgnoreCase(patient.getFirstName(),
                        patientFirstNameFilter.getValue())));
        filterRow.getCell(firstNameColumn).setComponent(patientFirstNameFilter);

        TextField patientLastNameFilter = new TextField();
        patientLastNameFilter.setPlaceholder("Filter");
        patientLastNameFilter.setClearButtonVisible(true);
        patientLastNameFilter.setWidth("100%");
        patientLastNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        patientLastNameFilter.addValueChangeListener(event -> dataProvider.addFilter(
                patient -> StringUtils.containsIgnoreCase(patient.getLastName(),
                        patientLastNameFilter.getValue())));
        filterRow.getCell(lastNameColumn).setComponent(patientLastNameFilter);

        TextField patientMiddleNameFilter = new TextField();
        patientMiddleNameFilter.setPlaceholder("Filter");
        patientMiddleNameFilter.setClearButtonVisible(true);
        patientMiddleNameFilter.setWidth("100%");
        patientMiddleNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        patientMiddleNameFilter.addValueChangeListener(event -> dataProvider.addFilter(
                patient -> StringUtils.containsIgnoreCase(patient.getMiddleName(),
                        patientMiddleNameFilter.getValue())));
        filterRow.getCell(middleNameColumn).setComponent(patientMiddleNameFilter);

        TextField patientPhoneFilter = new TextField();
        patientPhoneFilter.setPlaceholder("Filter");
        patientPhoneFilter.setClearButtonVisible(true);
        patientPhoneFilter.setWidth("100%");
        patientPhoneFilter.setValueChangeMode(ValueChangeMode.EAGER);
        patientPhoneFilter.addValueChangeListener(event -> dataProvider.addFilter(
                patient -> StringUtils.containsIgnoreCase(patient.getPhone(),
                        patientPhoneFilter.getValue())));
        filterRow.getCell(phoneColumn).setComponent(patientPhoneFilter);
    }

    private List<Patient> getPatients() {
        return patientService.findAll();
    }
};
