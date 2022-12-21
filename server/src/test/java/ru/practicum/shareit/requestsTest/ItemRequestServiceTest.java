package ru.practicum.shareit.requestsTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.exception.*;
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
import java.util.Optional;

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
        Mockito.when(userRepository.findUserByIdEquals(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        Assertions.assertEquals(itemRequest.getId(), itemRequestService.createItemRequest(itemRequestDto, user.getId()).getId());
        Assertions.assertEquals(itemRequest.getDescription(), itemRequestService.createItemRequest(itemRequestDto, user.getId()).getDescription());
    }

    /*@Test
    void createNewItemRequestWithEmptyDescriptionTest() {
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemRequestService.createItemRequest(itemRequestEmptyDescription, user.getId()));

        Assertions.assertEquals("Запрос не содержит описания.", exception.getMessage());
    }*/

    @Test
    void checkUserIdExceptionTest() {
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(false);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.createItemRequest(itemRequestDto, user.getId()));

        Assertions.assertEquals(String.format("Пользователь с данным id %s не зарегистрирован.", user.getId()), exception.getMessage());
    }

    @Test
    void getItemRequestsTest() {
        Mockito.when(itemRepository.findAllByRequestIdEquals(anyInt()))
                .thenReturn(listItems);
        Mockito.when(itemRequestReporistory.findByRequester_Id(anyInt()))
                .thenReturn(listItemRequests);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        Assertions.assertEquals(listItemRequests.size(), itemRequestService.getRequests(user.getId()).size());
        Assertions.assertEquals(listItemRequests.get(0).getId(), itemRequestService.getRequests(user.getId()).get(0).getId());
    }

    @Test
    void getItemRequestsWithPaginationTest() {
        Mockito.when(itemRepository.findAllByRequestIdEquals(anyInt()))
                .thenReturn(listItems);
        Mockito.when(itemRequestReporistory.findAllByRequester_IdNot(anyInt(), (Pageable) any()))
                .thenReturn(pageItemRequests);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        Assertions.assertEquals(listItemRequests.size(), itemRequestService.getRequestWithPagination(userErrorId.getId(), 1, 1).size());
        Assertions.assertEquals(listItemRequests.get(0).getId(), itemRequestService.getRequestWithPagination(userErrorId.getId(), 1, 1).get(0).getId());
    }

    /*@Test
    void getItemRequestsWithExceptionTest() {
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemRequestService.getRequestWithPagination(user.getId(), 0, 0));

        Assertions.assertEquals("Некорректные параметры пагинации.", exception.getMessage());
    }*/

    @Test
    void getItemRequestTest() {
        Mockito.when(itemRequestReporistory.existsById(anyInt()))
                .thenReturn(true);
        Mockito.when(itemRequestReporistory.findItemRequestByIdEquals(anyInt()))
                .thenReturn(itemRequest);
        Mockito.when(itemRepository.findAllByRequestIdEquals(anyInt()))
                .thenReturn(listItems);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        Assertions.assertEquals(itemRequest.getId(), itemRequestService.getRequestListOnRequesterId(itemRequest.getId(), user.getId()).getId());
        Assertions.assertEquals(itemRequest.getDescription(), itemRequestService.getRequestListOnRequesterId(itemRequest.getId(), user.getId()).getDescription());
    }

    @Test
    void getNotExistItemRequestTest() {
        Mockito.when(itemRequestReporistory.existsById(anyInt()))
                .thenReturn(false);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getRequestListOnRequesterId(99, user.getId()));

        Assertions.assertEquals(String.format("Запрос с данным id %s не зарегистрирован.", 99), exception.getMessage());
    }
}
