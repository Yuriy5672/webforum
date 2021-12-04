package com.yuriy.webforum.service;

import com.yuriy.webforum.entity.Role;
import com.yuriy.webforum.entity.User;
import com.yuriy.webforum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @PersistenceContext
    private EntityManager em;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public User findUserById(Long userId){
        Optional<User> userFromDB = userRepository.findById(userId);
        return userFromDB.orElse(new User());
    }

    public List<User> allUsers(){
        return userRepository.findAll();
    }

    public boolean saveUser(User user){
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if(userFromDB != null)
            return false;

        user.setRoles(Collections.singleton(new Role(1L, "USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean deleteUser(Long userId){
        if(userRepository.findById(userId).isPresent()){
            userRepository.deleteById(userId);
            return true;
        }

        return false;
    }

    public List<User> getUserList(Long id){
        return em.createQuery("SELECT u FROM User u WHERE u.id > :paramId", User.class)
                .setParameter("paramId", id).getResultList();
    }

}
