package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDto createBooking(int userId, BookingDtoIn booking) {
        LocalDateTime real = LocalDateTime.now();
        if (itemRepository.findAll().stream().noneMatch(x -> x.getId() == booking.getItemId())) {
            throw new NotFoundObjectException(String.format("Вещи с идентификатором %s не существует.", booking.getItemId()));
        } else if (!itemRepository.findItemByIdEquals(booking.getItemId()).getAvailable()) {
            throw new ItemIdStatusUnavailableException(String.format("Вещь с id %s недоступна для бронирования", booking.getItemId()));
        } else if (userRepository.findAll().stream().noneMatch(x -> x.getId() == userId)) {
            throw new NotFoundObjectException(String.format("Владельца с идентификатором %s не существует.", userId));
        } else if ((booking.getEnd().isBefore(booking.getStart())
                || booking.getStart().isBefore(real) || booking.getEnd().isBefore(real)) && booking.getId() == 0) {
            throw new DateTimeBookingException("Даты бронирования некорректно заданы");
        } else if (itemRepository.findItemByIdEquals(booking.getItemId()).getOwner().getId() == userId) {
            throw new InvalidHeaderUserId("Некорректный владелец item в заголовке 'X-Sharer-User-Id'");
        }
        booking.setStatus(Status.WAITING);
        User tempUser = userRepository.findUserByIdEquals(userId);
        Item tempItem = itemRepository.findItemByIdEquals(booking.getItemId());
        Booking test = BookingMapper.toBookingDtoIn(booking, tempUser, tempItem);
        return BookingMapper.toBookingDto(bookingRepository.save(test), tempUser, tempItem);
    }

    @Transactional
    @Override
    public BookingDto changeStatusOnApprovedOrRejected(int bookingId, int userId, String approved) {
        Booking existBooking = getBooking(bookingId, userId);
        if (approved == null) {
            throw new InvalidStateBookingException("Отсутствует статус в заголовке");
        } else if (existBooking.getItem().getOwner().getId() != userId) {
            throw new InvalidHeaderUserId("Некорректный владелец item в заголовке 'X-Sharer-User-Id'");
        } else if (existBooking.getStatus().equals(Status.APPROVED)) {
            throw new InvalidPatchBookingException("Бронирование уже на статусе APPROVED");
        } else if (approved.equals("true")) {
            existBooking.setStatus(Status.APPROVED);
        } else if (approved.equals("false")) {
            existBooking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(existBooking), existBooking.getUser(), existBooking.getItem());
    }

    @Override
    public Booking getBooking(int bookingId, int userId) {
        return bookingRepository.findBookingByIdEquals(bookingId);
    }

    @Override
    public BookingDto getBookingDto(int bookingId, int userId) {
        checkUserId(userId);
        checkBookingId(bookingId);
        Booking booking = bookingRepository.findBookingByIdEquals(bookingId);
        checkUserAndBookerId(userId, bookingId);
        return BookingMapper.toBookingDto(booking, booking.getUser(), booking.getItem());
    }

    @Override
    public Collection<BookingDto> getAllBookings(int userId, String state) {
        LocalDateTime real = LocalDateTime.now();
        checkUserId(userId);
        State stateAfterCheck = checkStatus(state);
        Collection<BookingDto> bookingDtos = new ArrayList<>();
        Collection<Booking> temp = bookingRepository.findAll().stream().filter(x -> x.getUser().getId() == userId).collect(Collectors.toList());
        if (stateAfterCheck.equals(State.WAITING)) {
            for (Booking booking : temp) {
                if (booking.getStatus().equals(Status.WAITING)) {
                    bookingDtos.add(BookingMapper.toBookingDto(booking, booking.getUser(), booking.getItem()));
                }
            }
        } else if (stateAfterCheck.equals(State.REJECTED)) {
            for (Booking booking : temp) {
                if (booking.getStatus().equals(Status.REJECTED)) {
                    bookingDtos.add(BookingMapper.toBookingDto(booking, booking.getUser(), booking.getItem()));
                }
            }
        } else if (stateAfterCheck.equals(State.ALL)) {
            for (Booking booking : temp) {
                bookingDtos.add(BookingMapper.toBookingDto(booking, booking.getUser(), booking.getItem()));
            }
        } else if (stateAfterCheck.equals(State.CURRENT)) {
            for (Booking booking : temp) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking, booking.getUser(), booking.getItem());
                if (bookingDtoAdd.getStart().isBefore(real)
                        && bookingDtoAdd.getEnd().isAfter(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        } else if (stateAfterCheck.equals(State.PAST)) {
            for (Booking booking : temp) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking, booking.getUser(), booking.getItem());
                if (bookingDtoAdd.getStart().isBefore(real) && bookingDtoAdd.getEnd().isBefore(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        } else if (stateAfterCheck.equals(State.FUTURE)) {
            for (Booking booking : temp) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking, booking.getUser(), booking.getItem());
                if (bookingDtoAdd.getStart().isAfter(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        }
        return bookingDtos.stream()
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart())).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getAllBookingsOwner(int userId, String state) {
        LocalDateTime real = LocalDateTime.now();
        checkUserId(userId);
        State stateAfterCheck = checkStatus(state);
        Collection<BookingDto> bookingDtos = new ArrayList<>();
        Collection<Booking> bookingsFull = bookingRepository.findAll();
        if (stateAfterCheck.equals(State.WAITING)) {
            for (Booking booking : bookingsFull) {
                if (booking.getStatus().equals(Status.WAITING)) {
                    bookingDtos.add(BookingMapper.toBookingDto(booking, booking.getUser(), booking.getItem()));
                }
            }
        } else if (stateAfterCheck.equals(State.REJECTED)) {
            for (Booking booking : bookingsFull) {
                if (booking.getStatus().equals(Status.REJECTED)) {
                    bookingDtos.add(BookingMapper.toBookingDto(booking, booking.getUser(), booking.getItem()));
                }
            }
        } else if (stateAfterCheck.equals(State.ALL)) {
            for (Booking booking : bookingsFull) {
                bookingDtos.add(BookingMapper.toBookingDto(booking, booking.getUser(), booking.getItem()));
            }
        } else if (stateAfterCheck.equals(State.CURRENT)) {
            for (Booking booking : bookingsFull) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking, booking.getUser(), booking.getItem());
                if (bookingDtoAdd.getStart().isBefore(real)
                        && bookingDtoAdd.getEnd().isAfter(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        } else if (stateAfterCheck.equals(State.PAST)) {
            for (Booking booking : bookingsFull) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking, booking.getUser(), booking.getItem());
                if (bookingDtoAdd.getStart().isBefore(real) && bookingDtoAdd.getEnd().isBefore(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        } else if (stateAfterCheck.equals(State.FUTURE)) {
            for (Booking booking : bookingsFull) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking, booking.getUser(), booking.getItem());
                if (bookingDtoAdd.getStart().isAfter(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        }
        return bookingDtos.stream()
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart())).filter(x -> x.getItem().getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    public void checkUserId(int userId) {
        if (userRepository.findAll().stream().noneMatch(x -> x.getId() == userId)) {
            throw new NotFoundObjectException(String.format(
                    "Пользователя с идентификатором %s не существует.", userId));
        }
    }

    public void checkUserAndBookerId(int userId, int bookingId) {
        if (bookingRepository.findBookingByIdEquals(bookingId).getUser().getId() != userId
                && bookingRepository.findBookingByIdEquals(bookingId).getItem().getOwner().getId() != userId) {
            throw new InvalidHeaderUserId(String.format(
                    "Пользователю с идентификатором %s бронирование с идентификатором "
                            + bookingId + " не принадлежит.", userId));
        }
    }

    public void checkBookingId(int bookingId) {
        if (bookingRepository.findAll().stream().noneMatch(x -> x.getId() == bookingId)) {
            throw new NotFoundObjectException(String.format(
                    "Бронирования с идентификатором %s не существует.", bookingId));
        }
    }

    public State checkStatus(String state) {
        if (state == null || state.equals("ALL")) {
            return State.ALL;
        } else if (state.equals("WAITING")) {
            return State.WAITING;
        } else if (state.equals("CURRENT")) {
            return State.CURRENT;
        } else if (state.equals("REJECTED")) {
            return State.REJECTED;
        } else if (state.equals("FUTURE")) {
            return State.FUTURE;
        } else if (state.equals("PAST")) {
            return State.PAST;
        } else {
            throw new InvalidStateBookingException("Unknown state: " + state);
        }
    }

    public boolean checkItemId(int itemId) {
        boolean isExistUser = false;
        for (Item value : itemRepository.findAll()) {
            if (value.getId() == itemId) {
                isExistUser = true;
                break;
            }
        }
        return isExistUser;
    }
}
