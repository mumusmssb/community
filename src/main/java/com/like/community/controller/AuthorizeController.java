package com.like.community.controller;

import com.like.community.dto.AccessToKenDTO;
import com.like.community.dto.GitHubUser;
import com.like.community.mapper.UserMapper;
import com.like.community.model.User;
import com.like.community.provider.GitHubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

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

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response){
        AccessToKenDTO accessToKenDTO = new AccessToKenDTO();
        accessToKenDTO.setClient_id(clientID);
        accessToKenDTO.setClient_secret(clientSecret);
        accessToKenDTO.setRedirect_uri(redirectURI);
        accessToKenDTO.setCode(code);
        accessToKenDTO.setState(state);
        String accessToKen = gitHubProvider.getAccessToKen(accessToKenDTO);
        GitHubUser githubUser = gitHubProvider.getGitHubUser(accessToKen);
        if (githubUser != null){
            //登录成功  写入cookie 和 session
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            response.addCookie(new Cookie("token",token));
//            request.getSession().setAttribute("user",githubUser);
            return "redirect:/";
        }else{
            //登录失败
            return "redirect:/";
        }

    }
}
