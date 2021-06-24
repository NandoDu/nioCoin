package niocoin.kgsystem.kgbackend.util;

import niocoin.kgsystem.kgbackend.dto.userDTO;
import niocoin.kgsystem.kgbackend.service.AccountService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Task {
    private final AccountService accountService;

    public Task(AccountService accountService) {
        this.accountService = accountService;
    }
    @PostConstruct
    public void run() throws Err{
        if (accountService.isInit()){
            System.out.println("init!");
            accountService.register(new userDTO("123","123","123@qq.com","0000"));
        }
        else{
            System.out.println("already init!");
        }
    }
}
