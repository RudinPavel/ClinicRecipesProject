package com.example.application.service;

import com.example.application.dao.DAO;
import com.example.application.model.Doctor;
import com.example.application.model.Prescription;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private DAO<Doctor> doctorDAO;
    private DAO<Prescription> prescriptionDAO;

    public DoctorService(DAO<Doctor> doctorDAO, DAO<Prescription> prescriptionDAO) {
        this.doctorDAO = doctorDAO;
        this.prescriptionDAO = prescriptionDAO;
    }

    public boolean create(Doctor doctor) {
        return doctorDAO.add(doctor);
    }

    public Doctor findById(Long id) {
        return doctorDAO.findById(id);
    }

    public List<Doctor> findAll() {
        return doctorDAO.findAll();
    }

    public boolean update(Doctor doctor) {
        return doctorDAO.update(doctor);
    }

    public boolean deleteById(Long doctorId) {
        return doctorDAO.deleteById(doctorId);
    }

    public int getTotalRecipesCount() {return prescriptionDAO.findAll().size();}
}
