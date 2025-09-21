package it.uniroma3.siw.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @Column(unique = true)
    @NotBlank
    private String email;

    private String specialization;

    @OneToOne(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
    private Credentials credentials;
    
    @OneToMany(mappedBy = "trainer")
    private List<CourseSlot> courseSlots = new ArrayList<>();

    public Staff() {}

    // --- Metodi Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public Credentials getCredentials() { return credentials; }
    public void setCredentials(Credentials credentials) { this.credentials = credentials; }
    
    public List<CourseSlot> getCourseSlots() {return courseSlots;}
    public void setCourseSlots(List<CourseSlot> courseSlots) {this.courseSlots = courseSlots;}

    // --- Metodi equals() e hashCode() ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Staff staff = (Staff) o;
        return email != null ? email.equals(staff.email) : staff.email == null;
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}