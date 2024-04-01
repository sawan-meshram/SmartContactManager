package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
//	
//	@GetMapping("/test")
//	@ResponseBody
//	public String test() {
//		User u1 = new User();
//		u1.setName("Sawan Meshram");
//		u1.setEmail("sawan.shatam@gmail.com");
//		
//		userRepository.save(u1);
//		return "working";
//	}
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	@RequestMapping(value = "/do_register", method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, //accepting the User from form page 
			BindingResult result, //binding result for validation
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, //accepting the checkbox value from form page
			Model model, //Use this model to send server-side information to client-side
			HttpSession session) //created a session
	{ 
		
		try {
			if(!agreement) {
				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agreed the terms and conditions");
			}
			
			if(result.hasErrors()) {
				System.out.println("ERROR :"+result.toString());
				model.addAttribute("user", user); //return user data to client-end
				return "signup";
			}
//			System.out.println("BindingResult :"+result.toString());

			System.out.println("Agreement :"+agreement);

			
			user.setRole("ROLE_USER");
			user.setEnable(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword())); //encode password
			
			User result1 = this.userRepository.save(user);
			
			System.out.println(result1);
			
			model.addAttribute("user", new User()); //send new User in case, user added successfully
			session.setAttribute("message", new Message("Registered successfully!!", "alert-success"));


		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!! "+e.getMessage(), "alert-danger"));
		}
		return "signup";

	}

}
