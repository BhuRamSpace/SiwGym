package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Booking;
import it.uniroma3.siw.model.CourseSlot;
import it.uniroma3.siw.model.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Long> {
	public List<Booking> findByCourseSlot(CourseSlot courseSlot);
	public List<Booking> findByUser(User user);
	public Long countByCourseSlot(CourseSlot courseSlot);
	public Optional<Booking> findByCourseSlotAndUser(CourseSlot courseSlot, User user);
	public boolean existsByCourseSlotAndUser(CourseSlot courseSlot, User user);
	public void deleteAllByUser(User user);
	public Optional<Booking> findByUserIdAndCourseSlotId(Long userId, Long courseSlotId);
}