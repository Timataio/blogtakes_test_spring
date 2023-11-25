package edu.blogtakes.demo.model;

public class Greeting {
	private String text;
	private String inventor;
	public Greeting() {
		
	}
	public Greeting(String text) {
		this.text = text;
	}
	public Greeting(String text, String inventor) {
		this.text = text;
		this.inventor = inventor;
	}
	public String getText() {
		return text;
	}
	public void setText(String greeting) {
		this.text = greeting;
	}
	public String getInventor() {
		return inventor;
	}
	public void setInventor(String inventor) {
		this.inventor = inventor;
	}
}
