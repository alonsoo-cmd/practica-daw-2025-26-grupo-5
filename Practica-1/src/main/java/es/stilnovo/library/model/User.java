package es.stilnovo.library.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

@Entity(name = "UserTable")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	public Long getId() {
        return id;
    }

	private String name;

	private String encodedPassword;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;

	@Lob
	private Blob profileImage; 	

	private Double rating;
	
	public User() {
	}

	public User(String name, String encodedPassword, Blob profileImage, Double rating, String... roles) {
		this.name = name;
		this.encodedPassword = encodedPassword;
		this.profileImage = profileImage;
		this.rating = rating;
		this.roles = List.of(roles);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getEncodedPassword() {
		return encodedPassword;
	}

	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public Blob getProfileImage() { 
		return profileImage; 
	}

	public void setProfileImage(Blob profileImage) { 
		this.profileImage = profileImage; 
	}

	public Double getRating() { 
		return rating; 
	}

	public void setRating(Double rating) { 
		this.rating = rating; 
	}

}