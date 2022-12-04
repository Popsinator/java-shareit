package ru.practicum.shareit.requestsTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.exception.EmptyDescriptionReuestException;
import ru.practicum.shareit.exception.IdItemOrUserNotExistException;
import ru.practicum.shareit.exception.IdItemRequestNotExistException;
import ru.practicum.shareit.exception.InvalidParamsPaginationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestReporistory;
import ru.practicum.shareit.requests.ItemRequestServiceImpl;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestReporistory itemRequestReporistory;

    private ItemRequestServiceImpl itemRequestService;

    private final User user = new User(1, "user", "user@user");
    private final User userErrorId = new User(99, "user", "user@user");
    private final Item item = new Item("test", "Description test", true, user, 1);
    private final List<User> listUsers = List.of(user);
    private final List<User> listUsersWithOtherId = List.of(userErrorId);
    private final List<User> listUsersEmpty = List.of();
    private final List<Item> listItems = List.of(item);
    private final ItemRequest itemRequest = new ItemRequest(1, "test", user, LocalDateTime.now(), listItems);
    private final ItemRequestDto itemRequestEmptyDescription = new ItemRequestDto(null, LocalDateTime.now(), null);
    private final ItemRequestDto itemRequestDto = new ItemRequestDto("test", LocalDateTime.now(), null);
    private final List<ItemRequest> listItemRequests = List.of(itemRequest);
    Page<ItemRequest> pageItemRequests = new PageImpl<>(listItemRequests);

    @BeforeEach
    void set() {
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRepository, itemRequestReporistory);
    }

    @Test
    void createItemRequestTest() {
        Mockito.when(itemRequestReporistory.save(any()))
                .thenReturn(itemRequest);
        Mockito.when(userRepository.findAll())
                .thenReturn(listUsers);
        Mockito.when(userRepository.findUserByIdEquals(anyInt()))
                .thenReturn(user);
        Assertions.assertEquals(itemRequest.getId(), itemRequestService.createItemRequest(itemRequestDto, user.getId()).getId());
        Assertions.assertEquals(itemRequest.getDescription(), itemRequestService.createItemRequest(itemRequestDto, user.getId()).getDescription());
    }

    @Test
    void createNewItemRequestWithEmptyDescriptionTest() {
        Mockito.when(userRepository.findAll())
                .thenReturn(listUsers);

        final EmptyDescriptionReuestException exception = Assertions.assertThrows(
                EmptyDescriptionReuestException.class,
                () -> itemRequestService.createItemRequest(itemRequestEmptyDescription, user.getId()));

        Assertions.assertEquals("Запрос не содержит описания.", exception.getMessage());
    }

    @Test
    void checkUserIdExceptionTest() {
        Mockito.when(userRepository.findAll())
                .thenReturn(listUsersEmpty);

        final IdItemOrUserNotExistException exception = Assertions.assertThrows(
                IdItemOrUserNotExistException.class,
                () -> itemRequestService.createItemRequest(itemRequestDto, user.getId()));

        Assertions.assertEquals(String.format("Пользователь с данным id %s не зарегистрирован.", user.getId()), exception.getMessage());
    }

    @Test
    void getItemRequestsTest() {
        Mockito.when(userRepository.findAll())
                .thenReturn(listUsers);
        Mockito.when(itemRepository.findAllByRequestIdEquals(anyInt()))
                .thenReturn(listItems);
        Mockito.when(itemRequestReporistory.findByRequester_Id(anyInt()))
                .thenReturn(listItemRequests);
        Assertions.assertEquals(listItemRequests.size(), itemRequestService.getRequest(user.getId()).size());
        Assertions.assertEquals(listItemRequests.get(0).getId(), itemRequestService.getRequest(user.getId()).get(0).getId());
    }

    @Test
    void getItemRequestsWithPaginationTest() {
        Mockito.when(userRepository.findAll())
                .thenReturn(listUsersWithOtherId);
        Mockito.when(itemRepository.findAllByRequestIdEquals(anyInt()))
                .thenReturn(listItems);
        Mockito.when(itemRequestReporistory.findAll((Pageable) any()))
                .thenReturn(pageItemRequests);
        Assertions.assertEquals(listItemRequests.size(), itemRequestService.getRequestWithPagination(userErrorId.getId(), 1, 1).size());
        Assertions.assertEquals(listItemRequests.get(0).getId(), itemRequestService.getRequestWithPagination(userErrorId.getId(), 1, 1).get(0).getId());
    }

    @Test
    void getItemRequestsWithExceptionTest() {
        Mockito.when(userRepository.findAll())
                .thenReturn(listUsers);

        final InvalidParamsPaginationException exception = Assertions.assertThrows(
                InvalidParamsPaginationException.class,
                () -> itemRequestService.getRequestWithPagination(user.getId(), 0, 0));

        Assertions.assertEquals("Некорректные параметры пагинации.", exception.getMessage());
    }

    @Test
    void getItemRequestTest() {
        Mockito.when(userRepository.findAll())
                .thenReturn(listUsers);
        Mockito.when(itemRequestReporistory.findAll())
                .thenReturn(listItemRequests);
        Mockito.when(itemRequestReporistory.findItemRequestByIdEquals(anyInt()))
                .thenReturn(itemRequest);
        Mockito.when(itemRepository.findAllByRequestIdEquals(anyInt()))
                .thenReturn(listItems);
        Assertions.assertEquals(itemRequest.getId(), itemRequestService.getRequestListOnRequesterId(itemRequest.getId(), user.getId()).getId());
        Assertions.assertEquals(itemRequest.getDescription(), itemRequestService.getRequestListOnRequesterId(itemRequest.getId(), user.getId()).getDescription());
    }

    @Test
    void getNotExistItemRequestTest() {
        Mockito.when(userRepository.findAll())
                .thenReturn(listUsers);
        Mockito.when(itemRequestReporistory.findAll())
                .thenReturn(List.of());

        final IdItemRequestNotExistException exception = Assertions.assertThrows(
                IdItemRequestNotExistException.class,
                () -> itemRequestService.getRequestListOnRequesterId(99, user.getId()));

        Assertions.assertEquals(String.format("Запрос с данным id %s не зарегистрирован.", 99), exception.getMessage());
    }
}
