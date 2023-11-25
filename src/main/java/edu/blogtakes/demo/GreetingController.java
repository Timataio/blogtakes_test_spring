package edu.blogtakes.demo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import edu.blogtakes.demo.model.Greeting;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring6.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;

import reactor.core.publisher.Flux;

@Controller
public class GreetingController {
	
	private static List<Greeting> greetings = new ArrayList<>();
	
	static {
		greetings.add(new Greeting("Hello", "God"));
		greetings.add(new Greeting("こんいちは", "神"));
		greetings.add(new Greeting("Good tidings to you.", "Santa"));
		greetings.add(new Greeting("Yo", "Some random guy"));
	}
	
	@GetMapping("/")
	public String index(final Model model) {
		IReactiveDataDriverContextVariable greetings = new ReactiveDataDriverContextVariable(getGreetings());
		
		model.addAttribute("greetings", greetings);
		model.addAttribute("greeting", new Greeting());
		return "index";
	}
	
	@GetMapping("/greeting/{greeting}")
	public String greeting(@PathVariable String greeting, final Model model) {
		model.addAttribute("greeting", greeting);
		return "greeting";
	}
	
	@PostMapping("/")
	public String postGreeting(@ModelAttribute Greeting greeting) {
		greeting.setInventor("You, the user");
		greetings.add(greeting);
		System.out.println("DEBUG: Greeting added");
		return "redirect:/";
	}
	
	public Flux<Greeting> getGreetings() {
		
		return Flux.fromIterable(greetings).delayElements(Duration.ofSeconds(3));
	}
}
