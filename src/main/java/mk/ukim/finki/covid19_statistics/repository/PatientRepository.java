package mk.ukim.finki.covid19_statistics.repository;

import mk.ukim.finki.covid19_statistics.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findBySsn(Long ssn);
    List<Patient> findByNameAndSurname(String name, String surname);
    Patient findByUhid(Long uhid);
    Patient findBySsnAndNameAndSurname(Long ssn,String name, String surname);
}
