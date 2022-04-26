package mk.ukim.finki.covid19_statistics.service;

import mk.ukim.finki.covid19_statistics.model.Referral;

import java.time.LocalDateTime;
import java.util.*;

public interface ReferralService {
    Referral create(LocalDateTime term, String patientName, String patientSurname, Long patientSsn,
                    Long forwardBySsn, Long forwardToSsn);
    Referral edit(Long id, LocalDateTime term, String patientName, String patientSurname, Long patientSsn,
                  Long forwardBySsn, Long forwardToSsn);
    Referral deleteById(Long id);
    List<Referral> findAll();
    List<Referral> filter(Long patientSsn);
    Referral findById(Long id);
}
