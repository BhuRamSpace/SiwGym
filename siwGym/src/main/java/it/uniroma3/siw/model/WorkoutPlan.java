package it.uniroma3.siw.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "workout_plans")
public class WorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String name;

    // Relazione molti-a-uno con la classe User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Relazione molti-a-uno con la classe Staff (il trainer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Staff trainer;

    @Temporal(TemporalType.DATE)
    private Date creationDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    private String goal;

    // Relazione uno-a-molti con la classe WorkoutPlanDetail
    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutPlanDetail> planDetails;

    public WorkoutPlan() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() {return name;}
	public void setName(String name) {this.name = name;}

	public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Staff getTrainer() { return trainer; }
    public void setTrainer(Staff trainer) { this.trainer = trainer; }

    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public List<WorkoutPlanDetail> getPlanDetails() { return planDetails; }
    public void setPlanDetails(List<WorkoutPlanDetail> planDetails) { this.planDetails = planDetails; }

    // --- equals() and hashCode() ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkoutPlan that = (WorkoutPlan) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}