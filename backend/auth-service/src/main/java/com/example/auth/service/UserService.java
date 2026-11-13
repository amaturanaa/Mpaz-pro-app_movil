package com.example.auth.service;

import com.example.auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.auth.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private HashService hashService;
    
    public String login (String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null) return null;
        // compare SHA-1 hashes
        String hashedInput = hashService.sha1(password);
        if (!hashedInput.equals(user.getPassword())) return null;

        return jwtService.generateToken(email);
    }

    public String getRole(String email){
        User user = userRepository.findByEmail(email);
        return user.getRole();
    }

    public String register(String email, String password) {
        User existing = userRepository.findByEmail(email);
        if (existing != null) {
            return "Usuario ya existe!";
        }

        User user = new User();
        user.setEmail(email);
        // store SHA-1 hash of the password
        user.setPassword(hashService.sha1(password));
        user.setRole("USER");

        userRepository.save(user);

        return "Usuario creado exitosamente!";
    }
}
