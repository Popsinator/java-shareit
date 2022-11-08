package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;

    @Override
    public BookingDto createBooking(int userId, Booking booking) {
        LocalDateTime real = LocalDateTime.now();
        if (!itemService.getItem(booking.getItemId(), userId).getAvailable()) {
            throw new ItemIdStatusUnavailableException(String.format("Вещь с id %s недоступна для бронирования", booking.getItemId()));
        } else if (userService.findAllUsers().stream().noneMatch(x -> x.getId() == userId)) {
            throw new NotFoundObjectException(String.format("Владельца с идентификатором %s не существует.", userId));
        } else if (!itemService.checkItemId(booking.getItemId())) {
            throw new NotFoundObjectException(String.format("Вещи с идентификатором %s не существует.", booking.getItemId()));
        } else if ((booking.getEnd().isBefore(booking.getStart())
                || booking.getStart().isBefore(real) || booking.getEnd().isBefore(real)) && booking.getId() == 0) {
            throw new DateTimeBookingException("Даты бронирования некорректно заданы");
        } else if (itemService.getItem(booking.getItemId(), userId).getOwner() == userId) {
            throw new InvalidHeaderUserId("Некорректный владелец item в заголовке 'X-Sharer-User-Id'");
        }
        booking.setBookerId(userId);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDto(repository.save(booking), userService.getUser(booking.getBookerId()), itemService.getItem(booking.getItemId(), userId));
    }

    @Override
    public BookingDto changeStatusOnApprovedOrRejected(int bookingId, int userId, String approved) {
        Booking existBooking = getBooking(bookingId, userId);
        if (approved == null) {
            throw new InvalidStateBookingException("Отсутствует статус в заголовке");
        } else if (itemService.getItem(existBooking.getItemId(), userId).getOwner() != userId) {
            throw new InvalidHeaderUserId("Некорректный владелец item в заголовке 'X-Sharer-User-Id'");
        } else if (existBooking.getStatus().equals(Status.APPROVED)) {
            throw new InvalidPatchBookingException("Бронирование уже на статусе APPROVED");
        } else if (approved.equals("true")) {
            existBooking.setStatus(Status.APPROVED);
        } else if (approved.equals("false")) {
            existBooking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(repository.save(existBooking),
                userService.getUser(existBooking.getBookerId()),
                itemService.getItem(existBooking.getItemId(), userId));
    }

    @Override
    public Booking getBooking(int bookingId, int userId) {
        return repository.findBookingByIdEquals(bookingId);
    }

    @Override
    public BookingDto getBookingDto(int bookingId, int userId) {
        checkUserId(userId);
        checkBookingId(bookingId);
        Booking booking = repository.findBookingByIdEquals(bookingId);
        checkUserAndBookerId(userId, bookingId);
        return BookingMapper.toBookingDto(booking,
                userService.getUser(booking.getBookerId()),
                itemService.getItem(booking.getItemId(), userId));
    }

    @Override
    public Collection<BookingDto> getAllBookings(int userId, String state) {
        LocalDateTime real = LocalDateTime.now();
        checkUserId(userId);
        State stateAfterCheck = checkStatus(state);
        Collection<BookingDto> bookingDtos = new ArrayList<>();
        Collection<Booking> temp = repository.findAllByBookerIdEquals(userId);
        if (stateAfterCheck.equals(State.WAITING)) {
            for (Booking booking : temp) {
                if (booking.getStatus().equals(Status.WAITING)) {
                    bookingDtos.add(BookingMapper.toBookingDto(booking,
                            userService.getUser(booking.getBookerId()),
                            itemService.getItem(booking.getItemId(), userId)));
                }
            }
        } else if (stateAfterCheck.equals(State.REJECTED)) {
            for (Booking booking : temp) {
                if (booking.getStatus().equals(Status.REJECTED)) {
                    bookingDtos.add(BookingMapper.toBookingDto(booking,
                            userService.getUser(booking.getBookerId()),
                            itemService.getItem(booking.getItemId(), userId)));
                }
            }
        } else if (stateAfterCheck.equals(State.ALL)) {
            for (Booking booking : temp) {
                bookingDtos.add(BookingMapper.toBookingDto(booking,
                        userService.getUser(booking.getBookerId()),
                        itemService.getItem(booking.getItemId(), userId)));
            }
        } else if (stateAfterCheck.equals(State.CURRENT)) {
            for (Booking booking : temp) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking,
                        userService.getUser(booking.getBookerId()),
                        itemService.getItem(booking.getItemId(), userId));
                if (bookingDtoAdd.getStart().isBefore(real)
                        && bookingDtoAdd.getEnd().isAfter(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        } else if (stateAfterCheck.equals(State.PAST)) {
            for (Booking booking : temp) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking,
                        userService.getUser(booking.getBookerId()),
                        itemService.getItem(booking.getItemId(), userId));
                if (bookingDtoAdd.getStart().isBefore(real) && bookingDtoAdd.getEnd().isBefore(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        } else if (stateAfterCheck.equals(State.FUTURE)) {
            for (Booking booking : temp) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking,
                        userService.getUser(booking.getBookerId()),
                        itemService.getItem(booking.getItemId(), userId));
                if (bookingDtoAdd.getStart().isAfter(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        }
        return bookingDtos.stream()
                .sorted(new Comparator<BookingDto>() {
                    @Override
                    public int compare(BookingDto o1, BookingDto o2) {
                        return o2.getStart().compareTo(o1.getStart());
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getAllBookingsOwner(int userId, String state) {
        LocalDateTime real = LocalDateTime.now();
        checkUserId(userId);
        State stateAfterCheck = checkStatus(state);
        Collection<BookingDto> bookingDtos = new ArrayList<>();
        Collection<Booking> bookingsFull = repository.findAll();
        if (stateAfterCheck.equals(State.WAITING)) {
            for (Booking booking : bookingsFull) {
                if (booking.getStatus().equals(Status.WAITING)) {
                    bookingDtos.add(BookingMapper.toBookingDto(booking, userService.getUser(booking.getBookerId()),
                            itemService.getItem(booking.getItemId(), userId)));
                }
            }
        } else if (stateAfterCheck.equals(State.REJECTED)) {
            for (Booking booking : bookingsFull) {
                if (booking.getStatus().equals(Status.REJECTED)) {
                    bookingDtos.add(BookingMapper.toBookingDto(booking, userService.getUser(booking.getBookerId()),
                            itemService.getItem(booking.getItemId(), userId)));
                }
            }
        } else if (stateAfterCheck.equals(State.ALL)) {
            for (Booking booking : bookingsFull) {
                bookingDtos.add(BookingMapper.toBookingDto(booking, userService.getUser(booking.getBookerId()),
                        itemService.getItem(booking.getItemId(), userId)));
            }
        } else if (stateAfterCheck.equals(State.CURRENT)) {
            for (Booking booking : bookingsFull) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking,
                        userService.getUser(booking.getBookerId()),
                        itemService.getItem(booking.getItemId(), userId));
                if (bookingDtoAdd.getStart().isBefore(real)
                        && bookingDtoAdd.getEnd().isAfter(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        } else if (stateAfterCheck.equals(State.PAST)) {
            for (Booking booking : bookingsFull) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking,
                        userService.getUser(booking.getBookerId()),
                        itemService.getItem(booking.getItemId(), userId));
                if (bookingDtoAdd.getStart().isBefore(real) && bookingDtoAdd.getEnd().isBefore(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        } else if (stateAfterCheck.equals(State.FUTURE)) {
            for (Booking booking : bookingsFull) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking,
                        userService.getUser(booking.getBookerId()),
                        itemService.getItem(booking.getItemId(), userId));
                if (bookingDtoAdd.getStart().isAfter(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        }
        return bookingDtos.stream()
                .sorted(new Comparator<BookingDto>() {
                    @Override
                    public int compare(BookingDto o1, BookingDto o2) {
                        return o2.getStart().compareTo(o1.getStart());
                    }
                }).filter(x -> x.getItem().getOwner() == userId)
                .collect(Collectors.toList());
    }

    public void checkUserId(int userId) {
        if (userService.findAllUsers().stream().noneMatch(x -> x.getId() == userId)) {
            throw new NotFoundObjectException(String.format(
                    "Пользователя с идентификатором %s не существует.", userId));
        }
    }

    public void checkUserAndBookerId(int userId, int bookingId) {
        if (repository.findBookingByIdEquals(bookingId).getBookerId() != userId
                && itemService.getItem(repository.findBookingByIdEquals(bookingId).getItemId(), userId).getOwner() != userId) {
            throw new InvalidHeaderUserId(String.format(
                    "Пользователю с идентификатором %s бронирование с идентификатором "
                            + bookingId + " не принадлежит.", userId));
        }
    }

    public void checkBookingId(int bookingId) {
        if (repository.findAll().stream().noneMatch(x -> x.getId() == bookingId)) {
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
}
