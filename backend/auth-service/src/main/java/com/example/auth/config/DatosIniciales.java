package com.example.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.HashService;

/**
 * Crea al iniciar el servicio los usuarios de prueba (FICTICIOS) de cada perfil.
 * Si ya existen, no hace nada.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final UserRepository userRepository;
    private final HashService hashService;

    public DatosIniciales(UserRepository userRepository, HashService hashService) {
        this.userRepository = userRepository;
        this.hashService = hashService;
    }

    @Override
    public void run(String... args) {
        crearSiNoExiste("utp@mpaz.cl", "UTP");
        crearSiNoExiste("docente@mpaz.cl", "DOCENTE");
        crearSiNoExiste("estudiante@mpaz.cl", "ESTUDIANTE");
    }

    private void crearSiNoExiste(String email, String rol) {
        if (userRepository.findByEmail(email) == null) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(hashService.sha1("123456"));
            user.setRole(rol);
            userRepository.save(user);
        }
    }
}