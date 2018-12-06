package cgg.informatique.abl.webSocket.controleurs.rest;

import cgg.informatique.abl.webSocket.dao.AvatarDao;
import cgg.informatique.abl.webSocket.entites.Avatar;
//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AvatarController {
    private final AvatarDao avatarDao;

    public AvatarController(@Autowired AvatarDao avatarDao) {
        this.avatarDao = avatarDao;
    }

    @GetMapping(value = "avatars/{id}", produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public @ResponseBody byte[] getAvatarJpegById(@PathVariable Long id) throws IOException {
        Optional<Avatar> avatar = avatarDao.findById(id);

        if (!avatar.isPresent()) return null;

        String b64 = avatar.get().getImage();
        byte[] b64Bytes = getB64Bytes(b64);

        return b64Bytes;
    }

    private byte[] getB64Bytes(String b64) {
        String data = b64.split(",")[1];
        //return Base64.decode(data);
        return null;
    }
}
