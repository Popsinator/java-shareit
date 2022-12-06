package ru.practicum.shareit.bookingsTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;

    private BookingServiceImpl bookingService;

    private final User user = new User(1, "user", "user@user");

    private final User userNotOwner = new User(99, "user", "user@user");

    private final User userErrorId = new User(99, "user", "user@user");

    private final Item item = new Item("test", "Description test", true, user, 1);

    private final Item itemNotAvailable = new Item("test", "Description test", false, user, 1);

    private final Item itemErrorId = new Item("test", "Description test", true, userErrorId, 1);

    private final Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.APPROVED, user, item);

    private final Booking bookingStatusWaiting = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING, user, item);

    private final Booking bookingStatusRejected = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.REJECTED, user, item);

    private final Booking bookingStatusCurrent = new Booking(1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), Status.APPROVED, user, item);

    private final Booking bookingStatusPast = new Booking(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), Status.APPROVED, user, item);

    private final Booking bookingStatusFuture = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.APPROVED, user, item);

    private final BookingDto bookingDto = new BookingDto(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.APPROVED, user, item);

    private final BookingDtoIn bookingDtoIn = new BookingDtoIn(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1, Status.APPROVED);

    private final BookingDtoIn bookingDtoInBeforeDateReal = new BookingDtoIn(0, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), 1, Status.APPROVED);

    private final List<Item> listItems = List.of(item);
    private final List<Item> listItemsEmpty = List.of();

    private final List<Booking> listBookings = List.of(booking);

    private final List<Booking> listBookingsEmpty = List.of();

    Page<Booking> pageBooking = new PageImpl<>(listBookings);

    @BeforeEach
    void set() {
        item.setId(1);
        item.setOwner(user);
        itemErrorId.setId(99);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createNewBookingTest() {
        Mockito.when(bookingRepository.save(any()))
                .thenReturn(booking);
        Mockito.when(itemRepository.findItemByIdEquals(item.getId()))
                .thenReturn(item);
        Mockito.when(userRepository.findUserByIdEquals(anyInt()))
                .thenReturn(user);
        Mockito.when(itemRepository.findAll())
                .thenReturn(listItems);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        Assertions.assertEquals(booking.getId(), bookingService.createBooking(userNotOwner.getId(), bookingDtoIn).getId());
    }

    @Test
    void createNewBookingErrorWithItemNotExistTest() {
        Mockito.when(itemRepository.findAll())
                .thenReturn(listItemsEmpty);

        final NotFoundObjectException exception = Assertions.assertThrows(
                NotFoundObjectException.class,
                () -> bookingService.createBooking(userNotOwner.getId(), bookingDtoIn));

        Assertions.assertEquals(String.format("Вещи с идентификатором %s не существует.", bookingDtoIn.getItemId()), exception.getMessage());
    }

    @Test
    void createNewBookingErrorWithItemNotAvailableTest() {
        Mockito.when(itemRepository.findAll())
                .thenReturn(listItems);
        Mockito.when(itemRepository.findItemByIdEquals(anyInt()))
                .thenReturn(itemNotAvailable);

        final ItemIdStatusUnavailableException exception = Assertions.assertThrows(
                ItemIdStatusUnavailableException.class,
                () -> bookingService.createBooking(userNotOwner.getId(), bookingDtoIn));

        Assertions.assertEquals(String.format("Вещь с id %s недоступна для бронирования", bookingDtoIn.getItemId()), exception.getMessage());
    }

    @Test
    void createNewBookingErrorWithUserNotExistTest() {
        Mockito.when(itemRepository.findAll())
                .thenReturn(listItems);
        Mockito.when(itemRepository.findItemByIdEquals(anyInt()))
                .thenReturn(item);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(false);

        final NotFoundObjectException exception = Assertions.assertThrows(
                NotFoundObjectException.class,
                () -> bookingService.createBooking(userNotOwner.getId(), bookingDtoIn));

        Assertions.assertEquals(String.format("Владельца с идентификатором %s не существует.", userErrorId.getId()), exception.getMessage());
    }

    @Test
    void createNewBookingErrorWithIncorrectDateTest() {
        Mockito.when(itemRepository.findAll())
                .thenReturn(listItems);
        Mockito.when(itemRepository.findItemByIdEquals(anyInt()))
                .thenReturn(item);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        final DateTimeBookingException exception = Assertions.assertThrows(
                DateTimeBookingException.class,
                () -> bookingService.createBooking(user.getId(), bookingDtoInBeforeDateReal));

        Assertions.assertEquals("Даты бронирования некорректно заданы", exception.getMessage());
    }

    @Test
    void createNewBookingErrorWithIncorrectOwnerTest() {
        Mockito.when(itemRepository.findAll())
                .thenReturn(listItems);
        Mockito.when(itemRepository.findItemByIdEquals(anyInt()))
                .thenReturn(item);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        final InvalidHeaderUserId exception = Assertions.assertThrows(
                InvalidHeaderUserId.class,
                () -> bookingService.createBooking(user.getId(), bookingDtoIn));

        Assertions.assertEquals("Некорректный владелец item в заголовке 'X-Sharer-User-Id'", exception.getMessage());
    }

    @Test
    void changeStatusOnApprovedCompleteTest() {
        Mockito.when(bookingRepository.save(any()))
                .thenReturn(booking);
        Mockito.when(bookingRepository.findBookingByIdEquals(anyInt()))
                .thenReturn(bookingStatusWaiting);

        Assertions.assertEquals(Status.APPROVED, bookingService.changeStatusOnApprovedOrRejected(booking.getId(), user.getId(), "true").getStatus());
    }

    @Test
    void changeStatusOnRejectedCompleteTest() {
        Mockito.when(bookingRepository.save(any()))
                .thenReturn(bookingStatusRejected);
        Mockito.when(bookingRepository.findBookingByIdEquals(anyInt()))
                .thenReturn(bookingStatusWaiting);

        Assertions.assertEquals(Status.REJECTED, bookingService.changeStatusOnApprovedOrRejected(booking.getId(), user.getId(), "false").getStatus());
    }

    @Test
    void changeStatusOnApprovedOrRejectedWithNullStatusTest() {
        Mockito.when(bookingRepository.findBookingByIdEquals(anyInt()))
                .thenReturn(bookingStatusWaiting);

        final InvalidStateBookingException exception = Assertions.assertThrows(
                InvalidStateBookingException.class,
                () -> bookingService.changeStatusOnApprovedOrRejected(booking.getId(), user.getId(), null));

        Assertions.assertEquals("Отсутствует статус в заголовке", exception.getMessage());

    }

    @Test
    void changeStatusOnApprovedOrRejectedWithIncorrectUserHeaderIdTest() {
        Mockito.when(bookingRepository.findBookingByIdEquals(anyInt()))
                .thenReturn(bookingStatusWaiting);

        final InvalidHeaderUserId exception = Assertions.assertThrows(
                InvalidHeaderUserId.class,
                () -> bookingService.changeStatusOnApprovedOrRejected(booking.getId(), userErrorId.getId(), "true"));

        Assertions.assertEquals("Некорректный владелец item в заголовке 'X-Sharer-User-Id'", exception.getMessage());
    }

    @Test
    void changeStatusOnApprovedOrRejectedWithStatusApprovedTest() {
        Mockito.when(bookingRepository.findBookingByIdEquals(anyInt()))
                .thenReturn(booking);

        final InvalidPatchBookingException exception = Assertions.assertThrows(
                InvalidPatchBookingException.class,
                () -> bookingService.changeStatusOnApprovedOrRejected(booking.getId(), user.getId(), "true"));

        Assertions.assertEquals("Бронирование уже на статусе APPROVED", exception.getMessage());
    }

    @Test
    void getBookingCompleteTest() {
        Mockito.when(bookingRepository.findBookingByIdEquals(anyInt()))
                .thenReturn(booking);
        Mockito.when(bookingRepository.findAll())
                .thenReturn(listBookings);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        Assertions.assertEquals(bookingDto.getId(), bookingService.getBookingDto(booking.getId(), user.getId()).getId());
    }

    @Test
    void getAllBookingCompleteTest() {
        Mockito.when(bookingRepository.findAllByBooker_Id(anyInt(), any()))
                .thenReturn(pageBooking);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        Assertions.assertEquals(listBookings.size(), bookingService.getAllBookings(user.getId(), "ALL", "1", "1").size());
        Assertions.assertEquals(listBookings.get(0).getId(), new ArrayList<>(bookingService.getAllBookings(user.getId(), "ALL", "1", "1")).get(0).getId());
        Assertions.assertEquals(listBookings.get(0).getStatus(), new ArrayList<>(bookingService.getAllBookings(user.getId(), "ALL", "1", "1")).get(0).getStatus());
        Assertions.assertEquals(listBookings.get(0).getBooker(), new ArrayList<>(bookingService.getAllBookings(user.getId(), "ALL", "1", "1")).get(0).getBooker());
    }

    @Test
    void getAllBookingWithoutPaginationCompleteTest() {
        Mockito.when(bookingRepository.findAllByBooker_Id(anyInt()))
                .thenReturn(listBookings);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        Assertions.assertEquals(listBookings.size(), bookingService.getAllBookings(user.getId(), "ALL", "", "").size());
        Assertions.assertEquals(listBookings.get(0).getId(), new ArrayList<>(bookingService.getAllBookings(user.getId(), "ALL", "", "")).get(0).getId());
        Assertions.assertEquals(listBookings.get(0).getStatus(), new ArrayList<>(bookingService.getAllBookings(user.getId(), "ALL", "", "")).get(0).getStatus());
        Assertions.assertEquals(listBookings.get(0).getBooker(), new ArrayList<>(bookingService.getAllBookings(user.getId(), "ALL", "", "")).get(0).getBooker());
    }

    @Test
    void getAllBookingWithInvalidParamPaginationTest() {
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        final InvalidParamsPaginationException exception = Assertions.assertThrows(
                InvalidParamsPaginationException.class,
                () -> bookingService.getAllBookings(user.getId(), "ALL", "0", "0"));

        Assertions.assertEquals("Некорректные параметры пагинации.", exception.getMessage());
    }

    @Test
    void getAllBookingForOwnerCompleteTest() {
        Mockito.when(bookingRepository.findAllByItemOwner_Id(anyInt(), any()))
                .thenReturn(pageBooking);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        Assertions.assertEquals(listBookings.size(), bookingService.getAllBookingsOwner(user.getId(), "ALL", "1", "1").size());
        Assertions.assertEquals(listBookings.get(0).getId(), new ArrayList<>(bookingService.getAllBookingsOwner(user.getId(), "ALL", "1", "1")).get(0).getId());
        Assertions.assertEquals(listBookings.get(0).getStatus(), new ArrayList<>(bookingService.getAllBookingsOwner(user.getId(), "ALL", "1", "1")).get(0).getStatus());
        Assertions.assertEquals(listBookings.get(0).getBooker(), new ArrayList<>(bookingService.getAllBookingsOwner(user.getId(), "ALL", "1", "1")).get(0).getBooker());
    }

    @Test
    void getAllBookingForOwnerWithoutPaginationCompleteTest() {
        Mockito.when(bookingRepository.findAll())
                .thenReturn(listBookings);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        Assertions.assertEquals(listBookings.size(), bookingService.getAllBookingsOwner(user.getId(), "ALL", null, null).size());
        Assertions.assertEquals(listBookings.get(0).getId(), new ArrayList<>(bookingService.getAllBookingsOwner(user.getId(), "ALL", null, null)).get(0).getId());
        Assertions.assertEquals(listBookings.get(0).getStatus(), new ArrayList<>(bookingService.getAllBookingsOwner(user.getId(), "ALL", null, null)).get(0).getStatus());
        Assertions.assertEquals(listBookings.get(0).getBooker(), new ArrayList<>(bookingService.getAllBookingsOwner(user.getId(), "ALL", null, null)).get(0).getBooker());
    }

    @Test
    void getAllBookingForOwnerWithInvalidParamPaginationTest() {
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        final InvalidParamsPaginationException exception = Assertions.assertThrows(
                InvalidParamsPaginationException.class,
                () -> bookingService.getAllBookingsOwner(user.getId(), "ALL", "0", "0"));

        Assertions.assertEquals("Некорректные параметры пагинации.", exception.getMessage());
    }

    @Test
    void checkUserIdExceptionTest() {
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(false);

        final NotFoundObjectException exception = Assertions.assertThrows(
                NotFoundObjectException.class,
                () -> bookingService.checkUserId(userErrorId.getId()));

        Assertions.assertEquals(String.format(
                "Пользователя с идентификатором %s не существует.", userErrorId.getId()), exception.getMessage());
    }

    @Test
    void checkUserAndBookerIdExceptionTest() {
        Mockito.when(bookingRepository.findBookingByIdEquals(booking.getId()))
                .thenReturn(booking);

        final InvalidHeaderUserId exception = Assertions.assertThrows(
                InvalidHeaderUserId.class,
                () -> bookingService.checkUserAndBookerId(userErrorId.getId(), booking.getId()));

        Assertions.assertEquals(String.format(
                "Пользователю с идентификатором %s бронирование с идентификатором "
                        + booking.getId() + " не принадлежит.", userErrorId.getId()), exception.getMessage());
    }

    @Test
    void checkBookingIdExceptionTest() {
        Mockito.when(bookingRepository.findAll())
                .thenReturn(listBookingsEmpty);

        final NotFoundObjectException exception = Assertions.assertThrows(
                NotFoundObjectException.class,
                () -> bookingService.checkBookingId(booking.getId()));

        Assertions.assertEquals(String.format(
                "Бронирования с идентификатором %s не существует.", booking.getId()), exception.getMessage());
    }

    @Test
    void checkStatusTest() {
        Assertions.assertEquals(State.WAITING, bookingService.checkStatus("WAITING"));
        Assertions.assertEquals(State.CURRENT, bookingService.checkStatus("CURRENT"));
        Assertions.assertEquals(State.REJECTED, bookingService.checkStatus("REJECTED"));
        Assertions.assertEquals(State.FUTURE, bookingService.checkStatus("FUTURE"));
        Assertions.assertEquals(State.PAST, bookingService.checkStatus("PAST"));

        final InvalidStateBookingException exception = Assertions.assertThrows(
                InvalidStateBookingException.class,
                () -> bookingService.checkStatus("UNKNOWN"));

        Assertions.assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void fillBookingDtoWithStatusWaitingTest() {
        LocalDateTime real = LocalDateTime.now();
        State stateAfterCheck = State.WAITING;
        Collection<BookingDto> bookingDtos = new ArrayList<>();
        Collection<Booking> temp = List.of(bookingStatusWaiting);
        bookingService.fillBookingDto(real, stateAfterCheck, bookingDtos, temp);

        Assertions.assertEquals(temp.size(), bookingDtos.size());
        Assertions.assertEquals(bookingStatusWaiting.getId(), new ArrayList<>(bookingDtos).get(0).getId());
        Assertions.assertEquals(bookingStatusWaiting.getStatus(), new ArrayList<>(bookingDtos).get(0).getStatus());
        Assertions.assertEquals(bookingStatusWaiting.getBooker(), new ArrayList<>(bookingDtos).get(0).getBooker());
    }

    @Test
    void fillBookingDtoWithStatusRejectedTest() {
        LocalDateTime real = LocalDateTime.now();
        State stateAfterCheck = State.REJECTED;
        Collection<BookingDto> bookingDtos = new ArrayList<>();
        Collection<Booking> temp = List.of(bookingStatusRejected);
        bookingService.fillBookingDto(real, stateAfterCheck, bookingDtos, temp);

        Assertions.assertEquals(temp.size(), bookingDtos.size());
        Assertions.assertEquals(bookingStatusRejected.getId(), new ArrayList<>(bookingDtos).get(0).getId());
        Assertions.assertEquals(bookingStatusRejected.getStatus(), new ArrayList<>(bookingDtos).get(0).getStatus());
        Assertions.assertEquals(bookingStatusRejected.getBooker(), new ArrayList<>(bookingDtos).get(0).getBooker());
    }

    @Test
    void fillBookingDtoWithStatusCurrentTest() {
        LocalDateTime real = LocalDateTime.now();
        State stateAfterCheck = State.CURRENT;
        Collection<BookingDto> bookingDtos = new ArrayList<>();
        Collection<Booking> temp = List.of(bookingStatusCurrent);
        bookingService.fillBookingDto(real, stateAfterCheck, bookingDtos, temp);

        Assertions.assertEquals(temp.size(), bookingDtos.size());
        Assertions.assertEquals(bookingStatusCurrent.getId(), new ArrayList<>(bookingDtos).get(0).getId());
        Assertions.assertEquals(bookingStatusCurrent.getStatus(), new ArrayList<>(bookingDtos).get(0).getStatus());
        Assertions.assertEquals(bookingStatusCurrent.getBooker(), new ArrayList<>(bookingDtos).get(0).getBooker());
    }

    @Test
    void fillBookingDtoWithStatusPastTest() {
        LocalDateTime real = LocalDateTime.now();
        State stateAfterCheck = State.PAST;
        Collection<BookingDto> bookingDtos = new ArrayList<>();
        Collection<Booking> temp = List.of(bookingStatusPast);
        bookingService.fillBookingDto(real, stateAfterCheck, bookingDtos, temp);

        Assertions.assertEquals(temp.size(), bookingDtos.size());
        Assertions.assertEquals(bookingStatusPast.getId(), new ArrayList<>(bookingDtos).get(0).getId());
        Assertions.assertEquals(bookingStatusPast.getStatus(), new ArrayList<>(bookingDtos).get(0).getStatus());
        Assertions.assertEquals(bookingStatusPast.getBooker(), new ArrayList<>(bookingDtos).get(0).getBooker());
    }

    @Test
    void fillBookingDtoWithStatusFutureTest() {
        LocalDateTime real = LocalDateTime.now();
        State stateAfterCheck = State.FUTURE;
        Collection<BookingDto> bookingDtos = new ArrayList<>();
        Collection<Booking> temp = List.of(bookingStatusFuture);
        bookingService.fillBookingDto(real, stateAfterCheck, bookingDtos, temp);

        Assertions.assertEquals(temp.size(), bookingDtos.size());
        Assertions.assertEquals(bookingStatusFuture.getId(), new ArrayList<>(bookingDtos).get(0).getId());
        Assertions.assertEquals(bookingStatusFuture.getStatus(), new ArrayList<>(bookingDtos).get(0).getStatus());
        Assertions.assertEquals(bookingStatusFuture.getBooker(), new ArrayList<>(bookingDtos).get(0).getBooker());
    }
}
