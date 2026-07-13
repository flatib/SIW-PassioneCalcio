package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Referee;
import it.uniroma3.siw.repository.RefereeRepository;

@Service
public class RefereeService {

    private final RefereeRepository refereeRepository;

    public RefereeService(RefereeRepository refereeRepository) {
        this.refereeRepository = refereeRepository;
    }

    @Transactional(readOnly = true)
    public List<Referee> findAll() {
        return this.refereeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Referee findById(Long id) {
        return this.refereeRepository.findById(id).orElse(null);
    }

    @Transactional
    public Referee save(Referee referee) {
        return this.refereeRepository.save(referee);
    }
}