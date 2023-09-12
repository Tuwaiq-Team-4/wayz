package com.example.wayz.Service;

import com.example.wayz.Api.ApiException.ApiException;
import com.example.wayz.Model.*;
import com.example.wayz.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserTripsService {

    private final UserTripsRepository userTripsRepository;
    private final StudentTripsRepository studentTripsRepository;
    private final DriverTripsRepository driverTripsRepository;
    private final DriverRepository driverRepository;
    private final OrdersRepository ordersRepository;


    public List<UserTrips> findAll() {
        return userTripsRepository.findAll();
    }


    public void createUserTrips() {

        List<StudentTrips> studentTrips = studentTripsRepository.findAllStudentTripsBetweenTimestampAndEndTime("going");
        List<Driver> drivers = driverTripsRepository.findFirstFiveDriversInShift("morning");


        startProcess(drivers, studentTrips);


//        List<StudentTrips> studentTrips = studentTripsRepository.findAll();
//        List<Driver> drivers = driverRepository.findAll();
//
//
//        // O(n^2)
//
//        for (Driver driver : drivers) {
//            int counter = driver.getCar().getSeats();
//
//            UserTrips userTrips = new UserTrips();
//            userTrips.setDriver(driver);
//
//            for (StudentTrips studentTrips1 : studentTrips) {
//                if(counter == 0) {
//                    break;
//                }
//
//                userTrips.getStudentTrips().add(studentTrips1);
//
//                counter--;
//            }
//
//            userTripsRepository.save(userTrips);
//        }


    }


    public void startProcess(List<Driver> drivers, List<StudentTrips> studentTrips) {
        for (Driver driver : drivers) {

            if (studentTrips.isEmpty()) {
                break;
            }

            int counter = driver.getCar().getSeats();

            UserTrips userTrips = new UserTrips();
            userTrips.setStatus("in-progress");
            userTrips.setDriver(driver);

            for (StudentTrips studentTrip : studentTrips) {

                Orders order = new Orders(null, studentTrip.getStudent().getTripsLeft(), 18, "unpaid", LocalDateTime.now(), studentTrip.getStudent());
                ordersRepository.save(order);

                if (userTrips.getStudentTrips() == null) {
                    userTrips.setStudentTrips(Set.of());
                }

                userTripsRepository.save(userTrips);

                if (counter == 0) {
                    break;
                }


//                userTrips.getStudentTrips().add(studentTrips1);

                studentTrip.setUserTrips(userTrips);
                studentTripsRepository.save(studentTrip);


                counter--;
            }

        }
    }


    public String switchStatusToComplete(User user, Integer userTripId) {

        UserTrips userTrips = userTripsRepository.findUserTripsById(userTripId);

        if (userTrips == null) {
            throw new ApiException("user trip not found.");
        }

        for (StudentTrips trip : userTrips.getStudentTrips()) {
            int tripsNow = trip.getStudent().getTripsLeft();
            trip.getStudent().setTripsLeft(tripsNow - 1);
            studentTripsRepository.save(trip);
        }

        if (!Objects.equals(userTrips.getDriver().getId(), user.getId())) {
            throw new ApiException("you can not edit this trip.");
        }

        userTrips.setStatus("completed");

        userTripsRepository.save(userTrips);

        return "user trip with id: " + userTripId + " have been completed.";
    }


    public void cashOut(Integer id){
        List<UserTrips> allCompleted = userTripsRepository.findAllCompleted(id);
        Integer tripsCount = userTripsRepository.countTrips(id);
        if (allCompleted.isEmpty()){
            throw new ApiException("you do not have any Completed trips");
        }
        Driver driver = driverRepository.findDriverById(id);
        if (driver.getUser().getRole()=="DRIVER"){
            throw new ApiException("you are not allowed to do that , you most be driver");
        }
        for(int i=0;i<allCompleted.size();i++) {
            allCompleted.get(i).setStatus("paid");
        }
           if (tripsCount != 0) {
                driver.setUnCashedTrips(tripsCount);
                driver.setBalance(tripsCount * 8);
                tripsCount--;
            }
    }

}
