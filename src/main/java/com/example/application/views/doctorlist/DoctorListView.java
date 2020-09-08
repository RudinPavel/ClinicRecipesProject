package com.example.application.views.doctorlist;

import com.example.application.model.Doctor;
import com.example.application.service.DoctorService;
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
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;


@Route(value = "doctors", layout = MainView.class)
@PageTitle("Doctor List")
@CssImport(value = "./styles/views/doctorlist/doctor-list-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class DoctorListView extends Div {

    private GridPro<Doctor> grid;
    private ListDataProvider<Doctor> dataProvider;

    private DoctorService doctorService;

    private Button addDoctorButton;
    private Button updateDoctorButton;
    private Button deleteDoctorButton;
    private Button showStatisticButton;

    private Dialog createDoctorDialog;
    private Dialog updateDoctorDialog;
    private Dialog deleteDoctorDialog;
    private Dialog showStatisticDialog;

    private Notification notification = new Notification("", 3000);

    private Grid.Column<Doctor> idColumn;
    private Grid.Column<Doctor> firstNameColumn;
    private Grid.Column<Doctor> lastNameColumn;
    private Grid.Column<Doctor> middleNameColumn;
    private Grid.Column<Doctor> specializationColumn;

    public DoctorListView(DoctorService doctorService) {
        this.doctorService = doctorService;

        setId("doctor-list-view");
        setSizeFull();

        createButtons();
        add(addDoctorButton);
        add(updateDoctorButton);
        add(deleteDoctorButton);
        add(showStatisticButton);

        createDialogs();
        add(createDoctorDialog);
        add(updateDoctorDialog);
        add(deleteDoctorDialog);
        add(showStatisticDialog);

        createGrid();
        add(grid);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        addFiltersToGrid();
    }

    private void updateGrid() {
        dataProvider = new ListDataProvider<Doctor>(getDoctors());
        grid.setDataProvider(dataProvider);
    }

    private void createButtons() {
        addDoctorButton = new Button();
        addDoctorButton.setText("Create a new doctor");
        addDoctorButton.addClickListener(event -> createDoctorDialog.open());

        updateDoctorButton = new Button();
        updateDoctorButton.setText("Update doctor info");
        updateDoctorButton.addClickListener(event -> updateDoctorDialog.open());

        deleteDoctorButton = new Button();
        deleteDoctorButton.setText("Delete doctor");
        deleteDoctorButton.addClickListener(event -> deleteDoctorDialog.open());

        showStatisticButton = new Button();
        showStatisticButton.setText("Show statistic");
        showStatisticButton.addClickListener(event -> showStatisticDialog.open());
    }

    private void createDialogs() {
        createDoctorCreateDialog();
        createDoctorUpdateDialog();
        createDoctorDeleteDialog();
        createStatisticDialog();
    }

    private void createStatisticDialog() {
        showStatisticDialog = new Dialog();
        showStatisticDialog.add("Total recipes count = " + doctorService.getTotalRecipesCount() + "\n");

        Button cancelButton = new Button("Cancel", event -> {
            notification.setText("Cancelled...");
            notification.open();
            showStatisticDialog.close();
        });
        showStatisticDialog.add(cancelButton);
    }

    private void createDoctorCreateDialog() {
        createDoctorDialog = new Dialog();

        Input firstNameInput = new Input();
        firstNameInput.setPlaceholder("Enter the first name:");
        Input lastNameInput = new Input();
        lastNameInput.setPlaceholder("Enter the last name:");
        Input middleNameInput = new Input();
        middleNameInput.setPlaceholder("Enter the middle name:");
        Input specializationInput = new Input();
        specializationInput.setPlaceholder("Enter the specialization:");

        createDoctorDialog.add(firstNameInput, lastNameInput, middleNameInput, specializationInput);

        Button confirmButton = new Button("Confirm", event -> {

            Optional<String> optionalValueFirstName = firstNameInput.getOptionalValue();
            Optional<String> optionalValueLastName = lastNameInput.getOptionalValue();
            Optional<String> optionalValueMiddleName = middleNameInput.getOptionalValue();
            Optional<String> optionalValueSpecialization = specializationInput.getOptionalValue();

            if (optionalValueFirstName.isPresent() &&
                    optionalValueLastName.isPresent() &&
                    optionalValueMiddleName.isPresent() &&
                    optionalValueSpecialization.isPresent()) {

                String valueFirstName = optionalValueFirstName.get();
                String valueLastName = optionalValueLastName.get();
                String valueMiddleName = optionalValueMiddleName.get();
                String valueSpecialization = optionalValueSpecialization.get();

                Doctor doctor = new Doctor();
                doctor.setFirstName(valueFirstName);
                doctor.setLastName(valueLastName);
                doctor.setMiddleName(valueMiddleName);
                doctor.setSpecialization(valueSpecialization);

                boolean successful = doctorService.create(doctor);

                if (successful) {
                    notification.setText("Doctor was successfully created");
                    updateGrid();
                } else {
                    notification.setText("There is problem with creating doctor");
                }
            } else {
                notification.setText("Complete all fields!");
            }
            notification.open();
            createDoctorDialog.close();
        });

        Button cancelButton = new Button("Cancel", event -> {
            notification.setText("Cancelled...");
            notification.open();
            createDoctorDialog.close();
        });

        createDoctorDialog.add(confirmButton, cancelButton);
    }

    private void createDoctorUpdateDialog() {
        updateDoctorDialog = new Dialog();

        Input patientIdInput = new Input();
        patientIdInput.setPlaceholder("Enter the doctor ID:");
        Input firstNameInput = new Input();
        firstNameInput.setPlaceholder("Enter new first name:");
        Input lastNameInput = new Input();
        lastNameInput.setPlaceholder("Enter new last name:");
        Input middleNameInput = new Input();
        middleNameInput.setPlaceholder("Enter new middle name:");
        Input specializationInput = new Input();
        specializationInput.setPlaceholder("Enter new specialization:");

        updateDoctorDialog.add(patientIdInput, firstNameInput, lastNameInput, middleNameInput, specializationInput);

        Button confirmButton = new Button("Confirm", event -> {

            Optional<String> optionalValuePatientId = patientIdInput.getOptionalValue();
            Optional<String> optionalValueFirstName = firstNameInput.getOptionalValue();
            Optional<String> optionalValueLastName = lastNameInput.getOptionalValue();
            Optional<String> optionalValueMiddleName = middleNameInput.getOptionalValue();
            Optional<String> optionalValueSpecialization = specializationInput.getOptionalValue();

            if (optionalValuePatientId.isPresent() &&
                    optionalValueFirstName.isPresent() &&
                    optionalValueLastName.isPresent() &&
                    optionalValueMiddleName.isPresent() &&
                    optionalValueSpecialization.isPresent()) {

                String valuePatientId = optionalValuePatientId.get();
                String valueFirstName = optionalValueFirstName.get();
                String valueLastName = optionalValueLastName.get();
                String valueMiddleName = optionalValueMiddleName.get();
                String valueSpecialization = optionalValueSpecialization.get();

                try {
                    Long id = Long.parseLong(valuePatientId);
                    Doctor doctor = new Doctor();
                    doctor.setId(id);
                    doctor.setFirstName(valueFirstName);
                    doctor.setLastName(valueLastName);
                    doctor.setMiddleName(valueMiddleName);
                    doctor.setSpecialization(valueSpecialization);

                    boolean successful = doctorService.update(doctor);

                    if (successful) {
                        notification.setText("Doctor info was successfully updated");
                        updateGrid();
                        updateDoctorDialog.close();
                    } else {
                        notification.setText("There is problem with doctor info update");
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
            updateDoctorDialog.close();
        });

        updateDoctorDialog.add(confirmButton, cancelButton);
    }

    private void createDoctorDeleteDialog() {
        deleteDoctorDialog = new Dialog();

        Input idToDeleteInput = new Input();
        idToDeleteInput.setPlaceholder("Enter the id of the doctor to be deleted:");

        deleteDoctorDialog.add(idToDeleteInput);

        Notification notification = new Notification("", 3000);

        Button confirmButton = new Button("Confirm", event -> {
            Optional<String> optionalValue = idToDeleteInput.getOptionalValue();
            if (optionalValue.isPresent()) {

                String value = optionalValue.get();

                try {
                    Long idToDelete = Long.parseLong(value);

                    boolean successful = doctorService.deleteById(idToDelete);

                    if (successful) {
                        notification.setText("Doctor with ID= " + idToDelete + " successfully deleted");
                        updateGrid();
                        deleteDoctorDialog.close();
                    } else {
                        notification.setText("There is problem with delete doctor with ID= " + idToDelete);
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
            deleteDoctorDialog.close();
        });

        deleteDoctorDialog.add(confirmButton, cancelButton);
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");

        dataProvider = new ListDataProvider<Doctor>(getDoctors());
        grid.setDataProvider(dataProvider);
    }

    private void addColumnsToGrid() {
        createIdColumn();
        createFirstNameColumn();
        createLastNameColumn();
        createMiddleNameColumn();
        createSpecializationColumn();
    }

    private void createIdColumn() {
        idColumn = grid.addColumn(Doctor::getId, "id").setHeader("ID")
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
        })).setComparator(doctor -> doctor.getFirstName()).setHeader("First Name");
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
        })).setComparator(doctor -> doctor.getLastName()).setHeader("Last Name");
    }

    private void createMiddleNameColumn() {
        middleNameColumn = grid.addColumn(new ComponentRenderer<>(patient -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            //Image img = new Image(client.getImg(), "");
            Span span = new Span();
            span.setClassName("name");
            span.setText(patient.getMiddleName());
            hl.add(span);
            return hl;
        })).setComparator(doctor -> doctor.getMiddleName()).setHeader("Middle Name");
    }

    private void createSpecializationColumn() {
        specializationColumn = grid.addColumn(new ComponentRenderer<>(doctor -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Span span = new Span();
            span.setClassName("name");
            span.setText(doctor.getSpecialization());
            hl.add(span);
            return hl;
        })).setComparator(doctor -> doctor.getSpecialization()).setHeader("Specialization");
    }

    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        TextField idFilter = new TextField();
        idFilter.setPlaceholder("Filter");
        idFilter.setClearButtonVisible(true);
        idFilter.setWidth("100%");
        idFilter.setValueChangeMode(ValueChangeMode.EAGER);
        idFilter.addValueChangeListener(
                event -> dataProvider.addFilter(doctor -> StringUtils
                        .containsIgnoreCase(Integer.toString(doctor.getId().intValue()),
                                idFilter.getValue())));
        filterRow.getCell(idColumn).setComponent(idFilter);

        TextField doctorFirstNameFilter = new TextField();
        doctorFirstNameFilter.setPlaceholder("Filter");
        doctorFirstNameFilter.setClearButtonVisible(true);
        doctorFirstNameFilter.setWidth("100%");
        doctorFirstNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        doctorFirstNameFilter.addValueChangeListener(event -> dataProvider.addFilter(
                doctor -> StringUtils.containsIgnoreCase(doctor.getFirstName(),
                        doctorFirstNameFilter.getValue())));
        filterRow.getCell(firstNameColumn).setComponent(doctorFirstNameFilter);

        TextField doctorLastNameFilter = new TextField();
        doctorLastNameFilter.setPlaceholder("Filter");
        doctorLastNameFilter.setClearButtonVisible(true);
        doctorLastNameFilter.setWidth("100%");
        doctorLastNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        doctorLastNameFilter.addValueChangeListener(event -> dataProvider.addFilter(
                doctor -> StringUtils.containsIgnoreCase(doctor.getLastName(),
                        doctorLastNameFilter.getValue())));
        filterRow.getCell(lastNameColumn).setComponent(doctorLastNameFilter);

        TextField doctorMiddleNameFilter = new TextField();
        doctorMiddleNameFilter.setPlaceholder("Filter");
        doctorMiddleNameFilter.setClearButtonVisible(true);
        doctorMiddleNameFilter.setWidth("100%");
        doctorMiddleNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        doctorMiddleNameFilter.addValueChangeListener(event -> dataProvider.addFilter(
                doctor -> StringUtils.containsIgnoreCase(doctor.getMiddleName(),
                        doctorMiddleNameFilter.getValue())));
        filterRow.getCell(middleNameColumn).setComponent(doctorMiddleNameFilter);

        TextField doctorSpecializationFilter = new TextField();
        doctorSpecializationFilter.setPlaceholder("Filter");
        doctorSpecializationFilter.setClearButtonVisible(true);
        doctorSpecializationFilter.setWidth("100%");
        doctorSpecializationFilter.setValueChangeMode(ValueChangeMode.EAGER);
        doctorSpecializationFilter.addValueChangeListener(event -> dataProvider.addFilter(
                doctor -> StringUtils.containsIgnoreCase(doctor.getSpecialization(),
                        doctorSpecializationFilter.getValue())));
        filterRow.getCell(specializationColumn).setComponent(doctorSpecializationFilter);
    }

    private List<Doctor> getDoctors() {
        return doctorService.findAll();
    }
};
