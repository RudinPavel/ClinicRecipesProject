package com.example.application.dao;

import com.example.application.model.Patient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PatientDAO extends DAO<Patient> {

    public PatientDAO() {
        tableName = "Patients";
        columns = new String[]{"first_name", "last_name", "middle_name", "phone"};
    }

    @Override
    protected List<Patient> convertFrom(ResultSet resultSet) throws SQLException {
        List<Patient> Patients = new ArrayList<>();
        while (resultSet.next()) {
            Patient Patient = new Patient();
            Patient.setId(resultSet.getLong("id"));
            Patient.setFirstName(resultSet.getString("first_name"));
            Patient.setLastName(resultSet.getString("last_name"));
            Patient.setMiddleName(resultSet.getString("middle_name"));
            Patient.setPhone(resultSet.getString("phone"));
            Patients.add(Patient);
        }
        return Patients;
    }

    @Override
    protected String createInsertQuery(Patient Patient) {
        String fields = String.join(", ", columns);
        String values = "'" + Patient.getFirstName() + "', '"
                + Patient.getLastName() + "', '"
                + Patient.getMiddleName() + "', '"
                + Patient.getPhone() + "'";
        return String.format("INSERT INTO %s (%s)"
                + "VALUES (%s)", tableName, fields, values);
    }

    @Override
    protected String createUpdateQuery(Patient Patient) {
        String query =
                "UPDATE " + tableName + " SET "
                        + "first_name = " + "'" + Patient.getFirstName() + "',"
                        + "last_name = " + "'" + Patient.getLastName() + "',"
                        + "middle_name = " + "'" + Patient.getMiddleName() + "',"
                        + "phone = " + "'" + Patient.getPhone() + "'"
                        + "WHERE id = " + Patient.getId();
        return query;
    }
}
