package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class UserService {

	private UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Transactional(readOnly = true)
	public User findById(Long id) {
		return this.userRepository.findById(id).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public List<User> findAll() {
		return this.userRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public User findByUsername(String username) {
		return this.userRepository.findByUsername(username).orElse(null);
	}
	
	@Transactional
	public User saveUser(User user) {
		return this.userRepository.save(user);
	}
	
}
