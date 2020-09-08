package com.example.application.dao;

import com.example.application.enums.Priority;
import com.example.application.model.Prescription;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PrescriptionDAO extends DAO<Prescription> {

    public PrescriptionDAO() {
        tableName = "Prescriptions";
        columns = new String[]{"description", "patient_id", "doctor_id",
                "prescription_date", "prescription_expiration_date", "priority"};
    }

    @Override
    protected List<Prescription> convertFrom(ResultSet resultSet) throws SQLException {
        List<Prescription> prescriptionList = new ArrayList<>();
        while (resultSet.next()) {
            Prescription prescription = new Prescription();
            prescription.setId(resultSet.getLong("id"));
            prescription.setDescription(resultSet.getString("description"));
            prescription.setPatientId(resultSet.getLong("patient_id"));
            prescription.setDoctorId(resultSet.getLong("doctor_id"));
            prescription.setPrescriptionDate(resultSet.getDate("prescription_date"));
            prescription.setPrescriptionExpirationDate(resultSet.getDate("prescription_expiration_date"));
            prescription.setPriority(Priority.valueOf(resultSet.getString("priority")));
            prescriptionList.add(prescription);
        }
        return prescriptionList;
    }

    @Override
    protected String createInsertQuery(Prescription prescription) {
        String fields = String.join(", ", columns);
        String values = "'" + prescription.getDescription() + "', "
                + prescription.getPatientId() + ", "
                + prescription.getDoctorId() + ", DATE '"
                + prescription.getPrescriptionDate() + "', DATE '"
                + prescription.getPrescriptionExpirationDate() + "', '"
                + prescription.getPriority() + "'";
        return String.format("INSERT INTO %s (%s)"
                + "VALUES (%s)", tableName, fields, values);
    }

    @Override
    protected String createUpdateQuery(Prescription prescription) {
        String query =
                "UPDATE " + tableName + " SET "
                        + "description = " + "'" + prescription.getDescription() + "',"
                        + "patient_id = " + "" + prescription.getPatientId() + ","
                        + "doctor_id = " + "" + prescription.getDoctorId() + ","
                        + "prescription_date = " + " DATE '" + prescription.getPrescriptionDate() + "',"
                        + "prescription_expiration_date = " + " DATE '" + prescription.getPrescriptionExpirationDate() + "',"
                        + "priority = " + "'" + prescription.getPriority() + "'"
                        + "WHERE id = " + prescription.getId();
        return query;
    }
}
