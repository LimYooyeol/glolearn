package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.aspect.auth.UserContext;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.exception.InvalidAccessException;
import com.glolearn.newbook.service.MemberService;
import com.glolearn.newbook.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ImageController {

    private final MemberService memberService;

    private final ImageService imageService;

    @GetMapping("/image/{memberId}/{filename}")
    public void getImage(
            @PathVariable(name = "memberId") String memberId,
            @PathVariable(name = "filename") String filename,
            HttpServletResponse response
    ) throws IOException {
        imageService.findImage(memberId, filename, response);
    }

    @PostMapping("/image")
    @ResponseBody
    @Auth
    public String uploadImage(MultipartFile image) throws IOException, InterruptedException {
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){ throw new InvalidAccessException("Non-existing member.");}

        String filename = imageService.saveImage(image, member.getId().toString(), UUID.randomUUID().toString());

        JSONObject response = new JSONObject();
        response.put("url", "/image/" + filename);

        return response.toString();
    }
}
