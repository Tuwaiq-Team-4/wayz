package com.example.wayz.Repository;

import com.example.wayz.Model.StudentTrips;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentTripsRepository extends JpaRepository<StudentTrips, Integer> {


    StudentTrips findStudentTripsById(Integer id);


    @Query("select c from StudentTrips c where c.student.id=?1")
    List<StudentTrips> studentTrips(Integer id);

    @Query("select st from StudentTrips st where lower(st.type) = lower(:type)")
    List<StudentTrips> findByTypeIgnoreCase(String type);


    @Query("select count(st) / (DATEDIFF(max(st.timestamp), min(st.timestamp)) + 1) from StudentTrips st")
    Double calculateAverageTripsPerDay();

    @Query("select st from StudentTrips st where st.student.id = :studentId and lower(st.type) = lower(:type) ")
    List<StudentTrips> findTripsByStudentIdAndType(Integer studentId, String type);

    @Query("select count (c) from StudentTrips c where c.student.id=?1 ")
    Integer tripsNumber(Integer id);



    @Query("SELECT st FROM StudentTrips st WHERE st.type = ?1 and st.timestamp between st.timestamp and st.endTime and st.userTrips = null ORDER BY st.id ASC LIMIT 10")
    List<StudentTrips> findAllStudentTripsBetweenTimestampAndEndTime(String type);

}
