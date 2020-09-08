package com.example.application.service;


import com.example.application.dao.DAO;
import com.example.application.dao.PatientDAO;
import com.example.application.model.Patient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private DAO<Patient> patientDAO = new PatientDAO();

    public boolean create(Patient patient) {
        return patientDAO.add(patient);
    }

    public Patient findById(Long id) {
        return patientDAO.findById(id);
    }

    public List<Patient> findAll() {
        return patientDAO.findAll();
    }

    public boolean update(Patient patient) {
        return patientDAO.update(patient);
    }

    public boolean deleteById(Long patientId) {
        return patientDAO.deleteById(patientId);
    }
}
