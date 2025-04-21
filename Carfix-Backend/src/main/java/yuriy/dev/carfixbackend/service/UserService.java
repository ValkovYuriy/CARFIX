package yuriy.dev.carfixbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuriy.dev.carfixbackend.dto.UserDto;
import yuriy.dev.carfixbackend.mapper.UserMapper;
import yuriy.dev.carfixbackend.model.User;
import yuriy.dev.carfixbackend.repository.UserRepository;
import yuriy.dev.carfixbackend.token.JwtUtil;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private final UserMapper userMapper;


    @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User addedUser = userRepository.save(user);
        log.info("Был добавлен пользователь с id {}", addedUser.getId());
        return userMapper.toDto(addedUser);
    }


    @Transactional
    public String updateUser(UUID id, UserDto userDto) {
        User user = userRepository.findById(id).orElse(null);
        if(user != null){
            user.setUsername(userDto.username().isEmpty() ? user.getUsername() : userDto.username());
            user.setFirstName(userDto.firstName().isEmpty() ? user.getFirstName() : userDto.firstName());
            user.setLastName(userDto.lastName().isEmpty() ? user.getLastName() : userDto.lastName());
            user.setPhoneNumber(userDto.phoneNumber().isEmpty() ? user.getPhoneNumber() : userDto.phoneNumber());
            User updatedUser = userRepository.save(user);
            log.info("Был обновлен пользователь с id {}", updatedUser.getId());
            return jwtUtil.generateToken(updatedUser);
        }
        return null;
    }


    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
        log.info("Был удален пользователь с id {}", id);
    }


    public UserDto findById(UUID id) {
        User User = userRepository.findById(id).orElse(null);
        return userMapper.toDto(User);
    }

    public boolean existsByUsername(String email) {
        return userRepository.existsByUsername(email);
    }


    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    public User getByUsername(String email) {
        return userRepository.findByUsername(email)
                .orElse(null);
    }


//    /**
//     * Получение пользователя по имени пользователя
//     * <p>
//     * Нужен для Spring Security
//     *
//     * @return пользователь
//     */
//
//    public UserDetailsService userDetailsService() {
//        return this::getByUsername;
//    }

    public String getCurrentUserUsername() {
        // Получение имени пользователя из контекста Spring Security
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    public User getCurrentUser() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            return getByUsername(username);
        }

        throw new UsernameNotFoundException("User not found");
    }


}

