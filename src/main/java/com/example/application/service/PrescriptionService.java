package com.example.application.service;


import com.example.application.dao.DAO;
import com.example.application.dao.PrescriptionDAO;
import com.example.application.model.Prescription;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescriptionService {

    private DAO<Prescription> prescriptionDAO = new PrescriptionDAO();

    public boolean create(Prescription prescription) {
        return prescriptionDAO.add(prescription);
    }

    public Prescription findById(Long id) {
        return prescriptionDAO.findById(id);
    }

    public List<Prescription> findAll() {
        return prescriptionDAO.findAll();
    }

    public boolean update(Prescription prescription) {
        return prescriptionDAO.update(prescription);
    }

    public boolean deleteById(Long prescriptionId) {
        return prescriptionDAO.deleteById(prescriptionId);
    }
}
