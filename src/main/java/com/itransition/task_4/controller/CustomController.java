package com.itransition.task_4.controller;

import com.itransition.task_4.UserEntity;
import com.itransition.task_4.msg.Message;
import com.itransition.task_4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class CustomController {

    UserEntity user;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/logout")
    public String logout(){
        user = null;
        return "login";
    }

    @PostMapping("/checkUser")
    public String checkUser(@ModelAttribute UserEntity userEntity, Model model){
        try {
            UserEntity entity = null;
            System.out.println("Front: " + userEntity.toString());
            entity = userRepository.findUserEntityByMail(userEntity.getMail());
            System.out.println(entity.toString());
            if (entity != null && entity.getPassword().equals(userEntity.getPassword()) && entity.getStatus().equals("Active")) {
                user = entity;
                List<UserEntity> list = userRepository.findAll();
                model.addAttribute("users", list);
                return "index";
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return "login";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String getMapping(Model model) {
        return "registration";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserEntity userEntity, Model model) {
        if (!notExist(userEntity.getMail())){
            return "registration";
        }
        userEntity.setLastLoginTime(LocalDateTime.now());
        userEntity.setRegistrationTime(LocalDateTime.now());
        userEntity.setStatus("Active");
        UserEntity save = userRepository.save(userEntity);
        user = save;
        List<UserEntity> list = userRepository.findAll();
        model.addAttribute("users", list);
        return "index";
    }

    @PostMapping(value = "/edit", params = "block")
    public String blockUser(@RequestParam("idChecked") List<String> ids, Model model) {
        for (String id : ids) {
            Optional<UserEntity> user1 = userRepository.findById(Integer.valueOf(id));
            if (user1.isPresent()) {
                UserEntity userEntity = user1.get();
                if (userEntity.getStatus().equals("Active")) {
                    userEntity.setStatus("Blocked");
                    userRepository.save(userEntity);
                    if (userEntity.getMail().equals(user.getMail())){
                        return "login";
                    }
                }
            }
        }
        List<UserEntity> list = userRepository.findAll();
        model.addAttribute("users", list);
//        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        return "index";
    }

    @PostMapping(value = "/edit", params = "activate")
    public String unblockUser(@RequestParam("idChecked") List<String> ids, Model model) {
        List<UserEntity> list = userRepository.findAll();
        model.addAttribute("users", list);
        for (String id : ids) {
            Optional<UserEntity> user = userRepository.findById(Integer.valueOf(id));
            if (user.isPresent()) {
                UserEntity userEntity = user.get();
                if (userEntity.getStatus().equals("Blocked")) {
                    userEntity.setStatus("Active");
                    userRepository.save(userEntity);
                }
            }
        }
        return "index";
    }

    @PostMapping(value = "/edit", params = "delete")
    public String deleteUser(@RequestParam("idChecked") List<String> ids, Model model) {
        for (String id : ids) {
            Optional<UserEntity> user1 = userRepository.findById(Integer.valueOf(id));
            if (user1.isPresent()) {
                UserEntity userEntity = user1.get();
                userRepository.delete(userEntity);
                if (user.getMail().equals(userEntity.getMail())){
                    return "login";
                }
            }
        }
        List<UserEntity> list = userRepository.findAll();
        model.addAttribute("users", list);
        return "index";
    }

    private boolean notExist(String mail){
        UserEntity exist = null;
        exist = userRepository.findUserEntityByMail(mail);
        if (exist == null){
            return true;
        }
        return false;
    }
}
