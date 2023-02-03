package com.project.demo.controller;

import com.project.demo.requestmodels.ReviewRequest;
import com.project.demo.service.ReviewService;
import com.project.demo.utils.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/reviews")
@AllArgsConstructor
public class ReviewController {

    private ReviewService reviewService;

    @PostMapping("/secure")
    public void postReview(@RequestHeader(value = "Authorization") String token, @RequestBody ReviewRequest reviewRequest) throws Exception{
        String userEmail = JWTUtil.payloadJWTExtraction(token, "\"sub\"");
        if (userEmail == null){
            throw new Exception("User email is missing");
        }
        reviewService.postReview(userEmail, reviewRequest);
    }

    @GetMapping("/secure/user/book")
    public Boolean reviewBookByUser(@RequestHeader(value="Authorization") String token,
                                    @RequestParam Long bookId) throws Exception {
        String userEmail = JWTUtil.payloadJWTExtraction(token, "\"sub\"");

        if (userEmail == null) {
            throw new Exception("User email is missing");
        }
        return reviewService.userReviewListed(userEmail, bookId);
    }

}
