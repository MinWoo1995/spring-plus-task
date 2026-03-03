package org.example.expert.domain.todo.repository;

import org.example.expert.config.QueryDslConfig;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.example.expert.domain.user.entity.User;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QueryDslConfig.class)
class TodoRepositoryTest {
    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 할일_저장_시_생성자가_담당자로_자동_등록된다() {
        // given
        User user = new User("email@test.com", "password", UserRole.USER, "nickname");
        userRepository.save(user); // 유저 먼저 저장

        Todo todo = new Todo("제목", "내용", "Sunny", user);

        // when
        Todo savedTodo = todoRepository.save(todo);

        // then
        // 1. Todo가 잘 저장되었는지 확인
        assertNotNull(savedTodo.getId());

        // 2. Cascade에 의해 Manager가 자동으로 저장되었는지 확인
        assertEquals(1, savedTodo.getManagers().size());
        assertEquals(user.getId(), savedTodo.getManagers().get(0).getUser().getId());

        System.out.println("담당자 이름: " + savedTodo.getManagers().get(0).getUser().getNickname());
    }
}