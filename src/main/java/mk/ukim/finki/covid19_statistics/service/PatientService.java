package mk.ukim.finki.covid19_statistics.service;

import mk.ukim.finki.covid19_statistics.model.Patient;

import java.util.List;

public interface PatientService {
    List<Patient> findAll();
    Patient findBySsn(Long ssn);
    List<Patient> findPatient(Long ssn, String name, String surname);
    Patient findByUhid(Long uhid);
    List<Patient> filter(Long patientSsn);
}
