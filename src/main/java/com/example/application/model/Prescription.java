package com.example.application.model;

import com.example.application.enums.Priority;

import java.sql.Date;

public class Prescription {

    private Long id;
    private String description;
    private Long patientId;
    private Long doctorId;
    private Date prescriptionDate;
    private Date prescriptionExpirationDate;
    private Priority priority;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Date getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(Date prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public Date getPrescriptionExpirationDate() {
        return prescriptionExpirationDate;
    }

    public void setPrescriptionExpirationDate(Date prescriptionExpirationDate) {
        this.prescriptionExpirationDate = prescriptionExpirationDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", prescriptionDate=" + prescriptionDate +
                ", prescriptionExpirationDate=" + prescriptionExpirationDate +
                ", priority=" + priority +
                '}';
    }
}
