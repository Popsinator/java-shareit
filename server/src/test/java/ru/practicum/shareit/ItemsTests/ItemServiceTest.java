package ru.practicum.shareit.ItemsTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    CommentsRepository commentsRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemServiceImpl itemServiceMock;

    private ItemServiceImpl itemService;

    private final User user = new User(1, "user", "user@user");

    private final User userErrorId = new User(99, "user", "user@user");

    private final Item item = new Item("test", "Description test", true, user, 1);

    private final Item itemWithoutName = new Item("", "Description test", true, user, 1);

    private final Item itemNotExistUser = new Item("test", "Description test", true, userErrorId, 1);

    private final Item itemErrorId = new Item("test", "Description test", true, userErrorId, 1);

    private final Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.APPROVED, user, item);

    private final Booking bookingBeforeDateReal = new Booking(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), Status.APPROVED, user, item);

    private final Comment comment = new Comment(1, "text", item, user);

    private final Comment commentWithoutText = new Comment(1, "", item, user);

    private final List<Item> listItems = List.of(item);

    private final List<Booking> listBookings = List.of(booking);

    private final List<Booking> listBookingsBeforeRealDate = List.of(booking, bookingBeforeDateReal);

    private final List<Booking> listBookingsBeforeRealDateForTestComments = List.of(bookingBeforeDateReal);

    private final List<Booking> listBookingsEmpty = List.of();

    private final List<Comment> listComments = List.of(comment);

    @BeforeEach
    void set() {
        item.setId(1);
        item.setOwner(user);
        itemErrorId.setId(99);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentsRepository);
    }

    @Test
    void createNewItemTest() {
        Mockito.when(itemRepository.save(any()))
                .thenReturn(item);
        Mockito.when(userRepository.findUserByIdEquals(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        Assertions.assertEquals(item.getId(), itemService.createItem(item, user.getId()).getId());
    }

    @Test
    void updateItemCompleteTest() {
        Mockito.when(itemRepository.save(any()))
                .thenReturn(item);
        Mockito.when(itemRepository.findItemByIdEquals(item.getId()))
                .thenReturn(item);
        Mockito.when(bookingRepository.findAllByItem_Id(anyInt()))
                .thenReturn(listBookings);
        Mockito.when(commentsRepository.findAllByItem_Id(anyInt()))
                .thenReturn(listComments);
        Mockito.when(itemRepository.existsById(anyInt()))
                .thenReturn(true);
        Assertions.assertEquals(item.getId(), itemService.updateItem(item, item.getId(), item.getOwner().getId()).getId());
        Assertions.assertEquals(item.getName(), itemService.updateItem(item, item.getId(), item.getOwner().getId()).getName());
        Assertions.assertEquals(item.getDescription(), itemService.updateItem(item, item.getId(), item.getOwner().getId()).getDescription());
        Assertions.assertEquals(item.getAvailable(), itemService.updateItem(item, item.getId(), item.getOwner().getId()).getAvailable());
        Assertions.assertEquals(item.getOwner(), itemService.updateItem(item, item.getId(), item.getOwner().getId()).getOwner());
    }

    /*@Test
    void updateItemEmptyHeaderTest() {
        final InternalServerErrorException exception = Assertions.assertThrows(
                InternalServerErrorException.class,
                () -> itemService.updateItem(item, item.getId(), null));

        Assertions.assertEquals("Отсутствует заголовок 'X-Sharer-User-Id'", exception.getMessage());
    }*/

    @Test
    void updateItemIncorrectOwnerTest() {
        Mockito.when(itemRepository.findItemByIdEquals(item.getId()))
                .thenReturn(item);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(item, item.getId(), userErrorId.getId()));

        Assertions.assertEquals("Некорректный владелец item в заголовке 'X-Sharer-User-Id'", exception.getMessage());
    }

    @Test
    void getItemWithEmptyBookingsTest() {
        Mockito.when(itemRepository.findItemByIdEquals(item.getId()))
                .thenReturn(item);
        Mockito.when(bookingRepository.findAllByItem_Id(anyInt()))
                .thenReturn(listBookingsEmpty);
        Mockito.when(commentsRepository.findAllByItem_Id(anyInt()))
                .thenReturn(listComments);
        Mockito.when(itemRepository.existsById(anyInt()))
                .thenReturn(true);
        Assertions.assertEquals(item.getId(), itemService.getItem(item.getId(), item.getOwner().getId()).getId());
        Assertions.assertEquals(item.getName(), itemService.getItem(item.getId(), item.getOwner().getId()).getName());
        Assertions.assertEquals(item.getDescription(), itemService.getItem(item.getId(), item.getOwner().getId()).getDescription());
        Assertions.assertEquals(item.getAvailable(), itemService.getItem(item.getId(), item.getOwner().getId()).getAvailable());
        Assertions.assertEquals(item.getOwner(), itemService.getItem(item.getId(), item.getOwner().getId()).getOwner());
    }

    @Test
    void getItemWithUnEqualOwnerTest() {
        Mockito.when(itemRepository.findItemByIdEquals(item.getId()))
                .thenReturn(item);
        Mockito.when(bookingRepository.findAllByItem_Id(anyInt()))
                .thenReturn(listBookings);
        Mockito.when(commentsRepository.findAllByItem_Id(anyInt()))
                .thenReturn(listComments);
        Mockito.when(itemRepository.existsById(anyInt()))
                .thenReturn(true);
        Assertions.assertEquals(item.getId(), itemService.getItem(item.getId(), userErrorId.getId()).getId());
        Assertions.assertEquals(item.getName(), itemService.getItem(item.getId(), userErrorId.getId()).getName());
        Assertions.assertEquals(item.getDescription(), itemService.getItem(item.getId(), userErrorId.getId()).getDescription());
        Assertions.assertEquals(item.getAvailable(), itemService.getItem(item.getId(), userErrorId.getId()).getAvailable());
        Assertions.assertEquals(item.getOwner(), itemService.getItem(item.getId(), userErrorId.getId()).getOwner());
    }

    @Test
    void createItemWithoutOwnerTest() {
        Mockito.when(itemServiceMock.createItem(itemNotExistUser, userErrorId.getId()))
                .thenThrow(new NotFoundException(String.format(
                        "Владельца с идентификатором %s не существует.", item.getOwner())));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemServiceMock.createItem(itemNotExistUser, userErrorId.getId()));

        Assertions.assertEquals(String.format(
                "Владельца с идентификатором %s не существует.", item.getOwner()), exception.getMessage());
    }

    @Test
    void getNotExistItemTest() {
        Mockito.when(itemRepository.findItemByIdEquals(itemErrorId.getId()))
                .thenReturn(item);
        Mockito.when(bookingRepository.findAllByItem_Id(anyInt()))
                .thenReturn(listBookings);
        Mockito.when(commentsRepository.findAllByItem_Id(anyInt()))
                .thenReturn(listComments);
        Mockito.when(itemRepository.existsById(anyInt()))
                .thenReturn(false);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItem(itemErrorId.getId(), userErrorId.getId()));

        Assertions.assertEquals(String.format(
                "Вещь с данным id %s не зарегистрирована.", itemErrorId.getId()), exception.getMessage());
    }

    @Test
    void getAllItemsForUserTest() {
        Mockito.when(itemRepository.findAllByOwner_Id(anyInt()))
                .thenReturn(listItems);
        Mockito.when(bookingRepository.findAllByItem_Id(anyInt()))
                .thenReturn(listBookingsBeforeRealDate);
        Assertions.assertEquals(listItems.size(), itemService.getAllItems(user.getId()).size());
        Assertions.assertEquals(listItems.get(0).getId(), new ArrayList<>(itemService.getAllItems(user.getId())).get(0).getId());
        Assertions.assertEquals(listItems.get(0).getName(), new ArrayList<>(itemService.getAllItems(user.getId())).get(0).getName());
        Assertions.assertEquals(listItems.get(0).getDescription(), new ArrayList<>(itemService.getAllItems(user.getId())).get(0).getDescription());
        Assertions.assertEquals(listItems.get(0).getAvailable(), new ArrayList<>(itemService.getAllItems(user.getId())).get(0).getAvailable());
        Assertions.assertEquals(listItems.get(0).getOwner(), new ArrayList<>(itemService.getAllItems(user.getId())).get(0).getOwner());
    }

    @Test
    void getAllItemsWithAddLastAndNextBookingForUserTest() {
        Mockito.when(itemRepository.findAllByOwner_Id(anyInt()))
                .thenReturn(listItems);
        Mockito.when(bookingRepository.findAllByItem_Id(anyInt()))
                .thenReturn(listBookingsBeforeRealDate);
        Assertions.assertEquals(listItems.size(), itemService.getAllItems(user.getId()).size());
        Assertions.assertEquals(listItems.get(0).getId(), new ArrayList<>(itemService.getAllItems(user.getId())).get(0).getId());
        Assertions.assertEquals(listItems.get(0).getName(), new ArrayList<>(itemService.getAllItems(user.getId())).get(0).getName());
        Assertions.assertEquals(listItems.get(0).getDescription(), new ArrayList<>(itemService.getAllItems(user.getId())).get(0).getDescription());
        Assertions.assertEquals(listItems.get(0).getAvailable(), new ArrayList<>(itemService.getAllItems(user.getId())).get(0).getAvailable());
        Assertions.assertEquals(listItems.get(0).getOwner(), new ArrayList<>(itemService.getAllItems(user.getId())).get(0).getOwner());
    }

    @Test
    void findItemsOnDescriptionEmptyTest() {
        Assertions.assertTrue(itemService.findItemsOnDescription("").isEmpty());
    }

    @Test
    void findItemsOnDescriptionTest() {
        Mockito.when(itemRepository.findAllByDescriptionContainingIgnoreCase(anyString()))
                .thenReturn(listItems);
        Assertions.assertFalse(itemService.findItemsOnDescription("test").isEmpty());
    }

    @Test
    void deleteItemTest() {
        itemService.deleteItem(item.getId());
        Mockito.verify(itemRepository, Mockito.times(1))
                .deleteItemById(item.getId());
    }

    @Test
    void createCommentCompleteTest() {
        Mockito.when(itemRepository.findItemByIdEquals(anyInt()))
                .thenReturn(item);
        Mockito.when(userRepository.findUserByIdEquals(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(commentsRepository.save(any()))
                .thenReturn(comment);
        Mockito.when(bookingRepository.findAllByItem_IdAndAndBooker_Id(anyInt(), anyInt()))
                .thenReturn(listBookingsBeforeRealDateForTestComments);
        CommentDto commentDto = ItemMapper.toDtoComment(comment, user.getName(), "");
        Assertions.assertEquals(commentDto.getId(), itemService.createComment(comment, user.getId(), item.getId()).getId());
        Assertions.assertEquals(commentDto.getText(), itemService.createComment(comment, user.getId(), item.getId()).getText());
        Assertions.assertEquals(commentDto.getAuthorName(), itemService.createComment(comment, user.getId(), item.getId()).getAuthorName());
        Assertions.assertEquals(commentDto.getCreated(), itemService.createComment(comment, user.getId(), item.getId()).getCreated());
    }

    /*@Test
    void createCommentCompleteWithoutTextTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.createComment(commentWithoutText, user.getId(), item.getId()));

        Assertions.assertEquals("Пустой комментарий", exception.getMessage());
    }*/

    @Test
    void createCommentWithoutBookingTest() {
        Mockito.when(bookingRepository.findAllByItem_IdAndAndBooker_Id(anyInt(), anyInt()))
                .thenReturn(listBookings);

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.createComment(comment, user.getId(), item.getId()));

        Assertions.assertEquals(String.format("Отсутствуют бронирования у пользователя с идентификатором %s", user.getId()), exception.getMessage());
    }

    @Test
    void createItemWithoutNameTest() {
        Mockito.when(itemServiceMock.createItem(itemWithoutName, user.getId()))
                .thenThrow(new BadRequestException("Отсутствует имя, описание, статус или владелец"));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemServiceMock.createItem(itemWithoutName, user.getId()));

        Assertions.assertEquals("Отсутствует имя, описание, статус или владелец", exception.getMessage());
    }

    /*@Test
    void checkItemWithoutNameTest() {
        Mockito.when(userRepository.findUserByIdEquals(user.getId()))
                .thenReturn(Optional.of(user));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.createItem(itemWithoutName, user.getId()));

        Assertions.assertEquals("Отсутствует имя, описание, статус или владелец", exception.getMessage());
    }*/

    @Test
    void checkItemNotExistOwnerTest() {
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(false);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.checkItem(itemNotExistUser));

        Assertions.assertEquals(String.format(
                "Владельца с идентификатором %s не существует.", itemNotExistUser.getOwner()), exception.getMessage());
    }
}
