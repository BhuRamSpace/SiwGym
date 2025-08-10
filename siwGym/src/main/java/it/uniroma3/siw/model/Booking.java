package it.uniroma3.siw.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Relazione molti-a-uno con la classe User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Relazione molti-a-uno con la classe CourseSlot
    @ManyToOne
    @JoinColumn(name = "course_slot_id")
    private CourseSlot courseSlot;

    @Temporal(TemporalType.TIMESTAMP)
    private Date bookingDate;

    private String status;

    public Booking() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public CourseSlot getCourseSlot() { return courseSlot; }
    public void setCourseSlot(CourseSlot courseSlot) { this.courseSlot = courseSlot; }

    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // --- equals() and hashCode() ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id != null ? id.equals(booking.id) : booking.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}