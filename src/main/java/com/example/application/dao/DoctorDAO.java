package com.example.application.dao;


import com.example.application.model.Doctor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DoctorDAO extends DAO<Doctor> {

    public DoctorDAO() {
        tableName = "doctors";
        columns = new String[]{"first_name", "last_name", "middle_name", "specialization"};
    }

    @Override
    protected List<Doctor> convertFrom(ResultSet resultSet) throws SQLException {
        List<Doctor> Doctors = new ArrayList<>();
        while (resultSet.next()) {
            Doctor doctor = new Doctor();
            doctor.setId(resultSet.getLong("id"));
            doctor.setFirstName(resultSet.getString("first_name"));
            doctor.setLastName(resultSet.getString("last_name"));
            doctor.setMiddleName(resultSet.getString("middle_name"));
            doctor.setSpecialization(resultSet.getString("specialization"));
            Doctors.add(doctor);
        }
        return Doctors;
    }

    @Override
    protected String createInsertQuery(Doctor doctor) {
        String fields = String.join(", ", columns);
        String values = "'" + doctor.getFirstName() + "', '"
                + doctor.getLastName() + "', '"
                + doctor.getMiddleName() + "', '"
                + doctor.getSpecialization() + "'";
        return String.format("INSERT INTO %s (%s)"
                + "VALUES (%s)", tableName, fields, values);
    }

    @Override
    protected String createUpdateQuery(Doctor doctor) {
        String query =
                "UPDATE " + tableName + " SET "
                        + "first_name = " + "'" + doctor.getFirstName() + "',"
                        + "last_name = " + "'" + doctor.getLastName() + "',"
                        + "middle_name = " + "'" + doctor.getMiddleName() + "',"
                        + "specialization = " + "'" + doctor.getSpecialization() + "'"
                        + "WHERE id = " + doctor.getId();
        return query;
    }
}
