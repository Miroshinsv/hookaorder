package ru.hookaorder.backend.feature.place.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.hookaorder.backend.feature.image.entity.ImageEntity;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.place.exception.PlaceNotFoundException;
import ru.hookaorder.backend.feature.place.repository.PlaceRepository;
import ru.hookaorder.backend.feature.roles.entity.ERole;
import ru.hookaorder.backend.feature.user.repository.UserRepository;
import ru.hookaorder.backend.feature.image.service.ImageService;
import ru.hookaorder.backend.utils.CheckOwnerAndRolesAccess;
import ru.hookaorder.backend.utils.NullAwareBeanUtilsBean;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PlaceServiceImpl implements PlaceService{

    private static final Optional<PlaceEntity> EMPTY_PLACE = Optional.empty();
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    private final ImageService imageService;

    @Override
    public Optional<PlaceEntity> create(PlaceEntity placeEntity, Authentication authentication) {
        if (!authentication.getAuthorities().contains(ERole.ADMIN)) {
            var user = userRepository.findById((Long) authentication.getPrincipal()).get();
            placeEntity.setOwner(user);
        }
        return Optional.of(placeRepository.save(placeEntity));
    }

    @Override
    public Optional<PlaceEntity> update(Long id, PlaceEntity placeEntity, Authentication authentication) {
        return placeRepository.findById(id).map((place) -> {
            if (!CheckOwnerAndRolesAccess.isOwnerOrAdmin(place, authentication)) {
                return EMPTY_PLACE;
            }
            NullAwareBeanUtilsBean.copyNoNullProperties(placeEntity, place);
            return Optional.of(placeRepository.save(place));
        }).orElse(EMPTY_PLACE);
    }

    @Override
    public Optional<String> uploadImage(Long id, String base64EncodedImage) {
        String imageUploadedURL = imageService.uploadImage(base64EncodedImage);
        if (imageUploadedURL != null) {
            PlaceEntity place = placeRepository.findById(id)
                .orElseThrow(() -> new PlaceNotFoundException("by place ID " + id));
            place.getImages().add(new ImageEntity(imageUploadedURL));
            placeRepository.save(place);
        }
        return Optional.of(imageUploadedURL);
    }

    @Override
    public boolean delete(Long id) {
        return placeRepository.findById(id).map((place) -> {
            placeRepository.deleteById(place.getId());
            return Boolean.TRUE;
        }).orElse(Boolean.FALSE);
    }
}
