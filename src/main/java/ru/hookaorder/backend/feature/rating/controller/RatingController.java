package ru.hookaorder.backend.feature.rating.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.place.repository.PlaceRepository;
import ru.hookaorder.backend.feature.rating.entity.RatingEntity;
import ru.hookaorder.backend.feature.rating.repository.RatingRepository;
import ru.hookaorder.backend.feature.roles.entity.ERole;
import ru.hookaorder.backend.feature.user.entity.UserEntity;
import ru.hookaorder.backend.feature.user.repository.UserRepository;

@RestController
@RequestMapping(value = "/rating")
@AllArgsConstructor
public class RatingController {

    private final UserRepository userRepository;

    private final PlaceRepository placeRepository;

    private final RatingRepository ratingRepository;


    @PostMapping(value = "/set/staff/{staffUserId}")
    ResponseEntity<?> addRatingByUserId(@RequestBody RatingEntity rating, @PathVariable Long staffUserId, Authentication authentication) {
        UserEntity staffUserEntity = userRepository.findById(staffUserId).get();
        if (staffUserEntity.getRolesSet().isEmpty() || staffUserEntity
                .getRolesSet().stream().map(roleEntity ->
                        ERole.valueOf(roleEntity.getRoleName())).anyMatch(roleName ->
                        !(roleName.equals(ERole.WAITER) || roleName.equals(ERole.HOOKAH_MASTER)))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden for this user");
        } else {
            RatingEntity newRating = staffUserEntity.getRatings()
                    .stream()
                    .filter((val) -> val.getOwnerId().equals(authentication.getPrincipal()))
                    .findFirst()
                    .orElse(rating);
            newRating.setRatingValue(rating.getRatingValue());
            newRating.setOwnerId((Long) authentication.getPrincipal());
            ratingRepository.save(newRating);
            staffUserEntity.getRatings().add(newRating);
            userRepository.save(staffUserEntity);
            return ResponseEntity.ok().body(newRating);

        }
    }

    @PostMapping(value = "/set/place/{placeId}")
    ResponseEntity<?> addRatingByPlaceId(@RequestBody RatingEntity rating, @PathVariable Long placeId, Authentication authentication) {
        PlaceEntity placeEntity = placeRepository.findById(placeId).get();

        RatingEntity newRating = placeEntity.getRatings()
                .stream()
                .filter((val) -> val.getOwnerId().equals(authentication.getPrincipal()))
                .findFirst()
                .orElse(rating);
        newRating.setRatingValue(rating.getRatingValue());
        newRating.setComment(rating.getComment());
        newRating.setOwnerId((Long) authentication.getPrincipal());
        ratingRepository.save(newRating);
        placeEntity.getRatings().add(newRating);
        placeRepository.save(placeEntity);
        return ResponseEntity.ok().body(newRating);
    }
}
