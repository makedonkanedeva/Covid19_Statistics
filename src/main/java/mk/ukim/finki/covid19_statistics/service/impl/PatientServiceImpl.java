package mk.ukim.finki.covid19_statistics.service.impl;

import mk.ukim.finki.covid19_statistics.model.Patient;
import mk.ukim.finki.covid19_statistics.repository.PatientRepository;
import mk.ukim.finki.covid19_statistics.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    @Override
    public Patient findBySsn(Long ssn) {
        return this.patientRepository.findBySsn(ssn);
    }

    @Override
    public List<Patient> findPatient(Long ssn, String name, String surname) {
        if (ssn != null && name == null && surname == null)
            return Arrays.asList(patientRepository.findBySsn(ssn));
        if (ssn == null && name != null && surname != null)
            return patientRepository.findByNameAndSurname(name, surname);
        if (ssn != null && name != null && surname != null)
            return Arrays.asList(patientRepository.findBySsnAndNameAndSurname(ssn, name, surname));
        else
            return patientRepository.findAll();
    }

    @Override
    public Patient findByUhid(Long uhid) {
        return patientRepository.findByUhid(uhid);
    }

    @Override
    public List<Patient> filter(Long patientSsn) {
        if(patientSsn == null)
        {
            this.patientRepository.findAll();
        }
        else
        {
            this.patientRepository.findBySsn(patientSsn);
        }
        return null;
    }


}
