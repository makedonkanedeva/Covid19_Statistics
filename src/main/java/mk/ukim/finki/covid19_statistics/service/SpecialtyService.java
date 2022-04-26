package mk.ukim.finki.covid19_statistics.service;

import mk.ukim.finki.covid19_statistics.model.Specialty;

import java.util.List;

public interface SpecialtyService {
    List<Specialty> findAll();
    Specialty findByName(String specialtyName);
    Specialty save(String name);
    Specialty delete(String name);
}
