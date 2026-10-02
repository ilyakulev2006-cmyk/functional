package com.student.functional.service;

import com.student.functional.dto.AuthResponse;
import com.student.functional.dto.RegisterRequest;
import com.student.functional.model.Role;
import com.student.functional.model.Student;
import com.student.functional.model.Teacher;
import com.student.functional.model.User;
import com.student.functional.repository.StudentRepository;
import com.student.functional.repository.TeacherRepository;
import com.student.functional.repository.UserRepository;
import com.student.functional.util.PasswordHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public AuthService(UserRepository userRepository,
                       StudentRepository studentRepository,
                       TeacherRepository teacherRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        // 1. Валидация логина
        if (req.getLogin() == null || req.getLogin().isBlank()) {
            return new AuthResponse(false, "Логин не может быть пустым");
        }
        if (req.getLogin().length() < 3) {
            return new AuthResponse(false, "Логин должен быть минимум 3 символа");
        }

        // 2. Валидация пароля
        if (req.getPassword() == null || req.getPassword().length() < 6) {
            return new AuthResponse(false, "Пароль должен быть минимум 6 символов");
        }
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            return new AuthResponse(false, "Пароли не совпадают");
        }

        // 3. Валидация ФИО
        if (req.getFullName() == null || req.getFullName().isBlank()) {
            return new AuthResponse(false, "ФИО обязательно");
        }

        // 4. Валидация роли
        if (req.getRole() == null || req.getRole().isBlank()) {
            return new AuthResponse(false, "Роль обязательна");
        }

        Role role;
        try {
            role = Role.valueOf(req.getRole().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return new AuthResponse(false, "Недопустимая роль");
        }
        if (role == Role.ADMIN) {
            return new AuthResponse(false, "Регистрация админа запрещена");
        }

        // 5. Проверка уникальности логина
        if (userRepository.existsByLogin(req.getLogin())) {
            return new AuthResponse(false, "Логин уже занят");
        }

        // 6. Создаём пользователя
        User user = new User(
                req.getLogin(),
                PasswordHasher.hash(req.getPassword()),
                role
        );
        user = userRepository.save(user);

        // 7. Создаём профиль по роли
        if (role == Role.STUDENT) {
            Student student = new Student(user, req.getFullName(), req.getGroupId());
            studentRepository.save(student);
        } else if (role == Role.TEACHER) {
            Teacher teacher = new Teacher(user, req.getFullName(), req.getDepartment());
            teacherRepository.save(teacher);
        }

        // 8. Успех
        AuthResponse resp = new AuthResponse(true, "Регистрация успешна");
        resp.setUserId(user.getId());
        return resp;
    }
}