CREATE TABLE IF NOT EXISTS patients
(
    id IDENTITY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(70) NOT NULL,
    phone VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS doctors
(
    id IDENTITY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(70),
    specialization VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS prescriptions
(
    id IDENTITY,
    description VARCHAR(400) NOT NULL,
    patient_id BIGINT NOT NULL ,
    doctor_id BIGINT NOT NULL,
    prescription_date DATE NOT NULL,
    prescription_expiration_date DATE NOT NULL,
    priority VARCHAR(10) NOT NULL,
    CONSTRAINT fk_patient_id FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_doctor_id FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

INSERT INTO patients(first_name, last_name, middle_name, phone)
VALUES ('Ivankov', 'Oleg', 'Aleksandrovich', '79299305225');

INSERT INTO patients(first_name, last_name, middle_name, phone)
VALUES ('Kichenko', 'Alexey', 'Romanovich', '79569305226');

INSERT INTO patients(first_name, last_name, middle_name, phone)
VALUES ('Maxim', 'Mekseev', 'Gennadievich', '79149305227');

INSERT INTO doctors(first_name, last_name, middle_name, specialization)
VALUES ('Oleg', 'Golovin', 'Anatolievich', 'Surgeon');

INSERT INTO doctors(first_name, last_name, middle_name, specialization)
VALUES ('Ivan', 'Tarasov', 'Nikolaevich', 'Dentist');

INSERT INTO prescriptions
(description,
 patient_id,
 doctor_id,
 prescription_date,
 prescription_expiration_date,
 priority)
VALUES ('Vitamin A', 0, 0, '2020-09-08', '2020-09-15', 'NORMAL');

INSERT INTO prescriptions
(description,
 patient_id,
 doctor_id,
 prescription_date,
 prescription_expiration_date,
 priority)
VALUES ('Vitamin B', 0, 0, '2020-09-18', '2020-09-25', 'NORMAL');

INSERT INTO prescriptions
(description,
 patient_id,
 doctor_id,
 prescription_date,
 prescription_expiration_date,
 priority)
VALUES ('Painkillers', 0, 1, '2020-09-10', '2020-09-24', 'STATIM');

INSERT INTO prescriptions
(description,
 patient_id,
 doctor_id,
 prescription_date,
 prescription_expiration_date,
 priority)
VALUES ('Recovery pills', 0, 1, '2020-09-11', '2020-09-25', 'CITO');

INSERT INTO prescriptions
(description,
 patient_id,
 doctor_id,
 prescription_date,
 prescription_expiration_date,
 priority)
VALUES ('Vitamins', 1, 1, '2020-09-09', '2020-10-30', 'NORMAL');
