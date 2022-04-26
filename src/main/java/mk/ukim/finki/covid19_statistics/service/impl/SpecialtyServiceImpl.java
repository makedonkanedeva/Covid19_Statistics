package mk.ukim.finki.covid19_statistics.service.impl;

import mk.ukim.finki.covid19_statistics.model.Specialty;
import mk.ukim.finki.covid19_statistics.model.exceptions.SpecialtyAlreadyExistsException;
import mk.ukim.finki.covid19_statistics.repository.SpecialtyRepository;
import mk.ukim.finki.covid19_statistics.service.SpecialtyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public List<Specialty> findAll() {
        return specialtyRepository.findAll();
    }

    @Override
    public Specialty findByName(String specialtyName) {
        return specialtyRepository.findByName(specialtyName);
    }

    @Override
    public Specialty save(String specialtyName) {
        if (specialtyName == null || specialtyName.isEmpty()){
            throw new IllegalArgumentException();
        }
        if(specialtyRepository.findByName(specialtyName) != null){
            throw new SpecialtyAlreadyExistsException(specialtyName);
        }
        Specialty s = new Specialty(specialtyName);
        specialtyRepository.save(s);
        return s;
    }

    @Override
    public Specialty delete(String specialtyName) {
        if (specialtyName == null || specialtyName.isEmpty()){
            throw new IllegalArgumentException();
        }
        if(specialtyRepository.findByName(specialtyName) != null){
            throw new SpecialtyAlreadyExistsException(specialtyName);
        }
        return specialtyRepository.deleteByName(specialtyName);
    }
}
