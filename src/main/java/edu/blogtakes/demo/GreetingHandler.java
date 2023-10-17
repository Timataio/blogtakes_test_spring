package edu.blogtakes.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

public class GreetingHandler {
	private List<Greeting> greetings;
	public GreetingHandler() {
		this.greetings = new ArrayList<>();
		greetings.add(new Greeting("Hello"));
		greetings.add(new Greeting("こんいちは"));
		greetings.add(new Greeting("Good tidings to you."));
		greetings.add(new Greeting("Yo"));
	}
	
}
