package com.maknom.eco.guard.services;

import com.maknom.eco.guard.model.user.TypeRole;
import com.maknom.eco.guard.model.user.UserBean;
import com.maknom.eco.guard.model.user.UserRequest;
import com.maknom.eco.guard.model.user.UserService;
import com.maknom.eco.guard.repository.UserRepository;
import com.maknom.eco.guard.service.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CreateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @Captor
    private ArgumentCaptor<UserBean> argumentCaptor;

    private UserBean sampleUser;

    @BeforeEach
    void initTest() {
        userService = new UserServiceImpl(userRepository);
        sampleUser = new UserBean();
        sampleUser.setId(1L);
        sampleUser.setName("KOUAKOU");
        sampleUser.setRole(TypeRole.ADMIN);

        when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
    }


    @Test
    void createNewUser_ShouldReturnNull_WhenEmailAlreadyExist () {
        when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(sampleUser));

        UserRequest request = new UserRequest();
        request.setEmail("kouakou.ko@gmail.com");
        request.setName("Kouakou");
        request.setRole("admin");
        request.setPassword("abc");

        UserBean userBean = userService.create(request);

        Mockito.verify(userRepository, never()).save(Mockito.any());
        Assertions.assertNull(userBean);
    }


    @Test
    void createNewUser_ShouldPassCorrectValue () {
        UserRequest request = new UserRequest();
        request.setEmail("kouakou.ko@gmail.com");
        request.setName("Kouakou");
        request.setRole("admin");
        request.setPassword("abc");

        userService.create(request);

        verify(userRepository).save(argumentCaptor.capture());

        UserBean catureBean = argumentCaptor.getValue();
        Assertions.assertEquals("Kouakou", catureBean.getName());
        Assertions.assertEquals(TypeRole.parse("admin"), catureBean.getRole());
        Assertions.assertEquals("kouakou.ko@gmail.com", catureBean.getEmail());
        Assertions.assertNotNull(catureBean.getPassword());
    }
}
