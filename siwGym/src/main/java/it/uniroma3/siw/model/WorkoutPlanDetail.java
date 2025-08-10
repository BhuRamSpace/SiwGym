package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "workout_plan_details")
public class WorkoutPlanDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Relazione molti-a-uno con la classe WorkoutPlan
    @ManyToOne
    @JoinColumn(name = "workout_plan_id")
    private WorkoutPlan workoutPlan;

    // Relazione molti-a-uno con la classe Exercise
    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Min(value = 1, message = "Il numero di serie deve essere almeno 1")
    private Integer sets;

    private String repetitions;

    private String restTime;

    private String notes;

    public WorkoutPlanDetail() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WorkoutPlan getWorkoutPlan() { return workoutPlan; }
    public void setWorkoutPlan(WorkoutPlan workoutPlan) { this.workoutPlan = workoutPlan; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }

    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }

    public String getRepetitions() { return repetitions; }
    public void setRepetitions(String repetitions) { this.repetitions = repetitions; }

    public String getRestTime() { return restTime; }
    public void setRestTime(String restTime) { this.restTime = restTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // --- equals() and hashCode() ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkoutPlanDetail that = (WorkoutPlanDetail) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}