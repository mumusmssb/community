package com.like.community.controller;

import com.like.community.dto.AccessToKenDTO;
import com.like.community.dto.GitHubUser;
import com.like.community.provider.GitHubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {

    @Autowired
    private GitHubProvider gitHubProvider;

    @Value("${GitHub.client.id}")
    private  String clientID;
    @Value("${GitHub.client.secret}")
    private String clientSecret;
    @Value("${GitHub.redirect.uri}")
    private String redirectURI;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state){
        AccessToKenDTO accessToKenDTO = new AccessToKenDTO();
        accessToKenDTO.setClient_id(clientID);
        accessToKenDTO.setClient_secret(clientSecret);
        accessToKenDTO.setRedirect_uri(redirectURI);
        accessToKenDTO.setCode(code);
        accessToKenDTO.setState(state);
        String accessToKen = gitHubProvider.getAccessToKen(accessToKenDTO);
        GitHubUser user = gitHubProvider.getGitHubUser(accessToKen);
        System.out.println(user.getName());
        System.out.println(user.getBio());
        System.out.println(user.getId());
        return "index";
    }
}
