package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//methods for adding common details
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("USERNAME ::"+userName);
		
		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER ::"+user);
		
		model.addAttribute("user", user); 
		
	}
	
	//dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");

		return "normal/user_dashboard";
	}
	
	//add contact  
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contact_form";
	}
	
	@PostMapping("/process-contact")
	public String processContact(
			@ModelAttribute Contact contact, //it will return the form data
			@RequestParam("profileImage") MultipartFile file, // it takes image from add contact page
			Principal principal, HttpSession session) //used principal to get the user details 
	{ 
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			System.out.println("NickName :"+contact.getNickName());
			
			//Processing and uploading file
			if(file.isEmpty()) {
				System.out.println("Profile image is empty");
			}else {
				
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/image").getFile();
				
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("File is uploaded");
			}
			
			
			contact.setUser(user);
			
			user.getContacts().add(contact);
			
			this.userRepository.save(user);
			
			System.out.println("Data :"+contact);
			
			//success message
			session.setAttribute("message", new Message("Your contact is added!! Add more...", "success"));
		}catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			
			//error message
			session.setAttribute("message", new Message("Something went wrong!! Try later...", "danger"));

		}
		return "normal/add_contact_form";

	}
	
	//show contact handler
	//per page = 5[n]
	//current page = 0 [page]
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show Contacts Page");
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
//		List<Contact> contacts = user.getContacts();
		
		Pageable pageable = PageRequest.of(page, 2);
		
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
//		List<Contact> contacts = this.contactRepository.findContactsByUser(user.getId()); //before pagination
		model.addAttribute("contacts", contacts);
		
		//after adding pagination, set below values
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
}
