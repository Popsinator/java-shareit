package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
    public Booking createBooking(int userId, BookingDtoIn booking) {
        LocalDateTime real = LocalDateTime.now();
        if (!itemRepository.existsById(booking.getItemId())) {
            throw new NotFoundException(String.format("Вещи с идентификатором %s не существует.", booking.getItemId()));
        } else if (!itemRepository.findItemByIdEquals(booking.getItemId()).getAvailable()) {
            throw new BadRequestException(String.format("Вещь с id %s недоступна для бронирования", booking.getItemId()));
        } else if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Владельца с идентификатором %s не существует.", userId));
        } else if ((booking.getEnd().isBefore(booking.getStart())
                || booking.getStart().isBefore(real) || booking.getEnd().isBefore(real)) && booking.getId() == 0) {
            throw new BadRequestException("Даты бронирования некорректно заданы");
        } else if (itemRepository.findItemByIdEquals(booking.getItemId()).getOwner().getId() == userId) {
            throw new NotFoundException("Некорректный владелец item в заголовке 'X-Sharer-User-Id'");
        }
        booking.setStatus(Status.WAITING);
        Booking test = BookingMapper.toBookingDtoIn(booking, userRepository.findUserByIdEquals(userId).get(),
                itemRepository.findItemByIdEquals(booking.getItemId()));
        return bookingRepository.save(test);
    }

    @Transactional
    @Override
    public BookingDto changeStatusOnApprovedOrRejected(int bookingId, int userId, String approved) {
        Booking existBooking = getBooking(bookingId, userId);
        if (existBooking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Некорректный владелец item в заголовке 'X-Sharer-User-Id'");
        } else if (existBooking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Бронирование уже на статусе APPROVED");
        } else if (approved.equals("true")) {
            existBooking.setStatus(Status.APPROVED);
        } else if (approved.equals("false")) {
            existBooking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(existBooking),
                existBooking.getBooker(), existBooking.getItem());
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
        return BookingMapper.toBookingDto(booking, booking.getBooker(), booking.getItem());
    }

    @Override
    public List<BookingDto> getAllBookings(int userId, String state, String from, String size) {
        LocalDateTime real = LocalDateTime.now();
        checkUserId(userId);
        State stateAfterCheck = checkStatus(state);
        Collection<BookingDto> bookingDtos = new ArrayList<>();
        Collection<Booking> temp;
        if (Objects.equals(from, "") || Objects.equals(size, "")) {
            temp = new ArrayList<>(bookingRepository.findAllByBooker_Id(userId));
        } else {
            int start = Integer.parseInt(from) / Integer.parseInt(size);
            temp = bookingRepository.findAllByBooker_Id(userId, PageRequest.of(start, Integer.parseInt(size),
                            Sort.by(Sort.Direction.DESC, "start")))
                    .stream()
                    .collect(Collectors.toList());
        }
        fillBookingDto(real, stateAfterCheck, bookingDtos, temp);
        return bookingDtos.stream()
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart())).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsOwner(int userId, String state, String from, String size) {
        LocalDateTime real = LocalDateTime.now();
        checkUserId(userId);
        State stateAfterCheck = checkStatus(state);
        List<BookingDto> bookingDtos = new ArrayList<>();
        List<Booking> bookingsFull;
        if (Objects.equals(from, "") || Objects.equals(size, "")) {
            bookingsFull = bookingRepository.findAll();
        } else {
            int start = Integer.parseInt(from) / Integer.parseInt(size);
            bookingsFull = bookingRepository.findAllByItemOwner_Id(userId, PageRequest.of(start, Integer.parseInt(size),
                            Sort.by(Sort.Direction.DESC, "start")))
                    .stream()
                    .collect(Collectors.toList());
            fillBookingDto(real, stateAfterCheck, bookingDtos, bookingsFull);
            return bookingDtos;
        }
        fillBookingDto(real, stateAfterCheck, bookingDtos, bookingsFull);
        return bookingDtos.stream()
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart())).filter(x -> x.getItem().getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    public void checkUserId(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(
                    "Пользователя с идентификатором %s не существует.", userId));
        }
    }

    public void checkUserAndBookerId(int userId, int bookingId) {
        if (bookingRepository.findBookingByIdEquals(bookingId).getBooker().getId() != userId
                && bookingRepository.findBookingByIdEquals(bookingId).getItem().getOwner().getId() != userId) {
            throw new NotFoundException(String.format(
                    "Пользователю с идентификатором %s бронирование с идентификатором "
                            + bookingId + " не принадлежит.", userId));
        }
    }

    public void checkBookingId(int bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException(String.format(
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
            throw new BadRequestException("Unknown state: " + state);
        }
    }

    public void fillBookingDto(LocalDateTime real, State stateAfterCheck, Collection<BookingDto> bookingDtos,
                               Collection<Booking> temp) {
        if (stateAfterCheck.equals(State.WAITING)) {
            for (Booking booking : temp) {
                if (booking.getStatus().equals(Status.WAITING)) {
                    bookingDtos.add(BookingMapper.toBookingDto(booking, booking.getBooker(), booking.getItem()));
                }
            }
        } else if (stateAfterCheck.equals(State.REJECTED)) {
            for (Booking booking : temp) {
                if (booking.getStatus().equals(Status.REJECTED)) {
                    bookingDtos.add(BookingMapper.toBookingDto(booking, booking.getBooker(), booking.getItem()));
                }
            }
        } else if (stateAfterCheck.equals(State.ALL)) {
            for (Booking booking : temp) {
                bookingDtos.add(BookingMapper.toBookingDto(booking, booking.getBooker(), booking.getItem()));
            }
        } else if (stateAfterCheck.equals(State.CURRENT)) {
            for (Booking booking : temp) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking, booking.getBooker(), booking.getItem());
                if (bookingDtoAdd.getStart().isBefore(real)
                        && bookingDtoAdd.getEnd().isAfter(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        } else if (stateAfterCheck.equals(State.PAST)) {
            for (Booking booking : temp) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking, booking.getBooker(), booking.getItem());
                if (bookingDtoAdd.getStart().isBefore(real) && bookingDtoAdd.getEnd().isBefore(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        } else if (stateAfterCheck.equals(State.FUTURE)) {
            for (Booking booking : temp) {
                BookingDto bookingDtoAdd = BookingMapper.toBookingDto(booking, booking.getBooker(), booking.getItem());
                if (bookingDtoAdd.getStart().isAfter(real)) {
                    bookingDtos.add(bookingDtoAdd);
                }
            }
        }
    }
}
