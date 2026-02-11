package es.stilnovo.library.controller;

import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.stilnovo.library.model.User;
import es.stilnovo.library.repository.UserRepository;

@Controller
public class UserWebController {

    @Autowired
    private UserRepository userRepository;

    // ... other methods like /profile or /edit-profile ...

    /**
     * Endpoint to retrieve a specific user's profile photo from the database.
     * It fetches the Blob content and returns it as a streaming image resource.
     */
    @GetMapping("/user/{id}/profile-photo")
    public ResponseEntity<Object> getProfilePhoto(@PathVariable long id) throws SQLException {
        
        // Find the user in the database; throws an exception if the ID doesn't exist
        User user = userRepository.findById(id).orElseThrow();
        
        // Check if the user has a profile image (stored as a Blob)
        if (user.getProfileImage() != null) {
            Resource file = new InputStreamResource(user.getProfileImage().getBinaryStream());
            
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Adjust the media type if necessary
                .body(file);
        }
        
        // Return a 404 Not Found if the user exists but has no image
        return ResponseEntity.notFound().build();
    }
}